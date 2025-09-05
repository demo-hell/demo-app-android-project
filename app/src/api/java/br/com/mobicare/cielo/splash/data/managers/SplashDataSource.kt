package br.com.mobicare.cielo.splash.data.managers

import br.com.mobicare.cielo.splash.domain.entities.Configuration
import io.reactivex.Observable


interface SplashDataSource {
    fun getConfig(): Observable<List<Configuration>>
}