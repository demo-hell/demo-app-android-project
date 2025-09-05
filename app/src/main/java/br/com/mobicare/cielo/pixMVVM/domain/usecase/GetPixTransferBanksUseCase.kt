package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCaseWithoutParams
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferBank
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixTransactionsRepository

class GetPixTransferBanksUseCase(
    private val repository: PixTransactionsRepository
) : UseCaseWithoutParams<List<PixTransferBank>> {

    override suspend fun invoke() = repository.getTransferBanks()

}