
package com.example.balancetech_tcc_equipe05.activites;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Outline;
import android.net.Uri;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

public class FotoPerfil {

    private static final String PREFS = "fotos_perfil";
    private static final String CHAVE_PREFIXO = "foto_usuario_";

    private FotoPerfil() {
    }

    // Mantém a possibilidade de salvar uma URI original.
    public static void salvar(Context context, int usuarioId, Uri uri) {
        SharedPreferences preferencias =
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        preferencias.edit()
                .putString(CHAVE_PREFIXO + usuarioId, uri.toString())
                .apply();
    }

    // Salva a imagem já ajustada em um arquivo interno do aplicativo.
    public static void salvarBitmap(Context context, int usuarioId, Bitmap bitmap)
            throws Exception {

        String nomeArquivo = "foto_perfil_" + usuarioId + ".jpg";

        File arquivo = new File(context.getFilesDir(), nomeArquivo);

        try (FileOutputStream saida = new FileOutputStream(arquivo)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, saida);
            saida.flush();
        }

        Uri uri = Uri.fromFile(arquivo);

        SharedPreferences preferencias =
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        preferencias.edit()
                .putString(CHAVE_PREFIXO + usuarioId, uri.toString())
                .apply();
    }

    // Recupera a URI da foto da conta.
    public static Uri obter(Context context, int usuarioId) {

        SharedPreferences preferencias =
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        String endereco = preferencias.getString(
                CHAVE_PREFIXO + usuarioId,
                null
        );

        if (endereco == null) {
            return null;
        }

        try {
            return Uri.parse(endereco);
        } catch (Exception erro) {
            return null;
        }
    }

    // Aplica o formato circular à ImageView.
    public static void aplicarFormatoRedondo(ImageView imagem) {

        imagem.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imagem.setOutlineProvider(new ViewOutlineProvider() {
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

        imagem.setClipToOutline(true);
    }

    // Exibe a foto salva ou a imagem padrão.
    public static void carregar(
            Context context,
            int usuarioId,
            ImageView imagem,
            int imagemPadrao
    ) {

        aplicarFormatoRedondo(imagem);

        Uri foto = obter(context, usuarioId);

        if (foto != null) {
            try {
                if ("file".equalsIgnoreCase(foto.getScheme())) {

                    Bitmap bitmap = BitmapFactory.decodeFile(foto.getPath());

                    if (bitmap != null) {
                        imagem.setImageBitmap(bitmap);
                        return;
                    }

                } else {

                    imagem.setImageURI(foto);

                    if (imagem.getDrawable() != null) {
                        return;
                    }
                }

            } catch (Exception erro) {
                android.util.Log.e(
                        "BalanceTech",
                        "Não foi possível carregar a foto.",
                        erro
                );
            }
        }

        imagem.setImageResource(imagemPadrao);
    }
}
