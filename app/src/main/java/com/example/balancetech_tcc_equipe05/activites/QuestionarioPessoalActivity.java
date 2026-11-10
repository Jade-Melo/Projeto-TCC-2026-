package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

public class QuestionarioPessoalActivity extends AppCompatActivity {
    private static final String ESTADO_OBJETIVO = "objetivo";
    private String objetivo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionario_pessoal);
        if (savedInstanceState != null) {
            objetivo = savedInstanceState.getString(ESTADO_OBJETIVO, "");
        }
        findViewById(R.id.btnPerderPeso).setOnClickListener(v -> selecionar("Perder Peso"));
        findViewById(R.id.btnGanharMusculo).setOnClickListener(v -> selecionar("Ganhar Músculo"));
        findViewById(R.id.btnManterPeso).setOnClickListener(v -> selecionar("Manter Peso"));
        findViewById(R.id.btnComerSaudavel).setOnClickListener(v -> selecionar("Comer Saudável"));
        findViewById(R.id.btnObjetivo).setOnClickListener(v -> {
            if (objetivo.isEmpty()) {
                Toast.makeText(this, "Por favor, selecione um objetivo.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent proximo = new Intent(this, ExercicioActivity.class);
            Bundle dados = getIntent().getExtras();
            if (dados != null) {
                proximo.putExtras(dados);
            }
            proximo.putExtra("CHAVE_OBJETIVO", objetivo);
            startActivity(proximo);
            finish();
        });
    }

    private void selecionar(String escolha) {
        objetivo = escolha;
        Toast.makeText(this, "Selecionado: " + objetivo, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ESTADO_OBJETIVO, objetivo);
    }
}
