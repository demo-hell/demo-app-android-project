package br.com.mobicare.cielo.pixMVVM.utils.bottomSheets.pixAlertNewLayout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences.Companion.MODAL_ALERT_NEW_LAYOUT_PIX_VIEWED
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserViewHistoryUseCase
import br.com.mobicare.cielo.commons.domain.useCase.SaveUserViewHistoryUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.PIX_SHOW_MODAL_NEW_LAYOUT_PIX_2024_1
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PixAlertNewLayoutViewModel(
    val getUserViewHistoryUseCase: GetUserViewHistoryUseCase,
    val saveUserViewHistoryUseCase: SaveUserViewHistoryUseCase,
    val featureTogglePreferenceUseCase: GetFeatureTogglePreferenceUseCase,
) : ViewModel() {
    private val _isShowBottomSheet = MutableLiveData<Boolean>()
    val isShowBottomSheet: LiveData<Boolean> = _isShowBottomSheet

    fun verifyShowBottomSheet() {
        viewModelScope.launch {
            var flagModalViewed = false
            var ftShowModal = false

            val getUserViewHistoryUseCaseAsync =
                async { getUserViewHistoryUseCase(MODAL_ALERT_NEW_LAYOUT_PIX_VIEWED) }

            val featureTogglePreferenceUseCaseAsync =
                async { featureTogglePreferenceUseCase(PIX_SHOW_MODAL_NEW_LAYOUT_PIX_2024_1) }

            getUserViewHistoryUseCaseAsync.await().onSuccess { flagModalViewed = it }
            featureTogglePreferenceUseCaseAsync.await().onSuccess { ftShowModal = it }

            _isShowBottomSheet.value = flagModalViewed.not() && ftShowModal
        }
    }

    fun saveViewed() {
        viewModelScope.launch {
            saveUserViewHistoryUseCase(MODAL_ALERT_NEW_LAYOUT_PIX_VIEWED)
        }
    }
}
