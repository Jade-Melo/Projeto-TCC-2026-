package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;
import com.example.balancetech_tcc_equipe05.activites.dominio.Nutriente;
import com.example.balancetech_tcc_equipe05.activites.dominio.SerieEvolucao;

import java.text.NumberFormat;
import java.util.Map;

public class EvolucaoActivity extends AppCompatActivity {

    private ImageButton btnMenu;
    private ImageButton btnPerfilEvolucao;

    private TextView btnCarbs;
    private TextView btnGorduras;
    private TextView btnProteinas;

    private TextView txtMeta;
    private TextView txtAtual;

    private GraficoEvolucaoView graficoEvolucao;

    private static final int DIAS_NO_GRAFICO = 14;
    private Map<Nutriente, SerieEvolucao> series; // null enquanto não há dados reais
    private Nutriente selecionado = Nutriente.CARBOIDRATOS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_evolucao);

        // =========================
        // MENU
        // =========================

        btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(v -> {

            Intent intent = new Intent(
                    EvolucaoActivity.this,
                    MenuActivity.class
            );

            startActivity(intent);
        });

        // =========================
        // PERFIL
        // =========================

        btnPerfilEvolucao = findViewById(R.id.btnPerfilEvolucao);

        btnPerfilEvolucao.setOnClickListener(v -> {

            Intent intent = new Intent(
                    EvolucaoActivity.this,
                    PerfilActivity.class
            );

            startActivity(intent);
        });

        // =========================
        // BOTÕES
        // =========================

        btnCarbs = findViewById(R.id.btnCarbs);
        btnGorduras = findViewById(R.id.btnGorduras);
        btnProteinas = findViewById(R.id.btnProteinas);

        // =========================
        // META / ATUAL
        // =========================

        txtMeta = findViewById(R.id.txtMeta);
        txtAtual = findViewById(R.id.txtAtual);

        // =========================
        // GRÁFICO
        // =========================

        graficoEvolucao = findViewById(R.id.graficoEvolucao);

        // =========================
        // SELEÇÃO DE NUTRIENTE
        // =========================

        btnCarbs.setOnClickListener(v -> selecionar(Nutriente.CARBOIDRATOS));
        btnGorduras.setOnClickListener(v -> selecionar(Nutriente.GORDURA));
        btnProteinas.setOnClickListener(v -> selecionar(Nutriente.PROTEINA));

        // Atalhos da Home enviam qual gráfico abrir.
        String pedido = getIntent().getStringExtra("grafico");
        if ("proteinas".equals(pedido)) {
            selecionado = Nutriente.PROTEINA;
        } else if ("gorduras".equals(pedido)) {
            selecionado = Nutriente.GORDURA;
        }
        selecionar(selecionado); // sem sessão, mostra os valores de demonstração
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDados();
    }

    // =========================
    // DADOS REAIS (alimentação registrada)
    // =========================

    private void carregarDados() {
        final int usuarioId = SessaoUsuario.atual();
        if (usuarioId <= 0) {
            return;
        }
        NutricaoRepositorio repo = new NutricaoRepositorio(this);
        repo.executar(
                () -> repo.evolucao(usuarioId, DIAS_NO_GRAFICO),
                new NutricaoRepositorio.Resultado<Map<Nutriente, SerieEvolucao>>() {
                    @Override
                    public void sucesso(Map<Nutriente, SerieEvolucao> dados) {
                        if (!isFinishing() && !isDestroyed() && SessaoUsuario.atual() == usuarioId) {
                            series = dados;
                            mostrarCalorias(dados.get(Nutriente.KCAL));
                            selecionar(selecionado);
                        }
                    }

                    @Override
                    public void erro(Exception erro) {
                        android.util.Log.e("BalanceTech", "Erro ao carregar a evolução", erro);
                    }
                });
    }

    private void mostrarCalorias(SerieEvolucao kcal) {
        NumberFormat inteiro = NumberFormat.getIntegerInstance(Alimentacao.PT_BR);
        ((TextView) findViewById(R.id.txtCalorias)).setText(
                inteiro.format(Math.round(kcal.atual)) + " / "
                        + inteiro.format(Math.round(kcal.meta)) + " Kcal");
        ProgressBar barra = findViewById(R.id.progressCalorias);
        barra.setMax(100);
        barra.setProgress(com.example.balancetech_tcc_equipe05.activites.dominio
                .CalculoNutricional.percentual(kcal.atual, kcal.meta));
    }

    private void selecionar(Nutriente nutriente) {
        selecionado = nutriente;
        TextView botao;
        int cor;
        switch (nutriente) {
            case PROTEINA:
                botao = btnProteinas;
                cor = 0xFFB64ACB;
                break;
            case GORDURA:
                botao = btnGorduras;
                cor = 0xFF3295D5;
                break;
            default:
                botao = btnCarbs;
                cor = 0xFFE65B68;
                break;
        }
        selecionarBotao(botao);

        SerieEvolucao serie = series == null ? null : series.get(nutriente);
        if (serie != null) {
            graficoEvolucao.mostrarSerie(serie.valores, serie.rotulos,
                    serie.maximoGrafico(), "g", cor);
            txtMeta.setText(Math.round(serie.meta) + " g");
            txtAtual.setText(Math.round(serie.atual) + " g");
            return;
        }
        if (SessaoUsuario.atual() > 0) {
            // Logado, mas os dados ainda estão carregando: não mostra números de demonstração.
            graficoEvolucao.mostrarSerie(new float[0], new String[0], 1f, "g", cor);
            txtMeta.setText("-");
            txtAtual.setText("-");
            return;
        }
        // Demonstração (sem sessão)
        switch (nutriente) {
            case PROTEINA:
                graficoEvolucao.mostrarProteinas();
                txtMeta.setText("209 g");
                txtAtual.setText("205 g");
                break;
            case GORDURA:
                graficoEvolucao.mostrarGorduras();
                txtMeta.setText("175 g");
                txtAtual.setText("165 g");
                break;
            default:
                graficoEvolucao.mostrarCarbs();
                txtMeta.setText("349 g");
                txtAtual.setText("211 g");
                break;
        }
    }

    // =========================
    // BOTÃO SELECIONADO
    // =========================

    private void selecionarBotao(TextView botaoSelecionado) {

        btnCarbs.setBackgroundResource(
                R.drawable.menu_item_background
        );

        btnGorduras.setBackgroundResource(
                R.drawable.menu_item_background
        );

        btnProteinas.setBackgroundResource(
                R.drawable.menu_item_background
        );

        botaoSelecionado.setBackgroundResource(
                R.drawable.menu_item_background_selected
        );
    }
}