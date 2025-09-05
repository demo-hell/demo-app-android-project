package br.com.mobicare.cielo.centralDeAjuda.data.clients.managers

import android.content.Context
import android.os.Handler
import android.os.Looper

import br.com.mobicare.cielo.centralDeAjuda.domains.entities.CentralAjudaObj
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.utils.ReaderMock

/**
 * Created by benhur.souza on 19/04/2017.
 */
open class CentralDeAjudaRepository(val mContext: Context) {

    fun registrationData(callback: APICallbackDefault<CentralAjudaObj, String>) {
        callback.onStart()

        //Espera 3 segundos para enviar a resposta de sucesso
        Handler(Looper.getMainLooper()).postDelayed(// Tried new Handler(Looper.myLopper()) also
        {
            callback.onSuccess(ReaderMock.getRegistrationData(mContext))
//            callback.onError("Erro")
            callback.onFinish()
        }, (ReaderMock.TIME_LOADING).toLong())

    }

    companion object {
        fun getInstance(context: Context): CentralDeAjudaRepository {
            return CentralDeAjudaRepository(context)
        }
    }

}
