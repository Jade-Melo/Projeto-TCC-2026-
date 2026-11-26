package com.example.balancetech_tcc_equipe05.activites;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.balancetech_tcc_equipe05.R;

/** Barra inferior compartilhada das telas principais do BalanceTech. */
public final class NavegacaoInferior {

    private static final int VERDE = Color.rgb(87, 144, 92);
    private static final int CINZA = Color.rgb(120, 120, 120);
    private static final int ALTURA_BARRA_DP = 82;

    private NavegacaoInferior() { }

    public static void aplicar(Activity activity) {
        FrameLayout content = activity.findViewById(android.R.id.content);
        if (content == null || content.getChildCount() == 0) return;

        // Permite que o botão central ultrapasse os limites da barra sem ser cortado.
        content.setClipChildren(false);
        content.setClipToPadding(false);

        // Evita inserir a barra duas vezes na mesma tela.
        if (content.getChildAt(0) instanceof LinearLayout
                && "balancetech_conteudo_com_navegacao".equals(
                content.getChildAt(0).getTag())) return;

        View telaOriginal = content.getChildAt(0);
        content.removeView(telaOriginal);

        LinearLayout estrutura = new LinearLayout(activity);
        estrutura.setOrientation(LinearLayout.VERTICAL);
        estrutura.setBackgroundColor(Color.WHITE);
        estrutura.setClipChildren(false);
        estrutura.setClipToPadding(false);
        estrutura.setTag("balancetech_conteudo_com_navegacao");

        estrutura.addView(telaOriginal, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        LinearLayout barra = new LinearLayout(activity);
        barra.setOrientation(LinearLayout.HORIZONTAL);
        barra.setGravity(Gravity.CENTER_VERTICAL);
        barra.setBackgroundColor(Color.WHITE);
        barra.setElevation(dp(activity, 8));
        barra.setClipChildren(false);
        barra.setClipToPadding(false);

        estrutura.addView(barra, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(activity, ALTURA_BARRA_DP)));

        content.addView(estrutura, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        adicionarItem(activity, barra, "Início", R.drawable.ic_home, HomeActivity.class);
        adicionarItem(activity, barra, "Evolução", R.drawable.ic_evolucao, EvolucaoActivity.class);
        adicionarBotaoAdicionar(activity, barra);
        adicionarItem(activity, barra, "Histórico", R.drawable.ic_historico, HistoricoActivity.class);
        adicionarItem(activity, barra, "Perfil", R.drawable.ic_perfil, PerfilActivity.class);
    }

    private static void adicionarItem(Activity activity, LinearLayout barra,
                                      String titulo, int icone, Class<?> destino) {
        boolean selecionado = activity.getClass().equals(destino);
        LinearLayout item = new LinearLayout(activity);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setClickable(true);
        item.setFocusable(true);

        ImageView imagem = new ImageView(activity);
        imagem.setImageResource(icone);
        imagem.setColorFilter(selecionado ? VERDE : CINZA);
        item.addView(imagem, new LinearLayout.LayoutParams(dp(activity, 24), dp(activity, 24)));

        TextView texto = new TextView(activity);
        texto.setText(titulo);
        texto.setTextSize(10);
        texto.setGravity(Gravity.CENTER);
        texto.setTextColor(selecionado ? VERDE : CINZA);
        if (selecionado) texto.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams textoParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textoParams.topMargin = dp(activity, 3);
        item.addView(texto, textoParams);

        barra.addView(item, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        item.setOnClickListener(v -> {
            if (selecionado) return;
            Intent intent = new Intent(activity, destino);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activity.startActivity(intent);
        });
    }

    private static void adicionarBotaoAdicionar(Activity activity, LinearLayout barra) {
        FrameLayout area = new FrameLayout(activity);
        area.setClipChildren(false);
        area.setClipToPadding(false);
        barra.addView(area, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        TextView botao = new TextView(activity);
        botao.setText("+");
        botao.setTextColor(Color.WHITE);
        botao.setTextSize(36);
        botao.setGravity(Gravity.CENTER);
        botao.setTypeface(null, Typeface.NORMAL);
        botao.setElevation(dp(activity, 7));
        botao.setContentDescription("Adicionar refeição ou alimento");

        GradientDrawable fundo = new GradientDrawable();
        fundo.setShape(GradientDrawable.OVAL);
        fundo.setColor(VERDE);
        fundo.setStroke(dp(activity, 4), Color.WHITE);
        botao.setBackground(fundo);

        // O botão fica inteiramente dentro da barra para não ser recortado
        // por nenhum dos layouts pais, mantendo o efeito elevado pela sombra.
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dp(activity, 64), dp(activity, 64), Gravity.CENTER);
        area.addView(botao, params);
        botao.setOnClickListener(v -> mostrarOpcoes(activity));
    }

    private static void mostrarOpcoes(Activity activity) {
        String[] opcoes = {"Adicionar refeição", "Registrar alimento"};
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("O que deseja adicionar?")
                .setItems(opcoes, (d, escolha) -> {
                    if (escolha == 0) {
                        activity.startActivity(new Intent(activity, MontePratoActivity.class));
                    } else {
                        Toast.makeText(activity,
                                "O cadastro de alimentos será implementado na próxima etapa.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(VERDE));
        dialog.show();
    }

    private static int dp(Activity activity, int valor) {
        return (int) (valor * activity.getResources().getDisplayMetrics().density + 0.5f);
    }
}
