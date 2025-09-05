package br.com.mobicare.cielo.meusRecebimentos.domains.entities

/**
 * Created by silvia.miranda on 20/06/2017.
 */

class BankDataObj {
    var quantity: Int = 0
    var code: String? = null
    var name: String? = null
    var branch: String? = null
    var imgUrl: String? = null
    var account: String? = null
    var amount: Float? = 0F
    var postingsQty: String? = null
    var status: String? = null
    var statusCode: BankStatus? = null
    var isPrepaid: Boolean = false
    var bankPostings: Array<Double>? = null
}



enum class BankStatus(val status: String) {
    DE("Depositado"),
    AG("Agendado"),
    PR("Previsto"),
    PE("Pendente"),
    AN("Antecipado"),
    PA("Pago"),
    CA("Cancelado")
}