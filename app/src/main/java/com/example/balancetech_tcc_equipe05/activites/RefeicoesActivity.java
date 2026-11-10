package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

import java.util.ArrayList;

public class RefeicoesActivity extends AppCompatActivity {

    private static final int PEDIR_PRATO = 1;
    private static final String ESTADO_REFEICOES = "refeicoes";
    private IntegracaoAlimentacao.Metas metas;
    private Bundle refeicoes = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refeicoes);

        findViewById(R.id.btnMenu).setOnClickListener(v ->
                startActivity(new Intent(this, MenuActivity.class)));
        findViewById(R.id.btnPerfilRefeicoes).setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class)));

        if (savedInstanceState != null) {
            Bundle estado = savedInstanceState.getBundle(ESTADO_REFEICOES);
            if (estado != null) {
                refeicoes = estado;
            }
        }

        for (Alimentacao.Refeicao refeicao : Alimentacao.Refeicao.values()) {
            View botaoAdicionar = findViewById(refeicao.botao);
            botaoAdicionar.setOnClickListener(v -> abrirMontePrato(refeicao));
            View linha = (View) botaoAdicionar.getParent();
            linha.setOnClickListener(v -> mostrarRefeicao(refeicao));
        }
        if (savedInstanceState == null) {
            receberPrato(getIntent());
        }
        atualizarTotais();
    }

    @Override
    protected void onResume() {
        super.onResume();
        metas = null;
        atualizarTotais();
        MetasPerfil.carregar(this, carregadas -> {
            metas = carregadas;
            atualizarTotais();
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        receberPrato(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(ESTADO_REFEICOES, refeicoes);
    }

    private void abrirMontePrato(Alimentacao.Refeicao refeicao) {
        Intent intent = new Intent(this, MontePratoActivity.class);
        intent.putExtra(Alimentacao.EXTRA_REFEICAO, refeicao.name());
        startActivityForResult(intent, PEDIR_PRATO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dados) {
        super.onActivityResult(requestCode, resultCode, dados);
        if (requestCode == PEDIR_PRATO && resultCode == RESULT_OK && dados != null) {
            receberPrato(dados);
        }
    }

    private void receberPrato(Intent dados) {
        String refeicao = dados.getStringExtra(Alimentacao.EXTRA_REFEICAO);
        ArrayList<Bundle> porcoes = dados.getParcelableArrayListExtra(Alimentacao.EXTRA_PORCOES);
        if (refeicao == null || porcoes == null || porcoes.isEmpty()) {
            return;
        }
        ArrayList<Bundle> acumuladas = porcoesDaRefeicao(refeicao);
        acumuladas.addAll(porcoes);
        refeicoes.putParcelableArrayList(refeicao, acumuladas);

        // A mesma entrega não deve ser somada novamente após recriar a tela.
        dados.removeExtra(Alimentacao.EXTRA_REFEICAO);
        dados.removeExtra(Alimentacao.EXTRA_PORCOES);
        atualizarTotais();
    }

    private ArrayList<Bundle> porcoesDaRefeicao(String refeicao) {
        ArrayList<Bundle> porcoes = refeicoes.getParcelableArrayList(refeicao);
        return porcoes == null ? new ArrayList<>() : porcoes;
    }

    private void atualizarTotais() {
        Alimentacao.Totais total = new Alimentacao.Totais();
        for (Alimentacao.Refeicao refeicao : Alimentacao.Refeicao.values()) {
            ArrayList<Bundle> porcoes = porcoesDaRefeicao(refeicao.name());
            Alimentacao.Totais subtotal = new Alimentacao.Totais();
            subtotal.somar(porcoes);
            mostrarValor(refeicao.textoCalorias, subtotal.kcal, 0, " kcal");
            total.somar(porcoes);
        }
        mostrarValor(R.id.txtCaloriasTotal, total.kcal, metas == null ? 0 : metas.kcal, " kcal");
        mostrarValor(R.id.txtCarboidratosTotal, total.carboidratos,
                metas == null ? 0 : metas.carboidratos, " g");
        mostrarValor(R.id.txtProteinasTotal, total.proteina, metas == null ? 0 : metas.proteina, " g");
        mostrarValor(R.id.txtGordurasTotal, total.gordura, metas == null ? 0 : metas.gordura, " g");
    }

    private void mostrarValor(int id, double valor, double meta, String unidade) {
        TextView texto = findViewById(id);
        texto.setText(Alimentacao.comMeta(valor, meta, unidade));
    }

    private void mostrarRefeicao(Alimentacao.Refeicao refeicao) {
        ArrayList<Bundle> porcoes = porcoesDaRefeicao(refeicao.name());
        StringBuilder resumo = new StringBuilder();
        if (porcoes.isEmpty()) {
            resumo.append("Nenhum alimento adicionado.");
        } else {
            for (Bundle porcao : porcoes) {
                resumo.append(Alimentacao.descricao(porcao)).append('\n');
            }
            Alimentacao.Totais totais = new Alimentacao.Totais();
            totais.somar(porcoes);
            resumo.append('\n').append(totais.resumo());
        }
        new AlertDialog.Builder(this)
                .setTitle(refeicao.rotulo)
                .setMessage(resumo)
                .setPositiveButton("Fechar", null)
                .show();
    }
}
