package com.leonardocalderal.whatsapp.activity.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.leonardocalderal.whatsapp.R;
import com.leonardocalderal.whatsapp.helper.Permissao;
import com.leonardocalderal.whatsapp.helper.Preferencias;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private EditText telefone;
    private EditText nome;
    private EditText codArea;
    private EditText codPais;
    private Button botaoCadastrar;
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Permissao.validaPermissoes( 1,this, permissoesNecessarias);

        nome = (EditText) findViewById(R.id.editNome);
        telefone = (EditText) findViewById(R.id.editTelefone);
        codArea = (EditText) findViewById(R.id.editCodArea);
        codPais = (EditText) findViewById(R.id.editCodPais);
        botaoCadastrar = (Button) findViewById(R.id.btnCadastrarId);

        //primeiro foi no gradle do app e coloquei um implementation no, agora estou utilizando o MaskFormatter que importei do git

        //Definir mascaras
        SimpleMaskFormatter simpleMaskTelefone = new SimpleMaskFormatter("NNNNN-NNNN");
        SimpleMaskFormatter simpleMaskCodPais = new SimpleMaskFormatter("+NN");
        SimpleMaskFormatter simpleMaskCodArea = new SimpleMaskFormatter("NN");


        MaskTextWatcher maskTelefone = new MaskTextWatcher(telefone, simpleMaskTelefone);
        MaskTextWatcher codAreaMask = new MaskTextWatcher(codArea, simpleMaskCodArea);
        MaskTextWatcher codPaisMask = new MaskTextWatcher(codPais, simpleMaskCodPais);

        telefone.addTextChangedListener(maskTelefone);
        codArea.addTextChangedListener(codAreaMask);
        codPais.addTextChangedListener(codPaisMask);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeUsuario = nome.getText().toString();
                String numTelefone = codPais.getText().toString() +
                        codArea.getText().toString() +
                        telefone.getText().toString();

                //fazendo os replaces, tirando o + e o - substituindo por vazio
                String telefoneSemFormat = numTelefone.replace("+", "");
                telefoneSemFormat = telefoneSemFormat.replace("=", "");


                //Gerar token
                Random randomico = new Random();
                int numeroRandomico = randomico.nextInt(9999 - 1000) + 1000;
                String token = String.valueOf(numeroRandomico);
                String mensagemEnvio = "BatChat - Código de Confirmação: " + token;


                //Salvar os dados para validação
                Preferencias preferencias = new Preferencias(LoginActivity.this);
                preferencias.salvarUsuarioPreferencias(nomeUsuario, telefoneSemFormat, token);

                //Envio do SMS
                //------------- Em caso de usar o telefone do EMULADOR TERÁ QUE ALTERAR A VARIÁVEL DA MANEIRA ABAIXO -------------------------
                telefoneSemFormat = "5554";
                boolean enviadoSMS = enviaSMS("+" + telefoneSemFormat, mensagemEnvio);

                //mudando de activity caso a mensagem de SMS seja enviada
                if (enviadoSMS == true){

                    Intent intent = new Intent(LoginActivity.this, ValidadorActivity.class);
                    startActivity(intent);
                    finish();

                }else if (enviadoSMS == false){
                    Toast.makeText(LoginActivity.this, "Problema ao enviar SMS, tente novamente!", Toast.LENGTH_LONG).show();
                }


                /*
                HashMap<String, String> usuario = preferencias.getDadosUsuario();
                Log.i("TOKEN" + "T:", usuario.get("token"));
                */

            }
        });


    }

    private boolean enviaSMS(String telefone, String mensagem) {

        try {

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(telefone, null, mensagem, null, null);

            return true;

        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

    }


    //Função necessária quando o usuário nega alguma permissão
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //percorrer o array de grantResults pra saber se o usuário negou a permissão
        for(int resultado : grantResults){

            if (resultado == PackageManager.PERMISSION_DENIED){

                alertaValidacaoPermissao();

            }

        }


    }

    //Função necessária quando o usuário nega alguma permissão
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
}
