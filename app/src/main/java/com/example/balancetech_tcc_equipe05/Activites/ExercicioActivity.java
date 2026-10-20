package com.example.balancetech_tcc_equipe05.activites;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.balancetech_tcc_equipe05.R;

public class ExercicioActivity extends AppCompatActivity {

    private Button btnFinalizar;

    private Button btnPoucoExercicio;

    private Button btnExercicio1a3;

    private Button btnExercicio4a5;

    private Button btnExercicio6a7;

    private Button btnExercicioIntenso;

    private String exercicioSelecionado = "";

    private transient Button botaoAnterior = null;

    private void destarcarBotaoSelecionado(Button botaoAnterior){
        Toast.makeText(this, "Selecionado :" + exercicioSelecionado, Toast.LENGTH_SHORT);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_exercicio);

        btnFinalizar = findViewById(R.id.btnFinalizar);
        btnPoucoExercicio = findViewById(R.id.btnPoucoExercicio);
        btnExercicio1a3 = findViewById(R.id.btnExercicio1a3);
        btnExercicio4a5 = findViewById(R.id.btnExercicio4a5);
        btnExercicio6a7 = findViewById(R.id.btnExercicio6a7);
        btnExercicioIntenso = findViewById(R.id.btnExercicioIntenso);

        btnPoucoExercicio.setOnClickListener(v-> {
            exercicioSelecionado = "Pouco exercício";
            destarcarBotaoSelecionado(btnPoucoExercicio);
        });

        btnExercicio1a3.setOnClickListener(v-> {
            exercicioSelecionado = "Exercício de 1 a 3 vezes por semana";
            destarcarBotaoSelecionado(btnExercicio1a3);
        });

        btnExercicio4a5.setOnClickListener(v-> {
            exercicioSelecionado = "Exercício de 4 a 5 vezes por semana";
            destarcarBotaoSelecionado(btnExercicio4a5);
        });


        btnExercicio6a7.setOnClickListener(v-> {
            exercicioSelecionado = "Exercício de 6 a 7 vezes por semana";
            destarcarBotaoSelecionado(btnExercicio6a7);
        });



        btnExercicioIntenso.setOnClickListener(v-> {
            exercicioSelecionado = "Exercícios intensos diariamente";
            destarcarBotaoSelecionado(btnExercicioIntenso);
        });



        btnFinalizar.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ExercicioActivity.this,
                    HomeActivity.class
            );

            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }
}
