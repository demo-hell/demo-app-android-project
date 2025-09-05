package br.com.mobicare.cielo.meusCartoes.clients.api

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentRequest
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentResponse
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidResponse
import io.reactivex.Observable

class PrepaidDataSource(val context: Context) {

    val api: CieloAPIServices = CieloAPIServices.getInstance(context,
            BuildConfig.HOST_API)


    fun createPayment(cardProxy: String, accessToken: String,
                      paymentRequest: PrepaidPaymentRequest): Observable<PrepaidPaymentResponse> {
        return api.createPayment(cardProxy, accessToken, paymentRequest)
    }

    fun confirmPayment(cardProxy: String,
                       paymentId: String,
                       accessToken: String,
                       transferAuthorization: String):
            Observable<PrepaidPaymentResponse> {
        return api.confirmPayment(cardProxy, paymentId, accessToken, transferAuthorization)
    }

    fun getUserStatusPrepago(accessToken: String): Observable<PrepaidResponse> {
        return api.getUserStatusPrepago(accessToken)
    }

}