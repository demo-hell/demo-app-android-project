package br.com.mobicare.cielo.extrato.data.clients.api

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.meusCartoes.domains.entities.CreditCardStatement
import io.reactivex.Observable

class StatementApiDataSource private constructor(val context: Context) {

    val api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun getStatements(initialDt: String, finalDt: String,
                      pageSize: Int, page: Int, merchantId: String?,
                      accessToken: String, proxyCard: String):
            Observable<CreditCardStatement> {
        return api.getCreditCardsStatement(initialDt, finalDt, pageSize, page, merchantId, accessToken,proxyCard)
    }

    companion object {
        fun getInstance(context: Context): StatementApiDataSource {
            return StatementApiDataSource(context)
        }
    }

}