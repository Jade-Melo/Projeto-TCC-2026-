package com.example.balancetech_tcc_equipe05.activites;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.balancetech_tcc_equipe05.R;
import com.example.balancetech_tcc_equipe05.activites.classes.Perfil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExercicioActivity extends AppCompatActivity {
    private static final String ESTADO_ATIVIDADE = "atividade";
    private static final String[] NIVEIS = {
            "Pouco exercício",
            "Exercício de 1 a 3 vezes por semana",
            "Exercício de 4 a 5 vezes por semana",
            "Exercício de 6 a 7 vezes por semana",
            "Exercícios intensos diariamente"
    };
    private static final double[] FATORES = {1.2, 1.375, 1.55, 1.725, 1.9};

    private final ExecutorService banco = Executors.newSingleThreadExecutor();
    private int atividade = -1;
    private Button finalizar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercicio);

        if (savedInstanceState != null) {
            atividade = savedInstanceState.getInt(ESTADO_ATIVIDADE, -1);
        }

        finalizar = findViewById(R.id.btnFinalizar);
        configurar(R.id.btnPoucoExercicio, 0);
        configurar(R.id.btnExercicio1a3, 1);
        configurar(R.id.btnExercicio4a5, 2);
        configurar(R.id.btnExercicio6a7, 3);
        configurar(R.id.btnExercicioIntenso, 4);
        finalizar.setOnClickListener(v -> salvarPerfil());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });
    }

    private void configurar(int botao, int indice) {
        findViewById(botao).setOnClickListener(v -> {
            atividade = indice;
            Toast.makeText(this, "Selecionado: " + NIVEIS[indice], Toast.LENGTH_SHORT).show();
        });
    }

    private void salvarPerfil() {
        if (atividade < 0 || atividade >= NIVEIS.length) {
            Toast.makeText(this, "Selecione seu nível de atividade física.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent dados = getIntent();
        int usuarioId = dados.getIntExtra(SessaoUsuario.EXTRA_ID, 0);
        String genero = dados.getStringExtra("CHAVE_GENERO");
        int idade = dados.getIntExtra("CHAVE_IDADE", 0);
        double peso = dados.getDoubleExtra("CHAVE_PESO", 0);
        int altura = dados.getIntExtra("CHAVE_ALTURA", 0);
        String objetivo = dados.getStringExtra("CHAVE_OBJETIVO");
        boolean homem = "Homem".equalsIgnoreCase(genero);
        if (usuarioId <= 0 || idade <= 0 || !Alimentacao.positivo(peso) || altura <= 0
                || (!homem && !"Mulher".equalsIgnoreCase(genero)) || objetivo == null) {
            Toast.makeText(this, "Confira os dados do questionário.", Toast.LENGTH_LONG).show();
            Intent reiniciar = new Intent(this, usuarioId > 0
                    ? QuestionarioActivity.class : LoginActivity.class);
            reiniciar.putExtra(SessaoUsuario.EXTRA_ID, usuarioId);
            startActivity(reiniciar);
            finish();
            return;
        }

        double alturaEmMetros = altura / 100.0;
        double imc = peso / (alturaEmMetros * alturaEmMetros);
        double tmb = homem
                ? 88.362 + 13.397 * peso + 4.799 * altura - 5.677 * idade
                : 447.593 + 9.247 * peso + 3.098 * altura - 4.330 * idade;
        double gasto = tmb * FATORES[atividade];
        double ajuste = 0;
        if ("Perder Peso".equalsIgnoreCase(objetivo)) {
            ajuste = -400;
        } else if ("Ganhar Músculo".equalsIgnoreCase(objetivo)) {
            ajuste = 400;
        }
        double meta = gasto + ajuste;
        if (!Alimentacao.positivo(meta) || !Alimentacao.positivo(imc)) {
            Toast.makeText(this, "Confira idade, peso e altura para calcular a meta.",
                    Toast.LENGTH_LONG).show();
            Intent reiniciar = new Intent(this, QuestionarioActivity.class);
            reiniciar.putExtra(SessaoUsuario.EXTRA_ID, usuarioId);
            startActivity(reiniciar);
            finish();
            return;
        }

        Perfil perfil = new Perfil();
        perfil.setUsuarioId(usuarioId);
        perfil.setGenero(genero);
        perfil.setIdade(idade);
        perfil.setPeso(peso);
        perfil.setAltura(altura);
        perfil.setObjetivo(objetivo);
        perfil.setExercicio(NIVEIS[atividade]);
        perfil.setImc(imc);
        perfil.setMetaCalorica(meta);

        finalizar.setEnabled(false);
        banco.execute(() -> {
            try {
                MetasPerfil.salvar(getApplicationContext(), perfil);
                runOnUiThread(() -> abrirHome(usuarioId, imc, tmb, gasto, meta));
            } catch (RuntimeException erro) {
                Log.e("BalanceTech", "Erro ao salvar o perfil", erro);
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        finalizar.setEnabled(true);
                        Toast.makeText(this, "Não foi possível salvar o perfil. Tente novamente.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void abrirHome(int usuarioId, double imc, double tmb, double gasto, double meta) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        SessaoUsuario.iniciar(usuarioId);
        Intent home = new Intent(this, HomeActivity.class);
        home.putExtra("RESULTADO_IMC", imc);
        home.putExtra("RESULTADO_TMB", tmb);
        home.putExtra("RESULTADO_GASTO", gasto);
        home.putExtra("RESULTADO_META", meta);
        home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(home);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle estado) {
        super.onSaveInstanceState(estado);
        estado.putInt(ESTADO_ATIVIDADE, atividade);
    }

    @Override
    protected void onDestroy() {
        banco.shutdown();
        super.onDestroy();
    }
}
