package br.com.mobicare.cielo.fidelidade.data.managers

import android.content.Context
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.fidelidade.domains.ProdutoFidelidadeObjList
/**
 * Modify by Silvia Miranda on 2220174000/08/17.
 */
class FidelidadeRepository(private val mContext: Context) {

    companion object {
        fun getInstance(context: Context): FidelidadeRepository {
            return FidelidadeRepository(context)
        }
    }

    fun getProdutosFidelidade(callback: APICallbackDefault<ProdutoFidelidadeObjList, ErrorMessage>) {

    }
}