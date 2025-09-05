package br.com.mobicare.cielo.splash.data.clients.api

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPI
import br.com.mobicare.cielo.splash.data.managers.SplashDataSource
import br.com.mobicare.cielo.splash.domain.entities.Configuration
import io.reactivex.Observable


class SplashAPIDataSource(private val api: CieloAPI) : SplashDataSource {

    override fun getConfig(): Observable<List<Configuration>> {
        return api.getConfiguration()
    }
}