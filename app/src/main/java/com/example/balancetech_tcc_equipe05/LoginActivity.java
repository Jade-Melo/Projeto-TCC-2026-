package com.example.balancetech_tcc_equipe05;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        TextView esqueciSenha = findViewById(R.id.esqueciSenha);

        esqueciSenha.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, EsqueceuSenha.class);
            startActivity(intent);
        });
    }
}
