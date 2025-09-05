package br.com.mobicare.cielo.commons.data.repository.local

import br.com.mobicare.cielo.commons.data.dataSource.local.UserInformationLocalDataSource
import br.com.mobicare.cielo.commons.domain.repository.local.UserInformationLocalRepository
import br.com.mobicare.cielo.me.MeResponse

class UserInformationLocalRepositoryImpl(private val local: UserInformationLocalDataSource) :
    UserInformationLocalRepository {
    override suspend fun getUserObj() = local.getUserObj()

    override suspend fun getMeInformation() = local.getUserInformation()

    override suspend fun saveMeInformation(meResponse: MeResponse) = local.saveMeInformation(meResponse)

    override suspend fun getUserViewHistory(key: String) = local.getUserViewHistory(key)

    override suspend fun deleteUserViewHistory(key: String) = local.deleteUserViewHistory(key)

    override suspend fun saveUserViewHistory(
        key: String,
        value: Boolean,
    ) = local.saveUserViewHistory(key, value)

    override suspend fun getUserViewCounter(key: String) = local.getUserViewCounter(key)

    override suspend fun deleteUserViewCounter(key: String) = local.deleteUserViewCounter(key)

    override suspend fun saveUserViewCounter(key: String) = local.saveUserViewCounter(key)
}
