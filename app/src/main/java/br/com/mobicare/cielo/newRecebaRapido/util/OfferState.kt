package br.com.mobicare.cielo.newRecebaRapido.util

sealed class OfferState<out T> {
    class Show<T>(val offer: T) : OfferState<T>()
    object Hide: OfferState<Nothing>()
}

