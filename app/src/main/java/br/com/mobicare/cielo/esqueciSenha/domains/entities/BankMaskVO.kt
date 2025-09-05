package br.com.mobicare.cielo.esqueciSenha.domains.entities

/**
 * Created by benhur.souza on 11/04/2017.
 */

const val CODE_BANCO_CAIXA_ECONOMICA = "104"
const val PF_CURRENT_ACCOUNT_CAIXA = "001"
const val PF_SIMPLE_ACCOUNT_CAIXA = "002"
const val PJ_CURRENT_ACCOUNT_CAIXA = "003"
const val PF_SAVINGS_ACCOUNT_CAIXA = "013"
const val PJ_PUBLIC_ENTITITY_CAIXA = "006"
const val CURRENT_ACCOUNT = "CC"
const val SAVINGS_ACCOUNT = "CP"

class BankMaskVO() {

    constructor(name: String) : this() {
        this.name = name
    }

    var code: String = ""
    var name: String = ""
    var branchMask: String = ""
    var branchDigitMask: String = ""
    var accountMask: String = ""
    var accountDigitMask: String = ""
}
