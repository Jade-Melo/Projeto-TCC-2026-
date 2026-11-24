package com.example.balancetech_tcc_equipe05.activites.dominio;

/** Fórmulas de IMC, gasto calórico e metas. Java puro, sem dependência de Android. */
public final class CalculoNutricional {

    public static final String[] NIVEIS = {
            "Pouco exercício",
            "Exercício de 1 a 3 vezes por semana",
            "Exercício de 4 a 5 vezes por semana",
            "Exercício de 6 a 7 vezes por semana",
            "Exercícios intensos diariamente"
    };
    public static final double[] FATORES = {1.2, 1.375, 1.55, 1.725, 1.9};

    public static final String OBJ_PERDER = "Perder Peso";
    public static final String OBJ_GANHAR = "Ganhar Músculo";
    public static final String OBJ_MANTER = "Manter Peso";
    public static final String OBJ_SAUDAVEL = "Comer Saudável";
    public static final String[] OBJETIVOS = {OBJ_PERDER, OBJ_GANHAR, OBJ_MANTER, OBJ_SAUDAVEL};

    private CalculoNutricional() {
    }

    public static double imc(double pesoKg, int alturaCm) {
        double m = alturaCm / 100.0;
        return pesoKg / (m * m);
    }

    /** Taxa metabólica basal (Harris-Benedict revisada). */
    public static double tmb(boolean homem, double pesoKg, int alturaCm, int idade) {
        return homem
                ? 88.362 + 13.397 * pesoKg + 4.799 * alturaCm - 5.677 * idade
                : 447.593 + 9.247 * pesoKg + 3.098 * alturaCm - 4.330 * idade;
    }

    public static double gastoDiario(double tmb, int nivelAtividade) {
        return tmb * FATORES[nivelAtividade];
    }

    public static double metaCalorica(double gastoDiario, String objetivo) {
        if (OBJ_PERDER.equalsIgnoreCase(objetivo)) return gastoDiario - 400;
        if (OBJ_GANHAR.equalsIgnoreCase(objetivo)) return gastoDiario + 400;
        return gastoDiario;
    }

    /** Índice em NIVEIS do texto salvo no perfil, ou -1. */
    public static int indiceNivel(String texto) {
        for (int i = 0; i < NIVEIS.length; i++) {
            if (NIVEIS[i].equalsIgnoreCase(texto == null ? "" : texto.trim())) return i;
        }
        return -1;
    }

    /**
     * Divide a meta calórica em macros conforme o objetivo
     * (proteína e carboidrato 4 kcal/g, gordura 9 kcal/g).
     */
    public static Totais metasMacros(double kcal, String objetivo) {
        double p, c, g; // fração das calorias
        if (OBJ_PERDER.equalsIgnoreCase(objetivo)) {
            p = 0.30; c = 0.40; g = 0.30;
        } else if (OBJ_GANHAR.equalsIgnoreCase(objetivo)) {
            p = 0.25; c = 0.50; g = 0.25;
        } else {
            p = 0.20; c = 0.50; g = 0.30;
        }
        com.example.balancetech_tcc_equipe05.activites.dominio.Totais t = new com.example.balancetech_tcc_equipe05.activites.dominio.Totais();
        t.kcal = kcal;
        t.proteina = kcal * p / 4;
        t.carboidratos = kcal * c / 4;
        t.gordura = kcal * g / 9;
        return t;
    }

    public static int percentual(double valor, double meta) {
        if (!(meta > 0) || Double.isInfinite(meta) || !(valor > 0)) return 0;
        return (int) Math.min(100, Math.round(valor / meta * 100));
    }
}