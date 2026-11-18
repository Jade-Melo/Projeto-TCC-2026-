package com.example.balancetech_tcc_equipe05.activites;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;

import com.example.balancetech_tcc_equipe05.activites.classes.AppDatabase;
import com.example.balancetech_tcc_equipe05.activites.classes.RegistroAlimentar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BalanceApplication extends Application implements IntegracaoAlimentacao {

    private static final UUID SPP_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final long TEMPO_ESTABILIZACAO_MS = 1200L;
    private static final long VALIDADE_LEITURA_MS = 10000L;

    private volatile String status = "Desconectado";
    private volatile Double ultimoPeso = null;
    private volatile Double pesoEstavel = null;
    private volatile long ultimaLeituraMs = 0L;
    private volatile long inicioEstavelMs = 0L;
    private volatile Double pesoReferenciaEstavel = null;
    private volatile boolean simulando = false;

    private BluetoothSocket socketAtual;
    private OutputStream saidaAtual;
    private Thread threadLeitura;

    public String statusAtual() {
        return status;
    }

    public Double pesoAtual() {
        if (!simulando && System.currentTimeMillis() - ultimaLeituraMs > VALIDADE_LEITURA_MS) {
            return null;
        }
        return ultimoPeso;
    }

    public boolean emSimulacao() {
        return simulando;
    }

    @Override
    public Double pesoEstavelGramas() {
        if (simulando) {
            return ultimoPeso;
        }
        if (System.currentTimeMillis() - ultimaLeituraMs > VALIDADE_LEITURA_MS) {
            return null;
        }
        return pesoEstavel;
    }

    @Override
    public synchronized boolean solicitarTara() {
        if (simulando) {
            ultimoPeso = 0.0;
            pesoEstavel = 0.0;
            ultimaLeituraMs = System.currentTimeMillis();
            return true;
        }
        if (saidaAtual == null) {
            return false;
        }
        try {
            saidaAtual.write("T\n".getBytes(StandardCharsets.UTF_8));
            saidaAtual.flush();
            pesoEstavel = null;
            pesoReferenciaEstavel = null;
            inicioEstavelMs = 0L;
            return true;
        } catch (IOException erro) {
            desconectar();
            return false;
        }
    }

    public synchronized void simular(double peso) {
        if (!Alimentacao.positivo(peso) || peso > 5000.0) {
            throw new IllegalArgumentException("Peso inválido para simulação");
        }
        encerrarConexaoFisica();
        simulando = true;
        ultimoPeso = peso;
        pesoEstavel = peso;
        ultimaLeituraMs = System.currentTimeMillis();
        status = "Simulação ativa (" + Alimentacao.numero(peso) + " g)";
    }

    @SuppressLint("MissingPermission")
    public synchronized void conectar(BluetoothDevice dispositivo) {
        if (dispositivo == null) return;
        desconectar();
        String nome = dispositivo.getName() != null ? dispositivo.getName() : dispositivo.getAddress();
        status = "Conectando a " + nome + "...";

        threadLeitura = new Thread(() -> {
            try {
                BluetoothSocket socket = dispositivo.createRfcommSocketToServiceRecord(SPP_UUID);
                socket.connect();
                synchronized (BalanceApplication.this) {
                    socketAtual = socket;
                    saidaAtual = socket.getOutputStream();
                    status = "Conectado a " + nome;
                }
                lerFluxoBluetooth(socket);
            } catch (Exception erro) {
                synchronized (BalanceApplication.this) {
                    if (!simulando) {
                        status = "Falha ao conectar com " + nome;
                        encerrarConexaoFisica();
                    }
                }
            }
        }, "balance-bt-leitura");
        threadLeitura.start();
    }

    private void lerFluxoBluetooth(BluetoothSocket socket) throws IOException {
        BufferedReader leitor = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
        );
        String linha;
        while (!Thread.currentThread().isInterrupted() && (linha = leitor.readLine()) != null) {
            String limpa = linha.replaceAll("[^0-9.,-]", "").replace(',', '.').trim();
            if (limpa.isEmpty()) continue;
            try {
                double valor = Double.parseDouble(limpa);
                if (valor < 0) valor = 0.0;
                registrarLeitura(valor);
            } catch (NumberFormatException ignorado) {
            }
        }
    }

    private void registrarLeitura(double valor) {
        long agora = System.currentTimeMillis();
        ultimoPeso = valor;
        ultimaLeituraMs = agora;

        if (pesoReferenciaEstavel == null || Math.abs(valor - pesoReferenciaEstavel) > 1.5) {
            pesoReferenciaEstavel = valor;
            inicioEstavelMs = agora;
            pesoEstavel = null;
        } else if (agora - inicioEstavelMs >= TEMPO_ESTABILIZACAO_MS) {
            pesoEstavel = valor;
        }
    }

    public synchronized void desconectar() {
        simulando = false;
        encerrarConexaoFisica();
        status = "Desconectado";
    }

    private void encerrarConexaoFisica() {
        ultimoPeso = null;
        pesoEstavel = null;
        pesoReferenciaEstavel = null;
        inicioEstavelMs = 0L;
        if (threadLeitura != null) {
            threadLeitura.interrupt();
            threadLeitura = null;
        }
        if (saidaAtual != null) {
            try {
                saidaAtual.close();
            } catch (IOException ignorado) {
            }
            saidaAtual = null;
        }
        if (socketAtual != null) {
            try {
                socketAtual.close();
            } catch (IOException ignorado) {
            }
            socketAtual = null;
        }
    }

    @Override
    public Registros registrosAtuais() {
        final int usuarioId = SessaoUsuario.atual();
        if (usuarioId <= 0) {
            return null;
        }
        return registro -> {
            RegistroAlimentar entidade = new RegistroAlimentar();
            entidade.usuarioId = usuarioId;
            entidade.id = registro.id;
            entidade.refeicao = registro.refeicao;
            entidade.horarioMillis = registro.horarioMillis;
            entidade.porcoesJson = serializarPorcoes(registro.porcoes);

            AppDatabase.getDatabase(getApplicationContext())
                    .registroAlimentarDao()
                    .inserir(entidade);
        };
    }

    private static String serializarPorcoes(List<Porcao> porcoes) throws JSONException {
        JSONArray array = new JSONArray();
        for (Porcao p : porcoes) {
            JSONObject obj = new JSONObject();
            obj.put("codigoTaco", p.codigoTaco);
            obj.put("nome", p.nome);
            obj.put("gramas", p.gramas);
            obj.put("pesoDaBalanca", p.pesoDaBalanca);
            obj.put("kcal", p.kcal);
            obj.put("proteina", p.proteina);
            obj.put("carboidratos", p.carboidratos);
            obj.put("gordura", p.gordura);
            array.put(obj);
        }
        return array.toString();
    }

    public static List<Bundle> lerPorcoes(RegistroAlimentar registro) {
        List<Bundle> lista = new ArrayList<>();
        if (registro == null || registro.porcoesJson == null) {
            return lista;
        }
        try {
            JSONArray array = new JSONArray(registro.porcoesJson);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Bundle b = new Bundle();
                b.putInt("codigoTaco", obj.optInt("codigoTaco"));
                b.putString("nome", obj.optString("nome"));
                b.putDouble("gramas", obj.optDouble("gramas"));
                b.putBoolean("pesoDaBalanca", obj.optBoolean("pesoDaBalanca"));
                b.putDouble("kcal", obj.optDouble("kcal"));
                b.putDouble("proteina", obj.optDouble("proteina"));
                b.putDouble("carboidratos", obj.optDouble("carboidratos"));
                b.putDouble("gordura", obj.optDouble("gordura"));
                lista.add(b);
            }
        } catch (JSONException ignorado) {
        }
        return lista;
    }
}