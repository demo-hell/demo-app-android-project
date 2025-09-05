package br.com.mobicare.cielo.extrato.domains.entities.extratoRecibo

import java.io.Serializable

/**
 * Created by benhur.souza on 13/06/2017.
 */

open class ExtratoReciboObj: Serializable {

    /**
     * {
     * "brand": "VISA",
     * "paymentType": "Crédito à vista",
     * "creditCardNumber": "************1234",
     * "terminalNumber": "12345678",
     * "establishment": {
     * "name": "Nome do Estabelecimento",
     * "documentNumber": "12.345.678/0001-23",
     * "address": "R Venceslau 141 Centro São Paulo SP"
     * },
     * "authorizationCode": "1234567891",
     * "doc": "12345",
     * "aut": "12345",
     * "date": "01/07/2016 14:00",
     * "quantity": "R$ 6.400,00"
     * }
     */

    var brand: String? = null
    var paymentType: String? = null
    var creditCardNumber: String? = null
    var terminalNumber: String? = null
    var establishment: ExtratoReciboEstabelecimentoObj? = null
    var authorizationCode: String? = null
    var doc: String? = ""
    var aut: String? = ""
    var date: String? = null
    var amount: String? = null
}
