package br.com.mobicare.cielo.antifraud

import android.content.Context
import org.koin.standalone.KoinComponent

class ThreatMetrixProfilerImpl(private val context: Context) : ThreatMetrixProfiler, KoinComponent {

//    private var tmxProfiling: TMXProfiling? = null
//
//    private var tmxProfileHandler: TMXProfilingHandle? = null

    override fun init(orgId: String) {
//        tmxProfiling = TMXProfiling.getInstance()
//        tmxProfiling?.init(getTmxConfig(orgId))
    }

//    private fun getTmxConfig(
//        orgId: String,
//        timeOut: Int = TIME_OUT,
//        retryTime: Int = RETRY_TIMES
//    ): TMXConfig {
//        val profilingConnections = TMXProfilingConnections().apply {
//            setRetryTimes(retryTime)
//        }
//        return TMXConfig()
//            .setOrgId(orgId)
//            .setContext(context)
//            .setProfileTimeout(timeOut, TimeUnit.SECONDS)
//            .setProfilingConnections(profilingConnections)
//    }

//    override fun analyzeUserDevice(sessionID: String): Single<String> {
//        return Single.create { emitter ->
//
//            tmxProfileHandler?.let {
//                it.cancel()
//                tmxProfileHandler = null
//            }
//
//            val txmProfileOptions = TMXProfilingOptions()
//            txmProfileOptions.setSessionID(sessionID)
//            tmxProfileHandler = tmxProfiling?.profile(txmProfileOptions) {
//                when (it.status) {
//                    TMXStatusCode.TMX_OK -> emitter.onSuccess(it.sessionID)
//                    else -> {
//                        val message = "$ERROR_MESSAGE: ${it.status}"
//                        emitter.onError(Exception(message))
//                        FirebaseCrashlytics.getInstance()
//                            .log(message)
//                    }
//                }
//            }
//        }
//    }

//    override suspend fun analyzeUserDeviceToSuspend(sessionID: String): CieloDataResult<String> {
//        return suspendCoroutine {
//            try {
//                val result = analyzeUserDevice(sessionID).blockingGet()
//                it.resume(CieloDataResult.Success(result))
//            } catch (e: Exception) {
//                it.resume(CieloDataResult.Empty())
//            }
//        }
//    }

    companion object {
        private const val TIME_OUT = 10
        private const val RETRY_TIMES = 2
        const val ERROR_MESSAGE = "TMX_PROFILER_ERROR"
    }
}