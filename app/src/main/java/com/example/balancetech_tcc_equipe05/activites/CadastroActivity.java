package com.example.balancetech_tcc_equipe05.activites;


import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;
import com.example.balancetech_tcc_equipe05.activites.classes.AppDatabase;
import com.example.balancetech_tcc_equipe05.activites.classes.Usuario;
import com.example.balancetech_tcc_equipe05.activites.classes.UsuarioDAO;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CadastroActivity extends AppCompatActivity {

    private EditText edtNome, edtEmail, edtSenha;
    private Button btnCriarConta;
    private UsuarioDAO usuarioDAO;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    private boolean cadastrando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        btnCriarConta = findViewById(R.id.btnCriarConta);
        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);

        String nomeGoogle = getIntent().getStringExtra("nome_google");
        String emailGoogle = getIntent().getStringExtra("email_google");

        if (nomeGoogle != null) {
            edtNome.setText(nomeGoogle);
        }

        if (emailGoogle != null) {
            edtEmail.setText(emailGoogle);
        }

        usuarioDAO = AppDatabase.getDatabase(
                getApplicationContext()
        ).usuarioDao();

        btnCriarConta.setOnClickListener(v -> tentarCadastrar());
    }

    private void tentarCadastrar() {

        if (cadastrando) {
            return;
        }

        String nome = edtNome.getText().toString().trim();

        String email = edtEmail.getText().toString()
                .trim()
                .toLowerCase(Locale.ROOT);

        String senha = edtSenha.getText().toString().trim();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this,
                    "Por favor, preencha todos os campos!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Digite um e-mail válido!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (senha.length() < 8) {
            Toast.makeText(this,
                    "A senha deve ter no mínimo 8 caracteres!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String regexSenha =
                "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&._-]).*$";

        if (!senha.matches(regexSenha)) {
            Toast.makeText(this,
                    "A senha deve conter letras, números e caracteres especiais!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        cadastrando = true;
        btnCriarConta.setEnabled(false);

        executorService.execute(() -> {

            try {
                Usuario usuarioExistente =
                        usuarioDAO.buscarPorEmail(email);

                if (usuarioExistente != null) {

                    runOnUiThread(() -> {
                        cadastrando = false;
                        btnCriarConta.setEnabled(true);

                        Toast.makeText(CadastroActivity.this,
                                "Este e-mail já está cadastrado! Faça o login.",
                                Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(
                                CadastroActivity.this,
                                LoginActivity.class
                        );

                        intent.putExtra("email_preenchido", email);
                        startActivity(intent);
                        finish();
                    });

                    return;
                }

                Usuario novoUsuario = new Usuario(nome, email, senha);

                long idGerado = usuarioDAO.cadastrar(novoUsuario);

                runOnUiThread(() -> {

                    Toast.makeText(CadastroActivity.this,
                            "Cadastro realizado com sucesso!",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(
                            CadastroActivity.this,
                            QuestionarioActivity.class
                    );

                    intent.putExtra("id_usuario", (int) idGerado);
                    startActivity(intent);
                    finish();
                });

            } catch (android.database.sqlite.SQLiteConstraintException e) {

                runOnUiThread(() -> {
                    cadastrando = false;
                    btnCriarConta.setEnabled(true);

                    Toast.makeText(CadastroActivity.this,
                            "Este e-mail já está cadastrado! Faça o login.",
                            Toast.LENGTH_LONG).show();
                });

            } catch (Exception e) {

                runOnUiThread(() -> {
                    cadastrando = false;
                    btnCriarConta.setEnabled(true);

                    Toast.makeText(CadastroActivity.this,
                            "Não foi possível realizar o cadastro.",
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}