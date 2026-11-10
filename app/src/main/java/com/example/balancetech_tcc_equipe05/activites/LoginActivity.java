package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;
import com.example.balancetech_tcc_equipe05.activites.classes.AppDatabase;
import com.example.balancetech_tcc_equipe05.activites.classes.Usuario;
import com.example.balancetech_tcc_equipe05.activites.classes.UsuarioDAO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtSenha;
    private Button btnEntrar;
    private UsuarioDAO usuarioDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessaoUsuario.encerrar();
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmailLogin);
        edtSenha = findViewById(R.id.edtSenhaLogin);
        btnEntrar = findViewById(R.id.btnEntrar);
        TextView esqueciSenha = findViewById(R.id.esqueciSenha);
        TextView txtCadastro = findViewById(R.id.txtCadastro);

        usuarioDao = AppDatabase.getDatabase(getApplicationContext()).usuarioDao();
        btnEntrar.setOnClickListener(v -> tentarLogar());
        esqueciSenha.setOnClickListener(v ->
                startActivity(new Intent(this, EsqueceuSenhaActivity.class)));
        txtCadastro.setOnClickListener(v ->
                startActivity(new Intent(this, CadastroActivity.class)));
    }

    private void tentarLogar() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnEntrar.setEnabled(false);
        executorService.execute(() -> {
            try {
                Usuario usuario = usuarioDao.autenticar(email, senha);
                if (usuario != null) {
                    boolean temPerfil = MetasPerfil.consultarCalorias(
                            getApplicationContext(), usuario.getId()) != null;
                    runOnUiThread(() -> abrirConta(usuario, temPerfil));
                } else {
                    boolean emailExiste = usuarioDao.buscarPorEmail(email) != null;
                    runOnUiThread(() -> tratarLoginInvalido(email, emailExiste));
                }
            } catch (RuntimeException erro) {
                Log.e("BalanceTech", "Erro ao consultar a conta", erro);
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        btnEntrar.setEnabled(true);
                        Toast.makeText(this, "Não foi possível entrar. Tente novamente.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void abrirConta(Usuario usuario, boolean temPerfil) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        SessaoUsuario.iniciar(usuario.getId());
        Toast.makeText(this, "Bem-vindo, " + usuario.getNome(), Toast.LENGTH_LONG).show();
        Intent destino = new Intent(this, temPerfil ? HomeActivity.class : QuestionarioActivity.class);
        destino.putExtra(SessaoUsuario.EXTRA_ID, usuario.getId());
        destino.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(destino);
        finish();
    }

    private void tratarLoginInvalido(String email, boolean emailExiste) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        btnEntrar.setEnabled(true);
        if (emailExiste) {
            Toast.makeText(this, "Senha incorreta! Tente novamente", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "E-mail não encontrado. Vamos criar uma conta!",
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, CadastroActivity.class);
        intent.putExtra("email_preenchido", email);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        executorService.shutdown();
        super.onDestroy();
    }
}
