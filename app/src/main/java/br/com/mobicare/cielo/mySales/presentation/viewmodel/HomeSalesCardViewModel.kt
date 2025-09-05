package br.com.mobicare.cielo.mySales.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.mySales.data.model.bo.HomeCardSummarySaleBO
import br.com.mobicare.cielo.mySales.data.model.params.GetSalesDataParams
import br.com.mobicare.cielo.mySales.domain.usecase.GetHomeCardSummarySalesUseCase
import br.com.mobicare.cielo.mySales.presentation.utils.MySalesViewState
import kotlinx.coroutines.launch

class HomeSalesCardViewModel(
    private val homeCardSummarySalesUseCase: GetHomeCardSummarySalesUseCase,
    private val userPreferences: UserPreferences,
    private val userObjUseCase: GetUserObjUseCase,
    private val featureTogglePreference: FeatureTogglePreference): ViewModel() {

    private val _getHomeCardSummarySalesViewState = MutableLiveData<MySalesViewState<HomeCardSummarySaleBO>>()
    val getHomeCardSummarySalesViewState: LiveData<MySalesViewState<HomeCardSummarySaleBO>>
        get() = _getHomeCardSummarySalesViewState

    var isRefreshing: Boolean = false

    fun getHomeCardSummarySale(quickFilter: QuickFilter) {
        if(userPreferences.isConvivenciaUser){
            viewModelScope.launch {
                val params = GetSalesDataParams(
                    accessToken = userPreferences.token,
                    authorization = Utils.authorization(),
                    page = null,
                    quickFilter = quickFilter
                )
                _getHomeCardSummarySalesViewState.postValue(MySalesViewState.LOADING)
                homeCardSummarySalesUseCase.invoke(params)
                    .onSuccess {homeCardSummarySaleBO ->
                        _getHomeCardSummarySalesViewState.postValue(MySalesViewState.SUCCESS(homeCardSummarySaleBO))
                    }
                    .onError { apiError ->
                        val newErrorMessage = apiError.apiException.newErrorMessage
                        newErrorHandler(
                            getUserObjUseCase = userObjUseCase,
                            newErrorMessage = newErrorMessage,
                            onHideLoading = {
                                _getHomeCardSummarySalesViewState.postValue(MySalesViewState.HIDE_LOADING) },
                            onErrorAction = {
                                _getHomeCardSummarySalesViewState.postValue(MySalesViewState.ERROR(newErrorMessage = newErrorMessage)) }
                        )


                    }.onEmpty {
                        _getHomeCardSummarySalesViewState.postValue(MySalesViewState.EMPTY)
                    }
            }
        }else
            _getHomeCardSummarySalesViewState.postValue(MySalesViewState.ERROR_FULL_SCREEN())
    }

    fun checkShowSalesWebPage(): Boolean {
        return featureTogglePreference.getFeatureToggleObject(FeatureTogglePreference.SHOW_SALES_WEB)?.show ?: false
    }
}