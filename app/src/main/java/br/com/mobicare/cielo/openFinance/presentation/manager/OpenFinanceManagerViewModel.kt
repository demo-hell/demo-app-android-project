package br.com.mobicare.cielo.openFinance.presentation.manager

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_403
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.openFinance.domain.model.Brand
import br.com.mobicare.cielo.openFinance.domain.usecase.BrandsUseCase
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConsentDetail
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateFilterList
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateShowFeatureToggles
import kotlinx.coroutines.launch

class OpenFinanceManagerViewModel(
    private val brandsUseCase: BrandsUseCase,
    private val userPreferences: UserPreferences,
    private val getFeatureTogglePreference: GetFeatureTogglePreferenceUseCase
) : ViewModel() {

    private val _getNewSharingLiveData =
        MutableLiveData<UIStateShowFeatureToggles<Boolean>>()
    val getNewSharingLiveData get() = _getNewSharingLiveData

    private val _getDataSharedLiveData =
        MutableLiveData<UIStateShowFeatureToggles<Boolean>>()
    val getDataSharedLiveData get() = _getDataSharedLiveData

    private val _getPaymentsLiveData =
        MutableLiveData<UIStateShowFeatureToggles<Boolean>>()
    val getPaymentsLiveData get() = _getPaymentsLiveData

    private val _getBanksLiveData =
        MutableLiveData<UIStateConsentDetail<List<Brand>>?>()
    val getBanksLiveData get() = _getBanksLiveData

    private val _getListFilterLiveData =
        MutableLiveData<UIStateFilterList<List<Brand>>?>()
    val getListFilterLiveData get() = _getListFilterLiveData

    private var listBank: List<Brand> = emptyList()

    fun checkSeenOnboarding(): Boolean {
        return userPreferences.isOnboardingOpenFinanceWasViewed
    }

    private fun checkFeatureToggleNewSharing() {
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

    private fun checkFeatureDataShared() {
        viewModelScope.launch {
            getFeatureTogglePreference(FeatureTogglePreference.OPEN_FINANCE_HOME_DATA)
                .onSuccess { show ->
                    if (show) {
                        _getDataSharedLiveData.postValue(UIStateShowFeatureToggles.ShowFeatureToggles())
                    } else {
                        _getDataSharedLiveData.postValue(UIStateShowFeatureToggles.HideFeatureToggles())
                    }
                }
        }
    }

    private fun checkFeatureTogglePayments() {
        viewModelScope.launch {
            getFeatureTogglePreference(FeatureTogglePreference.OPEN_FINANCE_HOME_PAYMENTS)
                .onSuccess { show ->
                    if (show) {
                        _getPaymentsLiveData.postValue(UIStateShowFeatureToggles.ShowFeatureToggles())
                    } else {
                        _getPaymentsLiveData.postValue(UIStateShowFeatureToggles.HideFeatureToggles())
                    }
                }
        }
    }

    fun checkFeatureToggles() {
        checkFeatureToggleNewSharing()
        checkFeatureDataShared()
        checkFeatureTogglePayments()
    }

    fun getBanks(name: String) {
        viewModelScope.launch {
            _getBanksLiveData.postValue(UIStateConsentDetail.Loading)
            brandsUseCase.invoke(name)
                .onSuccess { bank ->
                    listBank = bank
                    _getBanksLiveData.postValue(UIStateConsentDetail.Success(bank))
                }
                .onError {
                    if (it.apiException.httpStatusCode == HTTP_STATUS_403){
                        _getBanksLiveData.postValue(UIStateConsentDetail.ErrorWithoutAccess())
                    }else{
                        _getBanksLiveData.postValue(UIStateConsentDetail.Error())
                    }
                }
        }
    }

    fun filterList(searchString: String) {
        val filteredList = if (searchString.isEmpty()) listBank else filterBankList(searchString)
        _getListFilterLiveData.postValue(UIStateFilterList.ListFiltered(filteredList))
        if (filteredList.isEmpty() && searchString.isNotEmpty()) {
            _getListFilterLiveData.postValue(UIStateFilterList.NotFound(filteredList))
        }
    }

    private fun filterBankList(searchString: String): List<Brand> {
        return listBank.filter {
            it.brand.contains(
                searchString,
                true
            ) || it.institutions?.any { inst ->
                inst.organizationName.contains(
                    searchString,
                    true
                )
            } == true
        }
    }

    fun resetState() {
        _getBanksLiveData.value = null
        _getListFilterLiveData.value = null
    }
}