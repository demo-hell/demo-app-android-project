package br.com.mobicare.cielo.pixMVVM.data.model.request

import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQrCodeOperationType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransferType

data class PixTransferKeyRequest(
    val agentMode: String? = null,
    val agentWithdrawalIspb: String? = null,
    val finalAmount: Double? = null,
    val changeAmount: Double? = null,
    val purchaseAmount: Double? = null,
    val endToEndId: String? = null,
    val idTx: String? = null,
    val payee: Payee? = null,
    val message: String? = null,
    val pixType: String? = PixQrCodeOperationType.TRANSFER.name,
    val transferType: String = PixTransferType.CHAVE.name,
    val schedulingDate: String? = null,
    val frequencyTime: String? = null,
    val schedulingFinalDate: String? = null,
    val fingerprint: String? = null,
) {
    data class Payee(
        val key: String?,
        val keyType: String?,
    )
}
