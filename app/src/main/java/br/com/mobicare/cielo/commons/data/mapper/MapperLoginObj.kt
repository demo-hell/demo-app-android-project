package br.com.mobicare.cielo.commons.data.mapper

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.login.domains.entities.LoginObj
import br.com.mobicare.cielo.me.MeResponse

object MapperLoginObj {

    val userPreferences = UserPreferences.getInstance()

    fun mapToLoginObj(meResponse: MeResponse): LoginObj {
        return LoginObj().apply {
            establishment = MapperEstablishmentObj.mapToEstablishment(meResponse)
            user = MapperUserObj.mapToUser(meResponse)
            token = userPreferences.token
            isConvivenciaUser = userPreferences.isConvivenciaUser
        }
    }
}