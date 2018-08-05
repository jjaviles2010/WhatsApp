package br.com.whatsappandroid.cursoandroid.whatsapp.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Preferencias {
    private Context context;
    private SharedPreferences preferences;
    private final String NOME_ARQUIVO = "whatsapp.preferencias";
    private final String CHAVE_IDENTIFICADOR = "identificadorUsuarioLogado";
    private final String CHAVE_NOME = "nomeUsuarioLogado";
   /* private final String CHAVE_TELEFONE = "telefone";
    private final String CHAVE_TOKEN = "token";*/

    private SharedPreferences.Editor editor;


    public Preferencias(Context contextoParam){
        context = contextoParam;
        preferences = context.getSharedPreferences(NOME_ARQUIVO,Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void salvarDados(String identificadorUsuario, String nomeUsuario){

        editor.putString(CHAVE_IDENTIFICADOR,identificadorUsuario);
        editor.putString(CHAVE_NOME,nomeUsuario);

        editor.commit();
    }

    public String getIdentificador(){
        return preferences.getString(CHAVE_IDENTIFICADOR,null);
    }

    public String getNome(){
        return preferences.getString(CHAVE_NOME,null);
    }

    /*public HashMap<String, String> getDadosUsuarios(){

        HashMap<String, String> dadosUsuarios = new HashMap<>();
        dadosUsuarios.put(CHAVE_NOME, preferences.getString(CHAVE_NOME, null));
        dadosUsuarios.put(CHAVE_TELEFONE, preferences.getString(CHAVE_TELEFONE,null));
        dadosUsuarios.put(CHAVE_TOKEN, preferences.getString(CHAVE_TOKEN,null));

        return  dadosUsuarios;
    }*/
}
