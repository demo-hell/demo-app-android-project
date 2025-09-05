package br.com.mobicare.cielo.antifraud.domain.useCase

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.antifraud.ThreatMetrixProfiler
import br.com.mobicare.cielo.antifraud.domain.repository.AntiFraudRepository
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess

class GetAntiFraudSessionIDUseCase(
    private val repository: AntiFraudRepository,
    private val profiler: ThreatMetrixProfiler
) {

    init {
        profiler.init(BuildConfig.ORG_ID)
    }

    suspend operator fun invoke(): CieloDataResult<String> {
        lateinit var result: CieloDataResult<String>

        repository.getSessionID().onSuccess {
            // result = profiler.analyzeUserDeviceToSuspend(it)
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

}