package com.example.balancetech_tcc_equipe05.activites.dominio;

/** Dados prontos para o gráfico: um valor por dia, do mais antigo ao mais recente. */
public final class SerieEvolucao {
    public final Nutriente nutriente;
    public final float[] valores;
    public final String[] rotulos;
    public final double meta;
    /** Valor de hoje (último ponto). */
    public final double atual;

    public SerieEvolucao(Nutriente nutriente, float[] valores, String[] rotulos, double meta) {
        this.nutriente = nutriente;
        this.valores = valores;
        this.rotulos = rotulos;
        this.meta = meta;
        this.atual = valores.length == 0 ? 0 : valores[valores.length - 1];
    }

    /** Teto do eixo Y: a meta, ou o maior valor se ele a ultrapassar. */
    public float maximoGrafico() {
        float max = (float) Math.max(meta, 0);
        for (float v : valores) max = Math.max(max, v);
        return max > 0 ? max : 1f;
    }
}