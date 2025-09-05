package br.com.mobicare.cielo.simulator.simulation.presentation.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureToggleUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.SHOW_SALE_SIMULATOR_RECEIVE_TOTAL_VALUE_FT_KEY
import kotlinx.coroutines.launch

class SaleSimulatorReceiveTotalValueViewModel (
    private val getFeatureToggleUseCase: GetFeatureToggleUseCase,
) : ViewModel() {
    private val receiveTotalValueMutableLiveData = MutableLiveData<Pair<Boolean,String>>()
    val receiveTotalValueLiveData: LiveData<Pair<Boolean,String>> get() = receiveTotalValueMutableLiveData

    init {
        getShowReceiveTotalValue()
    }

    private fun getShowReceiveTotalValue() {
        viewModelScope.launch {
            getFeatureToggleUseCase(SHOW_SALE_SIMULATOR_RECEIVE_TOTAL_VALUE_FT_KEY)
                .onSuccess {
                    receiveTotalValueMutableLiveData.value = Pair(it.show, it.statusMessage ?: MESSAGE_DEFAULT)
                }
                .onEmpty {
                    receiveTotalValueMutableLiveData.value = Pair(SHOW_DEFAULT, MESSAGE_DEFAULT)
                }
        }
    }

    companion object {
        const val MESSAGE_DEFAULT = "Para receber o valor total que simulou, cobre:"
        const val SHOW_DEFAULT = false
    }
}