package br.com.mobicare.cielo.pagamentoLink

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLinkResponse
import io.reactivex.Observable

class PagamentoLinkDataSource (context: Context) {
    private var api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun paymentLinkActivity(token: String, size: Int, page : Int) : Observable<PaymentLinkResponse>  {
        return api.paymentLinkActive(token, size, page)
    }


}