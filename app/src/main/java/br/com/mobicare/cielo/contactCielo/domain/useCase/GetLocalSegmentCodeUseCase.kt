package br.com.mobicare.cielo.contactCielo.domain.useCase

import br.com.mobicare.cielo.contactCielo.domain.repository.SegmentCodeRepository

class GetLocalSegmentCodeUseCase(private val repositoy: SegmentCodeRepository) {
    suspend operator fun invoke() = repositoy.getLocalSegmentCode()
}