package br.com.mobicare.cielo.turboRegistration.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.turboRegistration.data.model.request.BillingRequest
import br.com.mobicare.cielo.turboRegistration.domain.repository.MonthlyIncomeRepository

class UpdateMonthlyIncomeUseCase(private val monthlyIncomeRepository: MonthlyIncomeRepository) {
    suspend operator fun invoke(billingRequest: BillingRequest): CieloDataResult<Void> = monthlyIncomeRepository.updateMonthlyIncome(billingRequest)
}