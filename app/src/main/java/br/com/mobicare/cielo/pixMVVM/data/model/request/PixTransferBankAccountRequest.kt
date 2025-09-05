package br.com.mobicare.cielo.pixMVVM.data.model.request


data class PixTransferBankAccountRequest(
    val finalAmount: Double? = null,
    val message: String? = null,
    val payee: Payee? = null,
    val schedulingDate: String? = null,
    val frequencyTime: String? = null,
    val schedulingFinalDate: String? = null,
    val fingerprint: String? = null,
) {

    data class Payee(
        val bankAccountNumber: String? = null,
        val bankAccountType: String? = null,
        val bankBranchNumber: String? = null,
        val beneficiaryType: String? = null,
        val documentNumber: String? = null,
        val ispb: Int? = null,
        val name: String? = null
    )

}
