package br.com.mobicare.cielo.meusRecebimentos.domains.entities

import java.io.Serializable

class ReceiptDetailObj: Serializable{
    var id: Int? = null
    var name: String? = null
    var amount: Double = 0.00
    var quantity: Int? = null
    var cieloDate: String? = null
}

