package br.com.whatsappandroid.cursoandroid.whatsapp.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.adapter.MensagemAdapter;
import br.com.whatsappandroid.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Base64Custom;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Conversa;
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Mensagem;

public class ConversaActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText editMensagem;
    private ImageButton btn_send;
    private DatabaseReference firebase;
    private ListView listView;
    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;
    private ValueEventListener valueEventListenerMensagem;
    //dados do destinatario
    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    //dados do remetente
    private String idUsuarioRemetente;
    private String nomeUsuarioRemetente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = (Toolbar) findViewById(R.id.tb_conversa);
        editMensagem = (EditText) findViewById(R.id.edit_mensagem);
        btn_send = (ImageButton) findViewById(R.id.btn_enviar);
        listView = (ListView) findViewById(R.id.lv_conversas);

        //dados do Usuario logado
        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        idUsuarioRemetente = preferencias.getIdentificador();
        nomeUsuarioRemetente = preferencias.getNome();

        Bundle extra = getIntent().getExtras();

        if(extra != null){
            nomeUsuarioDestinatario = extra.getString("nome");
            String emailDestinatario = extra.getString("email");
            idUsuarioDestinatario = Base64Custom.codificarBase64(emailDestinatario);
        }

        //Configurar toolbar
        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        //Montar a list View das mensagens
        mensagens = new ArrayList<>();
       /* adapter = new ArrayAdapter(
                ConversaActivity.this,
                android.R.layout.simple_list_item_1,
                mensagens);*/
       adapter = new MensagemAdapter(ConversaActivity.this,mensagens);
        listView.setAdapter(adapter);

        //Recuperar mensagens do firebase
        firebase = ConfiguracaoFirebase.getFirebase()
                    .child("mensagens")
                    .child(idUsuarioRemetente)
                    .child(idUsuarioDestinatario);

        //Criar listener para mensagens
        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Limpar mensagens
                mensagens.clear();

                //Recupera mensagens
                for (DataSnapshot dados: dataSnapshot.getChildren()) {
                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    mensagens.add(mensagem);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenerMensagem);

        //Enviar mensagem
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoMensagem = editMensagem.getText().toString();
                if(textoMensagem.isEmpty()){
                    Toast.makeText(ConversaActivity.this,"Digite uma mensagem para enviar", Toast.LENGTH_LONG).show();
                }else {
                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario( idUsuarioRemetente );
                    mensagem.setMensagem( textoMensagem );

                    //salvar mensagem para o remetente
                    boolean retornoMensagemRemetente = salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                    if(!retornoMensagemRemetente){
                        Toast.makeText(ConversaActivity.this,"Problema ao salvar mensagem, tente novamente!",Toast.LENGTH_LONG).show();
                    }else {

                        //salvar mensagem pra o destinatario
                        boolean retornoMensagemDestinatario = salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
                        if(!retornoMensagemDestinatario) {
                            Toast.makeText(ConversaActivity.this, "Problema ao salvar mensagem, tente novamente!", Toast.LENGTH_LONG).show();
                        }

                    }

                    //Salvar conversa pra o remetente
                    Conversa conversa = new Conversa();
                    conversa.setIdUsuario(idUsuarioDestinatario);
                    conversa.setNome(nomeUsuarioDestinatario);
                    conversa.setMensagem(mensagem.getMensagem());
                    boolean retornoConversaRemetente = salvarConversa(idUsuarioRemetente,idUsuarioDestinatario,conversa);

                    if(!retornoConversaRemetente){
                        Toast.makeText(ConversaActivity.this,"Problema ao salvar a conversa, tente novamente!",Toast.LENGTH_LONG).show();
                    }else {

                        //Salvar conversa pra o destinatario
                        conversa.setIdUsuario(idUsuarioRemetente);
                        conversa.setNome(nomeUsuarioRemetente);
                        conversa.setMensagem(mensagem.getMensagem());
                        boolean retornoConversaDestinatario = salvarConversa(idUsuarioDestinatario,idUsuarioRemetente,conversa);
                        if(!retornoConversaDestinatario) {
                            Toast.makeText(ConversaActivity.this, "Problema ao salvar a conversa, tente novamente!", Toast.LENGTH_LONG).show();
                        }
                    }



                    editMensagem.setText("");
                }
            }
        });
    }

    private boolean salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem){
        try {
            firebase = ConfiguracaoFirebase.getFirebase().child("mensagens");
            firebase.child(idRemetente).child(idDestinatario).push().setValue(mensagem);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean salvarConversa(String idRemetente, String idDestinatario, Conversa conversa){
        try {
            firebase = ConfiguracaoFirebase.getFirebase().child("conversas");
            firebase.child(idRemetente).child(idDestinatario).setValue(conversa);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerMensagem);
    }
}
