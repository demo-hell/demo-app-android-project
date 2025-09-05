package br.com.mobicare.cielo.autoAtendimento.domain

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.autoAtendimento.domain.model.SupliesResponse
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import io.reactivex.Observable

class AutoAtendimentoDataSource(context: Context) {

    private var api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun loadSuplies(accessToken: String, authorization: String) : Observable<SupliesResponse> {
        return api.loadSuplies(accessToken, authorization)
    }
}