package br.com.mobicare.cielo.contactCielo.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface SegmentCodeRepository {
    suspend fun getRemoteSegmentCode(): CieloDataResult<String>
    suspend fun getLocalSegmentCode(): CieloDataResult<String>
    suspend fun setLocalSegmentCode(segmentCode: String)
    suspend fun removeLocalSegmentCode()
}