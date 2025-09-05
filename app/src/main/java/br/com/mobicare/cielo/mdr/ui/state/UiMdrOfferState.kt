package br.com.mobicare.cielo.mdr.ui.state

sealed class UiMdrOfferState {
    object ShowPostponedWithRR : UiMdrOfferState()

    object ShowPostponedWithoutRR : UiMdrOfferState()

    object ShowWithoutPostponedWithRR : UiMdrOfferState()

    object ShowWithoutPostponedWithoutRR : UiMdrOfferState()

    object ShowWithoutEquipmentWithRR : UiMdrOfferState()

    object ShowWithoutEquipmentWithoutRR : UiMdrOfferState()

    object Error : UiMdrOfferState()
}
