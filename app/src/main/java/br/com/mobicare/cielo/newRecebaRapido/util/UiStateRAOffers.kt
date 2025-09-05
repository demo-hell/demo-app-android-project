package br.com.mobicare.cielo.newRecebaRapido.util

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiStateRAOffers<out T> {
    open class Success<T>(val data: T? = null) : UiStateRAOffers<T>()
    class Error(val message: Any, val error: NewErrorMessage? = null): UiStateRAOffers<Nothing>()
    object Loading : UiStateRAOffers<Nothing>()
    object HideLoading : UiStateRAOffers<Nothing>()
    object Empty : UiStateRAOffers<Nothing>()
    object HiredOfferExists : UiStateRAOffers<Nothing>()
    object OffersNotFound : UiStateRAOffers<Nothing>()
}

sealed class UiStateRAODetailsOffers<out T> {
    open class Success<T>(val data: T? = null) : UiStateRAODetailsOffers<T>()
    class Error(val message: Any, val error: NewErrorMessage? = null): UiStateRAODetailsOffers<Nothing>()
    object Loading : UiStateRAODetailsOffers<Nothing>()
    object HideLoading : UiStateRAODetailsOffers<Nothing>()
    object Empty : UiStateRAODetailsOffers<Nothing>()
}

sealed class UiStateRAOContract {
    object Success : UiStateRAOContract()
    class Error(val message: Any, val error: NewErrorMessage? = null): UiStateRAOContract()
    object Loading : UiStateRAOContract()
    object HideLoading : UiStateRAOContract()
    object Empty : UiStateRAOContract()
}

