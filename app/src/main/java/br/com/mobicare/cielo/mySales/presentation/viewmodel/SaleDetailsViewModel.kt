package br.com.mobicare.cielo.mySales.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.data.model.bo.SalesMerchantBO
import br.com.mobicare.cielo.mySales.domain.usecase.GetSaleMerchantUseCase
import br.com.mobicare.cielo.mySales.presentation.utils.MySalesViewState
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.component.impersonate.data.model.request.ImpersonateRequest
import br.com.mobicare.cielo.component.impersonate.domain.usecase.PostImpersonateUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.mySales.data.model.params.GetMerchantParams
import br.com.mobicare.cielo.mySales.presentation.utils.HIERARCHYTYPE_NODE
import kotlinx.coroutines.launch

class SaleDetailsViewModel(
    private val getSaleMerchantUseCase: GetSaleMerchantUseCase,
    private val impersonateUseCase: PostImpersonateUseCase,
    private val userPreferences: UserPreferences,
    private val menuPreferences: MenuPreference,
    private val featureToggleUseCase: GetFeatureTogglePreferenceUseCase
): ViewModel() {

    private val _getSaleMerchantViewState = MutableLiveData<MySalesViewState<SalesMerchantBO>>()
    val getSaleMerchantViewState: LiveData<MySalesViewState<SalesMerchantBO>>
        get() = _getSaleMerchantViewState


    private lateinit var sale: Sale
    private lateinit var fingerprint: String
    var useSecurityHash: Boolean = false

    init {
        getUeSecurityHashFeatureToggle()
    }


    fun createSaleDetailsStatement(sale: Sale, fingerprint: String){
        this.sale = sale
        this.fingerprint = fingerprint
        verifyMerchant()
    }


    private fun verifyMerchant(){
        sale.merchantId?.let { merchantEc ->
            menuPreferences.getEstablishment()?.let {
                if(it.ec != merchantEc) {
                    sale.paymentNode?.let { paymentNode ->
                        impersonateToMerchantId(paymentNode.toString())
                    }
                }else {
                    getMerchant(merchantEc)
                }
            }
        } ?: loadMerchantWithDefaultToken()
    }


    private fun getMerchant(token: String) {
        viewModelScope.launch {
            val params = GetMerchantParams(
                access_token = token,
                authorization = Utils.authorization()
            )
            getSaleMerchantUseCase.invoke(params)
                .onSuccess {
                    _getSaleMerchantViewState.postValue(MySalesViewState.SUCCESS(it))
                }.onError {
                    _getSaleMerchantViewState.postValue(MySalesViewState.ERROR())
                }
        }
    }

    private fun impersonateToMerchantId(id: String){
        viewModelScope.launch {
            val impersonateRequest = ImpersonateRequest( fingerprint = fingerprint )
            impersonateUseCase.invoke(
                ec = id,
                type = HIERARCHYTYPE_NODE,
                impersonateRequest = impersonateRequest
            )
                .onSuccess {
                    it.accessToken?.let { merchantToken ->
                        getMerchant(merchantToken)
                    }
                }
                .onError {
                    _getSaleMerchantViewState.postValue(MySalesViewState.ERROR())
                }
        }
    }

    private fun loadMerchantWithDefaultToken() {
        val token = userPreferences.token
        getMerchant(token)
    }


    private fun getUeSecurityHashFeatureToggle() {
        viewModelScope.launch {
            featureToggleUseCase(key = FeatureTogglePreference.SECURITY_HASH).onSuccess {
                useSecurityHash = it
            }.onError {
                useSecurityHash = false
            }
        }
    }
}