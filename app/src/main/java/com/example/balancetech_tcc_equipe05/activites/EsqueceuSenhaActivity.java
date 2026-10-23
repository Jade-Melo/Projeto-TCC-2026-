package com.example.balancetech_tcc_equipe05.activites;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;
import com.example.balancetech_tcc_equipe05.activites.classes.AppDatabase;
import com.example.balancetech_tcc_equipe05.activites.classes.UsuarioDAO;
import com.example.balancetech_tcc_equipe05.activites.classes.Usuario;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EsqueceuSenhaActivity extends AppCompatActivity {

    private UsuarioDAO usuarioDAO;
    private Button btnEsqueceuSenha;
    private EditText campoEmailRecuperacao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_senha);

        btnEsqueceuSenha = findViewById(R.id.esqueciSenha);
        campoEmailRecuperacao = findViewById(R.id.emailRecuperacao);

        usuarioDAO = AppDatabase.getDatabase(getApplicationContext()).usuarioDao();

        // Ação do botão: valida o e-mail e gera o código
        btnEsqueceuSenha.setOnClickListener(v -> verificarEmailEGerarCodigo());
    }

    private void verificarEmailEGerarCodigo() {
        String email = campoEmailRecuperacao.getText().toString().trim();

        // 1. Valida se o campo está vazio
        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor, digite o e-mail cadastrado!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Busca o e-mail no banco Room em segundo plano
        executorService.execute(() -> {
            Usuario usuario = usuarioDAO.buscarPorEmail(email);

            runOnUiThread(() -> {
                if (usuario != null) {
                    // O e-mail existe no banco! Gera o código de 4 dígitos
                    String codigoGerado = String.valueOf(new Random().nextInt(9000) + 1000);

                    // Exibe o código no Toast para visualizarmos agora nos testes
                    Toast.makeText(this, "E-mail encontrado! Código: " + codigoGerado, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(EsqueceuSenhaActivity.this, VerificacaoActivity.class);
                    intent.putExtra("email_usuario", email);
                    intent.putExtra("codigo_gerado", codigoGerado);
                    startActivity(intent);

                } else {
                    // O e-mail não está cadastrado
                    Toast.makeText(this, "Este e-mail não corresponde a nenhum cadastro.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}