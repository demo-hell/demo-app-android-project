package br.com.mobicare.cielo.meuCadastro.domains.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by silvia.miranda on 25/04/2017.
 */

@Parcelize
class MeuCadastroObj : Parcelable {

    var name: String? = null
    var ec: String? = null
    var documentNumber: String? = null
    var corporateName: String? = null
    var businessSegment: String? = null
    var owner: String? = null
    var phone: String? = null
    var openingDate: String? = null
    var addresses: Array<MeuCadastroEndereco>? = null
    var bankDatas: Array<MeuCadastroDomicilioBancario>? = null
    var hiredSolutions: Array<MeuCadastroSolucoesContratadas>? = null

    /**
     * Retorna o endereço dependente do tipo
     * @param tipo
     * *
     * @return
     */
    fun getEndereco(tipo: String): MeuCadastroEndereco? {
        if (addresses == null) {
            return null
        }

        for (end in addresses!!) {
            if (end.type.equals(tipo, ignoreCase = true)) {
                return end
            }
        }

        return null
    }

}
