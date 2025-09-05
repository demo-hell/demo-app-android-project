package br.com.mobicare.cielo.meusRecebimentos.domains.entities

import br.com.mobicare.cielo.extrato.domains.entities.extratoListaAcumulada.PaginationObj

/**
 * Created by silvia.miranda on 10/07/2017.
 */

class PostingObject {
    var helpText: String? = null
    var grouped: Boolean = false
    var groupedPostings: ArrayList<GroupedPostings> = ArrayList()
    var pagination: PaginationObj? = null
}
