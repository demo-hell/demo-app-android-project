package br.com.mobicare.cielo.component.impersonate.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetMeInformationUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetMenuUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.domain.useCase.userPreferences.PutUserPreferencesUseCase
import br.com.mobicare.cielo.commons.utils.UILoadingState
import br.com.mobicare.cielo.component.impersonate.data.model.request.ImpersonateRequest
import br.com.mobicare.cielo.component.impersonate.data.model.response.MerchantResponse
import br.com.mobicare.cielo.component.impersonate.domain.usecase.PostImpersonateUseCase
import br.com.mobicare.cielo.component.impersonate.presentation.model.MerchantUI
import br.com.mobicare.cielo.component.impersonate.utils.TypeImpersonateEnum
import br.com.mobicare.cielo.component.impersonate.utils.UIImpersonateState
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.POS_VIRTUAL_WHITE_LIST
import br.com.mobicare.cielo.pix.constants.EMPTY
import kotlinx.coroutines.launch

class ImpersonateViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val getMenuUseCase: GetMenuUseCase,
    private val getFeatureTogglePreferenceUseCase: GetFeatureTogglePreferenceUseCase,
    private val getMeInformationUseCase: GetMeInformationUseCase,
    private val postImpersonateUseCase: PostImpersonateUseCase,
    private val putUserPreferencesUseCase: PutUserPreferencesUseCase,
) : ViewModel() {

    private val _impersonateState = MutableLiveData<UIImpersonateState>()
    val impersonateState: LiveData<UIImpersonateState> get() = _impersonateState

    private val _loadingState = MutableLiveData<UILoadingState>()
    val loadingState: LiveData<UILoadingState> get() = _loadingState

    private var _merchantsUI: ArrayList<MerchantUI> = ArrayList()
    val merchantsUI get() = _merchantsUI

    private var _merchantSelected: String = EMPTY

    fun setMerchantsUI(list: List<MerchantResponse>) {
        _merchantsUI.clear()
        _merchantsUI.addAll(
            list.map { merchant ->
                MerchantUI(
                    merchant.id.orEmpty(),
                    merchant.name,
                    merchant.document,
                )
            }
        )
    }

    fun selectMerchant(id: String) {
        _merchantSelected = id
    }

    fun impersonate(fingerprint: String, typeImpersonateEnum: TypeImpersonateEnum?, flowOpenFinance: Boolean?) {
        _loadingState.value = UILoadingState.ShowLoading

        viewModelScope.launch {
            postImpersonateUseCase(
                _merchantSelected,
                typeImpersonateEnum?.name ?: TypeImpersonateEnum.HIERARCHY.name,
                ImpersonateRequest(fingerprint)
            )
                .onSuccess {
                    it.accessToken?.let { token ->
                        saveAccessToken(token)
                    } ?: run {
                        setError(isLogoutError = false)
                    }
                }
                .onEmpty {
                    setError(isLogoutError = false)
                }
                .onError {
                    if (it.apiException.httpStatusCode == NetworkConstants.HTTP_STATUS_420 && flowOpenFinance == true){
                        setWithoutAccess()
                    }else{
                        handleError(it.apiException.newErrorMessage, isLogoutError = false)
                    }
                }
        }
    }

    private suspend fun saveAccessToken(accessToken: String) {
        putUserPreferencesUseCase(UserPreferences.USER_TOKEN, accessToken, isProtected = true)
            .onSuccess {
                if (it) {
                    getMeInformation()
                } else {
                    setError(isLogoutError = false)
                }
            }
    }

    private suspend fun getMeInformation() {
        getMeInformationUseCase(isLocal = false)
            .onSuccess {
                _impersonateState.value = UIImpersonateState.SendMessageUpdateMainBottomNavigation
                getFeatureToggleWhiteList()
            }
            .onError {
                handleError(it.apiException.newErrorMessage, isLogoutError = true)
            }
            .onEmpty {
                setError(isLogoutError = true)
            }
    }

    private suspend fun getFeatureToggleWhiteList() {
        getFeatureTogglePreferenceUseCase(POS_VIRTUAL_WHITE_LIST)
            .onSuccess {
                getMenu(it)
            }
    }

    private suspend fun getMenu(ftTapOnPhoneWhiteList: Boolean) {
        getMenuUseCase(isLocal = false, ftTapOnPhoneWhiteList)
            .onSuccess {
                _impersonateState.value = UIImpersonateState.Success
                _loadingState.value = UILoadingState.HideLoading
            }
            .onError {
                handleError(it.apiException.newErrorMessage, isLogoutError = true)
            }
            .onEmpty {
                setError(isLogoutError = true)
            }
    }

    private suspend fun handleError(
        error: NewErrorMessage,
        isLogoutError: Boolean
    ) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error,
            onErrorAction = {
                setError(isLogoutError)
            },
            onHideLoading = {
                _loadingState.value = UILoadingState.HideLoading
            }
        )
    }

    private fun setError(isLogoutError: Boolean) {
        _loadingState.value = UILoadingState.HideLoading
        _impersonateState.value = if (isLogoutError) {
            UIImpersonateState.LogoutError
        } else {
            UIImpersonateState.ImpersonateError
        }
    }

    private fun setWithoutAccess() {
        _loadingState.value = UILoadingState.HideLoading
        _impersonateState.value = UIImpersonateState.WithoutAccess
    }

}