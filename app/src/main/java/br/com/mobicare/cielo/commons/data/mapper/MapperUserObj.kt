package br.com.mobicare.cielo.commons.data.mapper

import br.com.mobicare.cielo.commons.utils.dateFormatToBr
import br.com.mobicare.cielo.login.domains.entities.ActiveMerchantObj
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.me.MeResponse

object MapperUserObj {

    fun mapToUser(meResponse: MeResponse): UserObj {
        return UserObj().apply {
            activeMerchant = ActiveMerchantObj(
                meResponse.activeMerchant.id,
                meResponse.activeMerchant.hierarchyLevel
            )
            cpf = meResponse.identity?.cpf
            birthdayDate = meResponse.birthDate.dateFormatToBr()
            ec = meResponse.activeMerchant.id
            email = meResponse.email
            nameLogin = meResponse.login
            rg = meResponse.identity?.rg
            mainRole = meResponse.mainRole
            onboardingRequired = meResponse.onboardingRequired ?: false
            digitalId = meResponse.digitalId
            roles = meResponse.roles
            profile = meResponse.profile
        }
    }
}