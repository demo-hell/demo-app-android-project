package br.com.mobicare.cielo.mySales.domain.usecase

import br.com.mobicare.cielo.mySales.analytics.MySalesGA4


class GetGA4UseCase(private val repository: MySalesGA4) {
    operator fun invoke(screenName: String) {
        return repository.logScreenView(screenName)
    }

}