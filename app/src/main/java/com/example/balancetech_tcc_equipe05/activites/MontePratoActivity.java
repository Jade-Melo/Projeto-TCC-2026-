package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class MontePratoActivity extends AppCompatActivity {

    private final ArrayList<Bundle> porcoes = new ArrayList<>();
    private final View[] linhas = new View[Alimentacao.CATALOGO.length];
    private static final String ESTADO_ID = "alimentacao.id";
    private static final String ESTADO_HORARIO = "alimentacao.horario";
    private String idRegistro;
    private long horarioRegistro;
    private boolean confirmando;
    private TextView textoPrato;
    private TextView semResultados;
    private EditText pesquisa;
    private IntegracaoAlimentacao.Metas metas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monte_prato);

        findViewById(R.id.btnMenu).setOnClickListener(v ->
                startActivity(new Intent(this, MenuActivity.class)));
        findViewById(R.id.btnPerfilMontePrato).setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class)));

        if (savedInstanceState != null) {
            ArrayList<Bundle> rascunho = savedInstanceState.getParcelableArrayList(
                    Alimentacao.EXTRA_PORCOES);
            if (rascunho != null) {
                porcoes.addAll(rascunho);
            }
        }

        if (savedInstanceState != null) {
            idRegistro = savedInstanceState.getString(ESTADO_ID);
            horarioRegistro = savedInstanceState.getLong(ESTADO_HORARIO);
        }

        pesquisa = findViewById(R.id.edtPesquisa);
        textoPrato = findViewById(R.id.txtPratoAtual);
        semResultados = findViewById(R.id.txtSemAlimentos);
        LinearLayout lista = findViewById(R.id.listaAlimentos);
        for (int i = 0; i < Alimentacao.CATALOGO.length; i++) {
            Alimentacao.Alimento alimento = Alimentacao.CATALOGO[i];
            View linha = getLayoutInflater().inflate(R.layout.item_alimento, lista, false);
            TextView nome = linha.findViewById(R.id.txtNomeAlimento);
            nome.setText(alimento.nome);
            View botao = linha.findViewById(R.id.btnAdicionarAlimento);
            botao.setContentDescription("Adicionar " + alimento.nome);
            botao.setOnClickListener(v -> pedirPeso(alimento));
            lista.addView(linha);
            linhas[i] = linha;
        }

        pesquisa.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrar(s.toString());
            }

            @Override public void afterTextChanged(Editable s) {
            }
        });
        findViewById(R.id.btnTare).setOnClickListener(v -> tararBalanca());
        findViewById(R.id.btnPronto).setOnClickListener(v -> revisarPrato());
        filtrar(pesquisa.getText().toString());
        atualizarPrato();
    }

    @Override
    protected void onResume() {
        super.onResume();
        metas = null;
        MetasPerfil.carregar(this, carregadas -> metas = carregadas);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Alimentacao.EXTRA_PORCOES, porcoes);
        outState.putString(ESTADO_ID, idRegistro);
        outState.putLong(ESTADO_HORARIO, horarioRegistro);
    }

    private void filtrar(String texto) {
        boolean encontrou = false;
        for (int i = 0; i < linhas.length; i++) {
            boolean exibir = Alimentacao.corresponde(Alimentacao.CATALOGO[i].nome, texto);
            linhas[i].setVisibility(exibir ? View.VISIBLE : View.GONE);
            encontrou |= exibir;
        }
        semResultados.setVisibility(encontrou ? View.GONE : View.VISIBLE);
    }

    private void tararBalanca() {
        IntegracaoAlimentacao integracao = IntegracaoAlimentacao.de(getApplication());
        boolean aceitou = integracao != null && integracao.solicitarTara();
        Toast.makeText(this, aceitou ? "Tara solicitada. Aguarde o peso estabilizar."
                : "Balança indisponível. Você pode digitar o peso.", Toast.LENGTH_SHORT).show();
    }

    private void pedirPeso(Alimentacao.Alimento alimento) {
        EditText peso = new EditText(this);
        peso.setHint("Peso em gramas");
        peso.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        peso.setSingleLine();

        IntegracaoAlimentacao integracao = IntegracaoAlimentacao.de(getApplication());
        LinearLayout campos = new LinearLayout(this);
        campos.setOrientation(LinearLayout.VERTICAL);
        int margem = Math.round(20 * getResources().getDisplayMetrics().density);
        campos.setPadding(margem, 0, margem, 0);
        campos.addView(peso);
        TextView previa = new TextView(this);
        campos.addView(previa);
        peso.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                double gramas = lerGramas(s);
                if (!Alimentacao.positivo(gramas)) {
                    previa.setText("");
                    return;
                }
                Alimentacao.Totais totais = new Alimentacao.Totais();
                totais.somar(Collections.singletonList(alimento.porcao(gramas, false)));
                previa.setText(totais.resumo() + (metas == null ? "" : totais.compararComMetas(metas)));
            }

            @Override public void afterTextChanged(Editable s) {
            }
        });

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle(alimento.nome)
                .setView(campos)
                .setNegativeButton("Cancelar", null)
                .setNeutralButton("Usar balança", null)
                .setPositiveButton("Adicionar", null)
                .create();
        dialogo.setOnShowListener(ignorado -> {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                double gramas = lerGramas(peso.getText());
                if (!Alimentacao.positivo(gramas)) {
                    peso.setError("Digite um peso válido, maior que zero");
                    return;
                }
                adicionarPorcao(alimento, gramas, false);
                dialogo.dismiss();
            });
            dialogo.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                Double gramas = integracao == null ? null : integracao.pesoEstavelGramas();
                if (gramas == null || !Alimentacao.positivo(gramas)) {
                    peso.setError("Sem peso estável. Confira a balança ou digite o peso.");
                    return;
                }
                adicionarPorcao(alimento, gramas, true);
                dialogo.dismiss();
            });
        });
        dialogo.show();
    }

    private double lerGramas(CharSequence texto) {
        try {
            return Double.parseDouble(texto.toString().trim().replace(',', '.'));
        } catch (NumberFormatException erro) {
            return Double.NaN;
        }
    }

    private void adicionarPorcao(Alimentacao.Alimento alimento, double gramas,
                                 boolean pesoDaBalanca) {
        porcoes.add(alimento.porcao(gramas, pesoDaBalanca));
        idRegistro = null;
        atualizarPrato();
    }

    private void atualizarPrato() {
        Alimentacao.Totais totais = new Alimentacao.Totais();
        totais.somar(porcoes);
        textoPrato.setText(String.format(Alimentacao.PT_BR, "Porções no prato: %d · %s kcal",
                porcoes.size(), Alimentacao.numero(totais.kcal)));
    }

    private void revisarPrato() {
        if (confirmando) {
            return;
        }
        if (porcoes.isEmpty()) {
            Toast.makeText(this, "Adicione um alimento ao prato", Toast.LENGTH_SHORT).show();
            return;
        }

        Alimentacao.Totais totais = new Alimentacao.Totais();
        totais.somar(porcoes);
        StringBuilder resumo = new StringBuilder();
        for (Bundle porcao : porcoes) {
            resumo.append(Alimentacao.descricao(porcao)).append('\n');
        }
        resumo.append('\n').append(totais.resumo());
        if (metas != null) {
            resumo.append(totais.compararComMetas(metas));
        }

        String refeicao = getIntent().getStringExtra(Alimentacao.EXTRA_REFEICAO);
        new AlertDialog.Builder(this)
                .setTitle("Resumo do prato")
                .setMessage(resumo)
                .setNegativeButton("Voltar", null)
                .setNeutralButton("Desfazer último", (dialogo, botao) -> {
                    porcoes.remove(porcoes.size() - 1);
                    idRegistro = null;
                    atualizarPrato();
                })
                .setPositiveButton(refeicao == null ? "Escolher refeição" : "Confirmar",
                        (dialogo, botao) -> {
                            if (refeicao == null) {
                                escolherRefeicao();
                            } else {
                                confirmarPrato(Alimentacao.Refeicao.valueOf(refeicao), true);
                            }
                        })
                .show();
    }

    private void escolherRefeicao() {
        Alimentacao.Refeicao[] refeicoes = Alimentacao.Refeicao.values();
        String[] rotulos = new String[refeicoes.length];
        for (int i = 0; i < refeicoes.length; i++) {
            rotulos[i] = refeicoes[i].rotulo;
        }
        new AlertDialog.Builder(this)
                .setTitle("Em qual refeição?")
                .setItems(rotulos, (dialogo, posicao) -> confirmarPrato(refeicoes[posicao], false))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarPrato(Alimentacao.Refeicao refeicao, boolean retornar) {
        if (confirmando) {
            return;
        }
        IntegracaoAlimentacao integracao = IntegracaoAlimentacao.de(getApplication());
        IntegracaoAlimentacao.Registros registros = integracao == null
                ? null : integracao.registrosAtuais();
        if (registros == null) {
            entregarPrato(refeicao, retornar);
            return;
        }

        if (idRegistro == null) {
            idRegistro = UUID.randomUUID().toString();
            horarioRegistro = System.currentTimeMillis();
        }
        IntegracaoAlimentacao.Registro registro = new IntegracaoAlimentacao.Registro(
                idRegistro + "-" + refeicao.name(), refeicao.name(), horarioRegistro, porcoes);
        confirmando = true;
        AlertDialog progresso = new AlertDialog.Builder(this)
                .setMessage("Salvando refeição…")
                .setCancelable(false)
                .show();
        new Thread(() -> {
            boolean salvou;
            try {
                registros.salvar(registro);
                salvou = true;
            } catch (Exception erro) {
                android.util.Log.e("BalanceTech", "Falha ao registrar refeição", erro);
                salvou = false;
            }
            final boolean sucesso = salvou;
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                progresso.dismiss();
                confirmando = false;
                if (sucesso) {
                    entregarPrato(refeicao, retornar);
                } else {
                    Toast.makeText(this, "Falha ao salvar. Seu prato foi mantido. Tente novamente.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }, "balance-tech-registro").start();
    }

    private void entregarPrato(Alimentacao.Refeicao refeicao, boolean retornar) {
        Intent dados = new Intent();
        dados.putExtra(Alimentacao.EXTRA_REFEICAO, refeicao.name());
        dados.putParcelableArrayListExtra(Alimentacao.EXTRA_PORCOES, porcoes);

        if (retornar) {
            setResult(RESULT_OK, dados);
        } else {
            dados.setClass(this, RefeicoesActivity.class);
            dados.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(dados);
        }
        finish();
    }
}
