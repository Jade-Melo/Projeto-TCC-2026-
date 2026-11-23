package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;
import com.example.balancetech_tcc_equipe05.activites.dominio.Nutriente;
import com.example.balancetech_tcc_equipe05.activites.dominio.ResumoDia;

import java.text.NumberFormat;
import java.util.List;

public class HistoricoActivity extends AppCompatActivity {

    private ImageButton btnMenu;
    private ImageButton btnPerfilHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_historico);

        // =========================
        // MENU
        // =========================

        btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HistoricoActivity.this,
                    MenuActivity.class
            );

            // Não usar finish().
            // O Histórico continua atrás do menu.
            startActivity(intent);
        });


        // =========================
        // PERFIL
        // =========================

        btnPerfilHistorico = findViewById(R.id.btnPerfilHistorico);

        btnPerfilHistorico.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HistoricoActivity.this,
                    PerfilActivity.class
            );

            startActivity(intent);
        });
    }

    // =========================
    // HISTÓRICO (recarrega sempre que a tela volta, refletindo alterações)
    // =========================

    private static final int DIAS_NO_HISTORICO = 30;

    @Override
    protected void onResume() {
        super.onResume();
        final int usuarioId = SessaoUsuario.atual();
        if (usuarioId <= 0) {
            return;
        }
        NutricaoRepositorio repo = new NutricaoRepositorio(this);
        repo.executar(
                () -> repo.historico(usuarioId, DIAS_NO_HISTORICO),
                new NutricaoRepositorio.Resultado<List<ResumoDia>>() {
                    @Override
                    public void sucesso(List<ResumoDia> dias) {
                        if (!isFinishing() && !isDestroyed() && SessaoUsuario.atual() == usuarioId) {
                            mostrar(dias);
                        }
                    }

                    @Override
                    public void erro(Exception erro) {
                        android.util.Log.e("BalanceTech", "Erro ao carregar o histórico", erro);
                    }
                });
    }

    private void mostrar(List<ResumoDia> dias) {
        LinearLayout container = findViewById(R.id.containerHistorico);
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        NumberFormat inteiro = NumberFormat.getIntegerInstance(Alimentacao.PT_BR);

        boolean algumRegistro = false;
        for (int i = 0; i < dias.size(); i++) {
            ResumoDia dia = dias.get(i);
            boolean hoje = i == 0; // a lista vem do mais recente para o mais antigo
            algumRegistro |= dia.registros > 0;
            if (!hoje && dia.registros == 0) {
                continue; // dias sem refeição não poluem o histórico
            }
            View item = inflater.inflate(R.layout.item_historico_dia, container, false);
            ((TextView) item.findViewById(R.id.txtDia)).setText(dia.rotulo);
            ((TextView) item.findViewById(R.id.txtMetaDia))
                    .setText("Consumir " + inteiro.format(Math.round(dia.meta.kcal)) + " Kcal");
            ((TextView) item.findViewById(R.id.txtConsumidasDia))
                    .setText(inteiro.format(Math.round(dia.consumido.kcal)) + " Consumidas");
            ((ProgressBar) item.findViewById(R.id.progressDia)).setProgress(dia.percentual(Nutriente.KCAL));
            macro(item, R.id.txtCarbsDia, R.id.progressCarbsDia, dia, Nutriente.CARBOIDRATOS);
            macro(item, R.id.txtProteinaDia, R.id.progressProteinaDia, dia, Nutriente.PROTEINA);
            macro(item, R.id.txtGorduraDia, R.id.progressGorduraDia, dia, Nutriente.GORDURA);
            container.addView(item);
        }
        findViewById(R.id.txtHistoricoVazio).setVisibility(algumRegistro ? View.GONE : View.VISIBLE);
    }

    private void macro(View item, int texto, int barra, ResumoDia dia, Nutriente n) {
        ((TextView) item.findViewById(texto)).setText(
                Math.round(dia.consumido.valor(n)) + "/" + Math.round(dia.meta.valor(n)) + "g");
        ((ProgressBar) item.findViewById(barra)).setProgress(dia.percentual(n));
    }
}