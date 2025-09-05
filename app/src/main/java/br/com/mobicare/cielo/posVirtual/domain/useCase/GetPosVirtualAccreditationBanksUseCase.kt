package br.com.mobicare.cielo.posVirtual.domain.useCase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.posVirtual.domain.model.Bank
import br.com.mobicare.cielo.posVirtual.domain.repository.PosVirtualAccreditationRepository

class GetPosVirtualAccreditationBanksUseCase(
    private val repository: PosVirtualAccreditationRepository
) {

    suspend operator fun invoke(): CieloDataResult<List<Bank>> {
        lateinit var result: CieloDataResult<List<Bank>>

        repository.getBrands().onSuccess { it ->
            val banks = it.solutions?.flatMap { solution ->
                solution.banks.orEmpty()
            }
            result = banks?.let {
                if (it.isNotEmpty()) CieloDataResult.Success(it)
                else CieloDataResult.Empty()
            } ?: CieloDataResult.Empty()
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

}