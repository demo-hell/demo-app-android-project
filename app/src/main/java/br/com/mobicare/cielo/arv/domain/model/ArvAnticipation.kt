package br.com.mobicare.cielo.arv.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.arv.presentation.anticipation.adapter.ArvSelectableItem
import br.com.mobicare.cielo.arv.utils.ArvConstants
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ArvAnticipation(
    val acquirers: List<Acquirer?>? = null,
    val discountAmount: Double? = null,
    val finalDate: String? = null,
    val grossAmount: Double? = null,
    val id: String? = null,
    val initialDate: String? = null,
    val negotiationType: String? = null,
    val netAmount: Double? = null,
    val nominalFee: Double? = null,
    val standardFee: Double? = null,
    val token: String? = null,
    val effectiveFee: Double? = null,
    val simulationType: String? = null,
    val eligibleTimeToReceiveToday: Boolean = false,
    val isFromCardHomeFlow: Boolean = false,
) : Parcelable {
    private fun appendNotSelectedBrands(
        cardBrandList: List<CardBrand>
    ): ArvAnticipation {
        val notSelectedBrands = cardBrandList.filter { it.isSelected.not() }
        var acquirer = this.acquirers?.first()
        val cardBrands = acquirer?.cardBrands?.plus(notSelectedBrands)
        acquirer = acquirer?.copy(cardBrands = cardBrands.orEmpty())
        return this.copy(acquirers = listOf(acquirer))
    }

    private fun appendNotSelectedAcquirer(
        acquirerList: List<Acquirer>
    ): ArvAnticipation {
        val notSelectedAcquirers = acquirerList.filter { it.isSelected.not() }
        val acquirers = this.acquirers?.plus(notSelectedAcquirers).orEmpty()
        return this.copy(acquirers = acquirers)
    }

    fun appendNotSelected(
        selectionList: List<ArvSelectableItem>
    ): ArvAnticipation {
        return when (negotiationType) {
            ArvConstants.CIELO_NEGOTIATION_TYPE -> appendNotSelectedBrands(selectionList.map { it as CardBrand } )
            ArvConstants.MARKET_NEGOTIATION_TYPE -> appendNotSelectedAcquirer(selectionList.map { it as Acquirer })
            else -> this
        }
    }

    fun appendNotSelected(
        previousAnticipation: ArvAnticipation
    ): ArvAnticipation {
        return when (negotiationType) {
            ArvConstants.CIELO_NEGOTIATION_TYPE -> appendNotSelected(previousAnticipation.acquirers?.first()?.cardBrands.orEmpty())
            ArvConstants.MARKET_NEGOTIATION_TYPE -> appendNotSelected(previousAnticipation.acquirers?.filterNotNull().orEmpty())
            else -> this
        }
    }
}

@Keep
@Parcelize
data class Acquirer(
    override val code: Int? = null,
    override val cardBrands: List<CardBrand>? = null,
    override val discountAmount: Double? = null,
    override val grossAmount: Double? = null,
    override val name: String? = null,
    override val netAmount: Double? = null,
    override var isSelected: Boolean = true
) : Parcelable, ArvSelectableItem {
    override fun copy() = copy(
        code = code,
        cardBrands = cardBrands,
        discountAmount = discountAmount,
        grossAmount = grossAmount,
        name = name,
        netAmount = netAmount,
        isSelected = isSelected
    )
}

@Keep
@Parcelize
data class CardBrand(
    override val code: Int? = null,
    override val discountAmount: Double? = null,
    override val grossAmount: Double? = null,
    override val name: String? = null,
    override val netAmount: Double? = null,
    override var isSelected: Boolean = true
) : Parcelable, ArvSelectableItem {
    override val cardBrands: List<CardBrand>?
        get() = null

    override fun copy() = copy(
        code = code,
        discountAmount = discountAmount,
        grossAmount = grossAmount,
        name = name,
        netAmount = netAmount,
        isSelected = isSelected
    )
}

