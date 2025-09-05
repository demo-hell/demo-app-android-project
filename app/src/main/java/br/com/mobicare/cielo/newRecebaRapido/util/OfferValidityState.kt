package br.com.mobicare.cielo.newRecebaRapido.util

sealed class OfferValidityState {
    class Months(val months: Int): OfferValidityState()
    class FixedDate(val date: String): OfferValidityState()
    object Empty: OfferValidityState()
}

