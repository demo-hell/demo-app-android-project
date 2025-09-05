package br.com.mobicare.cielo.posVirtual.presentation.router

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualStatus
import br.com.mobicare.cielo.posVirtual.domain.model.PosVirtual
import br.com.mobicare.cielo.posVirtual.domain.useCase.GetPosVirtualEligibilityUseCase
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants
import br.com.mobicare.cielo.posVirtual.utils.UIPosVirtualRouterState
import kotlinx.coroutines.launch

class PosVirtualRouterViewModel(
    private val getPosVirtualEligibilityUseCase: GetPosVirtualEligibilityUseCase,
    private val getFeatureTogglePreferenceUseCase: GetFeatureTogglePreferenceUseCase,
    private val getUserObjUseCase: GetUserObjUseCase
) : ViewModel() {

    private val _routerState = MutableLiveData<UIPosVirtualRouterState>()
    val routerState: LiveData<UIPosVirtualRouterState> get() = _routerState

    private var _data: PosVirtual? = null
    val data get() = _data

    fun getEligibility(context: Context?) {
        viewModelScope.launch {
            setLoadingState()
            getPosVirtualEligibilityUseCase()
                .onSuccess {
                    _data = it
                    setLoadingState(false)
                    processSuccessResult(it)
                }.onError {
                    setLoadingState(false)
                    handleErrorMessage(it.apiException.newErrorMessage, context)
                }.onEmpty {
                    setLoadingState(false)
                    setGenericErrorState()
                }
        }
    }

    private fun setLoadingState(isLoading: Boolean = true) {
        _routerState.postValue(UIPosVirtualRouterState.Loading(isLoading))
    }

    private fun processSuccessResult(posVirtual: PosVirtual) {
        _routerState.postValue(
            when (posVirtual.status) {
                PosVirtualStatus.PENDING -> UIPosVirtualRouterState.StatusPending
                PosVirtualStatus.FAILED -> UIPosVirtualRouterState.StatusFailed
                PosVirtualStatus.CANCELED -> UIPosVirtualRouterState.StatusCanceled
                PosVirtualStatus.SUCCESS -> getSuccessState(posVirtual)
                else -> UIPosVirtualRouterState.GenericError()
            }
        )
    }

    private fun getSuccessState(posVirtual: PosVirtual) =
        if (posVirtual.impersonateRequired == true)
            UIPosVirtualRouterState.ImpersonateRequired(posVirtual)
        else
            UIPosVirtualRouterState.StatusSuccess(posVirtual)

    private suspend fun handleErrorMessage(errorMessage: NewErrorMessage, context: Context?) {
        context?.let {
            newErrorHandler(
                context = it,
                getUserObjUseCase = getUserObjUseCase,
                newErrorMessage = errorMessage,
                onErrorAction = {
                    processErrorResult(errorMessage)
                }
            )
        } ?: setGenericErrorState()
    }

    private fun processErrorResult(errorMessage: NewErrorMessage) {
        if (isProductNotFoundError(errorMessage))
            viewModelScope.launch {
                getFeatureTogglePreferenceUseCase(FeatureTogglePreference.POS_VIRTUAL_404)
                    .onSuccess { isTrue ->
                        if (isTrue) _routerState.postValue(UIPosVirtualRouterState.OnBoardingRequired)
                        else setGenericErrorState()
                    }.onError {
                        setGenericErrorState()
                    }
            }
        else if (errorMessage.httpCode == HTTP_ENHANCE)
            setGenericErrorState(errorMessage.message)
        else
            setGenericErrorState()
    }

    private fun isProductNotFoundError(errorMessage: NewErrorMessage) =
        errorMessage.flagErrorCode == PosVirtualConstants.POS_VIRTUAL_ERROR_CODE_PRODUCT_NOT_FOUND ||
                errorMessage.flagErrorCode == PosVirtualConstants.POS_VIRTUAL_ERROR_CODE_NOT_FOUND

    private fun setGenericErrorState(message: String? = null) {
        _routerState.postValue(UIPosVirtualRouterState.GenericError(message))
    }

}