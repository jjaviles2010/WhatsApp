package br.com.whatsappandroid.cursoandroid.whatsapp.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.HashMap;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;

public class ValidadorActivity extends AppCompatActivity {

    private EditText txtCode;
    private Button btnValidar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validador);

        txtCode = (EditText)findViewById(R.id.txtCode);
        btnValidar = (Button)findViewById(R.id.btnValidar);

        SimpleMaskFormatter simpleMaskCode = new SimpleMaskFormatter("NNNN");
        MaskTextWatcher maskCode = new MaskTextWatcher(txtCode,simpleMaskCode);

        txtCode.addTextChangedListener(maskCode);

       /* btnValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Recuperar Dados de preferencia de usuarios
                Preferencias preferencias = new Preferencias(ValidadorActivity.this);
                //HashMap<String, String> usuario = preferencias.getDadosUsuarios();
                String tokenGerado = usuario.get("token");
                String tokenDigitado = txtCode.getText().toString();

                if(tokenDigitado.equals( tokenGerado )){
                    Toast.makeText(ValidadorActivity.this, "token Validado", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(ValidadorActivity.this, "token nao Validado", Toast.LENGTH_LONG).show();
                }
            }
        });*/
    }
}
