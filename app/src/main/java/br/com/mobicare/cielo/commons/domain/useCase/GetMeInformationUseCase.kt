package br.com.mobicare.cielo.commons.domain.useCase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.repository.local.UserInformationLocalRepository
import br.com.mobicare.cielo.commons.domain.repository.remote.UserInformationRemoteRepository
import br.com.mobicare.cielo.me.MeResponse

class GetMeInformationUseCase(
    private val remote: UserInformationRemoteRepository,
    private val local: UserInformationLocalRepository
) {
    suspend operator fun invoke(isLocal: Boolean = true): CieloDataResult<MeResponse> {
        return if (isLocal) {
            getMeInformationLocal()
        } else {
            getMeInformationRemote()
        }
    }

    private suspend fun getMeInformationLocal(): CieloDataResult<MeResponse> {
        val localResult = local.getMeInformation()

        localResult.onSuccess {
            return localResult
        }
        return getMeInformationRemote()
    }

    private suspend fun getMeInformationRemote(): CieloDataResult<MeResponse> {
        val remoteResult = remote.getMeInformation()

        remoteResult.onSuccess {
            local.saveMeInformation(it)
        }
        return remoteResult
    }

}