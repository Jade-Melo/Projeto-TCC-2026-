package com.example.balancetech_tcc_equipe05.activites;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.balancetech_tcc_equipe05.activites.classes.AppDatabase;
import com.example.balancetech_tcc_equipe05.activites.classes.Usuario;
import com.example.balancetech_tcc_equipe05.activites.classes.UsuarioDAO;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtSenha;
    private Button btnEntrar;
    private TextView esqueciSenha;
    private TextView txtCadastro;

    private AppDatabase db;
    private UsuarioDAO usuarioDao;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmailLogin);
        edtSenha = findViewById(R.id.edtSenhaLogin);
        btnEntrar = findViewById(R.id.btnEntrar);
        esqueciSenha = findViewById(R.id.esqueciSenha);
        txtCadastro = findViewById(R.id.txtCadastro);

        usuarioDao = AppDatabase.getDatabase(getApplicationContext()).usuarioDao();

        // Botão Entrar chama a nossa função de validação (Apenas UM listener aqui!)
        btnEntrar.setOnClickListener(v -> tentarLogar());

        // Esqueceu a senha?
        esqueciSenha.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, EsqueceuSenhaActivity.class);
            startActivity(intent);
        });

        // Cadastre-se → Cadastro
        txtCadastro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });
    }

    private void tentarLogar() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if(email.isEmpty() || senha.isEmpty()){
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Executa em background para não travar a aplicação
        executorService.execute(() -> {
            // 1. Tenta autenticar com email e senha
            Usuario usuario = usuarioDao.autenticar(email, senha);

            if(usuario != null) {
                // Se achou o utilizador, vai para a tela do questionário
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Login realizado com sucesso! Bem-vindo, " + usuario.getNome(), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                });
            } else {
                // 2. Se falhou, verifica se o e-mail existe na base de dados (ainda na thread secundária!)
                Usuario usuarioPorEmail = usuarioDao.buscarPorEmail(email);

                runOnUiThread(() -> {
                    if(usuarioPorEmail == null){
                        Toast.makeText(LoginActivity.this, "E-mail não encontrado. Vamos criar uma conta!", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
                        intent.putExtra("email_preenchido", email);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Senha incorreta! Tente novamente", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}