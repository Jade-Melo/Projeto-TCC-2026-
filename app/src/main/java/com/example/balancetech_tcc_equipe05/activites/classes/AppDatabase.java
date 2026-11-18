package com.example.balancetech_tcc_equipe05.activites.classes;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {
                Usuario.class,
                Perfil.class,
                RegistroAlimentar.class
        },
        version = 4,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UsuarioDAO usuarioDao();

    public abstract PerfilDAO perfilDao();

    public abstract RegistroAlimentarDAO registroAlimentarDao();

    private static volatile AppDatabase INSTANCE;

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `registros_alimentares` (" +
                            "`usuarioId` INTEGER NOT NULL, " +
                            "`id` TEXT NOT NULL, " +
                            "`refeicao` TEXT NOT NULL, " +
                            "`horarioMillis` INTEGER NOT NULL, " +
                            "`porcoesJson` TEXT NOT NULL, " +
                            "PRIMARY KEY(`usuarioId`, `id`))"
            );
        }
    };

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "app_database"
                            )
                            .addMigrations(MIGRATION_3_4)
                            .build();
                }
            }
        }

        return INSTANCE;
    }
}
