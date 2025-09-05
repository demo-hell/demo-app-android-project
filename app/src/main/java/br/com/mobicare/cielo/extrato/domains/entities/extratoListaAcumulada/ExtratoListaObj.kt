package br.com.mobicare.cielo.extrato.domains.entities.extratoListaAcumulada


/**
 * Created by benhur.souza on 06/06/2017.
 */
class ExtratoListaObj{
    var quantity: Int = 0
    var totalAmount: String? = null
    var aggregatedTransactions: ArrayList<ExtratoListaTransicaoObj>? = null
    var pagination: PaginationObj? = null
}