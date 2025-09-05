package br.com.mobicare.cielo.chargeback.data.model.request

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants.ASC
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants.PENDING
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWENTY_FIVE
import kotlinx.android.parcel.Parcelize

enum class ChargebackListParamsStatus(val value: String) {
    PENDING("PENDING"), DONE("DONE"),
}

@Keep
@Parcelize
data class ChargebackListParams(
    val page: Int = ONE,
    val pageSize: Int = TWENTY_FIVE,
    val status: String = PENDING,
    val orderBy: String = ChargebackConstants.ChargebackListParams.DEFAULT_ORDER_BY,
    val order: String = ASC,
    val cardBrandCode: ArrayList<Int>? = null,
    val idCase: Int? = null,
    val processCode: ArrayList<Int>? = null,
    val reasonCode: ArrayList<Int>? = null,
    val initDate: String? = null,
    val finalDate: String? = null,
    val tid: String? = null,
    val nsu: String? = null,
    val disputeStatus: ArrayList<Int>? = null
) : Parcelable
