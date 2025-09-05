package br.com.mobicare.cielo.turboRegistration.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.turboRegistration.domain.model.Bank
import br.com.mobicare.cielo.turboRegistration.domain.repository.BankInfoRepository

class SearchBanksUseCase(private val bankInfoRepository: BankInfoRepository) {
    suspend operator fun invoke(searchQuery: String?): CieloDataResult<List<Bank>> = bankInfoRepository.getAllBanks(searchQuery)
}