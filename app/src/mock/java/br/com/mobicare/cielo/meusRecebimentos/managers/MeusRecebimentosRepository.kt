package br.com.mobicare.cielo.meusRecebimentos.managers

import android.content.Context
import android.os.Handler
import android.os.Looper
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.ReaderMock
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.BankDataObj
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.MeusRecebimentosObj
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.PostingObject
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.PostingOfDetailObject

/**
 * Created by benhur.souza on 21/06/2017.
 */
open class MeusRecebimentosRepository(val context: Context) {

    companion object {
        fun getInstance(context: Context): MeusRecebimentosRepository {
            return MeusRecebimentosRepository(context)
        }
    }

    fun meusRecebimentos(dailyDate: String? = null, initialDate: String? = null, finalDate: String? = null, period: String? = null, callback: APICallbackDefault<MeusRecebimentosObj, ErrorMessage>) {
        callback.onStart()
        Handler(Looper.getMainLooper()).postDelayed(// Tried new Handler(Looper.myLopper()) also
                {
                    if(!initialDate.isNullOrBlank() || !finalDate.isNullOrBlank() || !period.isNullOrBlank()){
                        callback.onSuccess(ReaderMock.getMeusRecebimentosFiltro(context))
                    }else{
                        callback.onSuccess(ReaderMock.getMeusRecebimentos(context))
                    }

//                    val e = ErrorMessage()
//                    e.logout = true
//
//                    callback.onError(e)
                    callback.onFinish()
                }, (ReaderMock.TIME_LOADING).toLong())
    }

    fun meusRecebimentosResumoOperacoes(id: String, resumoQuantity: String, resumoDate: String, pageNumber: Int, callback: APICallbackDefault<PostingObject, ErrorMessage>, finalDate: String?) {
        callback.onStart()
        Handler(Looper.getMainLooper()).postDelayed(// Tried new Handler(Looper.myLopper()) also
                {
                    callback.onSuccess(ReaderMock.getResumoOperacoes(context))
//
//                    val e = ErrorMessage()
//                    e.logout = true
//
//                    callback.onError(e)
                    callback.onFinish()
                }, (ReaderMock.TIME_LOADING).toLong())
    }

    fun meusRecebimentosLancamentos(
            date: String?,
            bank: BankDataObj?,
            callback: APICallbackDefault<Array<Double>?, ErrorMessage>) {

        callback.onStart()
        Handler(Looper.getMainLooper()).postDelayed(// Tried new Handler(Looper.myLopper()) also
                {
                    callback.onSuccess(ReaderMock.getLancamentos(context))
                    callback.onFinish()
                }, (ReaderMock.TIME_LOADING).toLong())
    }

    fun meusRecebimentosResumoOperacoesDetalhe(id: String?, cvsQty: String?, payDay: String?, uniqueKeyROPart1: String?, uniqueKeyROPart2: String?, uniqueKeyROPart3: String?, pageNumber: Int, finalDate: String?, merchantId: String?, callback: APICallbackDefault<PostingOfDetailObject, ErrorMessage>) {
        callback.onStart()
        Handler(Looper.getMainLooper()).postDelayed(// Tried new Handler(Looper.myLopper()) also
                {
                    callback.onSuccess(ReaderMock.getResumoOperacoesDetalhes(context))
//                    callback.onError(ErrorMessage())
                    callback.onFinish()
                }, (ReaderMock.TIME_LOADING).toLong())
    }

}