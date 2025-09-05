package br.com.mobicare.cielo.accessManager.data.repository

import br.com.mobicare.cielo.accessManager.data.datasource.remote.AccessManagerRemoteDataSource
import br.com.mobicare.cielo.accessManager.domain.model.CustomProfiles
import br.com.mobicare.cielo.accessManager.domain.repository.NewAccessManagerRepository
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult


class NewAccessManagerRepositoryImpl(
    private val remoteDataSource: AccessManagerRemoteDataSource
) : NewAccessManagerRepository {
    override suspend fun getCustomActiveProfiles(
        profileType: String,
        status: String
    ): CieloDataResult<List<CustomProfiles>> {
        return remoteDataSource.getCustomActiveProfiles(
            profileType = profileType,
            status = status
        )
    }

    override suspend fun postAssignRole(
        usersId: List<String>,
        role: String,
        otpCode: String
    ): CieloDataResult<Void> {
        return remoteDataSource.postAssignRole(usersId, role, otpCode)
    }

}