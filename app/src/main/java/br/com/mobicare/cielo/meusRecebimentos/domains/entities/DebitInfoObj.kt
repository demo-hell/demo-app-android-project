package br.com.mobicare.cielo.meusRecebimentos.domains.entities

import br.com.mobicare.cielo.commons.domains.entities.SystemMessage

/**
 * Created by silvia.miranda on 20/06/2017.
 */


class DebitInfoObj{
    var dateRange: String? = null
    var pending: ReceiptDetailObj? = null
    var details: ArrayList<SystemMessage?>? = null
}
