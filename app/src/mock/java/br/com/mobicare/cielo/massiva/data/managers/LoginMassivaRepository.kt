package br.com.mobicare.cielo.massiva.data.managers

import android.content.Context

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.utils.ReaderMock
import br.com.mobicare.cielo.massiva.domain.entities.MassivaStatusObj

/**
 * Created by benhur.souza on 04/04/2017.
 */

open class LoginMassivaRepository(val mContext: Context) {

    fun isMassiva(ec: String, callback: APICallbackDefault<MassivaStatusObj?, String>) {
        callback.onStart()
        if(ec == "1234567890") {
            callback.onSuccess(ReaderMock.getMassivaStatusOK(mContext))
        }else{
            callback.onSuccess(ReaderMock.getMassivaStatus(mContext))
        }
        callback.onFinish()
    }

    companion object {
        fun getInstance(context: Context): LoginMassivaRepository {
            return LoginMassivaRepository(context)
        }

    }
}
