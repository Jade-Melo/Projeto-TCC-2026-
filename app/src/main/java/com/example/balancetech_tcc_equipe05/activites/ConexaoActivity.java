package com.example.balancetech_tcc_equipe05.activites;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.balancetech_tcc_equipe05.R;

import java.util.ArrayList;

public class ConexaoActivity extends AppCompatActivity {

    private static final int PEDIR_BLUETOOTH = 100;

    private BalanceApplication app;
    private Button btnConectar;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable atualizar = new Runnable() {
        @Override
        public void run() {
            String status = app.statusAtual();

            if (app.emSimulacao()) {
                btnConectar.setText("Modo de simulação ativo");
            } else if (status != null && status.startsWith("Conectado")) {
                btnConectar.setText("Balança conectada");
            } else if (status != null && status.startsWith("Conectando")) {
                btnConectar.setText("Conectando...");
            } else {
                btnConectar.setText("Conectar balança");
            }

            handler.postDelayed(this, 300);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conexao);

        app = (BalanceApplication) getApplication();

        findViewById(R.id.btnMenu).setOnClickListener(v ->
                startActivity(new Intent(this, MenuActivity.class))
        );

        findViewById(R.id.btnPerfilConexao).setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class))
        );

        btnConectar = findViewById(R.id.btnConectar);

        btnConectar.setOnClickListener(v -> {
            String status = app.statusAtual();
            boolean conectadoOuSimulando = app.emSimulacao()
                    || (status != null && status.startsWith("Conectado"));

            if (conectadoOuSimulando) {
                mostrarOpcoesConectado();
            } else {
                escolherDispositivo();
            }
        });

        btnConectar.setOnLongClickListener(v -> {
            mostrarOpcoesConectado();
            return true;
        });
    }

    private void mostrarOpcoesConectado() {
        String[] opcoes = {
                "Conectar balança pareada",
                "Tarar balança",
                "Simular peso (modo teste)",
                "Desconectar / encerrar simulação"
        };

        new AlertDialog.Builder(this)
                .setTitle("Opções da Balança")
                .setItems(opcoes, (dialogo, qual) -> {
                    switch (qual) {
                        case 0:
                            escolherDispositivo();
                            break;
                        case 1:
                            boolean aceitou = app.solicitarTara();
                            avisar(aceitou ? "Tara solicitada" : "Conecte a balança primeiro");
                            break;
                        case 2:
                            mostrarSimulacao();
                            break;
                        case 3:
                            app.desconectar();
                            avisar("Balança desconectada");
                            break;
                    }
                })
                .setNegativeButton("Fechar", null)
                .show();
    }

    @SuppressLint("MissingPermission")
    private void escolherDispositivo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    PEDIR_BLUETOOTH
            );
            return;
        }

        BluetoothManager manager = getSystemService(BluetoothManager.class);
        BluetoothAdapter adapter = manager == null ? null : manager.getAdapter();

        if (adapter == null) {
            avisar("Bluetooth indisponível neste celular.");
            return;
        }

        try {
            if (!adapter.isEnabled()) {
                startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
                return;
            }

            ArrayList<BluetoothDevice> dispositivos =
                    new ArrayList<>(adapter.getBondedDevices());

            if (dispositivos.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("Nenhuma balança pareada")
                        .setMessage("Pareie a balança nas configurações Bluetooth do celular ou use o modo de simulação para testar.")
                        .setPositiveButton("Abrir Bluetooth", (d, w) ->
                                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                        )
                        .setNeutralButton("Simular peso", (d, w) -> mostrarSimulacao())
                        .setNegativeButton("Cancelar", null)
                        .show();
                return;
            }

            String[] nomes = new String[dispositivos.size()];
            for (int i = 0; i < dispositivos.size(); i++) {
                BluetoothDevice dispositivo = dispositivos.get(i);
                String nome = dispositivo.getName();
                nomes[i] = (nome == null ? "Sem nome" : nome) + "\n" + dispositivo.getAddress();
            }

            new AlertDialog.Builder(this)
                    .setTitle("Selecione a balança")
                    .setItems(nomes, (dialogo, posicao) ->
                            app.conectar(dispositivos.get(posicao))
                    )
                    .setNeutralButton("Configurações BT", (d, w) ->
                            startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                    )
                    .setNegativeButton("Cancelar", null)
                    .show();

        } catch (SecurityException erro) {
            avisar("Autorize o acesso aos dispositivos próximos.");
        }
    }

    private void mostrarSimulacao() {
        EditText entrada = new EditText(this);
        entrada.setHint("Peso em gramas. Exemplo: 150");
        entrada.setInputType(
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle("Simulação de peso")
                .setMessage(
                        "Ativa o modo de teste sem a balança física. " +
                                "Em Monte seu prato, digite o peso do alimento."
                )
                .setView(entrada)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Ativar simulação", null)
                .create();

        dialogo.setOnShowListener(ignorado ->
                dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {
                            try {
                                double peso = Double.parseDouble(
                                        entrada.getText().toString().trim().replace(',', '.')
                                );
                                app.simular(peso);
                                dialogo.dismiss();
                            } catch (IllegalArgumentException erro) {
                                entrada.setError("Informe um peso maior que zero e até 5000 g");
                            }
                        })
        );

        dialogo.show();
    }

    private void avisar(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PEDIR_BLUETOOTH) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                escolherDispositivo();
            } else {
                avisar("A permissão é necessária para conectar. Você ainda pode usar peso digitado.");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.removeCallbacks(atualizar);
        handler.post(atualizar);
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(atualizar);
        super.onPause();
    }
}
