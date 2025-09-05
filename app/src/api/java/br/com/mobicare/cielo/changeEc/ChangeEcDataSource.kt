package br.com.mobicare.cielo.changeEc

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.changeEc.domain.HierachyResponse
import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.changeEc.domain.ImpersonateRequest
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import io.reactivex.Observable

class ChangeEcDataSource(context: Context) {
    private var api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun impersonate(ec: String, token: String, type: String, fingerprint: ImpersonateRequest) : Observable<Impersonate> {
        return api.impersonate(ec, token, type,fingerprint)
    }

    fun children(token: String, pageSize: Int?, pageNumber: Int?, searchCriteria: String?) : Observable<HierachyResponse> {
        return api.children(token, pageSize, pageNumber, searchCriteria)
    }

    fun getMerchants(token: String)
        = this.api.getMerchants(token)

    fun loadMerchant(token: String, authorization: String)
        = api.getMerchat(token, authorization)

}