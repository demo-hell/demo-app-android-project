package br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureToggleUseCase
import kotlinx.coroutines.launch

class RaD1MigrationEffectiveTimeViewModel (
    private val getFeatureToggleUseCase: GetFeatureToggleUseCase,
) : ViewModel() {
    private val effectiveTimeMutableLiveData = MutableLiveData<String>()
    val effectiveTimeLiveData: LiveData<String> get() = effectiveTimeMutableLiveData

    init {
        getEffectiveTime()
    }

    private fun getEffectiveTime() {
        viewModelScope.launch {
            getFeatureToggleUseCase(RA_D1_MIGRATION_FT_KEY)
                .onSuccess {
                    effectiveTimeMutableLiveData.value = it.statusMessage ?: EFFECTIVE_TIME_DEFAULT
                }
                .onEmpty {
                    effectiveTimeMutableLiveData.value = EFFECTIVE_TIME_DEFAULT
                }
                .onError {
                    effectiveTimeMutableLiveData.value = EFFECTIVE_TIME_DEFAULT
                }
        }
    }

    companion object {
        const val RA_D1_MIGRATION_FT_KEY = "ra_d1_migration"
        const val EFFECTIVE_TIME_DEFAULT = "3 dias úteis"
    }
}