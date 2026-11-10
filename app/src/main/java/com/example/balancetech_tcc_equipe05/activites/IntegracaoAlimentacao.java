package com.example.balancetech_tcc_equipe05.activites;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Contratos implementados pela Application existente do projeto. */
public interface IntegracaoAlimentacao {

    /** Metas do perfil autenticado. Devolva null enquanto estiverem indisponíveis. */
    default Metas metasAtuais() {
        return null;
    }

    /** Último peso estável e recente, em gramas. Devolva null sem leitura válida. */
    default Double pesoEstavelGramas() {
        return null;
    }

    /** Enfileira o comando de tara e invalida a leitura anterior. Retorna se aceito. */
    default boolean solicitarTara() {
        return false;
    }

    /** Repositório vinculado ao usuário autenticado, ou null no modo de demonstração. */
    default Registros registrosAtuais() {
        return null;
    }

    static IntegracaoAlimentacao de(Object application) {
        return application instanceof IntegracaoAlimentacao
                ? (IntegracaoAlimentacao) application : null;
    }

    final class Metas {
        public final double kcal;
        public final double proteina;
        public final double carboidratos;
        public final double gordura;

        public Metas(double kcal, double proteina, double carboidratos, double gordura) {
            this.kcal = kcal;
            this.proteina = proteina;
            this.carboidratos = carboidratos;
            this.gordura = gordura;
        }
    }

    interface Registros {
        /** Salva de forma atômica e idempotente por ID. Executado fora da thread da tela. */
        void salvar(Registro registro) throws Exception;
    }

    final class Registro {
        public final String id;
        /** CAFE, ALMOCO, LANCHE ou JANTAR. */
        public final String refeicao;
        public final long horarioMillis;
        public final List<Porcao> porcoes;

        Registro(String id, String refeicao, long horarioMillis, List<Bundle> dados) {
            this.id = id;
            this.refeicao = refeicao;
            this.horarioMillis = horarioMillis;
            List<Porcao> itens = new ArrayList<>();
            for (Bundle item : dados) {
                itens.add(new Porcao(item));
            }
            this.porcoes = Collections.unmodifiableList(itens);
        }
    }

    final class Porcao {
        public final int codigoTaco;
        public final String nome;
        public final double gramas;
        public final boolean pesoDaBalanca;
        public final double kcal;
        public final double proteina;
        public final double carboidratos;
        public final double gordura;

        Porcao(Bundle dados) {
            codigoTaco = dados.getInt("codigoTaco");
            nome = dados.getString("nome");
            gramas = dados.getDouble("gramas");
            pesoDaBalanca = dados.getBoolean("pesoDaBalanca");
            kcal = dados.getDouble("kcal");
            proteina = dados.getDouble("proteina");
            carboidratos = dados.getDouble("carboidratos");
            gordura = dados.getDouble("gordura");
        }
    }
}
