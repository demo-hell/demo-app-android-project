package br.com.mobicare.cielo.commons.domain.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.main.domain.AppMenuResponse

interface MenuLocalDataSource {

    suspend fun getMenu(): CieloDataResult<AppMenuResponse>

    suspend fun saveMenuLocalStorage(menu: AppMenuResponse): CieloDataResult<Boolean>

    suspend fun savePosVirtualWhiteList(isEligible: Boolean): CieloDataResult<Boolean>

}