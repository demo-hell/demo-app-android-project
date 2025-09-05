package br.com.mobicare.cielo.pixMVVM.presentation.refund.models

data class PixCreateRefundStore(
    var otpCode: String? = null,
    var fingerprint: String? = null,
    var amount: Double? = null,
    var message: String? = null,
    var idEndToEnd: String? = null,
    var idTx: String? = null
) {
    fun validate() = otpCode != null
            && fingerprint != null
            && amount != null
            && idEndToEnd != null
            && idTx != null
}
