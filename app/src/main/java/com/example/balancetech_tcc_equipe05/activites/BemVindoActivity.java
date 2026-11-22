package com.example.balancetech_tcc_equipe05.activites;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;
import com.example.balancetech_tcc_equipe05.activites.classes.AppDatabase;
import com.example.balancetech_tcc_equipe05.activites.classes.Usuario;
import com.example.balancetech_tcc_equipe05.activites.classes.UsuarioDAO;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BemVindoActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private UsuarioDAO usuarioDAO;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        Intent data = result.getData();

                        if (data != null) {

                            Task<GoogleSignInAccount> task =
                                    GoogleSignIn.getSignedInAccountFromIntent(data);

                            tratarResultadoGoogle(task);

                        } else {

                            Log.e("LOGIN_GOOGLE_ERRO",
                                    "Resultado: " + result.getResultCode()
                                            + " | Intent retornado nulo");

                            Toast.makeText(
                                    this,
                                    "Não foi possível obter o resultado do Google.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bemvindo);

        usuarioDAO = AppDatabase
                .getDatabase(getApplicationContext())
                .usuarioDao();

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN
                )
                        .requestEmail()
                        .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Button btnEntrar = findViewById(R.id.btnEntrar);
        Button btnCadastrar = findViewById(R.id.btnCadastrar);
        Button btnGoogle = findViewById(R.id.btnGoogle);

        btnEntrar.setOnClickListener(v -> {
            startActivity(new Intent(
                    BemVindoActivity.this,
                    LoginActivity.class
            ));
        });

        btnCadastrar.setOnClickListener(v -> {
            startActivity(new Intent(
                    BemVindoActivity.this,
                    CadastroActivity.class
            ));
        });

        btnGoogle.setOnClickListener(v -> iniciarLoginComGoogle());
    }

    private void iniciarLoginComGoogle() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        googleSignInLauncher.launch(signInIntent);
    }

    private void tratarResultadoGoogle(
            Task<GoogleSignInAccount> completedTask) {

        try {

            GoogleSignInAccount account =
                    completedTask.getResult(ApiException.class);

            if (account != null) {

                String nomeGoogle = account.getDisplayName() != null
                        ? account.getDisplayName()
                        : "Usuário Google";

                String emailGoogle = account.getEmail();

                if (emailGoogle != null) {

                    verificarOuCadastrarNoRoom(nomeGoogle, emailGoogle);

                } else {

                    Toast.makeText(
                            this,
                            "Não foi possível obter o e-mail da conta.",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

        } catch (ApiException e) {

            Log.e(
                    "LOGIN_GOOGLE_ERRO",
                    "Código: " + e.getStatusCode()
                            + " | Mensagem: " + e.getMessage(),
                    e
            );

            Toast.makeText(
                    this,
                    "Falha no login Google. Código: "
                            + e.getStatusCode(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void verificarOuCadastrarNoRoom(
            String nome,
            String email) {

        String emailNormalizado = email.trim().toLowerCase(
                java.util.Locale.ROOT
        );

        executorService.execute(() -> {

            Usuario usuarioExistente =
                    usuarioDAO.buscarPorEmail(emailNormalizado);

            runOnUiThread(() -> {

                if (usuarioExistente != null) {

                    Toast.makeText(
                            BemVindoActivity.this,
                            "Este e-mail já possui cadastro. Faça login.",
                            Toast.LENGTH_LONG
                    ).show();

                    Intent intent = new Intent(
                            BemVindoActivity.this,
                            LoginActivity.class
                    );

                    intent.putExtra("email_preenchido", emailNormalizado);
                    startActivity(intent);

                } else {

                    Intent intent = new Intent(
                            BemVindoActivity.this,
                            CadastroActivity.class
                    );

                    intent.putExtra("nome_google", nome);
                    intent.putExtra("email_google", emailNormalizado);

                    startActivity(intent);
                }

                finish();
            });
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}


