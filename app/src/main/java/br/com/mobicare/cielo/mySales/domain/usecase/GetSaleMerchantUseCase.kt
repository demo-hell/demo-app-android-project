package br.com.mobicare.cielo.mySales.domain.usecase



import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.mySales.data.model.params.GetMerchantParams
import br.com.mobicare.cielo.mySales.data.model.bo.SalesMerchantBO
import br.com.mobicare.cielo.mySales.domain.repository.MySalesRemoteRepository

class GetSaleMerchantUseCase(private val repositoryImpl: MySalesRemoteRepository) {

    suspend operator fun invoke(params: GetMerchantParams): CieloDataResult<SalesMerchantBO> {
        return repositoryImpl.getSaleMerchant(params)
    }

}