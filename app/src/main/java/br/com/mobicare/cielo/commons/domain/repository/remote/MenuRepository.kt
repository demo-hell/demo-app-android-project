package br.com.mobicare.cielo.commons.domain.repository.remote

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.main.domain.AppMenuResponse

interface MenuRepository {

    suspend fun getMenu(
        isLocal: Boolean,
        ftTapOnPhoneWhiteList: Boolean
    ): CieloDataResult<AppMenuResponse>

}