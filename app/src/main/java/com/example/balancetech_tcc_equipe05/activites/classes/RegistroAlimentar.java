package com.example.balancetech_tcc_equipe05.activites.classes;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "registros_alimentares", primaryKeys = {"usuarioId", "id"})
public class RegistroAlimentar {

    public int usuarioId;

    @NonNull
    public String id = "";

    @NonNull
    public String refeicao = "";

    public long horarioMillis;

    @NonNull
    public String porcoesJson = "[]";
}

