package br.com.mobicare.cielo.main

import br.com.mobicare.cielo.main.domain.AppMenuResponse
import io.reactivex.Observable

interface MenuRepository {

    fun getMenu(accessToken: String): Observable<AppMenuResponse?>?

}