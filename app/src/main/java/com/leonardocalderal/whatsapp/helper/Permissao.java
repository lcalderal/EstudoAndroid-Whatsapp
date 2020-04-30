package com.leonardocalderal.whatsapp.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {

    public static boolean validaPermissoes(int requestCode, Activity activity, String[] permissoes){

        if (Build.VERSION.SDK_INT >= 23){

            //criando uma Lista de permissões para guardar as permissões que o usuário NEGOU
            List<String> listaPermissoes = new ArrayList<String>();

/*            Percorre as permissoes passadas, verificando uma a uma se já tem
              permissao liberada*/
            for (String permissao : permissoes){

                //Verifica se a Activity tem a permissão necessária e se ela é igual ao nível do pacote instalado
                boolean validaPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;

                //Verifica qual permissão o usuário negou para salvar em uma lista
                if (!validaPermissao){

                    //adiciona as permissões negadas na lista
                    listaPermissoes.add(permissao);

                }

                //verifica se a lista de permissão está vazia, então segue normalmente, pois todas as permissões foram aceitas
                if (listaPermissoes.isEmpty()){
                    return true;
                }

                //Para solicitar permissão precisa converter a lista em um array, para isso estou criando um array e atribuindo a lista a ele
                String[] novasPermissoes = new String[listaPermissoes.size()];
                listaPermissoes.toArray(novasPermissoes);

                //SOLICITAR PERMISSÃO
                ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode);

            }

        }

        return true;

    }

}
