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
import br.com.mobicare.cielo.turboRegistration.data.mapper.toBank
import br.com.mobicare.cielo.turboRegistration.data.mapper.toOperation
import br.com.mobicare.cielo.turboRegistration.data.model.request.PaymentAccountRequest
import br.com.mobicare.cielo.turboRegistration.domain.model.Bank
import br.com.mobicare.cielo.turboRegistration.domain.model.Operation
import com.facebook.stetho.server.http.HttpStatus

class BankInfoDataSource(
    private val registrationApi: RegistrationServerApi,
    private val safeApiCaller: SafeApiCaller
) {
    suspend fun getAllBanks(searchQuery: String? = null): CieloDataResult<List<Bank>> {

        val apiResult = safeApiCaller.safeApiCall { registrationApi.getAllBanks() }

        var result: CieloDataResult<List<Bank>> = CieloDataResult.APIError(
            CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
        )

        apiResult.onSuccess { response ->
            var banks = response.body()?.map { it.toBank() } ?: emptyList()
            searchQuery?.let {
                banks = banks.filter {
                    it.name?.contains(searchQuery, ignoreCase = true) == true
                }
            }

            result = CieloDataResult.Success(banks)

        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty { return result }
        return result
    }

    suspend fun getAllOperations(): CieloDataResult<List<Operation>> {
        val apiResult = safeApiCaller.safeApiCall { registrationApi.getAllOperations() }

        var result: CieloDataResult<List<Operation>> = CieloDataResult.APIError(
            CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
        )
        apiResult.onSuccess { response ->
            result = response.body()?.let { operationResponse ->
                CieloDataResult.Success(operationResponse.map { it.toOperation() })
            } ?: CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }
        return result
    }

    suspend fun registerNewAccount(
        paymentRequest: PaymentAccountRequest
    ): CieloDataResult<Void> {
        val apiResult = safeApiCaller.safeApiCall {
            registrationApi.registerNewAccount(paymentRequest)
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