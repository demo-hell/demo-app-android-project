package br.com.mobicare.cielo.extrato.data.managers

import android.content.Context
import android.os.Handler
import android.os.Looper
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.ReaderMock
import br.com.mobicare.cielo.extrato.domains.entities.extratoListaAcumulada.ExtratoListaObj
import br.com.mobicare.cielo.extrato.domains.entities.extratoRecibo.ExtratoReciboObj
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoTimeLineObj

open class ExtratoRepository(private val mContext: Context) {

    companion object {
        fun getInstance(context: Context): ExtratoRepository {
            return ExtratoRepository(context)
        }
    }

    fun timeLine(date: String?, pageNumber: Int, paginationId: String, callback: APICallbackDefault<ExtratoTimeLineObj, ErrorMessage>) {
        var obj = ReaderMock.getExtratoTimeLineHoje(mContext)
//        var obj = ReaderMock.getExtratoTimeLineZero(mContext)
        if (date != null) {
            obj = ReaderMock.getExtratoTimeLine(mContext)
        }

        callback.onStart()
        //Espera 3 segundos para enviar a resposta de sucesso
        Handler(Looper.getMainLooper()).postDelayed(// Tried new Handler(Looper.myLopper()) also
                {
                    callback.onSuccess(obj)
//                    var e = ErrorMessage()
//                    e.logout = true
//                    callback.onError(e)
                    callback.onFinish()
                }, (ReaderMock.TIME_LOADING).toLong())


    }

    fun aggregatedList(initialDate: String?, finalDate: String?, period: String?, page: Int, callback: APICallbackDefault<ExtratoListaObj, ErrorMessage>) {
        val obj = ReaderMock.getExtratoListaAcumulada(mContext)
//        obj.aggregatedTransactions = ArrayList()

        callback.onStart()
        //Espera 3 segundos para enviar a resposta de sucesso
        Handler(Looper.getMainLooper()).postDelayed(// Tried new Handler(Looper.myLopper()) also
                {
                    callback.onSuccess(obj)
//                    callback.onError(ErrorMessage())
                    callback.onFinish()
                }, (ReaderMock.TIME_LOADING).toLong())
    }

    fun salesReceipt(salesCode: String, salesDate: String, callback: APICallbackDefault<ExtratoReciboObj, ErrorMessage>) {
        val obj = ReaderMock.getExtratoRecibo(mContext)

        callback.onStart()
        //Espera 3 segundos para enviar a resposta de sucesso
        Handler(Looper.getMainLooper()).postDelayed(// Tried new Handler(Looper.myLopper()) also
                {
                    callback.onSuccess(obj)
                    callback.onFinish()
                }, (ReaderMock.TIME_LOADING).toLong())
    }
}
