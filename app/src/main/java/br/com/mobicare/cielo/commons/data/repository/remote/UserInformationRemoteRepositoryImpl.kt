package br.com.mobicare.cielo.commons.data.repository.remote

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.dataSource.remote.UserInformationRemoteDataSource
import br.com.mobicare.cielo.commons.domain.repository.remote.UserInformationRemoteRepository
import br.com.mobicare.cielo.me.MeResponse

class UserInformationRemoteRepositoryImpl(private val remote: UserInformationRemoteDataSource) :
    UserInformationRemoteRepository {

    override suspend fun getMeInformation(): CieloDataResult<MeResponse> =
        remote.getUserInformation()
}