package br.com.whatsappandroid.cursoandroid.whatsapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Random;

import br.com.whatsappandroid.cursoandroid.whatsapp.Manifest;
import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Base64Custom;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Permissao;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumber, txtDDD, txtDDI, txtNome, txtEmail, txtSenha;
    private Button btnCadastrar, btnLogar;
    private String[] permissoesNecessarias = new String[]{
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.INTERNET
    };
    private Usuario usuario;
    private FirebaseAuth autenticacao;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerUsuario;
    private String identificadorUsuarioLogado;

    private DatabaseReference referenciaFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = (EditText)findViewById(R.id.txtEmail);
        txtSenha = (EditText)findViewById(R.id.txtSenha);
        btnLogar = (Button)findViewById(R.id.btnLogar);

        verificarUsuarioLogado();

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario = new Usuario();
                usuario.setEmail(txtEmail.getText().toString());
                usuario.setSenha(txtSenha.getText().toString());
                validarLogin();
            }
        });
    }

    private void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if(autenticacao.getCurrentUser() != null){
            abrirTelaPrincipal();
        }
    }

    private void validarLogin(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    identificadorUsuarioLogado = Base64Custom.codificarBase64(usuario.getEmail());

                    firebase = ConfiguracaoFirebase.getFirebase()
                            .child("usuarios")
                            .child(identificadorUsuarioLogado);

                    valueEventListenerUsuario = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Usuario usuario = dataSnapshot.getValue(Usuario.class);

                            Preferencias preferencias = new Preferencias(LoginActivity.this);
                            preferencias.salvarDados( identificadorUsuarioLogado,usuario.getNome() );

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };

                    firebase.addListenerForSingleValueEvent(valueEventListenerUsuario);


                    Toast.makeText(LoginActivity.this,"Sucesso ao fazer login!",Toast.LENGTH_LONG).show();
                    abrirTelaPrincipal();
                }else {
                    Toast.makeText(LoginActivity.this,"Erro ao fazer login!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void abrirTelaPrincipal(){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void abrirCadastroUsuario(View view){

        Intent intent = new Intent(LoginActivity.this,CadastroUsuarioActivity.class);
        startActivity(intent);

    }

 /*   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Permissao.validaPermissoes(this,permissoesNecessarias);

        phoneNumber = (EditText)findViewById(R.id.txtPhone);
        txtDDD = (EditText)findViewById(R.id.txtDDD);
        txtDDI = (EditText)findViewById(R.id.txtDDI);
        btnCadastrar = (Button)findViewById(R.id.btnCadastrar);
        txtNome = (EditText)findViewById(R.id.txtNome);

        SimpleMaskFormatter simpleMaskTelefone = new SimpleMaskFormatter("NNNNN-NNNN");
        MaskTextWatcher maskTelefone = new MaskTextWatcher(phoneNumber,simpleMaskTelefone);

        SimpleMaskFormatter simpleMaskDDD = new SimpleMaskFormatter("NN");
        MaskTextWatcher maskDDD = new MaskTextWatcher(txtDDD,simpleMaskDDD);

        SimpleMaskFormatter simpleMaskDDI = new SimpleMaskFormatter("+NN");
        MaskTextWatcher maskDDI = new MaskTextWatcher(txtDDI,simpleMaskDDI);

        phoneNumber.addTextChangedListener( maskTelefone );
        txtDDD.addTextChangedListener(maskDDD);
        txtDDI.addTextChangedListener(maskDDI);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeUsuario = txtNome.getText().toString();
                String telefoneCompleto =
                        txtDDI.getText().toString() +
                        txtDDD.getText().toString() +
                        phoneNumber.getText();
                String telefoneSemFormatacao = telefoneCompleto.replace("+","");
                telefoneSemFormatacao = telefoneSemFormatacao.replace("-","");

                //Gerar Token
                Random randomico = new Random();
                int numeroRandomico = randomico.nextInt(9999-1000) + 1000;

                String token = String.valueOf(numeroRandomico);
                String mensagemEnvio = "Whatsapp codigo de confirmacao: " + token;

                //Salvar os dados para validacao
                Preferencias preferencias = new Preferencias(getApplicationContext());
                preferencias.salvarUsuarioPreferencias(nomeUsuario,telefoneSemFormatacao,token);

                //Envio do SMS
                telefoneSemFormatacao = "5554";
                boolean enviadoSMS = enviaSMS("+"+telefoneSemFormatacao,mensagemEnvio);

                if(enviadoSMS){
                    Intent intent = new Intent(LoginActivity.this, ValidadorActivity.class );
                    startActivity(intent);
                    finish();

                }else {
                    Toast.makeText(LoginActivity.this, "Problema ao enviar o SMS", Toast.LENGTH_LONG);
                }

                HashMap<String,String> usuario = preferencias.getDadosUsuarios();
                Log.i("TOKEN", "T:" + usuario.get("token"));


            }
        });
    }


    private boolean enviaSMS(String telefone, String mensagem){

        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(telefone,null, mensagem, null,null);

            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public void onRequestPermissionsResult(int RequestCode, String[] permissions, int[] grantResults){

        super.onRequestPermissionsResult(RequestCode,permissions,grantResults);

        for(int resultado : grantResults){
            if(resultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar esse app, é necessário aceitar as permissões");

        builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
*/

}
