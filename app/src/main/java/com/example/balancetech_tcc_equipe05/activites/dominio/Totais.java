package com.example.balancetech_tcc_equipe05.activites.dominio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Soma de energia e macronutrientes. */
public final class Totais {
    public double kcal;
    public double proteina;
    public double carboidratos;
    public double gordura;

    /** Soma as porções de um registro (mesmo JSON gravado por BalanceApplication). */
    public void somarJson(String porcoesJson) {
        if (porcoesJson == null) return;
        try {
            JSONArray array = new JSONArray(porcoesJson);
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                kcal += o.optDouble("kcal", 0);
                proteina += o.optDouble("proteina", 0);
                carboidratos += o.optDouble("carboidratos", 0);
                gordura += o.optDouble("gordura", 0);
            }
        } catch (JSONException ignorado) {
            // registro corrompido não derruba o resumo
        }
    }

    public double valor(com.example.balancetech_tcc_equipe05.activites.dominio.Nutriente n) {
        switch (n) {
            case PROTEINA: return proteina;
            case CARBOIDRATOS: return carboidratos;
            case GORDURA: return gordura;
            default: return kcal;
        }
    }
}