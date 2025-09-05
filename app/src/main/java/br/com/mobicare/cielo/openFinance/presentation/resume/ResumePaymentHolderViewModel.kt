package br.com.mobicare.cielo.openFinance.presentation.resume

import android.text.format.DateUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.THIRTY_TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.ValidationUtils.isCNPJ
import br.com.mobicare.cielo.commons.utils.ValidationUtils.isCPF
import br.com.mobicare.cielo.commons.utils.ValidationUtils.isEmail
import br.com.mobicare.cielo.commons.utils.ValidationUtils.isValidPhoneNumber
import br.com.mobicare.cielo.commons.utils.justDate
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidBalanceResponse
import br.com.mobicare.cielo.openFinance.data.model.request.ConsentIdRequest
import br.com.mobicare.cielo.openFinance.data.model.request.RejectConsentRequest
import br.com.mobicare.cielo.openFinance.data.model.response.ConsentResponse
import br.com.mobicare.cielo.openFinance.data.model.response.DetainerResponse
import br.com.mobicare.cielo.openFinance.domain.usecase.ApproveConsentUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.GetDetainerUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.GetUserCardBalanceUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.RejectConsentUseCase
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateApproveConsent
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateRejectConsent
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateResumeDetainer
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateShowPixKey
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.APPROVED_PAYMENT
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.EXPIRED_CONSENT
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.INSUFFICIENT_FUNDS
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.INSUFFICIENT_FUNDS_DETAIL
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.REJECTED_PAYMENT
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ResumePaymentHolderViewModel(
    private val useCaseDetainer: GetDetainerUseCase,
    private val useCaseCardBalanceUseCase: GetUserCardBalanceUseCase,
    private val useCaseApproveConsent: ApproveConsentUseCase,
    private val useCaseRejectConsent: RejectConsentUseCase,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _getResumeDetainerLiveData =
        MutableLiveData<UIStateResumeDetainer<DetainerResponse>>()
    val getResumeDetainerLiveData get() = _getResumeDetainerLiveData

    private val _getCardsBalanceLiveData =
        MutableLiveData<PrepaidBalanceResponse>()
    val getCardsBalanceLiveData get() = _getCardsBalanceLiveData

    private val _approveConsentLiveData =
        MutableLiveData<UIStateApproveConsent<ConsentResponse>>()
    val approveConsentLiveData get() = _approveConsentLiveData

    private val _rejectConsentLiveData =
        MutableLiveData<UIStateRejectConsent<ConsentResponse>>()
    val rejectConsentLiveData get() = _rejectConsentLiveData

    private val _pixKey =
        MutableLiveData<UIStateShowPixKey<Int>>()
    val pixKey get() = _pixKey

    lateinit var detainer: DetainerResponse
    var balance: Double = ZERO_DOUBLE

    fun getDetainer() {
        viewModelScope.launch {
            userPreferences.holderIntentId?.let { holderIntentId ->
                _getResumeDetainerLiveData.postValue(UIStateResumeDetainer.Loading)
                useCaseDetainer.invoke(holderIntentId)
                    .onSuccess { response ->
                        if (response.status.contains(APPROVED_PAYMENT)) {
                            _getResumeDetainerLiveData.postValue(UIStateResumeDetainer.InvalidPaymentAlreadyAuthorized())
                        } else if (response.status.contains(REJECTED_PAYMENT)) {
                            _getResumeDetainerLiveData.postValue(UIStateResumeDetainer.PaymentRequestRejected())
                        } else if (response.status.contains(EXPIRED_CONSENT)) {
                            _getResumeDetainerLiveData.postValue(UIStateResumeDetainer.PaymentTimeOver())
                        } else if (getCardsBalance(response.payment.detail.proxy)) {
                            _getResumeDetainerLiveData.postValue(
                                UIStateResumeDetainer.Success(response)
                            )
                            detainer = response
                        } else {
                            _getResumeDetainerLiveData.postValue(UIStateResumeDetainer.Error())
                        }
                    }
                    .onError {
                        if (it.apiException.httpStatusCode == NetworkConstants.HTTP_STATUS_403) {
                            _getResumeDetainerLiveData.postValue(UIStateResumeDetainer.WithoutAccess())
                        } else {
                            _getResumeDetainerLiveData.postValue(UIStateResumeDetainer.Error())
                        }
                    }
            }
        }
    }

    suspend fun getCardsBalance(proxy: String): Boolean {
        var result = false
        useCaseCardBalanceUseCase.invoke(proxy)
            .onSuccess { cardsBalance ->
                _getCardsBalanceLiveData.postValue(cardsBalance)
                balance = cardsBalance.amount
                result = true
            }
            .onError {
                result = false
            }
        return result
    }

    fun getPayer(): String {
        return userPreferences.userInformation?.activeMerchant?.name ?: ""
    }

    fun getCNPJ(): String {
        return userPreferences.userInformation?.activeMerchant?.cnpj?.number ?: ""
    }

    fun verifyPixKey(pixKey: String): Int {
        return when {
            isCNPJ(pixKey) || isCPF(pixKey) -> R.drawable.ic_cnh
            isEmail(pixKey) -> R.drawable.ic_email_pix_16dp
            isValidPhoneNumber(pixKey) -> R.drawable.ic_phone_key_pix_16_dp
            pixKey.length == THIRTY_TWO -> R.drawable.ic_key
            else -> ZERO
        }
    }

    fun showOrHidePixKey(pixKey: String) {
        val result = verifyPixKey(pixKey)
        if (result == ZERO) {
            _pixKey.postValue(UIStateShowPixKey.HidePixKey())
        } else {
            _pixKey.postValue(UIStateShowPixKey.ShowPixKey(result))
        }
    }

    fun paymentIsEligible(token: String) {
        val datePayment =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(detainer?.payment.date)
        val today = Calendar.getInstance().time.justDate()
        if (DateUtils.isToday(datePayment.time) && detainer?.payment.amount > balance) {
            rejectConsent(INSUFFICIENT_FUNDS_DETAIL, INSUFFICIENT_FUNDS)
        } else if (detainer?.payment.amount <= balance || datePayment.after(today)) {
            approveConsent(token)
        }
    }

    fun approveConsent(token: String) {
        viewModelScope.launch {
            userPreferences.holderIntentId?.let {
                _approveConsentLiveData.postValue(UIStateApproveConsent.Loading)
                val consentId = ConsentIdRequest(consentId = it)
                useCaseApproveConsent.invoke(consentId, token).onSuccess { approveConsentResponse ->
                    _approveConsentLiveData.postValue(
                        UIStateApproveConsent.Success(
                            approveConsentResponse
                        )
                    )
                }.onError {
                    _approveConsentLiveData.postValue(UIStateApproveConsent.ErrorPaymentInProgress())
                }
            }
        }
    }

    fun rejectConsent(detail: String, code: String) {
        viewModelScope.launch {
            userPreferences.holderIntentId?.let { consentId ->
                _rejectConsentLiveData.postValue(UIStateRejectConsent.Loading)
                val rejectConsentRequest =
                    RejectConsentRequest(consentId, detail, code)
                useCaseRejectConsent.invoke(rejectConsentRequest)
                    .onSuccess { consentResponse ->
                        _rejectConsentLiveData.postValue(
                            UIStateRejectConsent.Success(
                                consentResponse
                            )
                        )
                    }.onError {
                        _rejectConsentLiveData.postValue(UIStateRejectConsent.Error())
                    }
            }
        }

    }
}