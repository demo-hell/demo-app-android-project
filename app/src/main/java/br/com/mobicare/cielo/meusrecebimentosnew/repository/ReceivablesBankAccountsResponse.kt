package br.com.mobicare.cielo.meusrecebimentosnew.repository

data class ReceivablesBankAccountsResponse(
        val items: List<BankAccountItem>
)

data class BankAccountItem(
        val netAmount: Double? = 0.0,
        val quantity: Int? = 0,
        val collateralCredit: Boolean? = false,
        val creditInstitution: CreditInstitution?,
        val bank: BankAccount?
)

data class BankAccount(
        val code: String? = "",
        val name: String? = "",
        val agency: String? = "",
        val account: String? = ""
)

data class CreditInstitution(
        val name: String? = "",
        val identificationNumber: String? = ""
)