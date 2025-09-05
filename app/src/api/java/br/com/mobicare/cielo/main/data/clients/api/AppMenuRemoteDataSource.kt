package br.com.mobicare.cielo.main.data.clients.api

import android.content.Context
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.main.domain.AppMenuResponse
import io.reactivex.Observable

class AppMenuRemoteDataSource(val context: Context) : MenuDataSource {

    val api: CieloAPIServices = CieloAPIServices.getCieloBackInstance(context)

    override fun getOthersMenu(accessToken: String): Observable<AppMenuResponse?>? {
        return api.getOthersMenu(accessToken)
    }

}