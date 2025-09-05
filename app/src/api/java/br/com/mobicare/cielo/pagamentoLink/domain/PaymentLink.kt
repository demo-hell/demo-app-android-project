package br.com.mobicare.cielo.pagamentoLink.domain
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PaymentLink(
        val type: String?,
        val name: String?,
        val price: Double?,
        val url: String?,
        val id: String?,
        val shipping: Shipping?,
        val quantity: Int?,
        val createdDate: String?,
        val softDescriptor: String?,
        val sku: String?,
        val recurrence: String?,
        val expiration: String?,
        val finalRecurrentExpiration: String?,
        val maximumInstallment: String?
) : Parcelable