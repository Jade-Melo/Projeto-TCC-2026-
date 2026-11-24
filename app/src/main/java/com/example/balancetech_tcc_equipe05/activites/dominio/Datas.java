package com.example.balancetech_tcc_equipe05.activites.dominio;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/** Limites de dia no fuso local. */
public final class Datas {
    private static final String[] DIAS = {"Domingo", "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado"};
    private static final String[] INICIAIS = {"D", "S", "T", "Q", "Q", "S", "S"};

    private Datas() {
    }

    public static long inicioDoDia(long millis, TimeZone fuso) {
        Calendar c = Calendar.getInstance(fuso, Locale.US);
        c.setTimeInMillis(millis);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /** Soma dias a partir do início de um dia (correto em dias de 23h/25h por horário de verão). */
    public static long somarDias(long inicioDia, int dias, TimeZone fuso) {
        Calendar c = Calendar.getInstance(fuso, Locale.US);
        c.setTimeInMillis(inicioDia);
        c.add(Calendar.DAY_OF_YEAR, dias);
        return c.getTimeInMillis();
    }

    public static String rotuloDia(long inicioDia, long hoje, TimeZone fuso) {
        if (inicioDia == hoje) return "Hoje";
        if (inicioDia == somarDias(hoje, -1, fuso)) return "Ontem";
        return DIAS[diaDaSemana(inicioDia, fuso)];
    }

    public static String inicialSemana(long inicioDia, TimeZone fuso) {
        return INICIAIS[diaDaSemana(inicioDia, fuso)];
    }

    private static int diaDaSemana(long millis, TimeZone fuso) {
        Calendar c = Calendar.getInstance(fuso, Locale.US);
        c.setTimeInMillis(millis);
        return c.get(Calendar.DAY_OF_WEEK) - 1;
    }
}