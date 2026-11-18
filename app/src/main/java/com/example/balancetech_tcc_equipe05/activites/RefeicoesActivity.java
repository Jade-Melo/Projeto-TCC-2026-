package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;
import com.example.balancetech_tcc_equipe05.activites.classes.AppDatabase;
import com.example.balancetech_tcc_equipe05.activites.classes.RegistroAlimentar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RefeicoesActivity extends AppCompatActivity {

    private Bundle refeicoes = new Bundle();
    private IntegracaoAlimentacao.Metas metas;
    private int numeroConsulta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refeicoes);

        findViewById(R.id.btnMenu).setOnClickListener(v ->
                startActivity(new Intent(this, MenuActivity.class))
        );

        findViewById(R.id.btnPerfilRefeicoes).setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class))
        );

        for (Alimentacao.Refeicao refeicao : Alimentacao.Refeicao.values()) {
            View botao = findViewById(refeicao.botao);

            botao.setOnClickListener(v -> {
                Intent intent = new Intent(this, MontePratoActivity.class);
                intent.putExtra(Alimentacao.EXTRA_REFEICAO, refeicao.name());
                startActivityForResult(intent, 1);
            });

            View linha = (View) botao.getParent();
            linha.setOnClickListener(v -> mostrarRefeicao(refeicao));
        }

        atualizarTotais();
    }

    @Override
    protected void onResume() {
        super.onResume();

        metas = null;
        carregarRefeicoes();

        final int usuario = SessaoUsuario.atual();

        MetasPerfil.carregar(this, carregadas -> {
            if (!isFinishing()
                    && !isDestroyed()
                    && usuario == SessaoUsuario.atual()) {
                metas = carregadas;
                atualizarTotais();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        carregarRefeicoes();
    }

    private void carregarRefeicoes() {
        final int usuario = SessaoUsuario.atual();
        final int consultaAtual = ++numeroConsulta;

        refeicoes = new Bundle();
        atualizarTotais();

        if (usuario <= 0) {
            avisar("Entre na sua conta para consultar as refeições.");
            return;
        }

        Calendar calendario = Calendar.getInstance();
        calendario.set(Calendar.HOUR_OF_DAY, 0);
        calendario.set(Calendar.MINUTE, 0);
        calendario.set(Calendar.SECOND, 0);
        calendario.set(Calendar.MILLISECOND, 0);

        final long inicio = calendario.getTimeInMillis();

        calendario.add(Calendar.DAY_OF_MONTH, 1);
        final long fim = calendario.getTimeInMillis();

        new Thread(() -> {
            try {
                List<RegistroAlimentar> registros =
                        AppDatabase.getDatabase(getApplicationContext())
                                .registroAlimentarDao()
                                .consultarPeriodo(usuario, inicio, fim);

                Bundle resultado = new Bundle();

                for (RegistroAlimentar registro : registros) {
                    ArrayList<Bundle> itens =
                            resultado.getParcelableArrayList(registro.refeicao);

                    if (itens == null) {
                        itens = new ArrayList<>();
                    }

                    itens.addAll(BalanceApplication.lerPorcoes(registro));

                    resultado.putParcelableArrayList(registro.refeicao, itens);
                }

                runOnUiThread(() -> {
                    if (!consultaValida(consultaAtual, usuario)) return;

                    refeicoes = resultado;
                    atualizarTotais();
                });

            } catch (Exception erro) {
                android.util.Log.e(
                        "BalanceTech",
                        "Erro ao consultar refeições",
                        erro
                );

                runOnUiThread(() -> {
                    if (consultaValida(consultaAtual, usuario)) {
                        avisar("Não foi possível carregar as refeições.");
                    }
                });
            }
        }, "balance-refeicoes").start();
    }

    private boolean consultaValida(int consulta, int usuario) {
        return !isFinishing()
                && !isDestroyed()
                && consulta == numeroConsulta
                && usuario == SessaoUsuario.atual();
    }

    private ArrayList<Bundle> porcoesDaRefeicao(String refeicao) {
        ArrayList<Bundle> itens =
                refeicoes.getParcelableArrayList(refeicao);

        return itens == null ? new ArrayList<>() : itens;
    }

    private void atualizarTotais() {
        Alimentacao.Totais total = new Alimentacao.Totais();

        for (Alimentacao.Refeicao refeicao : Alimentacao.Refeicao.values()) {
            ArrayList<Bundle> itens = porcoesDaRefeicao(refeicao.name());

            Alimentacao.Totais subtotal = new Alimentacao.Totais();
            subtotal.somar(itens);

            mostrarValor(refeicao.textoCalorias, subtotal.kcal, 0, " kcal");
            total.somar(itens);
        }

        mostrarValor(
                R.id.txtCaloriasTotal,
                total.kcal,
                metas == null ? 0 : metas.kcal,
                " kcal"
        );

        mostrarValor(
                R.id.txtCarboidratosTotal,
                total.carboidratos,
                metas == null ? 0 : metas.carboidratos,
                " g"
        );

        mostrarValor(
                R.id.txtProteinasTotal,
                total.proteina,
                metas == null ? 0 : metas.proteina,
                " g"
        );

        mostrarValor(
                R.id.txtGordurasTotal,
                total.gordura,
                metas == null ? 0 : metas.gordura,
                " g"
        );
    }

    private void mostrarValor(
            int id,
            double valor,
            double meta,
            String unidade
    ) {
        TextView texto = findViewById(id);
        texto.setText(Alimentacao.comMeta(valor, meta, unidade));
    }

    private void mostrarRefeicao(Alimentacao.Refeicao refeicao) {
        ArrayList<Bundle> itens = porcoesDaRefeicao(refeicao.name());
        StringBuilder resumo = new StringBuilder();

        if (itens.isEmpty()) {
            resumo.append("Nenhum alimento registrado hoje.");
        } else {
            for (Bundle porcao : itens) {
                resumo.append(Alimentacao.descricao(porcao)).append('\n');
            }

            Alimentacao.Totais totais = new Alimentacao.Totais();
            totais.somar(itens);
            resumo.append('\n').append(totais.resumo());
        }

        new AlertDialog.Builder(this)
                .setTitle(refeicao.rotulo)
                .setMessage(resumo)
                .setPositiveButton("Fechar", null)
                .show();
    }

    private void avisar(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }
}
