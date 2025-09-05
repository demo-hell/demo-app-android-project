package br.com.mobicare.cielo.openFinance.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.domain.model.TermsOfUse
import br.com.mobicare.cielo.openFinance.domain.repository.TermsOfUseRemoteRepository

class TermsOfUseUseCase(private val repository: TermsOfUseRemoteRepository) {
    suspend operator fun invoke(): CieloDataResult<TermsOfUse> {
        return repository.getTermsOfUse()
    }
}