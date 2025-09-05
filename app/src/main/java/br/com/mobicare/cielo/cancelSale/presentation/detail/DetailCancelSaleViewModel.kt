package br.com.mobicare.cielo.cancelSale.presentation.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.cancelSale.data.model.request.BalanceInquiryRequest
import br.com.mobicare.cielo.cancelSale.data.model.request.CancelSaleRequest
import br.com.mobicare.cielo.cancelSale.domain.model.BalanceInquiry
import br.com.mobicare.cielo.cancelSale.domain.model.CancelSale
import br.com.mobicare.cielo.cancelSale.domain.usecase.BalanceInquiryUseCase
import br.com.mobicare.cielo.cancelSale.domain.usecase.CancelSaleUseCase
import br.com.mobicare.cielo.cancelSale.presentation.utils.UIStateCancelSale
import br.com.mobicare.cielo.cancelSale.presentation.utils.UIStateBalanceInquiry
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleConstants.WITHOUT_BALANCE
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWENTY_FIVE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_403
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_420
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import kotlinx.coroutines.launch

class DetailCancelSaleViewModel(
    private val balanceInquiryUseCase: BalanceInquiryUseCase,
    private val cancelSaleUseCase: CancelSaleUseCase
) :
    ViewModel() {

    private val _getBalanceInquiryLiveData =
        MutableLiveData<UIStateBalanceInquiry<BalanceInquiry>>()
    val getBalanceInquiryLiveData get() = _getBalanceInquiryLiveData

    private val _cancelSaleLiveData =
        MutableLiveData<UIStateCancelSale<CancelSale>>()
    val cancelSaleLiveData get() = _cancelSaleLiveData

    private var balanceInquiry: BalanceInquiry? = null
    private var error : NewErrorMessage? = null

    fun getBalanceInquiry(
        cardBrandCode: String,
        authorizationCode: String,
        nsu: String,
        truncatedCardNumber: String,
        authorizationDate: String,
        paymentTypeCode: String,
        grossAmount: String,
        saleMerchant: String
    ) {
        val request = BalanceInquiryRequest(
            cardBrandCode,
            authorizationCode,
            nsu,
            truncatedCardNumber,
            authorizationDate,
            authorizationDate,
            paymentTypeCode,
            grossAmount,
            ONE,
            TWENTY_FIVE,
        )
        viewModelScope.launch {
            _getBalanceInquiryLiveData.postValue(UIStateBalanceInquiry.Loading)
            balanceInquiryUseCase.invoke(request).onSuccess {
                balanceInquiry = it
                _getBalanceInquiryLiveData.postValue(UIStateBalanceInquiry.Success(it))
            }.onError {
                error = it.apiException.newErrorMessage
                if (it.apiException.newErrorMessage.flagErrorCode.contains(WITHOUT_BALANCE)) {
                    _getBalanceInquiryLiveData.postValue(UIStateBalanceInquiry.ErrorSaleHasBeenCancelled)
                } else {
                    _getBalanceInquiryLiveData.postValue(UIStateBalanceInquiry.Error)
                }
            }
        }
    }

    fun cancelSale(valueToCancel: Double, currentDateCancel: String, otpCode: String) {
        val request = CancelSaleRequest(
            balanceInquiry?.grossAmount,
            balanceInquiry?.saleDate,
            balanceInquiry?.cardBrandCode,
            balanceInquiry?.productCode,
            balanceInquiry?.authorizationCode,
            balanceInquiry?.nsu,
            valueToCancel,
            currentDateCancel
        )

        viewModelScope.launch {
            cancelSaleUseCase.invoke(otpCode, arrayListOf(request))
                .onSuccess {
                    _cancelSaleLiveData.postValue(UIStateCancelSale.Success(it))
                }.onError {
                    if (it.apiException.httpStatusCode == HTTP_STATUS_403 || it.apiException.httpStatusCode == HTTP_STATUS_420) {
                        val cancelSale = CancelSale(
                            null,
                            it.apiException.newErrorMessage.httpCode.toString(),
                            it.apiException.newErrorMessage.message
                        )
                        error = it.apiException.newErrorMessage
                        _cancelSaleLiveData.postValue(UIStateCancelSale.ErrorEspecify(cancelSale))
                    } else {
                        error = it.apiException.newErrorMessage
                        _cancelSaleLiveData.postValue(UIStateCancelSale.ErrorGeneric)
                    }
                }
        }
    }

    fun getError(): NewErrorMessage? {
        return error
    }
}