package com.example.balancetech_tcc_equipe05.Activites;

import static com.example.balancetech_tcc_equipe05.R.id.btnCriarConta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

public class CadastroActivity extends AppCompatActivity {

    private Button btnCriarConta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cadastro);

        btnCriarConta = findViewById(R.id.btnCriarConta);

    }
}
