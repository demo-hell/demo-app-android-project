package br.com.mobicare.cielo.main.data.clients.api

import br.com.mobicare.cielo.main.domain.AppMenuResponse
import io.reactivex.Observable

interface MenuDataSource {

    fun getOthersMenu(accessToken: String): Observable<AppMenuResponse?>?

}