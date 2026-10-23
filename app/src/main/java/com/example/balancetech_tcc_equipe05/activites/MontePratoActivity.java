package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

public class MontePratoActivity extends AppCompatActivity {

    private ImageButton btnMenu;
    private ImageButton btnPerfilMontePrato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_monte_prato);

        // =========================
        // MENU
        // =========================

        btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MontePratoActivity.this,
                    MenuActivity.class
            );

            // Não fechar o Monte seu prato.
            startActivity(intent);
        });


        // =========================
        // PERFIL
        // =========================

        btnPerfilMontePrato = findViewById(R.id.btnPerfilMontePrato);

        btnPerfilMontePrato.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MontePratoActivity.this,
                    PerfilActivity.class
            );

            startActivity(intent);
        });
    }

}