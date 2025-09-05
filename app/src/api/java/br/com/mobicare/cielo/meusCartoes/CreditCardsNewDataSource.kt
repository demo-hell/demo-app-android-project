package br.com.mobicare.cielo.meusCartoes

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.CardActivationCatenoRequest
import io.reactivex.Observable
import retrofit2.Response

class CreditCardsNewDataSource (context: Context) {
    private val api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun activateCardCateno(proxy: String, cardActivation: CardActivationCatenoRequest,
                           token: String, xAuthorization: String) : Observable<Response<Void>>
            = api.activateCardCateno(proxy, cardActivation, token, xAuthorization)

    fun activateCreditCard(merchantId: String,
                            accessToken: String,
                            serialNumber: String)
        = api.activateCard(merchantId, accessToken, serialNumber)
}