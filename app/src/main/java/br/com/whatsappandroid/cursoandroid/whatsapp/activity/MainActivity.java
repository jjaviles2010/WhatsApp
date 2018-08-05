package br.com.whatsappandroid.cursoandroid.whatsapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.adapter.TabAdapter;
import br.com.whatsappandroid.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Base64Custom;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.SlidingTabLayout;
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Contato;
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Usuario;

public class MainActivity extends AppCompatActivity {
    private Button btnSair;
    private FirebaseAuth autenticacao;
    private Toolbar toolbar;

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private String identificadorContato;
    private DatabaseReference firebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

        slidingTabLayout = (SlidingTabLayout)findViewById(R.id.stl_tabs);
        viewPager = (ViewPager)findViewById(R.id.vp_pagina);

        //Configurar sliding Tabs
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this,R.color.colorAccent));

        //Configurar adapter
        TabAdapter tabAdapter = new TabAdapter( getSupportFragmentManager() );
        viewPager.setAdapter(tabAdapter);

        slidingTabLayout.setViewPager( viewPager );


      /*  btnSair = (Button)findViewById(R.id.btnSair);

        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.signOut();

                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.item_sair:
                deslogarUsuario();
                return true;
            case R.id.item_configuracoes:
                return true;
            case R.id.item_adicionar:
                abrirCadastroContato();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void abrirCadastroContato(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        //Configuracao do Dialog
        alertDialog.setTitle("Novo contato");
        alertDialog.setMessage("E-mail do usuario");
        alertDialog.setCancelable(false);

        final EditText editText = new EditText(MainActivity.this);
        alertDialog.setView( editText );

        alertDialog.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String emailContato = editText.getText().toString();
                if(emailContato.isEmpty()){
                    Toast.makeText(MainActivity.this,"Preenche o email!",Toast.LENGTH_LONG).show();
                }else {
                    //Verificar se o usuario ja esta cadastrado
                    identificadorContato = Base64Custom.codificarBase64(emailContato);
                    firebase = ConfiguracaoFirebase.getFirebase();
                    firebase = firebase.child("usuarios").child(identificadorContato);

                    firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.getValue() != null){
                                //Recuperar dados do contato a ser adicionado
                                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                                Preferencias preferencias = new Preferencias(MainActivity.this);
                                String idUsuarioLogado = preferencias.getIdentificador();

                                firebase = ConfiguracaoFirebase.getFirebase();
                                firebase = firebase.child("contatos").child(idUsuarioLogado).child(identificadorContato);

                                Contato contato = new Contato();
                                contato.setIdentificadoUsuario( identificadorContato );
                                contato.setEmail( usuario.getEmail() );
                                contato.setNome( usuario.getNome() );

                                firebase.setValue(contato);

                            }else{
                                Toast.makeText(MainActivity.this, "Usuario nao possui cadastro.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.create();
        alertDialog.show();

    }


    private void deslogarUsuario(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signOut();

        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
