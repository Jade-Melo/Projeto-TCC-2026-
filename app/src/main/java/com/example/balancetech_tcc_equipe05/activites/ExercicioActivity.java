package com.example.balancetech_tcc_equipe05.activites;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.balancetech_tcc_equipe05.R;
import com.example.balancetech_tcc_equipe05.activites.classes.AppDatabase;
import com.example.balancetech_tcc_equipe05.activites.classes.Usuario;

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

        String generoRecebido = getIntent().getStringExtra("CHAVE_GENERO");
        int idadeRecebida = getIntent().getIntExtra("CHAVE_IDADE", 0);
        Double pesoRecebido = getIntent().getDoubleExtra("CHAVE_PESO", 0.0);
        int alturaRecebida = getIntent().getIntExtra("CHAVE_ALTURA", 0);
        String objetivoRecebido = getIntent().getStringExtra("CHAVE_OBJETIVO");

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
            //Trava de segurança para ver se o usuario escolheu o exercício
            if (exercicioSelecionado.isEmpty()) {
                Toast.makeText(this, "Por favor, selecione seu nível de atividade física.", Toast.LENGTH_SHORT).show();
                return; // Para o código aqui e não faz nenhuma conta com o "nada"
            }

            // Converte a altura de centímetros (int) para metros (double)]
            double alturaEmMetros = alturaRecebida / 100.0;

            //Aplicamos a fórmula do IMC já com a altura convertida
            double imc = pesoRecebido / (alturaEmMetros * alturaEmMetros);

            // declara a variavel tmb para realizar os calculos da taxa metabólica basal (TMB)
            double tmb = 0.0;

            //verifica se o genero é homem, se for realiza o cálculo da TMB para homens,
            if (generoRecebido.equalsIgnoreCase("Homem")) {
                //Fórmula da TMB para homens
                tmb = 88.362 + (13.397 * pesoRecebido) + (4.799 * alturaRecebida) - (5.677 * idadeRecebida);
            }else{
                // se não for homem realiza o cálculo da TMB com os valores das mulheres
                tmb =  447.593 + (9.247 * pesoRecebido) + (3.098 * alturaRecebida) - (4.330 * idadeRecebida);
            }

            double gastoCaloricoTotal = 0.0;

            //Analisa a escolha do usuario
            switch (exercicioSelecionado) {
                case "Pouco exercício":
                    gastoCaloricoTotal = tmb * 1.2;
                    break;

                case "Exercício de 1 a 3 vezes por semana":
                    gastoCaloricoTotal = tmb * 1.375;
                    break;

                case "Exercício de 4 a 5 vezes por semana":
                    gastoCaloricoTotal = tmb * 1.55;
                    break;

                case "Exercício de 6 a 7 vezes por semana":
                    gastoCaloricoTotal = tmb * 1.725;
                    break;

                case "Exercícios intensos diariamente":
                    gastoCaloricoTotal = tmb * 1.9; // Fator para atletas de alta intensidade
                    break;

                default:
                    gastoCaloricoTotal = tmb * 1.2; // Valor de segurança caso nada seja selecionado
                    break;
            }

            double metaCaloricaFinal = gastoCaloricoTotal;

            if (objetivoRecebido.equalsIgnoreCase("Perder Peso")) {
                metaCaloricaFinal = gastoCaloricoTotal - 400; //Déficit calórico
            } else if (objetivoRecebido.equalsIgnoreCase("Ganhar Músculo")) {
                metaCaloricaFinal = gastoCaloricoTotal + 400; //Superávit calórico
            }

            // Criamos um objeto do tipo Usuario para carregar todas as informações
            Usuario usuarioEntity = new Usuario("", "", ""); // Deixamos nome, email e senha vazios
            // por enquanto, pois serão atualizados no cadastro

            // Usamos os "Setters" para colocar os dados dentro desse usuário
            usuarioEntity.setGenero(generoRecebido);
            usuarioEntity.setIdade(idadeRecebida);
            usuarioEntity.setPeso(pesoRecebido);
            usuarioEntity.setAltura(alturaRecebida);
            usuarioEntity.setObjetivo(objetivoRecebido);
            usuarioEntity.setExercicio(exercicioSelecionado);
            usuarioEntity.setImc(imc);
            usuarioEntity.setMetaCalorica(metaCaloricaFinal);

            // Disparamos a linha em segundo plano para salvar no banco de dados SQLite sem travar a tela
            java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {

                // Pegamos a instância do banco e chamamos o metodo inserir do DAO
                AppDatabase.getDatabase(ExercicioActivity.this).usuarioDao().cadastrar(usuarioEntity);

                // Avisamos no console do PC que deu certo!
                Log.d("TESTE_BANCO", "Dados salvos com sucesso no SQLite local do Room!");
            });


            Log.d("TESTE_CALCULOS", "IMC Calculado: " + imc);
            Log.d("TESTE_CALCULOS", "TMB Calculada: " + tmb);
            Log.d("TESTE_CALCULOS", "Gasto Total: " + gastoCaloricoTotal);
            Log.d("TESTE_CALCULOS", "Meta Final: " + metaCaloricaFinal);

            Intent intent = new Intent(
                    ExercicioActivity.this,
                    HomeActivity.class
            );

            intent.putExtra("RESULTADO_IMC", imc);
            intent.putExtra("RESULTADO_TMB", tmb);
            intent.putExtra("RESULTADO_GASTO", gastoCaloricoTotal);
            intent.putExtra("RESULTADO_META", metaCaloricaFinal);

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
