package br.com.mobicare.cielo.turboRegistration.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.turboRegistration.data.model.request.PaymentAccountRequest
import br.com.mobicare.cielo.turboRegistration.domain.model.Bank
import br.com.mobicare.cielo.turboRegistration.domain.model.Operation

interface BankInfoRepository {
    suspend fun getAllBanks(searchQuery: String?): CieloDataResult<List<Bank>>
    suspend fun getAllOperations(): CieloDataResult<List<Operation>>
    suspend fun registerNewAccount(paymentRequest: PaymentAccountRequest): CieloDataResult<Void>
}