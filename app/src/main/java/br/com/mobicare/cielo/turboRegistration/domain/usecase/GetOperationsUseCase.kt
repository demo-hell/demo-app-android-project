package br.com.mobicare.cielo.turboRegistration.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.turboRegistration.domain.model.Operation
import br.com.mobicare.cielo.turboRegistration.domain.repository.BankInfoRepository

class GetOperationsUseCase(private val bankInfoRepository: BankInfoRepository) {
    suspend operator fun invoke(): CieloDataResult<List<Operation>> = bankInfoRepository.getAllOperations()

}