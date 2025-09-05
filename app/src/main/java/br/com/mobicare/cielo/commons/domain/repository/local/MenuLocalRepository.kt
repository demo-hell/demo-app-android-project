package br.com.mobicare.cielo.commons.domain.repository.local

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.main.domain.AppMenuResponse

interface MenuLocalRepository {

    suspend fun getMenu() : CieloDataResult<AppMenuResponse>

    suspend fun saveMenuLocalStorage(menu: AppMenuResponse) : CieloDataResult<Boolean>

    suspend fun saveTapOnPhoneWhiteList(isEligible: Boolean): CieloDataResult<Boolean>

}