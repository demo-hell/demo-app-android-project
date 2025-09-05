package br.com.mobicare.cielo.chargeback.presentation.details.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackDocumentParams
import br.com.mobicare.cielo.chargeback.domain.model.Chargeback
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackDocument
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackDocumentUseCase
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import kotlinx.coroutines.launch

class ChargebackDocumentViewModel(
    private val getChargebackDocumentUseCase: GetChargebackDocumentUseCase
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState<ChargebackDocument>>()
    val uiState: LiveData<UiState<ChargebackDocument>> get() = _uiState

    fun getChargebackDocument(chargeback: Chargeback) {
        _uiState.value = UiState.Loading

        val params = ChargebackDocumentParams(
            merchantId = (chargeback.merchantId ?: ZERO.toLong()) as Long,
            chargebackId = chargeback.chargebackId ?: ZERO
        )

        viewModelScope.launch {
            getChargebackDocumentUseCase(params)
                .onSuccess {
                    _uiState.postValue(UiState.Success(it))
                }.onError {
                    _uiState.postValue(UiState.Error(it.apiException.newErrorMessage))
                }.onEmpty {
                    _uiState.postValue(UiState.Empty)
                }
        }
    }

}