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

    @Query("SELECT * FROM registros_alimentares WHERE usuarioId = :usuarioId AND id = :id LIMIT 1")
    RegistroAlimentar buscarPorId(int usuarioId, String id);

    /** Substitui o registro inteiro (edição de refeição/horário/porções). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void substituir(RegistroAlimentar registro);

    @Query("DELETE FROM registros_alimentares WHERE usuarioId = :usuarioId AND id = :id")
    int excluir(int usuarioId, String id);
}