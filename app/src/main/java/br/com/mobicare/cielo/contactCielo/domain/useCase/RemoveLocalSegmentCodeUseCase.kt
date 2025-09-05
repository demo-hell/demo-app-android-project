package br.com.mobicare.cielo.contactCielo.domain.useCase

import br.com.mobicare.cielo.contactCielo.domain.repository.SegmentCodeRepository

class RemoveLocalSegmentCodeUseCase(private val repositoy: SegmentCodeRepository) {
    suspend operator fun invoke() = repositoy.removeLocalSegmentCode()
}