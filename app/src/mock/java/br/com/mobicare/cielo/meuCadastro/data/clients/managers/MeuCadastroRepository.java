package br.com.mobicare.cielo.meuCadastro.data.clients.managers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import br.com.mobicare.cielo.commons.utils.ReaderMock;
import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrandFees;
import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroEndereco;
import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroObj;
import br.com.mobicare.cielo.meuCadastro.presetantion.ui.MeuCadastroCallback;

/**
 * Created by silvia.miranda on 25/04/2017.
 */

public class MeuCadastroRepository {
    private Context context;

    public static MeuCadastroRepository getInstance(@NonNull Context context) {
        return new MeuCadastroRepository(context);
    }

    public MeuCadastroRepository(@NonNull Context context) {
        this.context = context;
    }

    public void getMeuCadastro(final MeuCadastroCallback callback){
        callback.onStart();

        //Espera 3 segundos para enviar a resposta de sucesso
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run() {

                MeuCadastroObj obj = ReaderMock.Companion.getMeuCadastro(context);
//                MeuCadastroObj obj = ReaderMock.Companion.getMeuCadastroUnico(context);
                callback.onSuccess(obj);

                MeuCadastroEndereco endFisico = obj.getEndereco(MeuCadastroEndereco.Tipo.FISICO);
                MeuCadastroEndereco endContato = obj.getEndereco(MeuCadastroEndereco.Tipo.CONTATO);

                if (endFisico == null && endContato == null) {
                    //Caso nao tenha nenhum dos dois enderecos retorna error
                    callback.onLoadContactAddress(null);
                    callback.onLoadPhysicalAddress(null);
                } else if (endFisico == null) {
                    //Caso nao tenha o endereco fisico
                    callback.onLoadContactAddress("OK");
                    callback.onLoadPhysicalAddress(null);
                } else if (endContato == null) {
                    //Caso nao tenha o endereco de contato
                    callback.onLoadPhysicalAddress("OK");
                    callback.onLoadContactAddress(null);
                } else {
                    //Caso tenham os dois enderecos
                    callback.onLoadPhysicalAddress("OK");
                    callback.onLoadContactAddress("Error");
                }
//                callback.onError(new ErrorMessage());
                callback.onFinish();
            }
        }, 3 * 1000);
    }

    public void getBrands(final MeuCadastroCallback callback){
        callback.onStart();

        //Espera 3 segundos para enviar a resposta de sucesso
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run() {

                CardBrandFees obj = ReaderMock.Companion.getBrands(context);
//                MeuCadastroObj obj = ReaderMock.Companion.getMeuCadastroUnico(context);
                callback.onSuccessBrands(obj);
                //callback.onErrorBrands(new ErrorMessage());

//                callback.onError(new ErrorMessage());
                callback.onFinish();
            }
        }, 3 * 1000);
    }
}
