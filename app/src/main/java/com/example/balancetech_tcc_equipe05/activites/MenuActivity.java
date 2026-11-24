
package com.example.balancetech_tcc_equipe05.activites;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;
import com.example.balancetech_tcc_equipe05.activites.classes.AppDatabase;
import com.example.balancetech_tcc_equipe05.activites.classes.Usuario;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MenuActivity extends AppCompatActivity {

    private LinearLayout menuHome;
    private LinearLayout menuBalanca;
    private LinearLayout menuRefeicoes;
    private LinearLayout menuPerfil;
    private LinearLayout btnSobreNos;
    private LinearLayout menuEvolucao;
    private LinearLayout menuHistorico;
    private LinearLayout menuMontePrato;

    private ImageView imgUsuario;
    private TextView txtNomeUsuario;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        // CONFIGURAÇÃO DA JANELA
        Window window = getWindow();

        window.setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );

        window.addFlags(
                WindowManager.LayoutParams.FLAG_DIM_BEHIND
        );

        WindowManager.LayoutParams params = window.getAttributes();

        params.dimAmount = 0.33f;

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.START | Gravity.TOP;

        window.setAttributes(params);

        // FOTO E NOME DO USUÁRIO
        imgUsuario = findViewById(R.id.imgUsuario);
        txtNomeUsuario = findViewById(R.id.txtNomeUsuario);

        FotoPerfil.aplicarFormatoRedondo(imgUsuario);

        carregarFotoUsuario();
        carregarNomeUsuario();

        // Clicar na foto ou no nome abre o perfil.
        imgUsuario.setOnClickListener(v ->
                abrirTela(PerfilActivity.class));

        txtNomeUsuario.setOnClickListener(v ->
                abrirTela(PerfilActivity.class));

        // FECHAR AO CLICAR FORA DO MENU
        findViewById(R.id.menuFora).setOnClickListener(v -> finish());

        // HOME
        menuHome = findViewById(R.id.menuHome);
        menuHome.setOnClickListener(v -> abrirTela(HomeActivity.class));

        // BALANÇA
        menuBalanca = findViewById(R.id.menuBalanca);
        menuBalanca.setOnClickListener(v -> abrirTela(ConexaoActivity.class));

        // REFEIÇÕES
        menuRefeicoes = findViewById(R.id.menuRefeicoes);
        menuRefeicoes.setOnClickListener(v ->
                abrirTela(RefeicoesActivity.class));

        // PERFIL
        menuPerfil = findViewById(R.id.menuPerfil);
        menuPerfil.setOnClickListener(v ->
                abrirTela(PerfilActivity.class));

        // SOBRE NÓS
        btnSobreNos = findViewById(R.id.btnSobreNos);
        btnSobreNos.setOnClickListener(v ->
                abrirTela(SobreNosActivity.class));

        // EVOLUÇÃO
        menuEvolucao = findViewById(R.id.menuEvolucao);
        menuEvolucao.setOnClickListener(v ->
                abrirTela(EvolucaoActivity.class));

        // HISTÓRICO
        menuHistorico = findViewById(R.id.menuHistorico);
        menuHistorico.setOnClickListener(v ->
                abrirTela(HistoricoActivity.class));

        // MONTE SEU PRATO
        menuMontePrato = findViewById(R.id.menuMontePrato);
        menuMontePrato.setOnClickListener(v ->
                abrirTela(MontePratoActivity.class));
    }

    // CARREGAR FOTO DO USUÁRIO
    private void carregarFotoUsuario() {

        int usuarioId = SessaoUsuario.atual();

        if (usuarioId <= 0) {
            imgUsuario.setImageResource(R.drawable.ic_perfil_grande);
            return;
        }

        FotoPerfil.carregar(
                this,
                usuarioId,
                imgUsuario,
                R.drawable.ic_perfil_grande
        );
    }

    // CARREGAR NOME DO USUÁRIO
    private void carregarNomeUsuario() {

        int usuarioId = SessaoUsuario.atual();

        if (usuarioId <= 0) {
            txtNomeUsuario.setText("Usuário");
            return;
        }

        txtNomeUsuario.setText("Carregando...");

        executorService.execute(() -> {

            Usuario usuario = AppDatabase
                    .getDatabase(getApplicationContext())
                    .usuarioDao()
                    .buscarPorId(usuarioId);

            runOnUiThread(() -> {

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (SessaoUsuario.atual() != usuarioId) {
                    return;
                }

                if (usuario != null
                        && usuario.getNome() != null
                        && !usuario.getNome().trim().isEmpty()) {

                    txtNomeUsuario.setText(usuario.getNome());

                } else {

                    txtNomeUsuario.setText("Usuário");
                }
            });
        });
    }

    // NAVEGAÇÃO
    private void abrirTela(Class<?> destino) {

        Intent intent = new Intent(MenuActivity.this, destino);

        startActivity(intent);

        finish();
    }

    @Override
    protected void onDestroy() {
        executorService.shutdown();
        super.onDestroy();
    }
}
