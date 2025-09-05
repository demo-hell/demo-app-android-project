package br.com.mobicare.cielo.meusRecebimentos.domains.entities

import br.com.mobicare.cielo.commons.domains.entities.SystemMessage

/**
 * Created by silvia.miranda on 10/07/2017.
 */
class PostingDetailObject {
    var id: Integer? = null
    var title: String? = null
    var date: String? = null
    var postingsQty: Integer? = null
    var cvsQty: Integer? = null
    var valorTotal: String? = null
    var hasDescriptionDetails: Boolean = false
    var uniquekeyROPart1: Long? = null
    var uniquekeyROPart2: Long? = null
    var uniquekeyROPart3: Long? = null
    var merchantId: String? = null
    var productName: String? = null
    var brand: String? = null
    var details: ArrayList<SystemMessage> = ArrayList()
}
