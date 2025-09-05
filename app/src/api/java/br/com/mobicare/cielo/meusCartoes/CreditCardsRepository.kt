package br.com.mobicare.cielo.meusCartoes

import br.com.mobicare.cielo.commons.domains.entities.MessagePhoto
import br.com.mobicare.cielo.meusCartoes.clients.api.CreditCardsDataSource
import br.com.mobicare.cielo.meusCartoes.domains.entities.ImageDocument
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidBalanceResponse
import io.reactivex.Observable
import retrofit2.Response

class CreditCardsRepository(private val creditCardsDataSource: CreditCardsDataSource) {

    fun getUserCardBalance(cardProxy: String, accessToken: String):
            Observable<PrepaidBalanceResponse> {
        return creditCardsDataSource.getUserCardBalance(cardProxy, accessToken)
    }

    fun activateCreditCard(merchantId: String,
                           accessToken: String,
                           serialNumber: String): Observable<Response<Void>> {
        return creditCardsDataSource.activateCard(merchantId, accessToken, serialNumber)
    }

    fun sendDocumentCreate(
            merchantId: String,
            accessToken: String,
            imageDocument: ImageDocument): Observable<MessagePhoto> {
        return creditCardsDataSource.sendDocumentCreate(merchantId, accessToken, imageDocument)
    }

    fun sendDocumentUpdate(
            merchantId: String,
            accessToken: String,
            imageDocument: ImageDocument): Observable<MessagePhoto> {
        return creditCardsDataSource.sendDocumentUpdate(merchantId, accessToken, imageDocument)
    }


    companion object {
        fun getInstance(remoteDataSource: CreditCardsDataSource): CreditCardsRepository {
            return CreditCardsRepository(remoteDataSource)
        }
    }
}