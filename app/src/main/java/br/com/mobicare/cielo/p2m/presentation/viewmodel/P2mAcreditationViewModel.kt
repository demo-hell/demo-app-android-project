package br.com.mobicare.cielo.p2m.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.p2m.domain.model.TaxModel
import br.com.mobicare.cielo.p2m.domain.usecase.GetFeatureToggleMessageUseCase
import br.com.mobicare.cielo.p2m.domain.usecase.PutP2mAcceptUseCase
import br.com.mobicare.cielo.p2m.utils.UiP2mAcceptState
import br.com.mobicare.cielo.p2m.utils.UiP2mLoadingState
import br.com.mobicare.cielo.p2m.utils.UiTaxTextState
import kotlinx.coroutines.launch

class P2mAcreditationViewModel(
    private val putP2mAcceptUseCase: PutP2mAcceptUseCase,
    private val userObjUseCase: GetUserObjUseCase,
    private val featureToggleMessageUseCase: GetFeatureToggleMessageUseCase
) : ViewModel() {

    private val _p2mAcceptUiState = MutableLiveData<UiP2mAcceptState>()
    val p2mAcceptUiState: LiveData<UiP2mAcceptState> get() = _p2mAcceptUiState

    private val _loadingState = MutableLiveData<UiP2mLoadingState>()
    val loadingState: LiveData<UiP2mLoadingState> get() = _loadingState

    private val _featureToggle = MutableLiveData<UiTaxTextState<TaxModel>>()
    val featureToggle: LiveData<UiTaxTextState<TaxModel>> get() = _featureToggle


    fun getFeatureToggle(){
        viewModelScope.launch {
            featureToggleMessageUseCase.invoke(key = FeatureTogglePreference.P2M_WHATS_APP)
                .onSuccess{ taxText ->
                    _featureToggle.value = UiTaxTextState.Success(taxText)
                }.onEmpty {
                    _featureToggle.value = UiTaxTextState.Empty
                }
        }
    }

    fun p2mAccept(
        context: Context?,
        bannerId: String
    ) {
        _p2mAcceptUiState.value = UiP2mAcceptState.ShowLoading
        viewModelScope.launch {
            putP2mAcceptUseCase.invoke(
                bannerId
            ).onSuccess {
                _p2mAcceptUiState.value = UiP2mAcceptState.HideLoading
                _p2mAcceptUiState.value = UiP2mAcceptState.Success
            }.onEmpty {
                _p2mAcceptUiState.value = UiP2mAcceptState.HideLoading
                _p2mAcceptUiState.value = UiP2mAcceptState.Success
            }.onError { dataResultApiError ->
                val error = dataResultApiError.apiException.newErrorMessage
                context?.let { itContext ->
                    newErrorHandler(
                        context = itContext,
                        getUserObjUseCase = userObjUseCase,
                        newErrorMessage = error,
                        onErrorAction = {
                            _p2mAcceptUiState.value = UiP2mAcceptState.HideLoading
                            _p2mAcceptUiState.value = UiP2mAcceptState.Error(error)
                        }
                    )
                }
            }
        }
    }
}
