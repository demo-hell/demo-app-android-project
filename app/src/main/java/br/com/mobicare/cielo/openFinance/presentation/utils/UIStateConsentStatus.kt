package br.com.mobicare.cielo.openFinance.presentation.utils

sealed class UIStateConsentStatus<out T> {
    object Active : UIStateConsentStatus<Nothing>()
    data class Expired<T>(val data: T? = null) : UIStateConsentStatus<T>()
    data class Closed<T>(val data: T? = null) : UIStateConsentStatus<T>()
    object Empty : UIStateConsentStatus<Nothing>()
}