package br.com.mobicare.cielo.contactCielo.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.contactCielo.data.datasource.SegmentCodeRemoteSource
import br.com.mobicare.cielo.contactCielo.data.datasource.local.SegmentCodeLocalSource
import br.com.mobicare.cielo.contactCielo.domain.repository.SegmentCodeRepository

class SegmentCodeRepositoryImpl(
    private val segmentCodeRemoteSource: SegmentCodeRemoteSource,
    private val segmentCodeLocalSource: SegmentCodeLocalSource
) :
    SegmentCodeRepository {
    override suspend fun getRemoteSegmentCode(): CieloDataResult<String> =
        segmentCodeRemoteSource.getSegmentCode()

    override suspend fun getLocalSegmentCode(): CieloDataResult<String> =
        segmentCodeLocalSource.getLocalSegmentCode()

    override suspend fun setLocalSegmentCode(segmentCode: String) {
        segmentCodeLocalSource.setLocalSegmentCode(segmentCode)
    }

    override suspend fun removeLocalSegmentCode() {
        segmentCodeLocalSource.removeLocalSegmentCode()
    }
}