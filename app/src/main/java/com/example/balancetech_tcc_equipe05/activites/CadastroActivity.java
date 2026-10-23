package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;
import com.example.balancetech_tcc_equipe05.activites.classes.AppDatabase;
import com.example.balancetech_tcc_equipe05.activites.classes.Usuario;
import com.example.balancetech_tcc_equipe05.activites.classes.UsuarioDAO;

public class CadastroActivity extends AppCompatActivity {

    private EditText edtNome, edtEmail, edtSenha;
    private Button btnCriarConta;
    private UsuarioDAO usuarioDAO;

    private final ExecutorService exercutorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cadastro);

        btnCriarConta = findViewById(R.id.btnCriarConta);
        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);

        usuarioDAO = AppDatabase.getDatabase(getApplicationContext()).usuarioDao();

        btnCriarConta.setOnClickListener(v -> tentarCadastrar());
    }

    private void tentarCadastrar() {
        String nome = edtNome.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Digite um e-mail válido!", Toast.LENGTH_SHORT).show();
        }

        if (senha.length() < 8) {
            Toast.makeText(this, "A senha deve ter no mínimo 8 caracteres!", Toast.LENGTH_SHORT).show();
            return;
        }

        String regexSenha = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&._-]).*$";
        if (!senha.matches(regexSenha)) {
            Toast.makeText(this, "A senha deve conter letras, números e caracteres especiais!", Toast.LENGTH_LONG).show();
            return;
        }

        exercutorService.execute(() -> {
            Usuario usuarioExistente = usuarioDAO.buscarPorEmail(email);

            runOnUiThread(() -> {
                if (usuarioExistente != null) {
                    Toast.makeText(CadastroActivity.this, "Este e-mail já está cadastrado! Faça o login", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
                    intent.putExtra("email_preenchido", email);
                    startActivity(intent);
                    finish();
                } else {
                    // Se passou em tudo, salva no banco!
                    salvarUsuario(nome, email, senha);
                }
            });
        });
    }

    private void salvarUsuario(String nome, String email, String senha) {
        exercutorService.execute(() -> {
            Usuario novoUsuario = new Usuario(nome, email, senha);

            // O Room cadastra e retorna o ID gerado (que o colega vai usar na tabela de perfil)
            long idGerado = usuarioDAO.cadastrar(novoUsuario);

            runOnUiThread(() -> {
                Toast.makeText(CadastroActivity.this, "Cadastro realizado com sucesso! ID: " + idGerado, Toast.LENGTH_LONG).show();

                // Limpa os campos
                edtNome.setText("");
                edtEmail.setText("");
                edtSenha.setText("");

                Intent intent = new Intent(
                        CadastroActivity.this,
                        QuestionarioActivity.class
                );

                intent.putExtra("id_usuario",(int) idGerado);
                startActivity(intent);
                finish();

            });
        });

    }
}
