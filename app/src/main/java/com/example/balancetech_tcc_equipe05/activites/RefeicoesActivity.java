package com.example.balancetech_tcc_equipe05.activites;

import static com.example.balancetech_tcc_equipe05.R.id.btnPerfilRefeicoes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

public class RefeicoesActivity extends AppCompatActivity {

    private ImageButton btnMenu;
    private ImageButton btnPerfilRefeicoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_refeicoes);

        // =========================
        // MENU
        // =========================

        btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RefeicoesActivity.this,
                    MenuActivity.class
            );

            // NÃO usar finish() aqui.
            // A tela de Refeições deve continuar atrás do menu.
            startActivity(intent);
        });


        // =========================
        // PERFIL
        // =========================

        btnPerfilRefeicoes = findViewById(R.id.btnPerfilRefeicoes);

        btnPerfilRefeicoes.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RefeicoesActivity.this,
                    PerfilActivity.class
            );

            startActivity(intent);
        });
    }
}
