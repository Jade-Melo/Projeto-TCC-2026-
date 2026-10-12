package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

public class QuestionarioActivity extends AppCompatActivity {

    private Spinner spinnerGenero;
    private Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionario);

        spinnerGenero = findViewById(R.id.spinnerGenero);
        btnContinue = findViewById(R.id.btnContinue);

        // Opções de gênero
        String[] opcoesGenero = {
                "Selecione seu gênero",
                "Homem",
                "Mulher"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                opcoesGenero
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerGenero.setAdapter(adapter);

        // Botão Continue → Questionário 2
        btnContinue.setOnClickListener(v -> {
            // Valida se o utilizador escolheu uma opção válida (posição 0 é o "Selecione seu gênero")
            if (spinnerGenero.getSelectedItemPosition() == 0) {
                Toast.makeText(QuestionarioActivity.this, "Por favor, selecione seu gênero.", Toast.LENGTH_SHORT).show();
                return; // Para a execução aqui e não deixa avançar
            }

            // Opcional: Podes capturar o valor escolhido se precisares de o passar para a próxima tela
            String generoSelecionado = spinnerGenero.getSelectedItem().toString();

            // Avança para a próxima tela
            Intent intent = new Intent(
                    QuestionarioActivity.this,
                    QuestionarioPessoalActivity.class
            );

            startActivity(intent);
            finish();
        });
    }
}