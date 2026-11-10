package com.example.balancetech_tcc_equipe05.activites;

import android.os.Bundle;

import com.example.balancetech_tcc_equipe05.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

final class Alimentacao {

    static final String EXTRA_REFEICAO = "alimentacao.refeicao";
    static final String EXTRA_PORCOES = "alimentacao.porcoes";
    static final Locale PT_BR = new Locale("pt", "BR");

    enum Refeicao {
        CAFE("Café da manhã", R.id.btnAddCafe, R.id.txtCaloriasCafe),
        ALMOCO("Almoço", R.id.btnAddAlmoco, R.id.txtCaloriasAlmoco),
        LANCHE("Lanche", R.id.btnAddLanche, R.id.txtCaloriasLanche),
        JANTAR("Jantar", R.id.btnAddJantar, R.id.txtCaloriasJantar);

        final String rotulo;
        final int botao;
        final int textoCalorias;

        Refeicao(String rotulo, int botao, int textoCalorias) {
            this.rotulo = rotulo;
            this.botao = botao;
            this.textoCalorias = textoCalorias;
        }
    }

    // TACO (NEPA/Unicamp), 4ª ed., tabela 1. Por 100 g da parte comestível.
    // Ordem: código TACO, kcal, proteína (g), carboidratos (g), gordura (g).
    // "Tr" (traços) é aproximado como zero.
    static final Alimento[] CATALOGO = {
        new Alimento(3, "Arroz branco cozido", 128, 2.5, 28.1, 0.2),
        new Alimento(1, "Arroz integral cozido", 124, 2.6, 25.8, 1),
        new Alimento(561, "Feijão carioca cozido", 76, 4.8, 13.6, 0.5),
        new Alimento(567, "Feijão preto cozido", 77, 4.5, 14, 0.5),
        new Alimento(577, "Lentilha cozida", 93, 6.3, 16.3, 0.5),
        new Alimento(410, "Peito de frango sem pele grelhado", 159, 32, 0, 2.5),
        new Alimento(490, "Ovo frito", 240, 15.6, 1.2, 18.6),
        new Alimento(488, "Ovo cozido", 146, 13.3, 0.6, 9.5),
        new Alimento(277, "Atum em conserva em óleo", 166, 26.2, 0, 6),
        new Alimento(231, "Manga Tommy Atkins crua", 51, 0.9, 12.8, 0.2),
        new Alimento(182, "Banana-prata crua", 98, 1.3, 26, 0.1),
        new Alimento(222, "Maçã Fuji com casca crua", 56, 0.3, 15.2, 0),
        new Alimento(226, "Mamão papaia cru", 40, 0.5, 10.4, 0.1),
        new Alimento(235, "Melancia crua", 33, 0.9, 8.1, 0),
        new Alimento(236, "Melão cru", 29, 0.7, 7.5, 0),
        new Alimento(163, "Abacate cru", 96, 1.2, 6, 8.4),
        new Alimento(100, "Brócolis cozido", 25, 2.1, 4.4, 0.5),
        new Alimento(109, "Cenoura cozida", 30, 0.8, 6.7, 0.2),
        new Alimento(157, "Tomate cru com sementes", 15, 1.1, 3.1, 0.2),
        new Alimento(78, "Alface crespa crua", 11, 1.3, 1.7, 0.2),
        new Alimento(64, "Abóbora cabotiá cozida", 48, 1.4, 10.8, 0.7),
        new Alimento(116, "Couve-manteiga refogada", 90, 1.7, 8.7, 6.6),
        new Alimento(129, "Mandioca cozida", 125, 0.6, 30.1, 0.3),
        new Alimento(91, "Batata inglesa cozida", 52, 1.2, 11.9, 0),
        new Alimento(88, "Batata-doce cozida", 77, 0.6, 18.4, 0.1),
        new Alimento(7, "Aveia em flocos crua", 394, 13.9, 66.6, 8.5),
        new Alimento(53, "Pão francês", 300, 8, 58.6, 3.1),
        new Alimento(448, "Iogurte natural", 51, 4.1, 1.9, 3),
        new Alimento(449, "Iogurte natural desnatado", 41, 3.8, 5.8, 0.3),
        new Alimento(461, "Queijo minas frescal", 264, 17.4, 3.2, 20.2)
    };

    private Alimentacao() {
    }

    static final class Alimento {
        final int codigoTaco;
        final String nome;
        final double kcal100;
        final double proteina100;
        final double carboidratos100;
        final double gordura100;

        Alimento(int codigoTaco, String nome, double kcal100, double proteina100,
                 double carboidratos100, double gordura100) {
            this.codigoTaco = codigoTaco;
            this.nome = nome;
            this.kcal100 = kcal100;
            this.proteina100 = proteina100;
            this.carboidratos100 = carboidratos100;
            this.gordura100 = gordura100;
        }

        Bundle porcao(double gramas, boolean pesoDaBalanca) {
            double proporcao = gramas / 100;
            Bundle dados = new Bundle();
            dados.putInt("codigoTaco", codigoTaco);
            dados.putString("nome", nome);
            dados.putBoolean("pesoDaBalanca", pesoDaBalanca);
            dados.putDouble("gramas", gramas);
            dados.putDouble("kcal", kcal100 * proporcao);
            dados.putDouble("proteina", proteina100 * proporcao);
            dados.putDouble("carboidratos", carboidratos100 * proporcao);
            dados.putDouble("gordura", gordura100 * proporcao);
            return dados;
        }
    }

    static final class Totais {
        double kcal;
        double proteina;
        double carboidratos;
        double gordura;

        void somar(List<Bundle> porcoes) {
            for (Bundle porcao : porcoes) {
                kcal += porcao.getDouble("kcal");
                proteina += porcao.getDouble("proteina");
                carboidratos += porcao.getDouble("carboidratos");
                gordura += porcao.getDouble("gordura");
            }
        }

        String compararComMetas(IntegracaoAlimentacao.Metas metas) {
            StringBuilder texto = new StringBuilder();
            adicionarPercentual(texto, "Energia", kcal, metas.kcal);
            adicionarPercentual(texto, "Proteínas", proteina, metas.proteina);
            adicionarPercentual(texto, "Carboidratos", carboidratos, metas.carboidratos);
            adicionarPercentual(texto, "Gorduras", gordura, metas.gordura);
            return texto.length() == 0 ? "" : "\n\nParticipação na sua meta diária" + texto;
        }

        private void adicionarPercentual(StringBuilder texto, String nome,
                                         double valor, double meta) {
            if (positivo(meta)) {
                texto.append('\n').append(nome).append(": ")
                        .append(numero(valor / meta * 100)).append('%');
            }
        }

        String resumo() {
            return numero(kcal) + " kcal\n"
                    + "Proteínas: " + numero(proteina) + " g\n"
                    + "Carboidratos: " + numero(carboidratos) + " g\n"
                    + "Gorduras: " + numero(gordura) + " g";
        }
    }

    static String numero(double valor) {
        DecimalFormat formato = new DecimalFormat("0.##", new DecimalFormatSymbols(PT_BR));
        return formato.format(valor);
    }

    static boolean positivo(double valor) {
        return valor > 0 && !Double.isInfinite(valor);
    }

    static String comMeta(double valor, double meta, String unidade) {
        return numero(valor) + (positivo(meta) ? " / " + numero(meta) : "") + unidade;
    }

    static boolean corresponde(String nome, String busca) {
        String normalizado = normalizar(nome);
        for (String termo : normalizar(busca).split("\\s+")) {
            if (!normalizado.contains(termo)) {
                return false;
            }
        }
        return true;
    }

    static String normalizar(String texto) {
        return Normalizer.normalize(texto.toLowerCase(PT_BR), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").trim();
    }

    static String descricao(Bundle porcao) {
        return porcao.getString("nome") + " · " + numero(porcao.getDouble("gramas")) + " g";
    }
}
