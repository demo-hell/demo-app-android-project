package br.com.mobicare.cielo.meusCartoes.clients.api

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.domains.entities.MessagePhoto
import br.com.mobicare.cielo.meusCartoes.domains.entities.CardActivationStatusResponse
import br.com.mobicare.cielo.meusCartoes.domains.entities.ImageDocument
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidBalanceResponse
import io.reactivex.Observable
import retrofit2.Response

class CreditCardsDataSource (val context: Context) {

    val api: CieloAPIServices = CieloAPIServices.getInstance(context,
            BuildConfig.HOST_API)

    companion object {

        fun getInstance(context: Context): CreditCardsDataSource {
            return CreditCardsDataSource(context)
        }
    }

    fun getUserCardBalance(cardProxy: String, accessToken: String) : Observable<PrepaidBalanceResponse> =
            api.fetchUserCardBalance(cardProxy, accessToken)

    fun activateCard(merchantId: String,
                     accessToken: String,
                     serialNumber: String): Observable<Response<Void>> {
        return api.activateCard(merchantId, accessToken, serialNumber)
    }

    fun sendDocumentCreate(
            merchantId: String,
            accessToken: String,
            imageDocument: ImageDocument): Observable<MessagePhoto> {
        return api.sendDocumentCreate(merchantId, accessToken, imageDocument)
    }

    fun sendDocumentUpdate(
            merchantId: String,
            accessToken: String,
            imageDocument: ImageDocument): Observable<MessagePhoto> {
        return api.sendDocumentUpdate(merchantId, accessToken, imageDocument)
    }
}