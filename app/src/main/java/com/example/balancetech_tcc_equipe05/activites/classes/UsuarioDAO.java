package com.example.balancetech_tcc_equipe05.activites.classes;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UsuarioDAO {

    // Retorna long para capturar o ID gerado (mesmo que a entidade use int, o Room lida bem com isso)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long cadastrar(Usuario usuario);

    @Query("SELECT * FROM usuario WHERE email = :email LIMIT 1")
    Usuario buscarPorEmail(String email);

    @Query("SELECT * FROM usuario WHERE email = :email AND senha = :senha LIMIT 1")
    Usuario autenticar(String email, String senha);

    @Query("UPDATE usuario SET senha = :novaSenha WHERE email = :email")
    void atualizarSenha(String email, String novaSenha);
}

