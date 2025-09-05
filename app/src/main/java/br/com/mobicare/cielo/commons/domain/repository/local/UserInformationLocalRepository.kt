package br.com.mobicare.cielo.commons.domain.repository.local

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.repository.remote.UserInformationRemoteRepository
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.me.MeResponse

interface UserInformationLocalRepository : UserInformationRemoteRepository {
    suspend fun getUserObj(): CieloDataResult<UserObj>

    suspend fun saveMeInformation(meResponse: MeResponse): CieloDataResult<MeResponse>

    suspend fun getUserViewHistory(key: String): CieloDataResult<Boolean>

    suspend fun deleteUserViewHistory(key: String): CieloDataResult<Boolean>

    suspend fun saveUserViewHistory(
        key: String,
        value: Boolean,
    ): CieloDataResult<Boolean>

    suspend fun getUserViewCounter(key: String): CieloDataResult<Int>

    suspend fun deleteUserViewCounter(key: String): CieloDataResult<Int>

    suspend fun saveUserViewCounter(key: String): CieloDataResult<Int>
}
