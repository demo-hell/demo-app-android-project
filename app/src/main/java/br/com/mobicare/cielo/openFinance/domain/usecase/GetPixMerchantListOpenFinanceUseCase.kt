package br.com.mobicare.cielo.openFinance.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.domain.model.PixMerchantListResponse
import br.com.mobicare.cielo.openFinance.domain.repository.PixMerchantRemoteRepository

class GetPixMerchantListOpenFinanceUseCase(private val repository: PixMerchantRemoteRepository) {
    suspend operator fun invoke(): CieloDataResult<List<PixMerchantListResponse>> {
        return repository.getPixMerchantListOpenFinance()
    }
}