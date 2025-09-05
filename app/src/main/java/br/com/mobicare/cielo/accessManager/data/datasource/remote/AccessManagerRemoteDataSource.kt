package br.com.mobicare.cielo.accessManager.data.datasource.remote

import br.com.mobicare.cielo.accessManager.data.mapper.toCustomProfiles
import br.com.mobicare.cielo.accessManager.domain.model.CustomProfiles
import br.com.mobicare.cielo.accessManager.model.AccessManagerAssignRoleRequest
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess

class AccessManagerRemoteDataSource(
    private val api: NewAccessManagerApi,
    private val safeApiCaller: SafeApiCaller
) {

    suspend fun getCustomActiveProfiles(
        profileType: String,
        status: String
    ): CieloDataResult<List<CustomProfiles>> {
        var result: CieloDataResult<List<CustomProfiles>> =
            CieloDataResult.APIError(
                CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
            )

        safeApiCaller.safeApiCall {
            api.getCustomActiveProfiles(
                profileType = profileType,
                status = status,
                fetchDetails = true
            )
        }.onSuccess {
            val customProfiles = mutableListOf<CustomProfiles>()
            it.body()?.forEach { response ->
                customProfiles.add(response.toCustomProfiles())
            }

            result = CieloDataResult.Success(customProfiles)
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }

        return result
    }

    suspend fun postAssignRole(usersId: List<String>, role: String, otpCode: String): CieloDataResult<Void> {
        var result: CieloDataResult<Void> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val request = mutableListOf<AccessManagerAssignRoleRequest>()

        usersId.map {
            request.add(AccessManagerAssignRoleRequest(it, role))
        }

        safeApiCaller.safeApiCall {
            api.postAssignRole(
                request,
                otpCode
            )
        }.onSuccess {
            result = CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

}