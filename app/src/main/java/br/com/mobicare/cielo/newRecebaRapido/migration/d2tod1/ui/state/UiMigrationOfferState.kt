package br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.state

sealed class UiMigrationOfferState {
    class Both(val creditOffer: Double?, val installmentOffer: Double?): UiMigrationOfferState()
    class Installment(val installmentOffer: Double?): UiMigrationOfferState()
    class Credit(val creditOffer: Double?): UiMigrationOfferState()

    object NoOfferError: UiMigrationOfferState()
}
