package br.com.mobicare.cielo.antifraud

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.tapOnPhone.domain.repository.TapOnPhoneAccreditationRepository
import org.koin.standalone.KoinComponent

class KYCAntiFraudIntegration(
    private val profiler: ThreatMetrixProfiler,
    private val repository: TapOnPhoneAccreditationRepository
) : KYCAntiFraudContract, KoinComponent {

    init {
        profiler.init(BuildConfig.ORG_ID)
    }

//    override fun analyzeUserSession(): Observable<String> {
//        return repository.getSessionId()
//            .flatMap { response ->
//                val sessionId = response.sessionID ?: throw Exception(SESSION_ID_REQUEST_ERROR)
//                profiler.analyzeUserDevice(sessionId).toObservable()
//            }
//    }

    private companion object {
        const val SESSION_ID_REQUEST_ERROR = "SessionID request error"
    }
}
