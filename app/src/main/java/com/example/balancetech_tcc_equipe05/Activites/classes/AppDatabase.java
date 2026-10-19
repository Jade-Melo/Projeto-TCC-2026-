package com.example.balancetech_tcc_equipe05.activites.classes;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Usuario.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Declara o DAO de usuário para que o Room o implemente automaticamente
    public abstract UsuarioDAO usuarioDao();

    private static volatile AppDatabase INSTANCE;

    // Método Singleton para obter a instância do banco de dados
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "app_database" // Nome do arquivo do banco SQLite no celular
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
