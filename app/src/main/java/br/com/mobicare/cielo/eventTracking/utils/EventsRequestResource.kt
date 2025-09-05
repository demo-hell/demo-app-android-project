package br.com.mobicare.cielo.eventTracking.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException

sealed class EventsRequestResource<out T> {
    data class Success<out T>(val data: T) : EventsRequestResource<T>()
    data class Error(val error: CieloAPIException) : EventsRequestResource<Nothing>()
    object Loading : EventsRequestResource<Nothing>()
}
