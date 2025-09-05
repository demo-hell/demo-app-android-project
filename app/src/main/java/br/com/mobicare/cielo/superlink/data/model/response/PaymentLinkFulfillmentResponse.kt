package br.com.mobicare.cielo.superlink.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.component.impersonate.data.model.response.MerchantResponse
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PaymentLinkFulfillmentResponse(
    val isEligible: Boolean? = null,
    val isSubscribed: Boolean? = null,
    val isImpersonateRequired: Boolean? = null,
    val merchants: List<MerchantResponse>? = null,
    val inconsistencies: List<Inconsistency>? = null
) : Parcelable

@Keep
@Parcelize
data class Inconsistency(
    val description: String? = null,
    val requiredAction: String? = null
): Parcelable