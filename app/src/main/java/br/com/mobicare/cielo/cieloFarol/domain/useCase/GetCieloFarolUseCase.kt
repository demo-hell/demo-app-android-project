package br.com.mobicare.cielo.cieloFarol.domain.useCase

import br.com.mobicare.cielo.cieloFarol.domain.repository.CieloFarolRepository

class GetCieloFarolUseCase(private val repository: CieloFarolRepository) {
    suspend operator fun invoke(
            authorization: String,
            merchantId: String?
    ) = repository.getCieloFarol(authorization, merchantId)
}