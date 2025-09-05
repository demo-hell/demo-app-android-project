package br.com.mobicare.cielo.eventTracking.utils

sealed class FilterBottomSheetEvents {
    object HeaderOnCloseTap : FilterBottomSheetEvents()
    data class MainButtonClick(val selected: Int) : FilterBottomSheetEvents()
    object SecondaryButtonClick : FilterBottomSheetEvents()
}
