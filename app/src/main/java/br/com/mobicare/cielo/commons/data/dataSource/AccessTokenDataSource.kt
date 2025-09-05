package br.com.mobicare.cielo.commons.data.dataSource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences

class AccessTokenDataSource(private val userPreferences: UserPreferences) {

    fun getAccessToken(): CieloDataResult<String> =
        CieloDataResult.Success(userPreferences.token)
}