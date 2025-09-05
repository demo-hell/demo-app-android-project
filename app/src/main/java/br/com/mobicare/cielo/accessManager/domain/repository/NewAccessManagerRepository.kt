package br.com.mobicare.cielo.accessManager.domain.repository

import br.com.mobicare.cielo.accessManager.domain.model.CustomProfiles
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface NewAccessManagerRepository {
    suspend fun getCustomActiveProfiles(
        profileType: String,
        status: String
    ): CieloDataResult<List<CustomProfiles>>

    suspend fun postAssignRole(
        usersId: List<String>,
        role: String,
        otpCode: String
    ): CieloDataResult<Void>
}