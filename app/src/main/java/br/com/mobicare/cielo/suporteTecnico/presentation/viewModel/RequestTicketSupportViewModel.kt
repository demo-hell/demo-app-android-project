package br.com.mobicare.cielo.suporteTecnico.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.constants.FIVE
import br.com.mobicare.cielo.commons.constants.TWENTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.suporteTecnico.data.UserOwnerSupportResponse
import br.com.mobicare.cielo.suporteTecnico.domain.useCase.GetRequestTicketSupportUseCase
import br.com.mobicare.cielo.suporteTecnico.utils.UIStateRequestTicketSupport
import kotlinx.coroutines.launch


class RequestTicketSupportViewModel(
    private val requestTicketSupportUseCase: GetRequestTicketSupportUseCase
) : ViewModel() {

    private val _merchantLiveData = MutableLiveData<UIStateRequestTicketSupport>()
    val merchantLiveData: LiveData<UIStateRequestTicketSupport> get() = _merchantLiveData

    fun getMerchant() {
        _merchantLiveData.value = UIStateRequestTicketSupport.Loading

        viewModelScope.launch {
            requestTicketSupportUseCase()
                .onSuccess {
                    verifyRequestTicketSupport(it)
                }
                .onError {
                    _merchantLiveData.postValue(UIStateRequestTicketSupport.Error)
                }
                .onEmpty {
                    _merchantLiveData.postValue(UIStateRequestTicketSupport.Empty)
                }
        }
    }

    fun verifyRequestTicketSupport(userOwnerSupportResponse: UserOwnerSupportResponse) {
        val isBlock = userOwnerSupportResponse.blocks.any { block ->
            block.codeType == FIVE && block.codeReason == TWENTY
        }

        _merchantLiveData.postValue(
            if (isBlock) {
                UIStateRequestTicketSupport.AuthorizationError
            } else {
                UIStateRequestTicketSupport.AuthorizationSuccess
            }
        )

    }
}
