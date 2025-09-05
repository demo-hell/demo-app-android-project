package br.com.mobicare.cielo.chargeback.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.chargeback.data.model.response.RefundFileInformationList
import br.com.mobicare.cielo.commons.constants.TEN
import br.com.mobicare.cielo.commons.constants.ZERO
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

enum class ChargebackStatus(val statusName: String) {
    ACCEPTED("ACEITO"), DECLINED("RECUSADO"), OUT_OF_TIME("FORA DO PRAZO")
}

enum class ChargebackProcess {
    PRIMEIRO_CHARGEBACK,
    COBRANCA_AMIGAVEL,
    CHARGEBACK_ARBITRAGEM,
    FEE_COLLECTION,
    REVERSAO_CHARGEBACK,
    REVERSAO_COBRANCA_AMIGAVEL,
    REVERSAO_FEE_COLLECTION,
    REVERSAO_ARBITRAGEM,
    RECLAMACAO_DEBITO,
    REVERSAO_RECLAMACAO_DEBITO,
    CHARGEBACK_VOUCHER,
    REVERSAO_CHARGEBACK_VOUCHER,
    PRE_ARBITRAGEM,
    PRE_COMPLIANCE,
    ARBITRAGEM,
    COMPLIANCE,
    SEGUNDO_CHARGEBACK
}

@Keep
@Parcelize
data class Chargebacks(
    val content: List<Chargeback>,
    val totalElements: Int = ZERO,
    val firstPage: Boolean = true,
    val lastPage: Boolean = false,
    val totalPages: Int = ZERO,
    val pageSize: Int = TEN,
    val pageNumber: Int = ZERO,
    val numberOfElements: Int = ZERO,
    val empty: Boolean = true,
) : Parcelable

@Keep
@Parcelize
data class Chargeback(
    val chargebackId: Int?,
    val daysToDeadLine: Int?,
    val caseId: Int?,
    val merchantId: Long?,
    val transactionAmount: Double?,
    val process: String?,
    val disputeStatus: List<Int>?,
    val actionTakenCode: String?,
    val chargebackDetails: ChargebackDetails?,
    val lifecycle: Lifecycle?,
    val transactionDetails: TransactionDetails?,
    val treatmentDeadline: String?
) : Parcelable {
    val reasonName: String get() = "${chargebackDetails?.reasonCode} - ${chargebackDetails?.reasonDescription}"
    val isStatusDeclined get() = actionTakenCode?.uppercase() == ChargebackStatus.DECLINED.statusName
    val isDone: Boolean get() = actionTakenCode != null
}

@Keep
@Parcelize
data class ChargebackDetails(
    val descriptionReason: String?,
    val reasonCode: String?,
    val reasonDescription: String?,
    val reasonType: Int?,
    val receptionDate: LocalDate?,
    val replyDate: LocalDate?,
    val chargebackFraudMessage: String?,
    val descriptionReasonType: String?,
    val fastDisputeResolution: Boolean?,
    val refundFileInformation: List<RefundFileInformationList>
) : Parcelable

@Keep
@Parcelize
data class Lifecycle(val action: String?, val actionDate: LocalDate?) : Parcelable

@Keep
@Parcelize
data class TransactionDetails(
    val authorizationCode: String?,
    val cardBrandCode: Int?,
    val cardBrandName: String?,
    val issuerSenderCode: String?,
    val issuerSenderDescription: String?,
    val merchantName: String?,
    val nsu: Int?,
    val productType: String?,
    val productTypeCode: Int?,
    val referenceNumber: String?,
    val roNumber: String?,
    val terminal: String?,
    val tid: String?,
    val transactionDate: LocalDate?,
    val truncatedCardNumber: String?,
    val currency: String?
) : Parcelable

@Keep
@Parcelize
data class ChargebackFilterCardBrand(
    val brandCode: Int,
    val brandName: String,
    var isSelected: Boolean = false
): Parcelable


@Keep
@Parcelize
data class ChargebackFilterProcess(
    val chargebackProcessCode: Int,
    val chargebackProcessName: String,
    var isSelected: Boolean = false
): Parcelable

@Keep
data class ChargebackFilterDisputeStatus(
    val chargebackDisputeStatusCode: Int,
    val chargebackDisputeStatusName: String,
    var isSelected: Boolean = false
)

@Keep
data class ChargebackFilters(
    val brands: List<ChargebackFilterCardBrand>?,
    val process: List<ChargebackFilterProcess>?,
    val disputeStatus: List<ChargebackFilterDisputeStatus>?
)