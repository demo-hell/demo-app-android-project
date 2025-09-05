package br.com.mobicare.cielo.commons.domain.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.main.domain.AppMenuResponse
import br.com.mobicare.cielo.newLogin.domain.PosVirtualWhiteListResponse

interface MenuRemoteDataSource {

    suspend fun getMenu(): CieloDataResult<AppMenuResponse>

    suspend fun getPosVirtualWhiteList(): CieloDataResult<PosVirtualWhiteListResponse>

}