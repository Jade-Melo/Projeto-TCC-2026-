
package com.example.balancetech_tcc_equipe05.activites;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.balancetech_tcc_equipe05.R;
import com.example.balancetech_tcc_equipe05.activites.dominio.CalculoNutricional;
import com.example.balancetech_tcc_equipe05.activites.dominio.PerfilCompleto;

import java.io.InputStream;
import java.text.NumberFormat;

public class PerfilActivity extends AppCompatActivity {

    private static final int VERDE = 0xFF57905C;
    private static final int FUNDO = 0xFFFAFAF9;
    private static final int BRANCO = 0xFFFFFFFF;
    private static final int TEXTO = 0xFF171717;
    private static final int TEXTO_SECUNDARIO = 0xFF666666;
    private static final int BORDA = 0xFFE1E8E1;

    private ImageButton btnMenu;
    private ImageButton btnPerfil;
    private Button btnSair;
    private Button btnSairConta;
    private ImageView imgPerfil;

    private NutricaoRepositorio repositorio;
    private PerfilCompleto perfilAtual;

    private ActivityResultLauncher<Intent> seletorImagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        repositorio = new NutricaoRepositorio(this);

        imgPerfil = findViewById(R.id.imgPerfil);
        FotoPerfil.aplicarFormatoRedondo(imgPerfil);

        seletorImagem = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultado -> {
                    if (resultado.getResultCode() == RESULT_OK
                            && resultado.getData() != null) {

                        Uri imagemSelecionada =
                                resultado.getData().getData();

                        if (imagemSelecionada != null) {

                            int usuarioId = SessaoUsuario.atual();

                            if (usuarioId <= 0) {
                                Toast.makeText(
                                        this,
                                        "Nenhuma conta conectada.",
                                        Toast.LENGTH_SHORT
                                ).show();
                                return;
                            }

                            try {
                                int flags = resultado.getData().getFlags()
                                        & Intent.FLAG_GRANT_READ_URI_PERMISSION;

                                if (flags != 0) {
                                    getContentResolver()
                                            .takePersistableUriPermission(
                                                    imagemSelecionada,
                                                    flags
                                            );
                                }

                            } catch (SecurityException |
                                     IllegalArgumentException erro) {
                                android.util.Log.w(
                                        "BalanceTech",
                                        "Não foi possível manter a permissão da imagem.",
                                        erro
                                );
                            }

                            abrirAjusteFoto(imagemSelecionada);
                        }
                    }
                }
        );

        imgPerfil.setOnClickListener(v -> abrirGaleria());

        imgPerfil.setContentDescription(
                "Toque para alterar sua foto de perfil"
        );

        findViewById(R.id.btnEditarPerfil)
                .setOnClickListener(v -> abrirEdicao());

        btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PerfilActivity.this,
                    MenuActivity.class
            );
            startActivity(intent);
        });

        btnPerfil = findViewById(R.id.btnPerfil);

        btnPerfil.setOnClickListener(v -> {
            // O usuário já está na tela de Perfil.
        });

        btnSair = findViewById(R.id.btnSair);

        btnSair.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PerfilActivity.this,
                    HomeActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
            finish();
        });

        btnSairConta = findViewById(R.id.btnSairConta);

        btnSairConta.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PerfilActivity.this,
                    CadastroActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });

        carregarFotoPerfil();
    }

    private void abrirGaleria() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        seletorImagem.launch(intent);
    }

    private Bitmap obterBitmap(Uri uri) throws Exception {

        try (InputStream entrada =
                     getContentResolver().openInputStream(uri)) {

            if (entrada == null) {
                throw new Exception("Não foi possível abrir a imagem.");
            }

            Bitmap bitmap = BitmapFactory.decodeStream(entrada);

            if (bitmap == null) {
                throw new Exception("Imagem inválida.");
            }

            return bitmap;
        }
    }

    // Ajuste da foto com zoom por dois dedos.
    private void abrirAjusteFoto(Uri uri) {

        try {

            Bitmap bitmap = obterBitmap(uri);

            LinearLayout principal = criarPainelDialogo();

            principal.addView(
                    criarCabecalho(
                            "Ajustar foto",
                            "Personalize o enquadramento da sua imagem."
                    )
            );

            LinearLayout corpo = new LinearLayout(this);
            corpo.setOrientation(LinearLayout.VERTICAL);
            corpo.setGravity(Gravity.CENTER_HORIZONTAL);
            corpo.setPadding(dp(20), dp(22), dp(20), dp(18));
            corpo.setBackgroundColor(BRANCO);

            CropImageView visualizacao = new CropImageView(this);
            visualizacao.setImageBitmap(bitmap);

            LinearLayout.LayoutParams imagemParams =
                    new LinearLayout.LayoutParams(dp(240), dp(240));

            imagemParams.gravity = Gravity.CENTER;
            imagemParams.bottomMargin = dp(18);

            corpo.addView(visualizacao, imagemParams);

            TextView instrucao = texto(
                    "Use dois dedos para aproximar ou afastar a imagem. "
                            + "Com um dedo, arraste para ajustar a posição.",
                    13,
                    TEXTO_SECUNDARIO
            );

            instrucao.setGravity(Gravity.CENTER);
            instrucao.setPadding(0, 0, 0, dp(10));

            corpo.addView(instrucao);

            principal.addView(corpo);

            LinearLayout rodape = criarRodape();

            Button cancelar = criarBotao("Cancelar", false);
            Button salvar = criarBotao("Salvar foto", true);

            rodape.addView(
                    cancelar,
                    new LinearLayout.LayoutParams(0, dp(48), 1)
            );

            LinearLayout.LayoutParams salvarParams =
                    new LinearLayout.LayoutParams(0, dp(48), 1);

            salvarParams.leftMargin = dp(10);

            rodape.addView(salvar, salvarParams);

            principal.addView(rodape);

            AlertDialog dialogo = new AlertDialog.Builder(this)
                    .setView(principal)
                    .create();

            cancelar.setOnClickListener(v -> dialogo.dismiss());

            salvar.setOnClickListener(v -> {

                try {

                    Bitmap fotoAjustada =
                            visualizacao.gerarImagemFinal();

                    int usuarioId = SessaoUsuario.atual();

                    if (usuarioId <= 0) {
                        Toast.makeText(
                                this,
                                "Nenhuma conta conectada.",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    FotoPerfil.salvarBitmap(
                            this,
                            usuarioId,
                            fotoAjustada
                    );

                    carregarFotoPerfil();

                    Toast.makeText(
                            this,
                            "Foto de perfil atualizada!",
                            Toast.LENGTH_SHORT
                    ).show();

                    dialogo.dismiss();

                } catch (Exception erro) {

                    android.util.Log.e(
                            "BalanceTech",
                            "Erro ao salvar a foto.",
                            erro
                    );

                    Toast.makeText(
                            this,
                            "Não foi possível salvar a foto.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });

            dialogo.show();
            configurarJanela(dialogo);

        } catch (Exception erro) {

            android.util.Log.e(
                    "BalanceTech",
                    "Erro ao abrir a imagem.",
                    erro
            );

            Toast.makeText(
                    this,
                    "Não foi possível abrir essa imagem.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void carregarFotoPerfil() {

        int usuarioId = SessaoUsuario.atual();

        if (usuarioId <= 0) {
            return;
        }

        FotoPerfil.carregar(
                this,
                usuarioId,
                imgPerfil,
                R.drawable.ic_perfil_grande
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        carregarFotoPerfil();

        final int usuarioId = SessaoUsuario.atual();

        if (usuarioId <= 0) {
            return;
        }

        repositorio.executar(
                () -> repositorio.buscarPerfil(usuarioId),
                new NutricaoRepositorio.Resultado<PerfilCompleto>() {

                    @Override
                    public void sucesso(PerfilCompleto perfil) {

                        if (!isFinishing()
                                && !isDestroyed()
                                && SessaoUsuario.atual() == usuarioId) {

                            mostrarPerfil(perfil);
                        }
                    }

                    @Override
                    public void erro(Exception erro) {

                        android.util.Log.e(
                                "BalanceTech",
                                "Erro ao carregar o perfil",
                                erro
                        );

                        Toast.makeText(
                                PerfilActivity.this,
                                "Não foi possível carregar o perfil.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }

    private void mostrarPerfil(PerfilCompleto perfil) {

        perfilAtual = perfil;

        if (perfil == null) {
            return;
        }

        ((TextView) findViewById(R.id.tvNome))
                .setText(perfil.nome);

        ((TextView) findViewById(R.id.tvEmail))
                .setText(perfil.email);

        TextView dados = findViewById(R.id.tvDadosPerfil);

        if (perfil.metas == null) {
            dados.setText("");
            return;
        }

        NumberFormat inteiro =
                NumberFormat.getIntegerInstance(Alimentacao.PT_BR);

        dados.setText(
                perfil.genero + " · " + perfil.idade + " anos\n"
                        + Alimentacao.numero(perfil.peso) + " kg · "
                        + perfil.altura + " cm · IMC "
                        + String.format(
                        Alimentacao.PT_BR,
                        "%.1f",
                        perfil.imc
                ) + "\n"
                        + "Objetivo: " + perfil.objetivo + "\n"
                        + "Meta: "
                        + inteiro.format(Math.round(perfil.metas.kcal))
                        + " kcal por dia"
        );
    }

    private void abrirEdicao() {

        if (perfilAtual == null || perfilAtual.metas == null) {

            Toast.makeText(
                    this,
                    "Complete o questionário para editar o perfil.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        final int usuarioId = SessaoUsuario.atual();

        LinearLayout principal = criarPainelDialogo();

        principal.addView(
                criarCabecalho(
                        "Editar perfil",
                        "Atualize suas informações pessoais."
                )
        );

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(20), dp(16), dp(20), dp(20));
        form.setBackgroundColor(BRANCO);

        EditText nome = campo(
                form,
                "Nome completo",
                perfilAtual.nome,
                InputType.TYPE_CLASS_TEXT
        );

        EditText email = campo(
                form,
                "E-mail",
                perfilAtual.email,
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );

        EditText idade = campo(
                form,
                "Idade",
                String.valueOf(perfilAtual.idade),
                InputType.TYPE_CLASS_NUMBER
        );

        EditText peso = campo(
                form,
                "Peso (kg)",
                Alimentacao.numero(perfilAtual.peso).replace(',', '.'),
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        EditText altura = campo(
                form,
                "Altura (cm)",
                String.valueOf(perfilAtual.altura),
                InputType.TYPE_CLASS_NUMBER
        );

        Spinner genero = spinner(
                form,
                "Gênero",
                new String[]{"Homem", "Mulher"},
                perfilAtual.genero
        );

        Spinner objetivo = spinner(
                form,
                "Objetivo",
                CalculoNutricional.OBJETIVOS,
                perfilAtual.objetivo
        );

        Spinner exercicio = spinner(
                form,
                "Nível de atividade física",
                CalculoNutricional.NIVEIS,
                perfilAtual.exercicio
        );

        ScrollView rolagem = new ScrollView(this);
        rolagem.setFillViewport(false);
        rolagem.setBackgroundColor(BRANCO);
        rolagem.addView(form);

        principal.addView(
                rolagem,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(420)
                )
        );

        LinearLayout rodape = criarRodape();

        Button cancelar = criarBotao("Cancelar", false);
        Button salvar = criarBotao("Salvar alterações", true);

        rodape.addView(
                cancelar,
                new LinearLayout.LayoutParams(0, dp(48), 1)
        );

        LinearLayout.LayoutParams salvarParams =
                new LinearLayout.LayoutParams(0, dp(48), 1);

        salvarParams.leftMargin = dp(10);

        rodape.addView(salvar, salvarParams);

        principal.addView(rodape);

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setView(principal)
                .create();

        cancelar.setOnClickListener(v -> dialogo.dismiss());

        salvar.setOnClickListener(v -> {

            NutricaoRepositorio.DadosPerfil dados =
                    new NutricaoRepositorio.DadosPerfil();

            dados.nome = nome.getText().toString().trim();
            dados.email = email.getText().toString().trim();
            dados.genero = (String) genero.getSelectedItem();
            dados.objetivo = (String) objetivo.getSelectedItem();
            dados.nivelAtividade = exercicio.getSelectedItemPosition();

            if (dados.nome.isEmpty() || dados.email.isEmpty()) {

                Toast.makeText(
                        this,
                        "Preencha o nome e o e-mail.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            try {

                dados.idade = Integer.parseInt(
                        idade.getText().toString().trim()
                );

                dados.peso = Double.parseDouble(
                        peso.getText().toString()
                                .trim()
                                .replace(',', '.')
                );

                dados.altura = Integer.parseInt(
                        altura.getText().toString().trim()
                );

            } catch (NumberFormatException erro) {

                Toast.makeText(
                        this,
                        "Preencha idade, peso e altura com números.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            salvar.setEnabled(false);
            salvar.setText("Salvando...");

            repositorio.executar(
                    () -> repositorio.atualizarPerfil(
                            usuarioId,
                            dados
                    ),

                    new NutricaoRepositorio.Resultado<PerfilCompleto>() {

                        @Override
                        public void sucesso(PerfilCompleto atualizado) {

                            dialogo.dismiss();

                            if (!isFinishing() && !isDestroyed()) {

                                mostrarPerfil(atualizado);

                                Toast.makeText(
                                        PerfilActivity.this,
                                        "Perfil atualizado.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                        @Override
                        public void erro(Exception erro) {

                            salvar.setEnabled(true);
                            salvar.setText("Salvar alterações");

                            boolean daTela =
                                    erro instanceof
                                            NutricaoRepositorio.DadosInvalidosException;

                            if (!daTela) {
                                android.util.Log.e(
                                        "BalanceTech",
                                        "Erro ao salvar o perfil",
                                        erro
                                );
                            }

                            Toast.makeText(
                                    PerfilActivity.this,
                                    daTela
                                            ? erro.getMessage()
                                            : "Não foi possível salvar. Tente novamente.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );
        });

        dialogo.show();
        configurarJanela(dialogo);
    }

    private EditText campo(
            LinearLayout pai,
            String titulo,
            String valor,
            int tipo
    ) {

        TextView rotulo = texto(titulo, 13, TEXTO);
        rotulo.setTypeface(null, android.graphics.Typeface.BOLD);

        LinearLayout.LayoutParams rotuloParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        rotuloParams.topMargin = dp(12);
        rotuloParams.bottomMargin = dp(6);

        pai.addView(rotulo, rotuloParams);

        EditText campo = new EditText(this);

        campo.setSingleLine(true);
        campo.setText(valor);
        campo.setHint(titulo);
        campo.setTextSize(14);
        campo.setTextColor(TEXTO);
        campo.setHintTextColor(0xFF999999);
        campo.setInputType(tipo);
        campo.setPadding(dp(14), 0, dp(14), 0);
        campo.setBackground(fundoCampo());

        campo.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(50)
                )
        );

        pai.addView(campo);

        return campo;
    }

    private Spinner spinner(
            LinearLayout pai,
            String titulo,
            String[] opcoes,
            String atual
    ) {

        TextView rotulo = texto(titulo, 13, TEXTO);
        rotulo.setTypeface(null, android.graphics.Typeface.BOLD);

        LinearLayout.LayoutParams rotuloParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        rotuloParams.topMargin = dp(14);
        rotuloParams.bottomMargin = dp(6);

        pai.addView(rotulo, rotuloParams);

        Spinner spinner = new Spinner(this);

        GradientDrawable fundoSpinner = new GradientDrawable();
        fundoSpinner.setColor(FUNDO);
        fundoSpinner.setCornerRadius(dp(10));
        fundoSpinner.setStroke(dp(1), BORDA);

        spinner.setBackground(fundoSpinner);
        spinner.setPadding(dp(10), 0, dp(10), 0);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                opcoes
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);

        if (atual != null) {
            for (int i = 0; i < opcoes.length; i++) {
                if (opcoes[i].equalsIgnoreCase(atual)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }

        pai.addView(
                spinner,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(50)
                )
        );

        return spinner;
    }

    private LinearLayout criarPainelDialogo() {

        LinearLayout painel = new LinearLayout(this);
        painel.setOrientation(LinearLayout.VERTICAL);
        painel.setBackground(fundoPainel());
        painel.setClipToOutline(true);

        return painel;
    }

    private View criarCabecalho(
            String titulo,
            String subtitulo
    ) {

        LinearLayout cabecalho = new LinearLayout(this);
        cabecalho.setOrientation(LinearLayout.VERTICAL);
        cabecalho.setPadding(dp(22), dp(20), dp(22), dp(20));
        cabecalho.setBackgroundColor(VERDE);

        TextView textoTitulo = texto(titulo, 20, BRANCO);
        textoTitulo.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView textoSubtitulo = texto(subtitulo, 12, BRANCO);
        textoSubtitulo.setAlpha(0.9f);

        LinearLayout.LayoutParams subtituloParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        subtituloParams.topMargin = dp(5);

        cabecalho.addView(textoTitulo);
        cabecalho.addView(textoSubtitulo, subtituloParams);

        return cabecalho;
    }

    private LinearLayout criarRodape() {

        LinearLayout rodape = new LinearLayout(this);
        rodape.setOrientation(LinearLayout.HORIZONTAL);
        rodape.setPadding(dp(18), dp(14), dp(18), dp(18));
        rodape.setBackgroundColor(FUNDO);

        return rodape;
    }

    private Button criarBotao(
            String titulo,
            boolean principal
    ) {

        Button botao = new Button(this);

        botao.setText(titulo);
        botao.setAllCaps(false);
        botao.setTextSize(13);
        botao.setTypeface(null, android.graphics.Typeface.BOLD);
        botao.setGravity(Gravity.CENTER);
        botao.setPadding(dp(8), 0, dp(8), 0);
        botao.setMinHeight(0);
        botao.setMinimumHeight(0);
        botao.setStateListAnimator(null);

        GradientDrawable fundoBotao = new GradientDrawable();
        fundoBotao.setCornerRadius(dp(12));

        if (principal) {
            fundoBotao.setColor(VERDE);
            botao.setTextColor(BRANCO);
        } else {
            fundoBotao.setColor(BRANCO);
            fundoBotao.setStroke(dp(1), BORDA);
            botao.setTextColor(TEXTO);
        }

        botao.setBackground(fundoBotao);

        return botao;
    }

    private TextView texto(
            String conteudo,
            float tamanho,
            int cor
    ) {

        TextView texto = new TextView(this);

        texto.setText(conteudo);
        texto.setTextSize(tamanho);
        texto.setTextColor(cor);
        texto.setGravity(Gravity.CENTER_VERTICAL);

        return texto;
    }

    private GradientDrawable fundoCampo() {

        GradientDrawable fundo = new GradientDrawable();
        fundo.setColor(FUNDO);
        fundo.setCornerRadius(dp(10));
        fundo.setStroke(dp(1), BORDA);

        return fundo;
    }

    private GradientDrawable fundoPainel() {

        GradientDrawable fundo = new GradientDrawable();
        fundo.setColor(BRANCO);
        fundo.setCornerRadius(dp(20));

        return fundo;
    }

    private void configurarJanela(AlertDialog dialogo) {

        if (dialogo.getWindow() != null) {

            dialogo.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            int largura = (int) (
                    getResources().getDisplayMetrics().widthPixels * 0.92f
            );

            dialogo.getWindow().setLayout(
                    largura,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            dialogo.getWindow().setDimAmount(0.45f);
        }
    }

    private int dp(float valor) {

        return (int) (
                valor * getResources().getDisplayMetrics().density + 0.5f
        );
    }

    // Classe responsável pelo arraste e zoom com dois dedos.
    private static class CropImageView extends androidx.appcompat.widget.AppCompatImageView {

        private Bitmap bitmap;

        private final Matrix matriz = new Matrix();

        private float zoom = 1f;

        private float deslocamentoX = 0f;
        private float deslocamentoY = 0f;

        private float toqueX;
        private float toqueY;

        private boolean arrastando = false;

        private final android.graphics.Paint pincel =
                new android.graphics.Paint(
                        android.graphics.Paint.ANTI_ALIAS_FLAG |
                                android.graphics.Paint.FILTER_BITMAP_FLAG
                );

        private final ScaleGestureDetector detectorZoom;

        public CropImageView(android.content.Context context) {
            super(context);

            setScaleType(ScaleType.MATRIX);
            setBackgroundColor(0xFFE8EEE8);
            setClickable(true);

            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(
                            0,
                            0,
                            view.getWidth(),
                            view.getHeight()
                    );
                }
            });

            setClipToOutline(true);

            detectorZoom = new ScaleGestureDetector(
                    context,
                    new ScaleGestureDetector.SimpleOnScaleGestureListener() {

                        @Override
                        public boolean onScale(
                                ScaleGestureDetector detector
                        ) {

                            float zoomAnterior = zoom;

                            zoom *= detector.getScaleFactor();

                            zoom = Math.max(1f, Math.min(4f, zoom));

                            float fatorReal = zoom / zoomAnterior;

                            float centroX = detector.getFocusX();
                            float centroY = detector.getFocusY();

                            deslocamentoX =
                                    centroX -
                                            (centroX - deslocamentoX)
                                                    * fatorReal;

                            deslocamentoY =
                                    centroY -
                                            (centroY - deslocamentoY)
                                                    * fatorReal;

                            atualizarMatriz();

                            return true;
                        }
                    }
            );

            detectorZoom.setQuickScaleEnabled(false);
        }

        @Override
        public void setImageBitmap(Bitmap imagem) {
            bitmap = imagem;
            super.setImageBitmap(imagem);
            atualizarMatriz();
        }

        @Override
        protected void onSizeChanged(
                int w,
                int h,
                int oldw,
                int oldh
        ) {
            super.onSizeChanged(w, h, oldw, oldh);
            atualizarMatriz();
        }

        private void atualizarMatriz() {

            if (bitmap == null || getWidth() == 0 || getHeight() == 0) {
                return;
            }

            float largura = getWidth();
            float altura = getHeight();

            float escalaBase = Math.max(
                    largura / bitmap.getWidth(),
                    altura / bitmap.getHeight()
            );

            float escala = escalaBase * zoom;

            float larguraImagem = bitmap.getWidth() * escala;
            float alturaImagem = bitmap.getHeight() * escala;

            float minimoX = largura - larguraImagem;
            float minimoY = altura - alturaImagem;

            float x = (largura - larguraImagem) / 2f
                    + deslocamentoX;

            float y = (altura - alturaImagem) / 2f
                    + deslocamentoY;

            x = Math.max(minimoX, Math.min(0f, x));
            y = Math.max(minimoY, Math.min(0f, y));

            deslocamentoX =
                    x - (largura - larguraImagem) / 2f;

            deslocamentoY =
                    y - (altura - alturaImagem) / 2f;

            matriz.reset();
            matriz.setScale(escala, escala);
            matriz.postTranslate(x, y);

            setImageMatrix(matriz);
            invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent evento) {

            detectorZoom.onTouchEvent(evento);

            switch (evento.getActionMasked()) {

                case MotionEvent.ACTION_DOWN:

                    toqueX = evento.getX();
                    toqueY = evento.getY();

                    arrastando = true;

                    return true;

                case MotionEvent.ACTION_POINTER_DOWN:

                    arrastando = false;

                    return true;

                case MotionEvent.ACTION_MOVE:

                    if (evento.getPointerCount() == 1
                            && !detectorZoom.isInProgress()
                            && arrastando) {

                        float dx = evento.getX() - toqueX;
                        float dy = evento.getY() - toqueY;

                        deslocamentoX += dx;
                        deslocamentoY += dy;

                        toqueX = evento.getX();
                        toqueY = evento.getY();

                        atualizarMatriz();
                    }

                    return true;

                case MotionEvent.ACTION_POINTER_UP:

                    arrastando = false;

                    return true;

                case MotionEvent.ACTION_UP:

                    arrastando = false;

                    performClick();

                    return true;

                case MotionEvent.ACTION_CANCEL:

                    arrastando = false;

                    return true;
            }

            return true;
        }

        @Override
        public boolean performClick() {
            super.performClick();
            return true;
        }

        public Bitmap gerarImagemFinal() {

            if (bitmap == null || getWidth() == 0 || getHeight() == 0) {
                throw new IllegalStateException("Imagem não carregada.");
            }

            int tamanhoFinal = 512;

            Bitmap resultado = Bitmap.createBitmap(
                    tamanhoFinal,
                    tamanhoFinal,
                    Bitmap.Config.ARGB_8888
            );

            Canvas canvas = new Canvas(resultado);

            float escalaSaida =
                    tamanhoFinal / (float) getWidth();

            canvas.scale(escalaSaida, escalaSaida);

            canvas.drawBitmap(bitmap, matriz, pincel);

            return resultado;
        }
    }
}
