package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

public class BemVindoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bemvindo);

        Button btnEntrar = findViewById(R.id.btnEntrar);
        Button btnCadastrar = findViewById(R.id.btnCadastrar);
        Button btnGoogle = findViewById(R.id.btnGoogle);

        // Entrar → Login
        btnEntrar.setOnClickListener(v -> {

            Intent intent = new Intent(
                    BemVindoActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
        });

        // Cadastrar → Cadastro
        btnCadastrar.setOnClickListener(v -> {

            Intent intent = new Intent(
                    BemVindoActivity.this,
                    CadastroActivity.class
            );

            startActivity(intent);
        });

        // Ação do Botão Google

        btnGoogle.setOnClickListener(v-> {
            iniciarLoginComGoogle();
        });

    }

    private void iniciarLoginComGoogle() {
        Toast.makeText(this, "Abrindo seleção da conta Google... ", Toast.LENGTH_SHORT).show();
    }
}


