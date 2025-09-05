package br.com.mobicare.cielo.turboRegistration.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException

sealed class RegistrationResource<out T> {
    data class Success<out T>(val data: T) : RegistrationResource<T>()
    data class Error(val error: CieloAPIException) : RegistrationResource<Nothing>()
    object Loading : RegistrationResource<Nothing>()

    object Empty : RegistrationResource<Nothing>()
}
