package br.com.mobicare.cielo.extrato.domains.entities.extratoListaAcumulada

/**
 * Created by benhur.souza on 06/06/2017.
 */
class ExtratoListaTransicaoObj{
    /**
     * {
    "date": "21 MAI",
    "formattedDate": "21/05/2017",
    "quantity": "05 Vendas",
    "quantity": "R$ 600,00"
    }
     */
    var date: String? = null
    var formattedDate: String? = null
    var quantity: Int = 0
    var amount: String? = null
    var hasSales: Boolean = false
}