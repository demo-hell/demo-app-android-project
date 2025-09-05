package br.com.mobicare.cielo.esqueciSenha.data.clients.managers;

import android.content.Context;

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault;
import br.com.mobicare.cielo.commons.utils.ReaderMock;
import br.com.mobicare.cielo.esqueciSenha.domains.entities.BankListResponse;
import br.com.mobicare.cielo.esqueciSenha.domains.entities.RecoveryPassword;
import br.com.mobicare.cielo.esqueciSenha.presentation.ui.activities.EsqueciSenhaActivity;

/**
 * Created by benhur.souza on 11/04/2017.
 */

public class EsqueciSenhaRepository {

    private Context mContext;

    public static EsqueciSenhaRepository getInstance(Context context){
        return new EsqueciSenhaRepository(context);
    }

    public EsqueciSenhaRepository(Context context){
        this.mContext = context;
    }

    public void recoveryPassword(RecoveryPassword bankData, final APICallbackDefault<String, String> callback) {
        callback.onStart();
        if (bankData.getBank().getCode().equals("0")) {
            callback.onError(ReaderMock.Companion.getRecoveryPasswordError(mContext));
        } else if (bankData.getOperation().equals(EsqueciSenhaActivity.OPERATION_CREATE)) {
            callback.onSuccess(ReaderMock.Companion.getRecoveryPasswordMassiva(mContext).getMessage());
        } else {
            callback.onSuccess(ReaderMock.Companion.getRecoveryPassword(mContext).getMessage());
        }
}

    public void banks(final APICallbackDefault<BankListResponse, String> callback){
        callback.onStart();
        callback.onSuccess(ReaderMock.Companion.getBanks(mContext));
        callback.onFinish();
    }

}
