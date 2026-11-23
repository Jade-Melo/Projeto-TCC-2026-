package com.example.balancetech_tcc_equipe05.activites.dominio;

import java.util.LinkedHashMap;
import java.util.Map;

/** Consumo de um dia comparado com as metas do perfil. */
public final class ResumoDia {
    public final long inicioDia;
    public final String rotulo;
    public final com.example.balancetech_tcc_equipe05.activites.dominio.Totais consumido = new com.example.balancetech_tcc_equipe05.activites.dominio.Totais();
    public final com.example.balancetech_tcc_equipe05.activites.dominio.Totais meta;
    /** kcal por refeição (CAFE, ALMOCO, LANCHE, JANTAR). */
    public final Map<String, Double> kcalPorRefeicao = new LinkedHashMap<>();
    public int registros;

    public ResumoDia(long inicioDia, String rotulo, com.example.balancetech_tcc_equipe05.activites.dominio.Totais meta) {
        this.inicioDia = inicioDia;
        this.rotulo = rotulo;
        this.meta = meta;
        kcalPorRefeicao.put("CAFE", 0.0);
        kcalPorRefeicao.put("ALMOCO", 0.0);
        kcalPorRefeicao.put("LANCHE", 0.0);
        kcalPorRefeicao.put("JANTAR", 0.0);
    }

    public int percentual(Nutriente n) {
        return CalculoNutricional.percentual(consumido.valor(n), meta.valor(n));
    }
}