package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

public class QuestionarioPessoalActivity extends AppCompatActivity {

    private Button btnPerderPeso;
    private Button btnGanharMusculo;
    private Button btnManterPeso;
    private Button btnComerSaudavel;
    private Button btnObjetivo;

    // Variável para guardar o objetivo escolhido
    private String objetivoSelecionado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionario_pessoal);

        btnPerderPeso = findViewById(R.id.btnPerderPeso);
        btnGanharMusculo = findViewById(R.id.btnGanharMusculo);
        btnManterPeso = findViewById(R.id.btnManterPeso);
        btnComerSaudavel = findViewById(R.id.btnComerSaudavel);
        btnObjetivo = findViewById(R.id.btnObjetivo);

        // Ação ao clicar em "Perder Peso"
        btnPerderPeso.setOnClickListener(v -> {
            objetivoSelecionado = "Perder Peso";
            destacarBotaoSelecionado(btnPerderPeso);
        });

        // Ação ao clicar em "Ganhar Músculo"
        btnGanharMusculo.setOnClickListener(v -> {
            objetivoSelecionado = "Ganhar Músculo";
            destacarBotaoSelecionado(btnGanharMusculo);
        });

        // Ação ao clicar em "Manter Peso"
        btnManterPeso.setOnClickListener(v -> {
            objetivoSelecionado = "Manter Peso";
            destacarBotaoSelecionado(btnManterPeso);
        });

        // Ação ao clicar em "Comer Saudável"
        btnComerSaudavel.setOnClickListener(v -> {
            objetivoSelecionado = "Comer Saudável";
            destacarBotaoSelecionado(btnComerSaudavel);
        });

        // Botão Continuar / Avançar
        btnObjetivo.setOnClickListener(v -> {
            // Valida se o usuário escolheu algum objetivo antes de avançar
            if (objetivoSelecionado.isEmpty()) {
                Toast.makeText(this, "Por favor, selecione um objetivo.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Vai para a próxima tela (ExercicioActivity)
            Intent intent = new Intent(
                    QuestionarioPessoalActivity.this,
                    ExercicioActivity.class
            );

            // Opcional: Se quiseres passar a escolha para a próxima tela
            // intent.putExtra("OBJETIVO_ESCOLHIDO", objetivoSelecionado);

            startActivity(intent);
            finish();
        });
    }
    private transient Button botaoAnterior = null;

    private void destacarBotaoSelecionado(Button botaoAtual) {
        // Opcional: Aqui podes mudar cores de fundo ou borda para dar feedback visual ao usuário
        Toast.makeText(this, "Selecionado: " + objetivoSelecionado, Toast.LENGTH_SHORT).show();
    }
}