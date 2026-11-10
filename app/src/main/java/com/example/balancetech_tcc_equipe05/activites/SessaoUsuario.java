package com.example.balancetech_tcc_equipe05.activites;

final class SessaoUsuario {
    static final String EXTRA_ID = "id_usuario";
    private static volatile int usuarioId;

    private SessaoUsuario() {
    }

    static int atual() {
        return usuarioId;
    }

    static void iniciar(int id) {
        usuarioId = id;
    }

    static void encerrar() {
        usuarioId = 0;
    }
}
