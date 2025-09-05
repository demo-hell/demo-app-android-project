package br.com.mobicare.cielo.openFinance.presentation.manager.newShare.conclusion

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.openFinance.data.model.request.ChangeOrRenewShareRequest
import br.com.mobicare.cielo.openFinance.data.model.request.ConfirmShareRequest
import br.com.mobicare.cielo.openFinance.data.model.request.GivenUpShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.ChangeOrRenewShare
import br.com.mobicare.cielo.openFinance.domain.model.ConfirmShare
import br.com.mobicare.cielo.openFinance.domain.model.InfoDetailsShare
import br.com.mobicare.cielo.openFinance.domain.usecase.ChangeOrRenewShareUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.ConfirmShareUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.GivenUpShareUseCase
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConclusionShare
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.gson.Gson
import kotlinx.coroutines.launch

class OpenFinanceConclusionViewModel(
    private val confirmShareUseCase: ConfirmShareUseCase,
    private val givenUpShareUseCase: GivenUpShareUseCase,
    private val changeOrRenewShareUseCase: ChangeOrRenewShareUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _conclusionShareLiveData =
        MutableLiveData<UIStateConclusionShare<ConfirmShare>>()
    val conclusionShareLiveData get() = _conclusionShareLiveData

    private fun confirmShare() {
        val request = ConfirmShareRequest(
            userPreferences.authorizationCodeOPF ?: EMPTY,
            userPreferences.requestIdOPF ?: EMPTY,
            userPreferences.idTokenOPF ?: EMPTY
        )
        viewModelScope.launch {
            confirmShareUseCase.invoke(request).onSuccess {
                _conclusionShareLiveData.postValue(UIStateConclusionShare.SuccessShare(it))
            }.onError {
                _conclusionShareLiveData.postValue(UIStateConclusionShare.ErrorShare)
            }
        }
    }

    private fun givenUpShare() {
        val request = GivenUpShareRequest(
            userPreferences.shareIdOPF ?: EMPTY,
            userPreferences.errorDescriptionOPF ?: EMPTY,
            userPreferences.requestIdOPF ?: EMPTY
        )
        viewModelScope.launch {
            givenUpShareUseCase.invoke(request).onSuccess {
                _conclusionShareLiveData.postValue(UIStateConclusionShare.ErrorShare)
            }.onError {
                _conclusionShareLiveData.postValue(UIStateConclusionShare.ErrorShare)
            }
        }
    }

    fun confirmOrGivenUpShare() {
        _conclusionShareLiveData.postValue(UIStateConclusionShare.Loading)
        if (userPreferences.errorDescriptionOPF.isNullOrEmpty() && userPreferences.authorizationCodeOPF.isNullOrEmpty().not()) {
            if (userPreferences.infoDetailsShare.isNullOrEmpty().not()) {
                changeOrRenewShare()
            } else{
                confirmShare()
            }
        } else {
            givenUpShare()
        }
    }

    fun changeOrRenewShare() {
        var infoDetailsShare = Gson().fromJson(userPreferences.infoDetailsShare, InfoDetailsShare::class.java)
        val request = ChangeOrRenewShareRequest(
            userPreferences.authorizationCodeOPF ?: EMPTY,
            userPreferences.requestIdOPF ?: EMPTY,
            userPreferences.idTokenOPF ?: EMPTY,
            infoDetailsShare.function,
            infoDetailsShare.consentId,
            infoDetailsShare.shareId,
            infoDetailsShare.flow
        )
        viewModelScope.launch {
            changeOrRenewShareUseCase.invoke(request).onSuccess {
                _conclusionShareLiveData.postValue(UIStateConclusionShare.SuccessShare(mapToConfirmShare(it)))
            }.onError {
                _conclusionShareLiveData.postValue(UIStateConclusionShare.ErrorShare)
            }
        }
    }

    fun clearUserPreferencesError() {
        userPreferences.deleteErrorDescriptionOPF()
        userPreferences.deleteShareIdOPF()
        userPreferences.deleteRequestIdOPF()
        userPreferences.deleteInfoDetailsShare()
    }

    fun clearUserPreferencesSuccess() {
        userPreferences.deleteAuthorizationCodeOPF()
        userPreferences.deleteRequestIdOPF()
        userPreferences.deleteIdTokenOPF()
        userPreferences.deleteShareIdOPF()
        userPreferences.deleteInfoDetailsShare()
    }

    private fun mapToConfirmShare(changeOrRenewShare: ChangeOrRenewShare): ConfirmShare {
        return ConfirmShare(
            changeOrRenewShare.consentId,
            changeOrRenewShare.customerFrindlyName,
            changeOrRenewShare.expirationDateTime,
            changeOrRenewShare.shareType
        )
    }
}