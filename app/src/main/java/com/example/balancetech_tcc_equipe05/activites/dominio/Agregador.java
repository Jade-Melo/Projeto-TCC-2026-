package com.example.balancetech_tcc_equipe05.activites.dominio;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/** Agrupa registros alimentares por dia. Java puro. */
public final class Agregador {

    /** Registro simplificado (independe da entidade do Room). */
    public static final class Linha {
        public final String refeicao;
        public final long horarioMillis;
        public final String porcoesJson;

        public Linha(String refeicao, long horarioMillis, String porcoesJson) {
            this.refeicao = refeicao;
            this.horarioMillis = horarioMillis;
            this.porcoesJson = porcoesJson;
        }
    }

    private Agregador() {
    }

    /**
     * Um {@link ResumoDia} por dia em [inicio, fim), do mais antigo ao mais recente,
     * inclusive dias sem registro. Registros fora do intervalo são ignorados.
     */
    public static List<ResumoDia> porDia(List<Linha> linhas, long inicio, long fim,
                                         long hoje, Totais meta, TimeZone fuso) {
        List<ResumoDia> dias = new ArrayList<>();
        for (long d = inicio; d < fim; d = Datas.somarDias(d, 1, fuso)) {
            dias.add(new ResumoDia(d, Datas.rotuloDia(d, hoje, fuso), meta));
        }
        for (Linha l : linhas) {
            long diaDoRegistro = Datas.inicioDoDia(l.horarioMillis, fuso);
            ResumoDia dia = null;
            for (ResumoDia candidato : dias) {
                if (candidato.inicioDia == diaDoRegistro) {
                    dia = candidato;
                    break;
                }
            }
            if (dia == null) continue;
            Totais parcial = new Totais();
            parcial.somarJson(l.porcoesJson);
            dia.consumido.kcal += parcial.kcal;
            dia.consumido.proteina += parcial.proteina;
            dia.consumido.carboidratos += parcial.carboidratos;
            dia.consumido.gordura += parcial.gordura;
            Double atual = dia.kcalPorRefeicao.get(l.refeicao);
            dia.kcalPorRefeicao.put(l.refeicao, (atual == null ? 0 : atual) + parcial.kcal);
            dia.registros++;
        }
        return dias;
    }
}