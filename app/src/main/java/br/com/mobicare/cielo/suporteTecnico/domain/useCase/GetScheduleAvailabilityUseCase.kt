package br.com.mobicare.cielo.suporteTecnico.domain.useCase

import br.com.mobicare.cielo.suporteTecnico.domain.repo.RequestTicketSupportRepository

class GetScheduleAvailabilityUseCase(
    private val repository: RequestTicketSupportRepository
) {

    suspend fun getScheduleAvailability() =
        repository.getScheduleAvailability()

    suspend fun getMerchant() =
        repository.getMerchant()
}