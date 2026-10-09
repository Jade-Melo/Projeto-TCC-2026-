
package com.example.balancetech_tcc_equipe05.Activites;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;

public class LoginActivity extends AppCompatActivity {

    private Button btnEntrar;
    private TextView esqueciSenha;
    private TextView txtCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        btnEntrar = findViewById(R.id.btnEntrar);
        esqueciSenha = findViewById(R.id.esqueciSenha);
        txtCadastro = findViewById(R.id.txtCadastro);

    }
}
