package br.com.mobicare.cielo.commons.domain.repository.remote

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.me.MeResponse

interface UserInformationRemoteRepository {
    suspend fun getMeInformation(): CieloDataResult<MeResponse>
}