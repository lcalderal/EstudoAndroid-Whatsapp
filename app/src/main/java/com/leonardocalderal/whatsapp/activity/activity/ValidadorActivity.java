package com.leonardocalderal.whatsapp.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.leonardocalderal.whatsapp.R;
import com.leonardocalderal.whatsapp.helper.Preferencias;

import java.util.HashMap;

public class ValidadorActivity extends AppCompatActivity {

    private EditText codigoValidacao;
    private Button botaoValidar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validador);

        codigoValidacao = (EditText) findViewById(R.id.editCodValidacao);
        botaoValidar    = (Button) findViewById(R.id.btnValidar);

        SimpleMaskFormatter simpleMaskFormatter = new SimpleMaskFormatter("NNNN");
        MaskTextWatcher mascaraCodigoValidacao = new MaskTextWatcher(codigoValidacao, simpleMaskFormatter);

        codigoValidacao.addTextChangedListener(mascaraCodigoValidacao);
        botaoValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Recuperar dados das preferências do usuário
                Preferencias preferencias = new Preferencias(ValidadorActivity.this);
                HashMap<String, String> usuario = preferencias.getDadosUsuario();

                //recuperando o token enviado por SMS
                String tokenGerado = usuario.get("token");
                //recuperando o token digitado
                String tokenDigitado = codigoValidacao.getText().toString();

                if (tokenDigitado.equals(tokenGerado)){

                    Toast.makeText(ValidadorActivity.this, "Token Validado!", Toast.LENGTH_LONG).show();

                }else {
                    Toast.makeText(ValidadorActivity.this, "Token NÂO Validado!", Toast.LENGTH_LONG).show();
                }

            }
        });


    }
}
