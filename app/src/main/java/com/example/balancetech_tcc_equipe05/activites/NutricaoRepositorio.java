package com.example.balancetech_tcc_equipe05.activites;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;

import com.example.balancetech_tcc_equipe05.activites.classes.AppDatabase;
import com.example.balancetech_tcc_equipe05.activites.classes.Perfil;
import com.example.balancetech_tcc_equipe05.activites.classes.RegistroAlimentar;
import com.example.balancetech_tcc_equipe05.activites.classes.RegistroAlimentarDAO;
import com.example.balancetech_tcc_equipe05.activites.classes.Usuario;
import com.example.balancetech_tcc_equipe05.activites.dominio.Agregador;
import com.example.balancetech_tcc_equipe05.activites.dominio.CalculoNutricional;
import com.example.balancetech_tcc_equipe05.activites.dominio.Datas;
import com.example.balancetech_tcc_equipe05.activites.dominio.Nutriente;
import com.example.balancetech_tcc_equipe05.activites.dominio.PerfilCompleto;
import com.example.balancetech_tcc_equipe05.activites.dominio.ResumoDia;
import com.example.balancetech_tcc_equipe05.activites.dominio.SerieEvolucao;
import com.example.balancetech_tcc_equipe05.activites.dominio.Totais;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Ponto único de acesso aos dados do app: resumo diário (Home), histórico,
 * série do gráfico de evolução e perfil.
 *
 * Os métodos síncronos acessam o Room e NÃO podem rodar na thread da tela;
 * use {@link #executar} para chamá-los e receber o resultado na thread principal.
 *
 * As metas (kcal e macros) vêm do perfil atual; o app não guarda o histórico
 * de metas, então dias antigos são comparados com a meta de hoje.
 */
public final class NutricaoRepositorio {

    public interface Resultado<T> {
        void sucesso(T valor);

        void erro(Exception erro);
    }

    /** Dados inválidos informados pelo usuário; a mensagem pode ser exibida na tela. */
    public static final class DadosInvalidosException extends Exception {
        public DadosInvalidosException(String mensagem) {
            super(mensagem);
        }
    }

    /** Campos editáveis do perfil. */
    public static final class DadosPerfil {
        public String nome;
        public String email;
        public String genero;
        public int idade;
        public double peso;
        public int altura;
        public String objetivo;
        public int nivelAtividade;
    }

    private static final ExecutorService BANCO = Executors.newSingleThreadExecutor();
    private static final Handler PRINCIPAL = new Handler(Looper.getMainLooper());

    private final Context contexto;
    private final AppDatabase db;
    private final TimeZone fuso = TimeZone.getDefault();

    public NutricaoRepositorio(Context contexto) {
        this.contexto = contexto.getApplicationContext();
        this.db = AppDatabase.getDatabase(this.contexto);
    }

    /** Roda a tarefa fora da thread da tela e entrega o resultado na thread principal. */
    public <T> void executar(Callable<T> tarefa, Resultado<T> resultado) {
        BANCO.execute(() -> {
            try {
                T valor = tarefa.call();
                PRINCIPAL.post(() -> resultado.sucesso(valor));
            } catch (Exception erro) {
                PRINCIPAL.post(() -> resultado.erro(erro));
            }
        });
    }

    // ------------------------------------------------------------------
    // Resumo diário e histórico
    // ------------------------------------------------------------------

    public ResumoDia resumoDoDia(int usuarioId, long referenciaMillis) {
        long inicio = Datas.inicioDoDia(referenciaMillis, fuso);
        List<ResumoDia> dias = resumos(usuarioId, inicio, Datas.somarDias(inicio, 1, fuso));
        return dias.get(0);
    }

    /** Últimos {@code dias} dias, do mais recente (hoje) ao mais antigo, inclusive dias sem registro. */
    public List<ResumoDia> historico(int usuarioId, int dias) {
        long hoje = Datas.inicioDoDia(System.currentTimeMillis(), fuso);
        long inicio = Datas.somarDias(hoje, -(dias - 1), fuso);
        List<ResumoDia> lista = resumos(usuarioId, inicio, Datas.somarDias(hoje, 1, fuso));
        Collections.reverse(lista);
        return lista;
    }

    /** Resumos dia a dia em [inicio, fim), do mais antigo ao mais recente. */
    private List<ResumoDia> resumos(int usuarioId, long inicio, long fim) {
        long hoje = Datas.inicioDoDia(System.currentTimeMillis(), fuso);
        List<Agregador.Linha> linhas = new ArrayList<>();
        for (RegistroAlimentar r : db.registroAlimentarDao().consultarPeriodo(usuarioId, inicio, fim)) {
            linhas.add(new Agregador.Linha(r.refeicao, r.horarioMillis, r.porcoesJson));
        }
        return Agregador.porDia(linhas, inicio, fim, hoje, metasDe(usuarioId), fuso);
    }

    public List<RegistroAlimentar> registrosDoDia(int usuarioId, long referenciaMillis) {
        long inicio = Datas.inicioDoDia(referenciaMillis, fuso);
        return db.registroAlimentarDao()
                .consultarPeriodo(usuarioId, inicio, Datas.somarDias(inicio, 1, fuso));
    }

    /** Remove um registro; o histórico e o gráfico refletem a mudança na próxima consulta. */
    public boolean excluirRegistro(int usuarioId, String idRegistro) {
        return db.registroAlimentarDao().excluir(usuarioId, idRegistro) > 0;
    }

    /** Edita refeição, horário ou porções de um registro existente. */
    public boolean editarRegistro(int usuarioId, String idRegistro, String refeicao,
                                  long horarioMillis, String porcoesJson) {
        RegistroAlimentarDAO dao = db.registroAlimentarDao();
        RegistroAlimentar atual = dao.buscarPorId(usuarioId, idRegistro);
        if (atual == null) return false;
        if (refeicao != null) atual.refeicao = refeicao;
        if (horarioMillis > 0) atual.horarioMillis = horarioMillis;
        if (porcoesJson != null) atual.porcoesJson = porcoesJson;
        dao.substituir(atual);
        return true;
    }

    // ------------------------------------------------------------------
    // Gráfico de evolução
    // ------------------------------------------------------------------

    /** Séries dos últimos {@code dias} dias (uma por nutriente) a partir da alimentação registrada. */
    public Map<Nutriente, SerieEvolucao> evolucao(int usuarioId, int dias) {
        long hoje = Datas.inicioDoDia(System.currentTimeMillis(), fuso);
        long inicio = Datas.somarDias(hoje, -(dias - 1), fuso);
        List<ResumoDia> lista = resumos(usuarioId, inicio, Datas.somarDias(hoje, 1, fuso));
        Totais meta = metasDe(usuarioId);

        String[] rotulos = new String[lista.size()];
        for (int i = 0; i < rotulos.length; i++) {
            rotulos[i] = Datas.inicialSemana(lista.get(i).inicioDia, fuso);
        }
        Map<Nutriente, SerieEvolucao> series = new EnumMap<>(Nutriente.class);
        for (Nutriente n : Nutriente.values()) {
            float[] valores = new float[lista.size()];
            for (int i = 0; i < valores.length; i++) {
                valores[i] = (float) lista.get(i).consumido.valor(n);
            }
            series.put(n, new SerieEvolucao(n, valores, rotulos, meta.valor(n)));
        }
        return series;
    }

    // ------------------------------------------------------------------
    // Perfil
    // ------------------------------------------------------------------

    /** Perfil do usuário ou null se ele ainda não respondeu o questionário. */
    public PerfilCompleto buscarPerfil(int usuarioId) {
        Usuario usuario = db.usuarioDao().buscarPorId(usuarioId);
        if (usuario == null) return null;
        PerfilCompleto completo = new PerfilCompleto();
        completo.usuarioId = usuarioId;
        completo.nome = usuario.getNome();
        completo.email = usuario.getEmail();

        Perfil perfil = perfilDe(usuarioId);
        if (perfil != null) {
            completo.genero = perfil.getGenero();
            completo.idade = perfil.getIdade();
            completo.peso = perfil.getPeso();
            completo.altura = perfil.getAltura();
            completo.objetivo = perfil.getObjetivo();
            completo.exercicio = perfil.getExercicio();
            completo.imc = perfil.getImc();
            completo.metas = CalculoNutricional.metasMacros(perfil.getMetaCalorica(), perfil.getObjetivo());
        }
        return completo;
    }

    /**
     * Valida e grava nome/e-mail e dados do perfil, recalculando IMC e meta calórica.
     *
     * @throws DadosInvalidosException se algum campo estiver fora do aceitável ou o e-mail já for de outra conta
     */
    public PerfilCompleto atualizarPerfil(int usuarioId, DadosPerfil d) throws DadosInvalidosException {
        String nome = d.nome == null ? "" : d.nome.trim();
        String email = d.email == null ? "" : d.email.trim();
        if (nome.isEmpty()) throw new DadosInvalidosException("Informe o nome.");
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw new DadosInvalidosException("Digite um e-mail válido.");
        }
        boolean homem = "Homem".equalsIgnoreCase(d.genero);
        if (!homem && !"Mulher".equalsIgnoreCase(d.genero)) {
            throw new DadosInvalidosException("Selecione o gênero.");
        }
        if (d.idade < 10 || d.idade > 120) throw new DadosInvalidosException("Idade inválida.");
        if (!(d.peso >= 20 && d.peso <= 500)) throw new DadosInvalidosException("Peso inválido.");
        if (d.altura < 80 || d.altura > 260) throw new DadosInvalidosException("Altura inválida.");
        if (d.nivelAtividade < 0 || d.nivelAtividade >= CalculoNutricional.NIVEIS.length) {
            throw new DadosInvalidosException("Selecione o nível de atividade.");
        }
        boolean objetivoValido = false;
        for (String o : CalculoNutricional.OBJETIVOS) {
            objetivoValido |= o.equalsIgnoreCase(d.objetivo);
        }
        if (!objetivoValido) throw new DadosInvalidosException("Selecione o objetivo.");

        Usuario usuario = db.usuarioDao().buscarPorId(usuarioId);
        Perfil perfil = perfilDe(usuarioId);
        if (usuario == null || perfil == null) {
            throw new DadosInvalidosException("Perfil não encontrado. Entre novamente.");
        }
        Usuario outro = db.usuarioDao().buscarPorEmail(email);
        if (outro != null && outro.getId() != usuarioId) {
            throw new DadosInvalidosException("Este e-mail já está em uso.");
        }

        double tmb = CalculoNutricional.tmb(homem, d.peso, d.altura, d.idade);
        double gasto = CalculoNutricional.gastoDiario(tmb, d.nivelAtividade);
        double meta = CalculoNutricional.metaCalorica(gasto, d.objetivo);
        if (!(meta > 0)) throw new DadosInvalidosException("Confira idade, peso e altura.");

        perfil.setGenero(homem ? "Homem" : "Mulher");
        perfil.setIdade(d.idade);
        perfil.setPeso(d.peso);
        perfil.setAltura(d.altura);
        perfil.setObjetivo(d.objetivo);
        perfil.setExercicio(CalculoNutricional.NIVEIS[d.nivelAtividade]);
        perfil.setImc(CalculoNutricional.imc(d.peso, d.altura));
        perfil.setMetaCalorica(meta);

        usuario.setNome(nome);
        usuario.setEmail(email);
        final Usuario usuarioFinal = usuario;
        final Perfil perfilFinal = perfil;
        db.runInTransaction(() -> {
            db.usuarioDao().atualizar(usuarioFinal);
            db.perfilDao().atualizar(perfilFinal);
        });
        return buscarPerfil(usuarioId);
    }

    // ------------------------------------------------------------------

    private Perfil perfilDe(int usuarioId) {
        long id = MetasPerfil.idPerfilVerificado(contexto, usuarioId);
        return id <= 0 ? null : db.perfilDao().buscarPorId(usuarioId, id);
    }

    /** Metas do usuário; zeradas se ainda não há perfil. */
    private Totais metasDe(int usuarioId) {
        Perfil perfil = perfilDe(usuarioId);
        return perfil == null
                ? new Totais()
                : CalculoNutricional.metasMacros(perfil.getMetaCalorica(), perfil.getObjetivo());
    }
}