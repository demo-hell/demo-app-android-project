package br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.data.clients.managers


import android.content.Context
import br.com.mobicare.cielo.commons.utils.ReaderMock
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.ui.EsqueciUsuarioAndEstabelecimentoCallback

/**
 * Created by benhur.souza on 12/04/2017.
 */

open class EsqueciUsuarioAndEstabelecimentoRepository(private val mContext: Context) {

    fun recoveryUser(ec: String, callback: EsqueciUsuarioAndEstabelecimentoCallback) {

        callback?.onStart()
        callback?.onUserSuccess(ReaderMock.getRecoveryUser(mContext)?.loginList)
        callback?.onFinish()
//        Handler(Looper.getMainLooper()).postDelayed({
//        }, (ReaderMock.TIME_LOADING).toLong())


    }

    fun sendEmail(cod: String?, ec: String?, callback: EsqueciUsuarioAndEstabelecimentoCallback) {
        callback?.onStart()
        callback?.onSuccess(ReaderMock.getSendEmail(mContext))
        callback?.onFinish()
//        Handler(Looper.getMainLooper()).postDelayed({
//
//        }, (ReaderMock.TIME_LOADING).toLong())

    }

    fun recoveryEC(cnpj: String, callback: EsqueciUsuarioAndEstabelecimentoCallback) {

        callback?.onStart()
        callback?.onSuccess(ReaderMock.getRecoveryEC(mContext))
        callback?.onFinish()

//        Handler(Looper.getMainLooper()).postDelayed({
//        }, (ReaderMock.TIME_LOADING).toLong())


    }

    companion object {

        fun getInstance(context: Context): EsqueciUsuarioAndEstabelecimentoRepository {
            return EsqueciUsuarioAndEstabelecimentoRepository(context)
        }
    }


}
