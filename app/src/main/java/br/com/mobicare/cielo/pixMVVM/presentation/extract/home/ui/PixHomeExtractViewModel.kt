package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.PIX_SHOW_NEW_SCHEDULING_EXTRACT_2024_2
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixAccountBalanceUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixUserDataUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.home.utils.UserDataUiResult
import br.com.mobicare.cielo.pixMVVM.presentation.home.viewmodel.PixAccountBalanceViewModel
import kotlinx.coroutines.launch

class PixHomeExtractViewModel(
    getPixAccountBalanceUseCase: GetPixAccountBalanceUseCase,
    private val getPixUserDataUseCase: GetPixUserDataUseCase,
    private val getFeatureTogglePreferenceUseCase: GetFeatureTogglePreferenceUseCase,
) : PixAccountBalanceViewModel(getPixAccountBalanceUseCase) {
    private val _userDataUiResult = MutableLiveData<UserDataUiResult>()
    val userDataUiResult: LiveData<UserDataUiResult> = _userDataUiResult

    private val _isShowNewPixSchedulingExtract = MutableLiveData<Boolean>()
    val isShowNewPixSchedulingExtract: LiveData<Boolean> = _isShowNewPixSchedulingExtract

    fun loadUserData() {
        _userDataUiResult.value = getPixUserDataUseCase()
    }

    // TODO: REMOVER APÓS ESTABELECER O NOVO EXTRATO PARA AGENDADOS, POIS SERÁ ADICIONADO DIRETAMENTE NA LISTA O NOVO FRAGMENT
    fun checkShowNewPixSchedulingExtract() {
        viewModelScope.launch {
            getFeatureTogglePreferenceUseCase
                .invoke(PIX_SHOW_NEW_SCHEDULING_EXTRACT_2024_2)
                .onSuccess {
                    _isShowNewPixSchedulingExtract.value = it
                }
        }
    }
}
