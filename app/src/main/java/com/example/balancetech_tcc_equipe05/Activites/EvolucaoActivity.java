package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

public class EvolucaoActivity extends AppCompatActivity {

    private ImageButton btnMenu;
    private ImageButton btnPerfilEvolucao;

    private TextView btnCarbs;
    private TextView btnGorduras;
    private TextView btnProteinas;

    private TextView txtMeta;
    private TextView txtAtual;

    private GraficoEvolucaoView graficoEvolucao;

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
        // CARBOIDRATOS
        // =========================

        btnCarbs.setOnClickListener(v -> {

            graficoEvolucao.mostrarCarbs();

            txtMeta.setText("349 g");
            txtAtual.setText("211 g");

            selecionarBotao(btnCarbs);
        });

        // =========================
        // GORDURAS
        // =========================

        btnGorduras.setOnClickListener(v -> {

            graficoEvolucao.mostrarGorduras();

            txtMeta.setText("175 g");
            txtAtual.setText("165 g");

            selecionarBotao(btnGorduras);
        });

        // =========================
        // PROTEÍNAS
        // =========================

        btnProteinas.setOnClickListener(v -> {

            graficoEvolucao.mostrarProteinas();

            txtMeta.setText("209 g");
            txtAtual.setText("205 g");

            selecionarBotao(btnProteinas);
        });

        // =========================
        // INICIAL
        // =========================

        graficoEvolucao.mostrarCarbs();

        txtMeta.setText("349 g");
        txtAtual.setText("211 g");

        selecionarBotao(btnCarbs);
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