package br.com.mobicare.cielo.superlink.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.superlink.domain.model.PaginationPaymentLink
import br.com.mobicare.cielo.superlink.domain.model.PaymentLink
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PaymentLinkResponse(
    val pagination: PaginationPaymentLink,
    val items: List<PaymentLink>? = null
) : Parcelable


