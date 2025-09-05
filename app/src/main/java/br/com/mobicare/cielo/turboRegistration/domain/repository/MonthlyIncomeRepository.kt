package br.com.mobicare.cielo.turboRegistration.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.turboRegistration.data.model.request.BillingRequest

interface MonthlyIncomeRepository {
    suspend fun updateMonthlyIncome(billingRequest: BillingRequest): CieloDataResult<Void>
}