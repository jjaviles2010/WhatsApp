package br.com.whatsappandroid.cursoandroid.whatsapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Base64Custom;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Usuario;

public class CadastroUsuarioActivity extends AppCompatActivity {
    private EditText txtNome, txtEmail, txtSenha;
    private Button btnCadastar;
    private FirebaseAuth autenticao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        txtNome = (EditText)findViewById(R.id.txtNome);
        txtEmail = (EditText)findViewById(R.id.txtEmail);
        txtSenha = (EditText)findViewById(R.id.txtSenha);
        btnCadastar = (Button)findViewById(R.id.btnCadastro);

        btnCadastar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Usuario usuario = new Usuario();
                usuario.setNome(txtNome.getText().toString());
                usuario.setEmail(txtEmail.getText().toString());
                usuario.setSenha(txtSenha.getText().toString());

                cadastrarUsuario(usuario);
            }
        });
    }

    private void cadastrarUsuario(final Usuario usuario){

        autenticao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticao.createUserWithEmailAndPassword(
          usuario.getEmail(),
          usuario.getSenha()
        ).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CadastroUsuarioActivity.this,"Sucesso ao cadastrar usuario",Toast.LENGTH_LONG).show();
                    String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setId(idUsuario);
                    usuario.salvar();



                    Preferencias preferencias = new Preferencias(CadastroUsuarioActivity.this);
                    preferencias.salvarDados( idUsuario, usuario.getNome() );



                    abrirLoginUsuario();
                }else{

                    String erroExcecao = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erroExcecao = "Digite uma senha mais forte, contendo mais caracteres e com letras e numeros!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "O e-mail digitado e invalido, digite um novo email!";
                    } catch (FirebaseAuthUserCollisionException e){
                        erroExcecao = "Esse e-mail ja esta em uso no App!";
                    } catch (Exception e) {
                        erroExcecao = "Erro ao efetuar o cadastro!";
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroUsuarioActivity.this,"Erro: " + erroExcecao,Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void abrirLoginUsuario() {
        Intent intent = new Intent(CadastroUsuarioActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
