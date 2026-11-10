package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

public class QuestionarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionario);

        Spinner genero = findViewById(R.id.spinnerGenero);
        EditText idade = findViewById(R.id.editIdade);
        EditText peso = findViewById(R.id.editPeso);
        EditText altura = findViewById(R.id.editAltura);
        int usuarioId = getIntent().getIntExtra(SessaoUsuario.EXTRA_ID, 0);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Selecione seu gênero", "Homem", "Mulher"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genero.setAdapter(adapter);

        findViewById(R.id.btnContinue).setOnClickListener(v -> {
            if (usuarioId <= 0) {
                Toast.makeText(this, "Entre na sua conta para continuar.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }
            if (genero.getSelectedItemPosition() <= 0) {
                Toast.makeText(this, "Por favor, selecione seu gênero.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                int anos = Integer.parseInt(idade.getText().toString().trim());
                double kg = Double.parseDouble(peso.getText().toString().trim().replace(',', '.'));
                int cm = Integer.parseInt(altura.getText().toString().trim());
                if (anos <= 0 || !Alimentacao.positivo(kg) || cm <= 0) {
                    Toast.makeText(this, "Use valores positivos para idade, peso e altura.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                Intent proximo = new Intent(this, QuestionarioPessoalActivity.class);
                proximo.putExtra(SessaoUsuario.EXTRA_ID, usuarioId);
                proximo.putExtra("CHAVE_GENERO", genero.getSelectedItem().toString());
                proximo.putExtra("CHAVE_IDADE", anos);
                proximo.putExtra("CHAVE_PESO", kg);
                proximo.putExtra("CHAVE_ALTURA", cm);
                startActivity(proximo);
                finish();
            } catch (NumberFormatException erro) {
                Toast.makeText(this, "Preencha idade, peso e altura com números válidos.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
