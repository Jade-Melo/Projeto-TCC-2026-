package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;
import com.example.balancetech_tcc_equipe05.activites.classes.AppDatabase;
import com.example.balancetech_tcc_equipe05.activites.classes.Usuario;
import com.example.balancetech_tcc_equipe05.activites.classes.UsuarioDAO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NovaSenhaActivity extends AppCompatActivity {

    private EditText edtNovaSenha, edtConfirmarSenha;
    private Button btnSalvarNovaSenha;
    private UsuarioDAO usuarioDAO;
    private String emailUsuario;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_senha);

        edtNovaSenha = findViewById(R.id.edtNovaSenha);
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenha);
        btnSalvarNovaSenha = findViewById(R.id.btnAlterarSenha);
        usuarioDAO = AppDatabase.getDatabase(getApplicationContext()).usuarioDao();

        // Recupera o e-mail passado pela VerificacaoActivity
        emailUsuario = getIntent().getStringExtra("email_usuario");

        // Trava de segurança: se por algum motivo o e-mail vier nulo, impede a continuação
        if (emailUsuario == null || emailUsuario.trim().isEmpty()) {
            Toast.makeText(this, "Erro: E-mail do utilizador não identificado!", Toast.LENGTH_LONG).show();
            finish();
        }

        btnSalvarNovaSenha.setOnClickListener(v -> validarESalvarSenha());
    }

    private void validarESalvarSenha() {
        String novaSenha = edtNovaSenha.getText().toString().trim();
        String confirmarSenha = edtConfirmarSenha.getText().toString().trim();

        // 1. Validação: Campos vazios
        if (novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Validação: As senhas coincidem?
        if (!novaSenha.equals(confirmarSenha)) {
            Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Validação: Critério de tamanho mínimo (ex: pelo menos 4 caracteres)
        if (novaSenha.length() < 4) {
            Toast.makeText(this, "A nova senha deve ter pelo menos 4 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Executa a operação de escrita no banco de dados em segundo plano (Obrigatório para o Room)
        executorService.execute(() -> {
            // Busca o utilizador atual no banco para comparar a senha
            Usuario usuarioAtual = usuarioDAO.buscarPorEmail(emailUsuario.trim());

            if (usuarioAtual != null && usuarioAtual.getSenha().equals(novaSenha)) {
                // Se a nova senha for igual à senha atual
                runOnUiThread(() -> {
                    Toast.makeText(this, "A nova senha não pode ser igual à senha atual!", Toast.LENGTH_LONG).show();
                });
            } else {
                // Se for diferente, prossegue com a atualização
                usuarioDAO.atualizarSenha(emailUsuario.trim(), novaSenha);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Senha alterada e salva no banco com sucesso!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(NovaSenhaActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        });
    }
}