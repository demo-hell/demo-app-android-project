package br.com.mobicare.cielo.meuCadastro.domains.entities

import java.io.Serializable

/**
 * Created by silvia.miranda on 25/04/2017.
 */

class MeuCadastroDomicilioBancario : Serializable {

    var name: String? = null
    var imgUrl: String? = null
    var code: String? = null
    var branch: String? = null
    var account: String? = null
    var blockedDomicile: Boolean = false
    var blockedAnticipation: Boolean = false
    var isPrepaid: Boolean = false

    fun travaDomicilio(): String {
        return if (blockedDomicile) "Sim" else "Não"
    }

    fun travaAntecipacao(): String {
        return if (blockedAnticipation) "Sim" else "Não"
    }
}
