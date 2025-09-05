package br.com.mobicare.cielo.contactCielo.domain.useCase

import br.com.mobicare.cielo.contactCielo.domain.repository.SegmentCodeRepository

class SaveLocalSegmentCodeUseCase(private val repositoy: SegmentCodeRepository) {
    suspend operator fun invoke(segmentCode: String) = repositoy.setLocalSegmentCode(segmentCode)
}