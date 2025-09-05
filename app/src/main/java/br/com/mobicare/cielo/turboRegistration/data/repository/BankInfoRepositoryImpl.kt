package br.com.mobicare.cielo.turboRegistration.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.turboRegistration.data.dataSource.BankInfoDataSource
import br.com.mobicare.cielo.turboRegistration.data.model.request.PaymentAccountRequest
import br.com.mobicare.cielo.turboRegistration.domain.model.Bank
import br.com.mobicare.cielo.turboRegistration.domain.model.Operation
import br.com.mobicare.cielo.turboRegistration.domain.repository.BankInfoRepository

class BankInfoRepositoryImpl(
        private val bankInfoDataSource: BankInfoDataSource
): BankInfoRepository {

    override suspend fun getAllBanks(searchQuery: String?) : CieloDataResult<List<Bank>> {
        return bankInfoDataSource.getAllBanks(searchQuery)
    }

    override suspend fun getAllOperations(): CieloDataResult<List<Operation>> {
        return bankInfoDataSource.getAllOperations()
    }

    override suspend fun registerNewAccount(paymentRequest: PaymentAccountRequest): CieloDataResult<Void> {
        return bankInfoDataSource.registerNewAccount(paymentRequest)
    }
}