package br.com.mobicare.cielo.openFinance.presentation.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.component.impersonate.data.model.response.MerchantResponse
import br.com.mobicare.cielo.openFinance.domain.model.PixMerchantListResponse
import br.com.mobicare.cielo.openFinance.domain.usecase.GetPixMerchantListOpenFinanceUseCase
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateMerchantList
import kotlinx.coroutines.launch

class OpenFinanceHomeViewModel(
    private val useCase: GetPixMerchantListOpenFinanceUseCase
) : ViewModel() {
    private val _getPixMerchantAccountListLiveData =
        MutableLiveData<UIStateMerchantList<List<MerchantResponse>>>()
    val getPixMerchantAccountListLiveData get() = _getPixMerchantAccountListLiveData
    
    fun getPixMerchantAccountList() {
        viewModelScope.launch {
            _getPixMerchantAccountListLiveData.postValue(UIStateMerchantList.Loading)
            useCase.invoke()
                .onSuccess { pixMerchantOpenFinanceBO ->
                    _getPixMerchantAccountListLiveData.postValue(
                        UIStateMerchantList.Success(
                            parserToImpersonateModel(pixMerchantOpenFinanceBO)
                        )
                    )
                }
                .onError {
                    if (it.apiException.httpStatusCode == NetworkConstants.HTTP_STATUS_404) {
                        _getPixMerchantAccountListLiveData.postValue(UIStateMerchantList.NotFound())
                    } else {
                        _getPixMerchantAccountListLiveData.postValue(UIStateMerchantList.Error())
                    }
                }
        }
    }

    fun parserToImpersonateModel(pixMerchantOpenFinanceBO: List<PixMerchantListResponse>): List<MerchantResponse> {
        return pixMerchantOpenFinanceBO.map {
            MerchantResponse(id = it.merchantNumber, name = it.name, document = it.documentNumber)
        }.toList()
    }
}