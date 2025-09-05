package br.com.mobicare.cielo.eventTracking.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException

sealed class MachineRequestResource<out T> {
    data class Success<out T>(val data: T) : MachineRequestResource<T>()
    data class Error(val error: CieloAPIException) : MachineRequestResource<Nothing>()
    object Loading : MachineRequestResource<Nothing>()
}
