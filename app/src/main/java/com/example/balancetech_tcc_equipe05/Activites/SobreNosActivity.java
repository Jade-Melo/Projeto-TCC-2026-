package com.example.balancetech_tcc_equipe05.activites;

import static com.example.balancetech_tcc_equipe05.R.id.btnMenu;
import static com.example.balancetech_tcc_equipe05.R.id.btnPerfilSobreNos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

public class SobreNosActivity extends AppCompatActivity {

    private ImageButton btnMenu;
    private ImageButton btnPerfilSobreNos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sobre_nos);

        // =========================
        // MENU
        // =========================

        btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(v -> {

            Intent intent = new Intent(
                    SobreNosActivity.this,
                    MenuActivity.class
            );

            // NÃO usar finish() aqui.
            // A tela Sobre Nós deve continuar atrás do menu.
            startActivity(intent);
        });


        // =========================
        // PERFIL
        // =========================

        btnPerfilSobreNos = findViewById(R.id.btnPerfilSobreNos);

        btnPerfilSobreNos.setOnClickListener(v -> {

            Intent intent = new Intent(
                    SobreNosActivity.this,
                    PerfilActivity.class
            );

            startActivity(intent);
        });
    }

}