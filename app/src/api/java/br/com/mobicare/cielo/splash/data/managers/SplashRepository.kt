package br.com.mobicare.cielo.splash.data.managers

import br.com.mobicare.cielo.splash.data.clients.api.SplashAPIDataSource
import br.com.mobicare.cielo.splash.domain.entities.Configuration
import io.reactivex.Observable

class SplashRepository(
    private val remoteDataSource: SplashAPIDataSource
) {

    fun getConfig(): Observable<List<Configuration>> {
        return remoteDataSource.getConfig()
    }
}