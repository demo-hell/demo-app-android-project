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
import br.com.mobicare.cielo.turboRegistration.data.mapper.toMcc
import br.com.mobicare.cielo.turboRegistration.data.model.request.BusinessUpdateRequest
import br.com.mobicare.cielo.turboRegistration.domain.model.Mcc
import com.facebook.stetho.server.http.HttpStatus

class BusinessSectorDataSource(
    private val registrationServerApi: RegistrationServerApi,
    private val safeApiCaller: SafeApiCaller
) {
    suspend fun getAllBusinessLine(searchQuery: String? = null): CieloDataResult<List<Mcc>> {
        val apiResult = safeApiCaller.safeApiCall { registrationServerApi.getAllBusinessSector() }

        var result: CieloDataResult<List<Mcc>> = CieloDataResult.APIError(
            CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
        )

        apiResult.onSuccess { response ->
            val allBusinessLines = mutableSetOf<Mcc>()
            val idAddress = response.body()?.idAddresses
            response.body()?.mcc?.forEach { mccResponseItem ->
                allBusinessLines.add(mccResponseItem.toMcc(idAddress))
            }

            val filteredLines = searchQuery?.let { query ->
                allBusinessLines.filter { line ->
                    line.description?.contains(query, ignoreCase = true) == true
                }
            } ?: allBusinessLines.toList()

            result = CieloDataResult.Success(filteredLines)
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }
        return result
    }

    suspend fun updateBusinessSector(businessRequest: BusinessUpdateRequest): CieloDataResult<Void> {
        val apiResult = safeApiCaller.safeApiCall {
            registrationServerApi.updateBusinessSector(body = businessRequest)
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
