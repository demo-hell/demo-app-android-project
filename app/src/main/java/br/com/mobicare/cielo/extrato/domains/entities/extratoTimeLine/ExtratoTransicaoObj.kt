package br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine

import br.com.mobicare.cielo.commons.domains.entities.SystemMessage
import br.com.mobicare.cielo.extrato.domains.entities.extratoRecibo.ExtratoReciboObj
import java.io.Serializable

/**
 * Created by Benhur on 02/06/17.
 */

class ExtratoTransicaoObj: Serializable {
    /**
     * "time": "19:15",
     * "description": "Credito à vista",
     * "quantity": "R$ 6.400,00",
     * "status": "Aprovada",
     * "statusCode: ": "AP"
     */

    var time: String? = null
    var description: String? = null
    var amount: String? = null
    var transactionCode: String? = null
    var status: String? = null
    var statusCode: String? = null
    var details: ArrayList<SystemMessage>? = null
    var receipt: ExtratoReciboObj? = null
}
