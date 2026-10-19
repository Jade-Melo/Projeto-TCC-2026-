package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

public class VerificacaoActivity extends AppCompatActivity {

    private EditText codigo1, codigo2, codigo3, codigo4;
    private Button btnVerificarCodigo;
    private String codigoGeradoRecebido, emailUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificacao); // Certifique-se de que o nome do layout está correto

        // Inicializar os campos de texto dos 4 dígitos e o botão
        codigo1 = findViewById(R.id.codigo1); // Ajuste para o ID real no seu XML
        codigo2 = findViewById(R.id.codigo2); // Ajuste para o ID real no seu XML
        codigo3= findViewById(R.id.codigo3); // Ajuste para o ID real no seu XML
        codigo4 = findViewById(R.id.codigo4); // Ajuste para o ID real no seu XML
        btnVerificarCodigo = findViewById(R.id.btnVerificacao); // Ajuste para o ID real do botão no seu XML

        // Recuperar os dados passados pela Intent da tela EsqueceuSenha
        codigoGeradoRecebido = getIntent().getStringExtra("codigo_gerado");
        emailUsuario = getIntent().getStringExtra("email_usuario");

        // Ação do botão para validar o código
        btnVerificarCodigo.setOnClickListener(v -> validarCodigo());
    }

    private void validarCodigo() {
        String d1 = codigo1.getText().toString().trim();
        String d2 = codigo2.getText().toString().trim();
        String d3 = codigo3.getText().toString().trim();
        String d4 = codigo4.getText().toString().trim();

        // Validação: Verificar se algum campo está vazio
        if (d1.isEmpty() || d2.isEmpty() || d3.isEmpty() || d4.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os 4 dígitos!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Junta os 4 dígitos numa única string
        String codigoDigitado = d1 + d2 + d3 + d4;

        // Valida se o código digitado confere estritamente com o código gerado
        if (codigoDigitado.equals(codigoGeradoRecebido)) {
            Toast.makeText(this, "Código correto!", Toast.LENGTH_SHORT).show();

            // Avança para a tela de Nova Senha, levando o e-mail do utilizador na Intent
            Intent intent = new Intent(VerificacaoActivity.this, NovaSenhaActivity.class);
            intent.putExtra("email_usuario", emailUsuario);
            startActivity(intent);
            finish(); // Encerra esta atividade para não voltar nela ao clicar em voltar

        } else {
            Toast.makeText(this, "Código incorreto! Tente novamente.", Toast.LENGTH_SHORT).show();
        }
    }
}
