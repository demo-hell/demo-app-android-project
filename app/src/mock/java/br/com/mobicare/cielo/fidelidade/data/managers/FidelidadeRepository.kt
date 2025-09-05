package br.com.mobicare.cielo.fidelidade.data.managers

import android.content.Context
import android.os.Handler
import android.os.Looper
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.ReaderMock
import br.com.mobicare.cielo.fidelidade.domains.ProdutoFidelidadeObjList

/**
 * Modify by Silvia Miranda on 16/08/17.
 */
class FidelidadeRepository(private val mContext: Context) {

    companion object {
        fun getInstance(context: Context): FidelidadeRepository {
            return FidelidadeRepository(context)
        }
    }

    fun getProdutosFidelidade(callback: APICallbackDefault<ProdutoFidelidadeObjList, ErrorMessage>) {
        val obj = ReaderMock.getProdutosFidelidade(mContext)

        callback.onStart()
        Handler(Looper.getMainLooper()).postDelayed(
                {
                    callback.onSuccess(obj)
                    callback.onFinish()
                }, (ReaderMock.TIME_LOADING).toLong())
    }
}

