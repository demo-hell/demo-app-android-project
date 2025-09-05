package br.com.mobicare.cielo.chargeback.presentation.details.model
import androidx.annotation.DrawableRes

data class ChargebackInfoContent(
    val firstField: ChargebackInfoContentField,
    val secondField: ChargebackInfoContentField?,
    val hideSecondField: Boolean = false,
)

enum class ChargebackInfoContentFieldType {
    REASON, MESSAGE, CARD_BRAND, DEFAULT,UNIQUE_FIELD
}

data class ChargebackInfoContentField(
    val type: ChargebackInfoContentFieldType = ChargebackInfoContentFieldType.DEFAULT,
    val labelText: String,
    @DrawableRes
    val contentIcon: Int? = null,
    val contentText: String? = null
)
