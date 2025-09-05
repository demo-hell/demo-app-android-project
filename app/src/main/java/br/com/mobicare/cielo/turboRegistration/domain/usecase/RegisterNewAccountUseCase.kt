package br.com.mobicare.cielo.turboRegistration.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.turboRegistration.data.model.request.PaymentAccountRequest
import br.com.mobicare.cielo.turboRegistration.domain.repository.BankInfoRepository

class RegisterNewAccountUseCase(private val bankInfoRepository: BankInfoRepository) {
    suspend operator fun invoke(paymentRequest: PaymentAccountRequest): CieloDataResult<Void> =
        bankInfoRepository.registerNewAccount(paymentRequest)
}