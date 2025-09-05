package br.com.mobicare.cielo.openFinance.presentation.manager.sharedData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWENTYFIVE_TEXT
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_403
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.openFinance.data.model.response.SharedDataConsentsResponse
import br.com.mobicare.cielo.openFinance.domain.usecase.SharedDataConsentsUseCase
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConsents
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateShowFeatureToggles
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateShowFinalList
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.RECEIVING_JOURNEY
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.TRANSMITTING_JOURNEY
import kotlinx.coroutines.launch

class OpenFinanceSharedDataViewModel(
    private val useCaseConsents: SharedDataConsentsUseCase,
    private val getFeatureTogglePreference: GetFeatureTogglePreferenceUseCase
) : ViewModel() {

    private val _getConsentsReceivedLiveData =
        MutableLiveData<UIStateConsents<SharedDataConsentsResponse>>()
    val getConsentsReceivedLiveData get() = _getConsentsReceivedLiveData

    private val _getConsentsSentLiveData =
        MutableLiveData<UIStateConsents<SharedDataConsentsResponse>>()
    val getConsentsSentLiveData get() = _getConsentsSentLiveData

    private val _showFinalListReceived = MutableLiveData<UIStateShowFinalList<Boolean>>()
    val showFinalListReceived get() = _showFinalListReceived

    private val _showFinalListSent = MutableLiveData<UIStateShowFinalList<Boolean>>()
    val showFinalListSent get() = _showFinalListSent

    private val _getNewSharingLiveData =
        MutableLiveData<UIStateShowFeatureToggles<Boolean>>()
    val getNewSharingLiveData get() = _getNewSharingLiveData

    var pageNumberReceived = ONE
    var pageNumberSent = ONE
    var lastPageReceived = ONE
    var lastPageSent = ONE
    var hasReceivedNextPage = false
    var hasSentNextPage = false

    fun getConsentsReceived() {
        viewModelScope.launch {
            useCaseConsents.invoke(
                RECEIVING_JOURNEY,
                pageNumberReceived.toString(),
                TWENTYFIVE_TEXT
            )
                .onSuccess {
                    if(it.summary.currentPage.isNullOrBlank() && it.summary.lastPage.isNullOrBlank()){
                        _getConsentsReceivedLiveData.postValue(UIStateConsents.Error())
                        return@onSuccess
                    }
                    _getConsentsReceivedLiveData.postValue(UIStateConsents.Success(it))
                    pageNumberReceived = it.summary.currentPage.toInt()
                    lastPageReceived = it.summary.lastPage.toInt()
                    if (pageNumberReceived == lastPageReceived) hasReceivedNextPage.not()
                    else hasReceivedNextPage = true
                }.onError {
                    if (it.apiException.httpStatusCode == HTTP_STATUS_403){
                        _getConsentsReceivedLiveData.postValue(UIStateConsents.ErrorWithoutAccess())
                    }else{
                        _getConsentsReceivedLiveData.postValue(UIStateConsents.Error())
                    }
                }
        }
    }

    fun getConsentsSent() {
        viewModelScope.launch {
            useCaseConsents.invoke(TRANSMITTING_JOURNEY, pageNumberSent.toString(), TWENTYFIVE_TEXT)
                .onSuccess {
                    if(it.summary.currentPage.isNullOrBlank() && it.summary.lastPage.isNullOrBlank()){
                        _getConsentsSentLiveData.postValue(UIStateConsents.Error())
                        return@onSuccess
                    }
                    _getConsentsSentLiveData.postValue(UIStateConsents.Success(it))
                    pageNumberSent = it.summary.currentPage.toInt()
                    lastPageSent = it.summary.lastPage.toInt()
                    if (pageNumberSent == lastPageSent) hasSentNextPage.not()
                    else hasSentNextPage = true
                }.onError {
                    _getConsentsSentLiveData.postValue(UIStateConsents.Error())
                }
        }
    }

    fun getNextPageReceived() {
        if (hasReceivedNextPage) {
            pageNumberReceived += ONE
            getConsentsReceived()
        }
    }

    fun getNextPageSent() {
        if (hasSentNextPage) {
            pageNumberSent += ONE
            getConsentsSent()
        }
    }

    fun reloadPageReceived() {
        pageNumberReceived = ONE
        lastPageReceived = ONE
        hasReceivedNextPage = false
        _getConsentsReceivedLiveData.postValue(UIStateConsents.Loading)
        getConsentsReceived()
    }

    fun reloadPageSent() {
        pageNumberSent = ONE
        lastPageSent = ONE
        hasSentNextPage = false
        _getConsentsSentLiveData.postValue(UIStateConsents.Loading)
        getConsentsSent()
    }

    fun checkFinalListReceived(showFinalList: Boolean) {
        if (showFinalList) {
            _showFinalListReceived.postValue(UIStateShowFinalList.ShowFinalList())
        } else {
            _showFinalListReceived.postValue(UIStateShowFinalList.HideFinalList())
        }
    }

    fun checkFinalListSent(showFinalList: Boolean) {
        if (showFinalList) {
            _showFinalListSent.postValue(UIStateShowFinalList.ShowFinalList())
        } else {
            _showFinalListSent.postValue(UIStateShowFinalList.HideFinalList())
        }
    }

    fun checkFeatureToggleNewSharing() {
        viewModelScope.launch {
            getFeatureTogglePreference(FeatureTogglePreference.OPEN_FINANCE_HOME_SHARING)
                .onSuccess { show ->
                    if (show) {
                        _getNewSharingLiveData.postValue(UIStateShowFeatureToggles.ShowFeatureToggles())
                    } else {
                        _getNewSharingLiveData.postValue(UIStateShowFeatureToggles.HideFeatureToggles())
                    }
                }
        }
    }
}