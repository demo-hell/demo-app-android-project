package br.com.mobicare.cielo.turboRegistration.data.dataSource

import br.com.mobicare.cielo.commons.constants.HTTP_204_SUCCESS
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.turboRegistration.data.dataSource.remote.RegistrationServerApi
import br.com.mobicare.cielo.turboRegistration.data.model.request.BillingRequest
import com.facebook.stetho.server.http.HttpStatus

class MonthlyIncomeDataSource(
    private val registrationServerApi: RegistrationServerApi,
    private val safeApiCaller: SafeApiCaller
) {
    suspend fun updateMonthlyIncome(
        billingRequest: BillingRequest
    ): CieloDataResult<Void> {
        val apiResult = safeApiCaller.safeApiCall {
            registrationServerApi.updateMonthlyIncome(billingRequest = billingRequest)
        }

        var result: CieloDataResult<Void> = CieloDataResult.APIError(
            CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
        )

        apiResult.onSuccess { response ->
            result = if (response.code() in HttpStatus.HTTP_OK..HTTP_204_SUCCESS) {
                CieloDataResult.Empty(response.code())
            } else {
                CieloDataResult.Empty()
            }
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }
        return result
    }

}