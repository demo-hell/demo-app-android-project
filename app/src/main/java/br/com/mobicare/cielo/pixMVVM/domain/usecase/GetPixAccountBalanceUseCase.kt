package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCaseWithoutParams
import br.com.mobicare.cielo.pixMVVM.domain.model.PixAccountBalance
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixAccountBalanceRepository

class GetPixAccountBalanceUseCase(
    private val repository: PixAccountBalanceRepository
) : UseCaseWithoutParams<PixAccountBalance> {

    override suspend fun invoke() = repository.getAccountBalance()

}