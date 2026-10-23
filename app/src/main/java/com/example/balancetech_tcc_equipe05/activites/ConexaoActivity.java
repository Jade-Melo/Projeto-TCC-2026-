package com.example.balancetech_tcc_equipe05.activites;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.balancetech_tcc_equipe05.R;

import java.io.IOException;
import java.util.UUID;

public class ConexaoActivity extends AppCompatActivity {

    private ImageButton btnMenu;
    private ImageButton btnPerfilConexao;

    private Button btnConectar;
    private Button btnVoltar;

    private TextView txtStatus;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;

    private static final UUID UUID_SPP =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_conexao);

        // =========================
        // MENU
        // =========================

        btnConectar = findViewById(R.id.btnConectar);

        btnConectar.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ConexaoActivity.this,
                    MenuActivity.class
            );

            startActivity(intent);
        });


        // =========================
        // PERFIL
        // =========================

        btnPerfilConexao = findViewById(R.id.btnPerfilConexao);

        btnPerfilConexao.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ConexaoActivity.this,
                    PerfilActivity.class
            );

            startActivity(intent);
        });


        // =========================
        // COMPONENTES
        // =========================

        btnConectar = findViewById(R.id.btnConectar);


        // =========================
        // BLUETOOTH
        // =========================

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        // =========================
        // CONECTAR
        // =========================

        btnConectar.setOnClickListener(v -> conectarBalanca());


        // =========================
        // VOLTAR
        // =========================

        btnVoltar.setOnClickListener(v -> finish());
    }


    private void conectarBalanca() {

        if (bluetoothAdapter == null) {

            txtStatus.setText("Bluetooth não disponível.");

            return;
        }


        if (!bluetoothAdapter.isEnabled()) {

            txtStatus.setText("Ative o Bluetooth para conectar.");

            return;
        }


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.BLUETOOTH_CONNECT
                    },
                    100
            );

            return;
        }


        try {

            txtStatus.setText("Tentando conectar...");

            for (BluetoothDevice device :
                    bluetoothAdapter.getBondedDevices()) {

                try {

                    bluetoothSocket =
                            device.createRfcommSocketToServiceRecord(UUID_SPP);

                    bluetoothSocket.connect();

                    txtStatus.setText("Conexão bem sucedida");

                    return;

                } catch (IOException e) {

                    try {

                        if (bluetoothSocket != null) {
                            bluetoothSocket.close();
                        }

                    } catch (IOException ignored) {
                    }
                }
            }

            txtStatus.setText("Falha na conexão");

        } catch (SecurityException e) {

            txtStatus.setText("Permissão Bluetooth não concedida.");

        }
    }

}