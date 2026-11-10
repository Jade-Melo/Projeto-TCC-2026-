package com.example.balancetech_tcc_equipe05.activites.classes;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface PerfilDAO {
    @Insert
    void inserir(Perfil perfil);

    @Insert
    long inserirComId(Perfil perfil);

    @Query("SELECT metaCalorica FROM Perfil WHERE usuarioId = :usuarioId AND idPerfil = :perfilId")
    Double buscarMetaCalorica(int usuarioId, long perfilId);
}
