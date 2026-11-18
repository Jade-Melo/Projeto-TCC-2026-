package com.example.balancetech_tcc_equipe05.activites.classes;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RegistroAlimentarDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void inserir(RegistroAlimentar registro);

    @Query("SELECT * FROM registros_alimentares WHERE usuarioId = :usuarioId AND horarioMillis >= :inicio AND horarioMillis < :fim ORDER BY horarioMillis ASC")
    List<RegistroAlimentar> consultarPeriodo(int usuarioId, long inicio, long fim);
}

