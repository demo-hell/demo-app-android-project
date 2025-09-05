package br.com.mobicare.cielo.login.domains.entities

/**
 * Created by benhur.souza on 11/05/2017.
 */
class EstabelecimentoObj(
    var ec: String,
    var tradeName: String?,
    var cnpj: String?,
    var hierarchyLevel: String? = null,
    var hierarchyLevelDescription: String? = null
) {

    /**
     * "establishment": {
     * "ec": "123456789",
     * "matrix": 0,
     * "cnpj": "001027058000191",
     * "tradeName": "",
     * "status": "R",
     * "antecipationCategory": "P"
     * },
     */

    val ecFormatado: String
        get() = "Nº EC: $ec"
}
