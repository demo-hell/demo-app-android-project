package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.asError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.isEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.isError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.successValueOrNull
import br.com.mobicare.cielo.commons.domain.useCase.UseCaseWithoutParams
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixKeysResponse
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixClaimType
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixKeysRepository

class GetPixMasterKeyUseCase(
    private val repository: PixKeysRepository
) : UseCaseWithoutParams<GetPixMasterKeyUseCase.Result> {

    override suspend fun invoke(): CieloDataResult<Result> {
        val result = repository.getAllKeys()

        if (result.isError) return CieloDataResult.APIError(result.asError.apiException)
        if (result.isEmpty) return CieloDataResult.Empty()

        val keys = result.successValueOrNull?.keys?.keys

        return CieloDataResult.Success(
            if (keys != null) getKeysResult(keys) else Result.NoKeysFound
        )
    }

    private fun getKeysResult(keys: List<PixKeysResponse.KeyItem>): Result {
        val masterKey = keys.lastOrNull { it.main == true }
        val shouldShowAlert = shouldShowAlert(keys)

        return if (masterKey != null) {
            Result.MasterKeyFound(
                Data(
                    keys = keys,
                    masterKey = masterKey,
                    shouldShowAlert = shouldShowAlert
                )
            )
        } else {
            Result.MasterKeyNotFound(
                Data(
                    keys = keys,
                    shouldShowAlert = shouldShowAlert
                )
            )
        }
    }

    private fun shouldShowAlert(keys: List<PixKeysResponse.KeyItem>) = keys.any {
        it.claimType == PixClaimType.PORTABILITY.name || it.claimType == PixClaimType.OWNERSHIP.name
    }

    sealed class Result {
        data class MasterKeyFound(val data: Data) : Result()
        data class MasterKeyNotFound(val data: Data) : Result()
        object NoKeysFound : Result()
    }

    data class Data(
        val keys: List<PixKeysResponse.KeyItem>,
        val masterKey: PixKeysResponse.KeyItem? = null,
        val shouldShowAlert: Boolean = false,
    )

}