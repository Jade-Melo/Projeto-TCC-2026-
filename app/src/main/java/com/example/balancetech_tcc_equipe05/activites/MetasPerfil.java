package com.example.balancetech_tcc_equipe05.activites;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.balancetech_tcc_equipe05.activites.classes.AppDatabase;
import com.example.balancetech_tcc_equipe05.activites.classes.Perfil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class MetasPerfil {
    private static final ExecutorService BANCO = Executors.newSingleThreadExecutor();

    interface Resultado {
        void receber(IntegracaoAlimentacao.Metas metas);
    }

    private MetasPerfil() {
    }

    private static SharedPreferences perfisVerificados(Context contexto) {
        return contexto.getSharedPreferences("balance_tech_perfis_verificados", Context.MODE_PRIVATE);
    }

    // Perfis antigos gravados com usuarioId = 1 não têm vínculo verificável.
    static void salvar(Context contexto, Perfil perfil) {
        long id = AppDatabase.getDatabase(contexto).perfilDao().inserirComId(perfil);
        boolean vinculado = perfisVerificados(contexto).edit()
                .putLong(String.valueOf(perfil.getUsuarioId()), id).commit();
        if (!vinculado) {
            throw new IllegalStateException("Não foi possível confirmar o vínculo do perfil");
        }
    }

    static Double consultarCalorias(Context contexto, int usuarioId) {
        long id = perfisVerificados(contexto).getLong(String.valueOf(usuarioId), 0);
        if (id <= 0) {
            return null;
        }
        Double kcal = AppDatabase.getDatabase(contexto).perfilDao().buscarMetaCalorica(usuarioId, id);
        return kcal != null && Alimentacao.positivo(kcal) ? kcal : null;
    }

    static void carregar(Activity tela, Resultado resultado) {
        int id = SessaoUsuario.atual();
        if (id <= 0) {
            resultado.receber(null);
            return;
        }
        Context contexto = tela.getApplicationContext();
        BANCO.execute(() -> {
            Double kcal = null;
            try {
                kcal = consultarCalorias(contexto, id);
            } catch (RuntimeException erro) {
                Log.e("BalanceTech", "Erro ao consultar meta do perfil", erro);
            }
            final Double calorias = kcal;
            tela.runOnUiThread(() -> {
                if (tela.isFinishing() || tela.isDestroyed() || SessaoUsuario.atual() != id) {
                    return;
                }
                IntegracaoAlimentacao integracao = IntegracaoAlimentacao.de(tela.getApplication());
                IntegracaoAlimentacao.Metas extras = integracao == null ? null : integracao.metasAtuais();
                IntegracaoAlimentacao.Metas metas = calorias == null ? extras
                        : new IntegracaoAlimentacao.Metas(calorias,
                                extras == null ? 0 : extras.proteina,
                                extras == null ? 0 : extras.carboidratos,
                                extras == null ? 0 : extras.gordura);
                resultado.receber(metas);
            });
        });
    }
}
