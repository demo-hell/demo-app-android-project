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
import br.com.mobicare.cielo.turboRegistration.data.mapper.toAddress
import br.com.mobicare.cielo.turboRegistration.data.model.request.AddressRequest
import br.com.mobicare.cielo.turboRegistration.domain.model.Address
import com.facebook.stetho.server.http.HttpStatus

class AddressDataSource(
    private val registrationServerApi: RegistrationServerApi,
    private val safeApiCaller: SafeApiCaller
) {
    suspend fun getAddressByCep(
        cep: String
    ): CieloDataResult<Address> {
        val apiResult = safeApiCaller.safeApiCall { registrationServerApi.getAddressByCep(cep) }

        var result: CieloDataResult<Address> = CieloDataResult.APIError(
            CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
        )

        apiResult.onSuccess { response ->
            result = response.body()?.let { address ->
                CieloDataResult.Success(address.toAddress(cep))
            } ?: CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }
        return result
    }

    suspend fun updateAddress(
        addressId: String,
        address: AddressRequest
    ): CieloDataResult<Void> {

        val apiResult = safeApiCaller.safeApiCall {
            registrationServerApi.updateAddress(
                idAddress = addressId,
                address = address
            )
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