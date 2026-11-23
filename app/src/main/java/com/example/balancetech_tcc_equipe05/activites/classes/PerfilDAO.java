package com.example.balancetech_tcc_equipe05.activites.classes;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface PerfilDAO {
    @Insert
    void inserir(Perfil perfil);

    @Insert
    long inserirComId(Perfil perfil);

    @Query("SELECT metaCalorica FROM Perfil WHERE usuarioId = :usuarioId AND idPerfil = :perfilId")
    Double buscarMetaCalorica(int usuarioId, long perfilId);

    @Query("SELECT * FROM Perfil WHERE usuarioId = :usuarioId AND idPerfil = :perfilId LIMIT 1")
    Perfil buscarPorId(int usuarioId, long perfilId);

    @Update
    int atualizar(Perfil perfil);
}