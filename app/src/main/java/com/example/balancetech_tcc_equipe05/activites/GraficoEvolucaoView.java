package com.example.balancetech_tcc_equipe05.activites;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class GraficoEvolucaoView extends View {

    private Paint paintLinha;
    private Paint paintGrade;
    private Paint paintTexto;
    private Paint paintPonto;

    private float[] valores;
    private String[] dias;

    private String unidade = "g";
    private float valorMaximo = 349f;

    public GraficoEvolucaoView(Context context) {
        super(context);
        inicializar();
    }

    public GraficoEvolucaoView(
            Context context,
            AttributeSet attrs
    ) {
        super(context, attrs);
        inicializar();
    }

    public GraficoEvolucaoView(
            Context context,
            AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        inicializar();
    }

    private void inicializar() {

        paintLinha = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLinha.setStyle(Paint.Style.STROKE);
        paintLinha.setStrokeWidth(5f);

        paintGrade = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintGrade.setStyle(Paint.Style.STROKE);
        paintGrade.setStrokeWidth(1f);
        paintGrade.setColor(0xFFD9D9D9);

        paintTexto = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTexto.setColor(0xFF222222);

        paintPonto = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPonto.setStyle(Paint.Style.FILL);

        dias = new String[]{
                "S", "S", "T", "Q", "Q", "S", "S",
                "D", "S", "T", "Q", "Q", "S", "D"
        };

        mostrarCarbs();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (valores == null || valores.length == 0) {
            return;
        }

        float largura = getWidth();
        float altura = getHeight();

        float margemEsquerda = 75f;
        float margemDireita = 30f;
        float margemSuperior = 35f;
        float margemInferior = 60f;

        float larguraGrafico =
                largura - margemEsquerda - margemDireita;

        float alturaGrafico =
                altura - margemSuperior - margemInferior;

        if (larguraGrafico <= 0 || alturaGrafico <= 0) {
            return;
        }

        // =========================
        // GRADE
        // =========================

        int quantidadeNiveis = 6;

        for (int i = 0; i < quantidadeNiveis; i++) {

            float proporcao =
                    (float) i / (quantidadeNiveis - 1);

            float y =
                    margemSuperior +
                            (alturaGrafico * proporcao);

            canvas.drawLine(
                    margemEsquerda,
                    y,
                    largura - margemDireita,
                    y,
                    paintGrade
            );

            float valorNivel =
                    valorMaximo -
                            (valorMaximo * proporcao);

            String textoNivel =
                    formatarValor(valorNivel) + " " + unidade;

            paintTexto.setTextAlign(Paint.Align.RIGHT);
            paintTexto.setTextSize(20f);

            canvas.drawText(
                    textoNivel,
                    margemEsquerda - 8f,
                    y + 7f,
                    paintTexto
            );
        }

        // =========================
        // DISTÂNCIA ENTRE PONTOS
        // =========================

        float distanciaEntrePontos;

        if (valores.length > 1) {

            distanciaEntrePontos =
                    larguraGrafico / (valores.length - 1);

        } else {

            distanciaEntrePontos = 0;
        }

        // =========================
        // LINHA
        // =========================

        Path caminho = new Path();

        for (int i = 0; i < valores.length; i++) {

            float x =
                    margemEsquerda +
                            (distanciaEntrePontos * i);

            float porcentagem =
                    valores[i] / valorMaximo;

            if (porcentagem > 1f) {
                porcentagem = 1f;
            }

            if (porcentagem < 0f) {
                porcentagem = 0f;
            }

            float y =
                    margemSuperior +
                            alturaGrafico -
                            (porcentagem * alturaGrafico);

            if (i == 0) {

                caminho.moveTo(x, y);

            } else {

                caminho.lineTo(x, y);
            }
        }

        canvas.drawPath(caminho, paintLinha);

        // =========================
        // PONTOS
        // =========================

        for (int i = 0; i < valores.length; i++) {

            float x =
                    margemEsquerda +
                            (distanciaEntrePontos * i);

            float porcentagem =
                    valores[i] / valorMaximo;

            if (porcentagem > 1f) {
                porcentagem = 1f;
            }

            if (porcentagem < 0f) {
                porcentagem = 0f;
            }

            float y =
                    margemSuperior +
                            alturaGrafico -
                            (porcentagem * alturaGrafico);

            canvas.drawCircle(
                    x,
                    y,
                    6f,
                    paintPonto
            );
        }

        // =========================
        // DIAS
        // =========================

        paintTexto.setTextAlign(Paint.Align.CENTER);
        paintTexto.setTextSize(20f);

        for (int i = 0;
             i < dias.length && i < valores.length;
             i++) {

            float x =
                    margemEsquerda +
                            (distanciaEntrePontos * i);

            canvas.drawText(
                    dias[i],
                    x,
                    altura - 20f,
                    paintTexto
            );
        }
    }

    // =========================
    // CARBOIDRATOS
    // =========================

    public void mostrarCarbs() {

        valores = new float[]{
                120, 180, 160, 220,
                145, 250, 190, 275,
                210, 300, 235, 315,
                280, 211
        };

        unidade = "g";
        valorMaximo = 349f;

        // Mesma cor do progress_red do Histórico
        paintLinha.setColor(0xFFE65B68);
        paintPonto.setColor(0xFFE65B68);

        invalidate();
    }

    // =========================
    // GORDURAS
    // =========================

    public void mostrarGorduras() {

        valores = new float[]{
                45, 80, 72, 95,
                60, 110, 88, 120,
                100, 135, 92, 150,
                130, 165
        };

        unidade = "g";
        valorMaximo = 175f;

        // Mesma cor do progress_blue do Histórico
        paintLinha.setColor(0xFF3295D5);
        paintPonto.setColor(0xFF3295D5);

        invalidate();
    }

    // =========================
    // PROTEÍNAS
    // =========================

    public void mostrarProteinas() {

        valores = new float[]{
                70, 110, 95, 130,
                85, 145, 125, 160,
                140, 175, 155, 190,
                180, 205
        };

        unidade = "g";
        valorMaximo = 209f;

        // Mesma cor do progress_purple do Histórico
        paintLinha.setColor(0xFFB64ACB);
        paintPonto.setColor(0xFFB64ACB);

        invalidate();
    }

    // =========================
    // ATUALIZAR DADOS
    // =========================

    public void atualizarDados(
            float[] novosValores,
            float novoValorMaximo,
            String novaUnidade
    ) {

        valores = novosValores;
        valorMaximo = novoValorMaximo;
        unidade = novaUnidade;

        invalidate();
    }

    // =========================
    // FORMATAR VALORES
    // =========================

    private String formatarValor(float valor) {

        if (valor == (int) valor) {
            return String.valueOf((int) valor);
        }

        return String.valueOf(valor);
    }
}
