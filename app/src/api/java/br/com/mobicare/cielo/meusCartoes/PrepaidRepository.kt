package br.com.mobicare.cielo.meusCartoes

import br.com.mobicare.cielo.meusCartoes.clients.api.PrepaidDataSource
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentRequest
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentResponse
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidResponse
import io.reactivex.Observable

class PrepaidRepository(private val prepaidDataSource: PrepaidDataSource) {


    fun createPayment(cardProxy: String,
                      accessToken: String,
                      paymentRequest: PrepaidPaymentRequest): Observable<PrepaidPaymentResponse> {
        return prepaidDataSource.createPayment(cardProxy, accessToken, paymentRequest)
    }

    fun confirmPayment(cardProxy: String,
                       paymentId: String,
                       accessToken: String,
                       transferAuthorization: String):
            Observable<PrepaidPaymentResponse> {
        return prepaidDataSource.confirmPayment(cardProxy, paymentId, accessToken, transferAuthorization)
    }

    fun getUserStatusPrepago(accessToken: String): Observable<PrepaidResponse> {
        return prepaidDataSource.getUserStatusPrepago(accessToken)
    }

}