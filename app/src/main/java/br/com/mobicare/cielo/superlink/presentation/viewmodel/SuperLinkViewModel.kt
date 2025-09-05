package br.com.mobicare.cielo.superlink.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.constants.FIVE_HUNDRED
import br.com.mobicare.cielo.commons.constants.FIVE_HUNDRED_NINETY_NINE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.successValueOrNull
import br.com.mobicare.cielo.commons.domain.useCase.GetAccessTokenUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.superlink.domain.usecase.CheckPaymentLinkActiveUseCase
import br.com.mobicare.cielo.superlink.utils.UiSuperLinkState
import kotlinx.coroutines.launch

class SuperLinkViewModel(
    private val checkPaymentLinkActiveUseCase: CheckPaymentLinkActiveUseCase,
    private val getUserObjUseCase: GetUserObjUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
) : ViewModel() {

    private val _paymentLinkActiveState = MutableLiveData<UiSuperLinkState>()
    val paymentLinkActiveState: LiveData<UiSuperLinkState> get() = _paymentLinkActiveState

    fun isPaymentLinkActive() {
        _paymentLinkActiveState.value = UiSuperLinkState.Loading

        viewModelScope.launch {
            val token = getAccessTokenUseCase().successValueOrNull
            val ec = getUserObjUseCase().successValueOrNull?.ec

            if (token.isNullOrBlank() || ec.isNullOrBlank()) {
                _paymentLinkActiveState.value = UiSuperLinkState.Error()
                return@launch
            }

            checkPaymentLinkActiveUseCase()
                .onSuccess {
                    _paymentLinkActiveState.value = UiSuperLinkState.Success
                }.onEmpty {
                    _paymentLinkActiveState.value = UiSuperLinkState.Error()
                }.onError { error ->
                    error.apiException.newErrorMessage.let {
                        _paymentLinkActiveState.value =
                            if (it.httpCode in FIVE_HUNDRED..FIVE_HUNDRED_NINETY_NINE)
                                UiSuperLinkState.Error(it)
                            else
                                UiSuperLinkState.ErrorNotEligible
                    }
                }
        }
    }

}