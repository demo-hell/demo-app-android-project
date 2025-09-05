package br.com.mobicare.cielo.interactBannersOffersNew.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers

sealed class InteractBannersUiState {
    object OnShowLoading: InteractBannersUiState()
    object OnHideLoading: InteractBannersUiState()
    object OnFeatureDisabled: InteractBannersUiState()
    object OnSuccess: InteractBannersUiState()
    data class OnError(val error: NewErrorMessage? = null): InteractBannersUiState()
    data class OnShowBannerByControl(val offers: List<HiringOffers>, val bannerControl: BannerControl): InteractBannersUiState()
}