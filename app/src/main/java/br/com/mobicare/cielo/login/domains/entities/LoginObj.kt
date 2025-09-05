package br.com.mobicare.cielo.login.domains.entities

import br.com.mobicare.cielo.changeEc.domain.Merchant
import br.com.mobicare.cielo.commons.domains.entities.ErroList

open class LoginObj : ErroList() {
    var token: String? = null
    var user: UserObj = UserObj()
    var establishment: EstabelecimentoObj? = null

    var hasLoyalty: Boolean = false
    var hasOffer: Boolean = false
    var merchants: ArrayList<Merchant>? = null

    var isConvivenciaUser: Boolean = false
}