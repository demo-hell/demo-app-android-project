package br.com.mobicare.cielo.turboRegistration.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.turboRegistration.data.dataSource.MonthlyIncomeDataSource
import br.com.mobicare.cielo.turboRegistration.data.model.request.BillingRequest
import br.com.mobicare.cielo.turboRegistration.domain.repository.MonthlyIncomeRepository

class MonthlyIncomeRepositoryImpl(
    private val monthlyIncomeDataSource: MonthlyIncomeDataSource
) : MonthlyIncomeRepository {
    override suspend fun updateMonthlyIncome(billingRequest: BillingRequest): CieloDataResult<Void> {
        return monthlyIncomeDataSource.updateMonthlyIncome(billingRequest)
    }
}