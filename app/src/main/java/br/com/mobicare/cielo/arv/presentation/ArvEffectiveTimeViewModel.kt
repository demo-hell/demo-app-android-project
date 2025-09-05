package br.com.mobicare.cielo.arv.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetConfigurationUseCase
import kotlinx.coroutines.launch

class ArvEffectiveTimeViewModel (
    private val getConfigurationUseCase: GetConfigurationUseCase,
) : ViewModel() {
    private val _arvEffectiveTimeMutableLiveData = MutableLiveData<String>()
    val arvEffectiveTimeLiveData: LiveData<String> get() = _arvEffectiveTimeMutableLiveData

    init {
        getArvEffectiveTimeLimit()
    }

    private fun getArvEffectiveTimeLimit() {
        viewModelScope.launch {
            getConfigurationUseCase(ARV_EFFECTIVE_TIME, ARV_EFFECTIVE_TIME_DEFAULT)
                .onSuccess {
                    _arvEffectiveTimeMutableLiveData.value = it
                }
                .onEmpty {
                    _arvEffectiveTimeMutableLiveData.value = ARV_EFFECTIVE_TIME_DEFAULT
                }
                .onError {
                    _arvEffectiveTimeMutableLiveData.value = ARV_EFFECTIVE_TIME_DEFAULT
                }
        }
    }

    private companion object {
        const val ARV_EFFECTIVE_TIME = "ARV_EFFECTIVE_TIME"
        const val ARV_EFFECTIVE_TIME_DEFAULT = "15h45"
    }
}