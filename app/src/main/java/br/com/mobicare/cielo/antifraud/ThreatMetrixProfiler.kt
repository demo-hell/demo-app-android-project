package br.com.mobicare.cielo.antifraud

interface ThreatMetrixProfiler {

    fun init(orgId: String)
//    fun analyzeUserDevice(sessionID: String): Single<String>
    //suspend fun analyzeUserDeviceToSuspend(sessionID: String): CieloDataResult<String>

}