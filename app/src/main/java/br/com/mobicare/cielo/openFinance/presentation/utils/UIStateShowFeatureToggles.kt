package br.com.mobicare.cielo.openFinance.presentation.utils

sealed class UIStateShowFeatureToggles<out T> {
    data class HideFeatureToggles<T>(val data: T? = null) : UIStateShowFeatureToggles<T>()
    data class ShowFeatureToggles<T>(val data: T? = null) : UIStateShowFeatureToggles<T>()
}