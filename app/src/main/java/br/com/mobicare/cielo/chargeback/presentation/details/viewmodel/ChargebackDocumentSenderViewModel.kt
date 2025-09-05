package br.com.mobicare.cielo.chargeback.presentation.details.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackDocumentSenderParams
import br.com.mobicare.cielo.chargeback.data.model.response.RefundFileInformationList
import br.com.mobicare.cielo.chargeback.domain.model.Chargeback
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackDocumentSender
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackDocumentSenderUseCase
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import kotlinx.coroutines.launch

class ChargebackDocumentSenderViewModel(
    private var chargebackDocumentSenderUseCase: GetChargebackDocumentSenderUseCase
) : ViewModel() {

    private val _documentSenderLiveData = MutableLiveData<UiState<ChargebackDocumentSender>>()
    val documentSenderLiveData: LiveData<UiState<ChargebackDocumentSender>> get() = _documentSenderLiveData

    fun getChargebackDocumentSender(
        chargeback: Chargeback,
        refundFileInformation: RefundFileInformationList
    ) {
        _documentSenderLiveData.value = UiState.Loading

        val params = ChargebackDocumentSenderParams(
            merchantId = (chargeback.merchantId ?: ZERO.toLong()),
            documentId = refundFileInformation.documentId
        )

        viewModelScope.launch {
            chargebackDocumentSenderUseCase(params)
                .onSuccess {
                        verifyDocument(it)
                }.onError {
                    _documentSenderLiveData.postValue(UiState.Error(null))
                }.onEmpty {
                    _documentSenderLiveData.postValue(UiState.Empty)
                }
        }
    }

    fun verifyDocument(document: ChargebackDocumentSender?) {
        if (document == null || document.fileBase64.isNullOrBlank() || document.nameFile.isNullOrBlank()) {
            _documentSenderLiveData.postValue(UiState.Error(null))
        } else {
            _documentSenderLiveData.postValue(UiState.Success(document))
        }
    }
}