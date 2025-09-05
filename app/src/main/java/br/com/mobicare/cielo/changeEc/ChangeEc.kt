package br.com.mobicare.cielo.changeEc

import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.changeEc.domain.Merchant
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.login.domains.entities.EstabelecimentoObj
import br.com.mobicare.cielo.login.domains.entities.LoginObj
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.pix.constants.EMPTY

const val CHANGE_EC_GRUPO_PAGAMENTO = "GRUPO_PAGAMENTO"
const val CHANGE_EC_GRUPO_COMERCIAL = "GRUPO_COMERCIAL"

class ChangeEc {

    fun createNewLoginConvivencia(impersonate: Impersonate, merchant: Merchant) {
        val loginObj = LoginObj()

        loginObj.token = impersonate.accessToken
        loginObj.establishment = EstabelecimentoObj(merchant.id, merchant.tradingName, EMPTY)
        loginObj.establishment?.hierarchyLevel = merchant.hierarchyLevel
        loginObj.establishment?.hierarchyLevelDescription = impersonate.hierarchyLevelDescription
        loginObj.hasLoyalty = impersonate.hasLoyalty ?: false
        loginObj.hasOffer = impersonate.hasOffer ?: false
        loginObj.isConvivenciaUser = impersonate.isConvivenciaUser ?: true

        MenuPreference.instance.saveLoginObj(loginObj)
        UserPreferences.getInstance().saveRefreshToken(impersonate.refreshToken)
    }
}