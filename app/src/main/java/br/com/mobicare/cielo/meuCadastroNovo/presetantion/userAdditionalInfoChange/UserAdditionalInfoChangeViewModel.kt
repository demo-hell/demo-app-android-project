package br.com.mobicare.cielo.meuCadastroNovo.presetantion.userAdditionalInfoChange

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.meuCadastroNovo.domain.usecase.GetAdditionalFieldsInfoUseCase
import br.com.mobicare.cielo.meuCadastroNovo.domain.usecase.PutAdditionalInfoUseCase
import br.com.mobicare.cielo.meuCadastroNovo.utils.AdditionalInfoUiState
import kotlinx.coroutines.launch

class UserAdditionalInfoChangeViewModel(
    private val getAdditionalFieldsInfoUseCase: GetAdditionalFieldsInfoUseCase,
    private val putAdditionalInfoUseCase: PutAdditionalInfoUseCase
) : ViewModel() {

    private val _additionalFieldsInfoLiveData = MutableLiveData<AdditionalInfoUiState>()
    val additionalFieldsInfoLiveData: LiveData<AdditionalInfoUiState>
        get() = _additionalFieldsInfoLiveData

    fun getAdditionalInfoFields() {
        viewModelScope.launch {
            getAdditionalFieldsInfoUseCase.invoke()
                .onSuccess {
                    _additionalFieldsInfoLiveData.value = AdditionalInfoUiState.GetSuccess(it)
                }.onError {
                    _additionalFieldsInfoLiveData.value = AdditionalInfoUiState.GetError()
                }
        }
    }

    fun putAdditionalInfo(
        timeOfDay: String?,
        typeOfCommunication: ArrayList<String>,
        contactPreference: String?,
        pcdType: String?
    ) {
        viewModelScope.launch {
            putAdditionalInfoUseCase.invoke(
                timeOfDay, typeOfCommunication, contactPreference, pcdType
            ).onSuccess {
                _additionalFieldsInfoLiveData.value = AdditionalInfoUiState.UpdateSuccess()
            }.onError {
                _additionalFieldsInfoLiveData.value = AdditionalInfoUiState.UpdateError()
            }
        }
    }
}