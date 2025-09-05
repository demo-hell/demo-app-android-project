package br.com.mobicare.cielo.openFinance.presentation.manager.sharedData.consentDetail

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.openFinance.data.model.request.EndShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.Brand
import br.com.mobicare.cielo.openFinance.domain.model.ConsentDetail
import br.com.mobicare.cielo.openFinance.domain.model.InfoDetailsShare
import br.com.mobicare.cielo.openFinance.domain.model.Institution
import br.com.mobicare.cielo.openFinance.domain.usecase.ConsentDetailUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.EndShareUseCase
import br.com.mobicare.cielo.openFinance.presentation.utils.CheckStatus
import br.com.mobicare.cielo.openFinance.presentation.utils.StatusEnum
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConsentDetail
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConsentStatus
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateEndShare
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateShowOptions
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.FLOW_RECEIVER
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.RECEIVING_JOURNEY
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.RENEW_SHARE
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.gson.Gson
import kotlinx.coroutines.launch

class ConsentDetailViewModel(
    private val consentDetailUseCase: ConsentDetailUseCase,
    private val endShareUseCase: EndShareUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _getConsentDetailLiveData =
        MutableLiveData<UIStateConsentDetail<ConsentDetail>>()
    val getConsentDetailLiveData get() = _getConsentDetailLiveData

    private val _getShowOptionsLiveData =
        MutableLiveData<UIStateShowOptions<Boolean>>()
    val getShowOptionsLiveData get() = _getShowOptionsLiveData

    private val _consentDetailStatus = MutableLiveData<UIStateConsentStatus<ConsentDetail>>()
    val consentDetailStatus get() = _consentDetailStatus

    private val _endShareLiveData = MutableLiveData<UIStateEndShare>()
    val endShareLiveData get() = _endShareLiveData

    private var brandToChangeOrRenew: Brand? = null
    private var consentDetail: ConsentDetail? = null

    fun getConsentDetail(consentId: String, context: Context) {
        viewModelScope.launch {
            _getConsentDetailLiveData.postValue(UIStateConsentDetail.Loading)
            consentDetailUseCase.invoke(consentId)
                .onSuccess {
                    _getConsentDetailLiveData.postValue(UIStateConsentDetail.Success(it))
                    checkConsentStatus(it, context)
                    checkVisibleOptions(it.flow)
                    consentDetail = it
                    mountBrandToChangeOrRenew(it)
                }.onError {
                    _getConsentDetailLiveData.postValue(UIStateConsentDetail.Error())
                }
        }
    }

    private fun checkConsentStatus(consentDetail: ConsentDetail, context: Context) {
        when (CheckStatus.getStatus(context, consentDetail.consentSatus)) {
            StatusEnum.ACTIVE -> {
                _consentDetailStatus.postValue(UIStateConsentStatus.Active)
            }

            StatusEnum.EXPIRED -> {
                _consentDetailStatus.postValue(UIStateConsentStatus.Expired(consentDetail))
            }

            StatusEnum.CLOSED -> {
                _consentDetailStatus.postValue(UIStateConsentStatus.Closed(consentDetail))
            }

            else -> {
                _consentDetailStatus.postValue(UIStateConsentStatus.Empty)
            }
        }
    }

    private fun checkVisibleOptions(journey: String) {
        if (journey.equals(RECEIVING_JOURNEY)) {
            _getShowOptionsLiveData.postValue(UIStateShowOptions.ShowOptions(true))
        } else {
            _getShowOptionsLiveData.postValue(UIStateShowOptions.HideOptions(false))
        }
    }

    private fun mountBrandToChangeOrRenew(consentDetail: ConsentDetail) {
        val listInstitutions = Institution(
            consentDetail.organizationId,
            consentDetail.brand,
            EMPTY,
            consentDetail.authorizationServerId,
            EMPTY
        )
        brandToChangeOrRenew = Brand(consentDetail.brand, listOf(listInstitutions))
    }

    fun getBrandToChangeOrRenew(): Brand? {
        return brandToChangeOrRenew
    }

    fun saveInfoDetailsShare(function: String) {
        val infoDetailsShare = consentDetail?.let {
            InfoDetailsShare(
                function,
                it.consentId,
                it.shareId,
                FLOW_RECEIVER,
                it.deadLine
            )
        }
        userPreferences.saveInfoDetailsShare(Gson().toJson(infoDetailsShare))
    }

    fun endShare(otpCode: String) {
        val requestEndShare = EndShareRequest(
            function = RENEW_SHARE,
            consentId = consentDetail?.consentId ?: EMPTY,
            shareId = consentDetail?.shareId ?: EMPTY,
            flow = consentDetail?.flow ?: EMPTY
        )
        viewModelScope.launch {
            _endShareLiveData.postValue(UIStateEndShare.LoadingEndShare)
            endShareUseCase.invoke(otpCode, requestEndShare)
                .onSuccess {
                    _endShareLiveData.postValue(UIStateEndShare.SuccessEndShare)
                }.onError {
                    if (it.apiException.httpStatusCode == 403) {
                        _endShareLiveData.postValue(UIStateEndShare.WithoutAccessEndShare)
                    } else {
                        _endShareLiveData.postValue(UIStateEndShare.ErrorEndShare)
                    }
                }
        }
    }
}