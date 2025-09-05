package br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.commons.constants.Postecipate.ZERO_VALUE_MONEY
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.pix.constants.EMPTY
import kotlinx.android.parcel.Parcelize

@Keep
class PlanInformationResponse : ArrayList<PostecipadoRentInformationResponse>()

@Parcelize
@Keep
data class PostecipadoRentInformationResponse(
        val billingPerformed: Double? = ZERO_VALUE_MONEY,
        val currentDate: String? = EMPTY,
        val dateUpdate: String? = EMPTY,
        val expirationDate: String? = EMPTY,
        val isExempted: Boolean?,
        val isWaitingPeriod: Boolean?,
        val missingValue: Double? = ZERO_VALUE_MONEY,
        val percentageDiscountNegotiated: String? = EMPTY,
        val percentageDiscountPartial: String? = EMPTY,
        val percentageMissing: String? = EMPTY,
        val percentageReached: String? = EMPTY,
        val referenceMonth: String? = EMPTY,
        val terminals: List<Terminal>? = listOf(),
        val valueContract: Double? = ZERO_VALUE_MONEY,
        val valueDiscountNegotiated: Double? = ZERO_VALUE_MONEY,
        val valueDiscountPartial: Double? = ZERO_VALUE_MONEY,
        val daysToEndTheMonth: Int? = ZERO
) : Parcelable

@Keep
@Parcelize
data class Terminal(
        val percentageDiscountNegotiated: String? = EMPTY,
        val percentageDiscountPartial: String? = EMPTY,
        val terminalCode: String? = EMPTY,
        val terminalName: String? = EMPTY,
        val terminalQuantity: Int? = ZERO,
        val valueDiscountNegotiated: Double? = ZERO_VALUE_MONEY,
        val valueDiscountPartial: Double? = ZERO_VALUE_MONEY
) : Parcelable