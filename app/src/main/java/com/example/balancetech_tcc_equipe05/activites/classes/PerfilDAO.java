package com.example.balancetech_tcc_equipe05.activites.classes;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface PerfilDAO {
    @Insert
    void inserir(Perfil perfil);
}
