package com.example.balancetech_tcc_equipe05.activites;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

public class PerfilActivity extends AppCompatActivity {

    private ImageButton btnMenu;
    private ImageButton btnPerfil;

    private Button btnSair;
    private Button btnSairConta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_perfil);

        // =========================
        // BOTÃO MENU
        // =========================

        btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PerfilActivity.this,
                    MenuActivity.class
            );

            // Não usar finish().
            // O Perfil continua atrás do menu.
            startActivity(intent);
        });


        // =========================
        // BOTÃO PERFIL
        // =========================

        btnPerfil = findViewById(R.id.btnPerfil);

        btnPerfil.setOnClickListener(v -> {
            // O usuário já está na tela de Perfil.
        });


        // =========================
        // BOTÃO SAIR
        // =========================

        btnSair = findViewById(R.id.btnSair);

        btnSair.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PerfilActivity.this,
                    HomeActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
            finish();
        });


        // =========================
        // BOTÃO SAIR DA CONTA
        // =========================

        btnSairConta = findViewById(R.id.btnSairConta);

        btnSairConta.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PerfilActivity.this,
                    CadastroActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }
}
