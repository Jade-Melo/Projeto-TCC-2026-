package com.example.balancetech_tcc_equipe05.activites;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

public class MenuActivity extends AppCompatActivity {

    private LinearLayout menuHome;
    private LinearLayout menuBalanca;
    private LinearLayout menuRefeicoes;
    private LinearLayout menuPerfil;
    private LinearLayout btnSobreNos;
    private LinearLayout menuEvolucao;
    private LinearLayout menuHistorico;
    private LinearLayout menuMontePrato;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_BalanceTech_TCC_Equipe05);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // =====================================================
        // CONFIGURAÇÃO DA JANELA
        // =====================================================

        Window window = getWindow();

        window.setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );

        window.addFlags(
                WindowManager.LayoutParams.FLAG_DIM_BEHIND
        );

        WindowManager.LayoutParams params = window.getAttributes();

        // Escurecimento da tela atrás do menu
        params.dimAmount = 0.35f;

        // A janela precisa ocupar a tela inteira para que
        // a área fora do painel possa receber o toque.
        params.width =
                WindowManager.LayoutParams.MATCH_PARENT;

        params.height =
                WindowManager.LayoutParams.MATCH_PARENT;

        params.gravity =
                Gravity.START | Gravity.TOP;

        window.setAttributes(params);

        // =====================================================
        // TOQUE FORA DO MENU
        // =====================================================

        findViewById(R.id.menuFora).setOnClickListener(v -> {
            finish();
        });

        // =====================================================
        // HOME
        // =====================================================

        menuHome = findViewById(R.id.menuHome);

        menuHome.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MenuActivity.this,
                    HomeActivity.class
            );
            startActivity(intent);
            finish();
        });

        // =====================================================
        // BALANÇA
        // =====================================================

        menuBalanca = findViewById(R.id.menuBalanca);

        menuBalanca.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MenuActivity.this,
                    ConexaoActivity.class
            );
            startActivity(intent);
            finish();
        });

        // =====================================================
        // REFEIÇÕES
        // =====================================================

        menuRefeicoes = findViewById(R.id.menuRefeicoes);

        menuRefeicoes.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MenuActivity.this,
                    RefeicoesActivity.class
            );
            startActivity(intent);
            finish();
        });

        // =====================================================
        // PERFIL
        // =====================================================

        menuPerfil = findViewById(R.id.menuPerfil);

        menuPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MenuActivity.this,
                    PerfilActivity.class
            );
            startActivity(intent);
            finish();
        });

        // =====================================================
        // SOBRE NÓS
        // =====================================================

        btnSobreNos = findViewById(R.id.btnSobreNos);

        btnSobreNos.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MenuActivity.this,
                    SobreNosActivity.class
            );
            startActivity(intent);
            finish();
        });

        // =====================================================
        // EVOLUÇÃO
        // =====================================================

        menuEvolucao = findViewById(R.id.menuEvolucao);

        menuEvolucao.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MenuActivity.this,
                    EvolucaoActivity.class
            );
            startActivity(intent);
            finish();
        });

        // =====================================================
        // HISTÓRICO
        // =====================================================

        menuHistorico = findViewById(R.id.menuHistorico);

        menuHistorico.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MenuActivity.this,
                    HistoricoActivity.class
            );
            startActivity(intent);
            finish();
        });

        // =====================================================
        // MONTE SEU PRATO
        // =====================================================

        menuMontePrato = findViewById(R.id.menuMontePrato);

        menuMontePrato.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MenuActivity.this,
                    MontePratoActivity.class
            );
            startActivity(intent);
            finish();
        });
    }
}