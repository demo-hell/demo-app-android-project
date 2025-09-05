package br.com.mobicare.cielo.meusRecebimentos.domains.entities

import br.com.mobicare.cielo.extrato.domains.entities.extratoListaAcumulada.PaginationObj

/**
 * Created by silvia.miranda on 12/07/2017.
 */

class PostingOfDetailObject {
    var totalAmount: Double= Double.NaN
    var roNumber: String? = null
    var postingsDetails: ArrayList<PostingOfDetailDetailObject> = ArrayList()
    var pagination: PaginationObj? = null
}
