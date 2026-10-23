package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

public class QuestionarioActivity extends AppCompatActivity {

    private Spinner spinnerGenero;
    private Button btnContinue;
    private EditText editIdade;
    private EditText editPeso;
    private EditText editAltura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionario);

        spinnerGenero = findViewById(R.id.spinnerGenero);
        btnContinue = findViewById(R.id.btnContinue);

        editIdade = findViewById(R.id.editIdade);
        editPeso = findViewById(R.id.editPeso);
        editAltura = findViewById(R.id.editAltura);

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
            // Valida se o usuario escolheu uma opção válida (posição 0 é o "Selecione seu gênero")
            if (spinnerGenero.getSelectedItemPosition() == 0) {
                Toast.makeText(QuestionarioActivity.this, "Por favor, selecione seu gênero.", Toast.LENGTH_SHORT).show();
                return; // Para a execução aqui e não deixa avançar
            }

            // Opcional: Pode capturar o valor escolhido se precisar passar esse valor para a próxima tela
            String generoSelecionado = spinnerGenero.getSelectedItem().toString();

            //Pega o que foi digitado no campo idade e guarda na memória em texto String
            String textoIdade = editIdade.getText().toString().trim();

            //Verifica se o campo idade está vazio, se estiver manda um aviso para digitar a idade
            if (textoIdade.isEmpty()) {
                //Mostra a mensagem avisando que o campo está vazio.
                Toast.makeText(this, "Por favor, digite sua idade.", Toast.LENGTH_SHORT).show();
                return; //não deixa mudar de tela se o campo estiver vazio
            }

            //Agora vamos converter a variável textoIdade em número inteiro (Int)
            int numIdade = Integer.parseInt(textoIdade);

            //Pega o que foi digitado no campo peso e guarda na memória em texto String
            String textoPeso = editPeso.getText().toString().trim();

            if (textoPeso.isEmpty()){
                Toast.makeText(this, "Por favor, digite seu peso.", Toast.LENGTH_SHORT).show();
                return;
            }

            //Agora vamos converter a variável textoPeso em número decimal (Double)
            Double numPeso = Double.parseDouble(textoPeso);

            String textoAltura = editAltura.getText().toString().trim();

            if (textoAltura.isEmpty()){
                Toast.makeText(this, "Por favor, digite sua altura.", Toast.LENGTH_SHORT).show();
                return;
            }

            //Agora vamos converter a variável textoAltura em número inteiro (Int)
            int numAltura = Integer.parseInt(textoAltura);

            // Avança para a próxima tela
            Intent intent = new Intent(
                    QuestionarioActivity.this,
                    QuestionarioPessoalActivity.class
            );

            // Pega os dados validados (genero, idade, peso e altura) e leva para a próxima tela (através do intent)
            //com etiquetas (constantes) para identificá-los corretamente na próxima tela
            intent.putExtra("CHAVE_GENERO", generoSelecionado);
            intent.putExtra("CHAVE_IDADE", numIdade);
            intent.putExtra("CHAVE_PESO", numPeso);
            intent.putExtra("CHAVE_ALTURA", numAltura);

            startActivity(intent);
            finish();
        });
    }
}