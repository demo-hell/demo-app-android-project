package br.com.mobicare.cielo.extrato.domains.entities.extratoRecibo

import java.io.Serializable

/**
 * Created by benhur.souza on 13/06/2017.
 */

class ExtratoReciboEstabelecimentoObj: Serializable{
    /**
     * {
     * "name": "Nome do Estabelecimento",
     * "documentNumber": "12.345.678/0001-23",
     * "address": "R Venceslau 141 Centro São Paulo SP"
     * }
     */
    var name: String? = null
    var documentNumber: String? = null
    var address: String? = null
}
