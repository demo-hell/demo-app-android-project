package br.com.mobicare.cielo.suporteTecnico.domain.useCase

import br.com.mobicare.cielo.suporteTecnico.domain.repo.RequestTicketSupportRepository

class GetMerchantEquipmentsUseCase(
    private val repository: RequestTicketSupportRepository
) {
    suspend operator fun invoke() =
        repository.getMerchantEquipaments()
}