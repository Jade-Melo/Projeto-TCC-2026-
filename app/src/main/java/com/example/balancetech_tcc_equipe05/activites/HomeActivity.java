package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.balancetech_tcc_equipe05.activites.dominio.Nutriente;
import com.example.balancetech_tcc_equipe05.activites.dominio.ResumoDia;

import java.text.NumberFormat;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

public class HomeActivity extends AppCompatActivity {

    private ImageButton btnMenu;
    private ImageButton btnPerfilHome;

    private LinearLayout btnHistorico;
    private LinearLayout btnEvolucao;
    private LinearLayout btnMontePrato;

    private LinearLayout cardCarboidratos;
    private LinearLayout cardProteina;
    private LinearLayout cardGordura;

    private LinearLayout cardJejum;
    private LinearLayout cardMassaMagra;

    private View btnBannerMontePrato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        // =========================
        // BOTÃO MENU
        // =========================

        btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    MenuActivity.class
            );

            startActivity(intent);
        });

        // =========================
        // BOTÃO PERFIL
        // =========================

        btnPerfilHome = findViewById(R.id.btnPerfilHome);

        btnPerfilHome.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    PerfilActivity.class
            );

            startActivity(intent);
        });

        // =========================
        // HISTÓRICO
        // =========================

        btnHistorico = findViewById(R.id.btnHistorico);

        btnHistorico.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    HistoricoActivity.class
            );

            startActivity(intent);
        });

        // =========================
        // EVOLUÇÃO
        // =========================

        btnEvolucao = findViewById(R.id.btnEvolucao);

        btnEvolucao.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    EvolucaoActivity.class
            );

            startActivity(intent);
        });

        // =========================
        // MONTE SEU PRATO
        // =========================

        btnMontePrato = findViewById(R.id.btnMontePrato);

        btnMontePrato.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    MontePratoActivity.class
            );

            startActivity(intent);
        });

        // =========================
        // MONTE SEU PRATO - BANNER
        // =========================

        btnBannerMontePrato =
                findViewById(R.id.btnBannerMontePrato);

        btnBannerMontePrato.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    MontePratoActivity.class
            );

            startActivity(intent);
        });

        // =========================
        // CARBOIDRATOS
        // =========================

        cardCarboidratos =
                findViewById(R.id.cardCarboidratos);

        cardCarboidratos.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    EvolucaoActivity.class
            );

            intent.putExtra(
                    "grafico",
                    "carboidratos"
            );

            startActivity(intent);
        });

        // =========================
        // PROTEÍNAS
        // =========================

        cardProteina =
                findViewById(R.id.cardProteina);

        cardProteina.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    EvolucaoActivity.class
            );

            intent.putExtra(
                    "grafico",
                    "proteinas"
            );

            startActivity(intent);
        });

        // =========================
        // GORDURAS
        // =========================

        cardGordura =
                findViewById(R.id.cardGordura);

        cardGordura.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    EvolucaoActivity.class
            );

            intent.putExtra(
                    "grafico",
                    "gorduras"
            );

            startActivity(intent);
        });

        // =========================
        // CARD JEJUM
        // =========================

        cardJejum = findViewById(R.id.cardJejum);

        cardJejum.setOnClickListener(v -> {

            // Conteúdo sobre jejum será conectado posteriormente.

        });

        // =========================
        // CARD MASSA MAGRA
        // =========================

        cardMassaMagra =
                findViewById(R.id.cardMassaMagra);

        cardMassaMagra.setOnClickListener(v -> {

            // Conteúdo sobre massa magra será conectado posteriormente.

        });
    }

    // =========================
    // RESUMO DO DIA
    // =========================

    @Override
    protected void onResume() {
        super.onResume();
        carregarResumoDoDia();
    }

    private void carregarResumoDoDia() {
        final int usuarioId = SessaoUsuario.atual();
        if (usuarioId <= 0) {
            return; // sem sessão: mantém os valores de demonstração do layout
        }
        NutricaoRepositorio repo = new NutricaoRepositorio(this);
        repo.executar(
                () -> repo.resumoDoDia(usuarioId, System.currentTimeMillis()),
                new NutricaoRepositorio.Resultado<ResumoDia>() {
                    @Override
                    public void sucesso(ResumoDia resumo) {
                        if (!isFinishing() && !isDestroyed() && SessaoUsuario.atual() == usuarioId) {
                            mostrarResumo(resumo);
                        }
                    }

                    @Override
                    public void erro(Exception erro) {
                        android.util.Log.e("BalanceTech", "Erro ao carregar o resumo do dia", erro);
                    }
                });
    }

    private void mostrarResumo(ResumoDia r) {
        NumberFormat inteiro = NumberFormat.getIntegerInstance(Alimentacao.PT_BR);
        ((TextView) findViewById(R.id.txtCalorias))
                .setText("Consumir " + inteiro.format(Math.round(r.meta.kcal)) + " Kcal");
        ((TextView) findViewById(R.id.txtConsumidas))
                .setText(inteiro.format(Math.round(r.consumido.kcal)) + " Consumidas");
        barra(R.id.progressCalorias, r.percentual(Nutriente.KCAL));

        macro(R.id.txtCarboidratos, R.id.progressCarbs, r, Nutriente.CARBOIDRATOS);
        macro(R.id.txtProteina, R.id.progressProteina, r, Nutriente.PROTEINA);
        macro(R.id.txtGordura, R.id.progressGordura, r, Nutriente.GORDURA);
    }

    private void macro(int texto, int barra, ResumoDia r, Nutriente n) {
        ((TextView) findViewById(texto)).setText(
                Math.round(r.consumido.valor(n)) + "/" + Math.round(r.meta.valor(n)) + "g");
        barra(barra, r.percentual(n));
    }

    private void barra(int id, int percentual) {
        ProgressBar barra = findViewById(id);
        barra.setMax(100);
        barra.setProgress(percentual);
    }
}