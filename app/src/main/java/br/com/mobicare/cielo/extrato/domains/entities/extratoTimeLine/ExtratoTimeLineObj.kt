package br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine

import br.com.mobicare.cielo.extrato.domains.entities.extratoListaAcumulada.PaginationObj


/**
 * Created by Benhur on 02/06/17.
 */

class ExtratoTimeLineObj {
    var quantity: Int = 0
    var totalAmount: String? = null
    var transactions: ArrayList<ExtratoTransicaoObj>? = null
    var pagination: PaginationObj? = null
    var paginationId: String = ""

}
