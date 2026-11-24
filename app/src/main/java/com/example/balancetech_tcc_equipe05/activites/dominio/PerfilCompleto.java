package com.example.balancetech_tcc_equipe05.activites.dominio;

/** Usuário + perfil nutricional já com IMC e metas calculados. */
public final class PerfilCompleto {
    public int usuarioId;
    public String nome;
    public String email;
    public String genero;
    public int idade;
    public double peso;
    public int altura;
    public String objetivo;
    public String exercicio;
    public double imc;
    public Totais metas;
}