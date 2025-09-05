package br.com.mobicare.cielo.interactBannersOffersNew.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.interactBannersOffersNew.domain.mapper.InteractBannerMapperNew
import br.com.mobicare.cielo.interactBannersOffersNew.domain.useCase.DeleteLocalInteractBannersOffersUseCase
import br.com.mobicare.cielo.interactBannersOffersNew.domain.useCase.GetLocalInteractBannersOffersUseCase
import br.com.mobicare.cielo.interactBannersOffersNew.domain.useCase.GetRemoteInteractBannersOffersUseCase
import br.com.mobicare.cielo.interactBannersOffersNew.domain.useCase.SaveLocalInteractBannersOffersUseCase
import br.com.mobicare.cielo.interactBannersOffersNew.utils.BannerControl
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUiState
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUiState.OnError
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUiState.OnFeatureDisabled
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUiState.OnHideLoading
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUiState.OnShowBannerByControl
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUiState.OnShowLoading
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers
import kotlinx.coroutines.launch

class InteractBannersViewModel(
    private val getRemoteInteractBannersOffersUseCase: GetRemoteInteractBannersOffersUseCase,
    private val getLocalInteractBannersOffersUseCase: GetLocalInteractBannersOffersUseCase,
    private val saveLocalInteractBannersOffersUseCase: SaveLocalInteractBannersOffersUseCase,
    private val deleteLocalInteractBannersOffersUseCase: DeleteLocalInteractBannersOffersUseCase,
    private val getFeatureTogglePreferenceUseCase: GetFeatureTogglePreferenceUseCase,
) : ViewModel() {
    private var _shouldGetOffersFromApi = false
    private var _hiringOffers: List<HiringOffers>? = null

    private val _interactBannersStateMutableLiveData = MutableLiveData<InteractBannersUiState>()
    val interactBannersStateMutableLiveData: LiveData<InteractBannersUiState> get() = _interactBannersStateMutableLiveData

    private var _bannerControl = BannerControl.LeaderboardHome

    fun getHiringOffers(
        shouldGetOffersFromApi: Boolean,
        bannerControl: BannerControl = BannerControl.LeaderboardHome
    ) {
        _bannerControl = bannerControl
        _shouldGetOffersFromApi = shouldGetOffersFromApi
        _interactBannersStateMutableLiveData.value = OnShowLoading

        viewModelScope.launch {
            getFeatureTogglePreferenceUseCase(key = FeatureTogglePreference.INTERACT_BANNERS)
                .onSuccess { itShow ->
                    if (itShow.not()) {
                        clearLocalOffers()
                        _interactBannersStateMutableLiveData.value = OnFeatureDisabled
                        return@launch
                    }

                    if (_shouldGetOffersFromApi) getRemoteOffers()
                    else getLocalOffers()

                    _interactBannersStateMutableLiveData.value = OnHideLoading
                }
                .onError {
                    _interactBannersStateMutableLiveData.value = OnFeatureDisabled
                }
        }
    }

    private fun clearLocalOffers() {
        viewModelScope.launch {
            deleteLocalInteractBannersOffersUseCase()
        }
    }

    private fun getRemoteOffers() {
        viewModelScope.launch {
            getRemoteInteractBannersOffersUseCase()
                .onSuccess { offers ->
                    saveLocalInteractBannersOffersUseCase(offers)
                    showBannersByControl(offers, _bannerControl)
                }.onError {
                    deleteLocalInteractBannersOffersUseCase()
                    _interactBannersStateMutableLiveData.value =
                        OnError(it.apiException.newErrorMessage)
                }
        }
    }

    private fun getLocalOffers() {
        viewModelScope.launch {
            getLocalInteractBannersOffersUseCase()
                .onSuccess { offers ->
                    showBannersByControl(offers, _bannerControl)
                }.onError {
                    getRemoteOffers()
                }
        }
    }

    private fun showBannersByControl(
        offers: List<HiringOffers>,
        bannerControl: BannerControl
    ) {
        _hiringOffers = InteractBannerMapperNew.getBannersByControl(offers, bannerControl).also {
            _interactBannersStateMutableLiveData.value = OnShowBannerByControl(it, bannerControl)
        }
    }
}