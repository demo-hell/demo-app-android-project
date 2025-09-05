package br.com.mobicare.cielo.superlink.domain.model

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

@Keep
@Parcelize
data class Shipping(
    val type: String,
    val zipCode: String? = null,
    val price: Double? = null,
    val name: String? = null,
    val spec: Specification? = null,
    val pickupAddress: Address? = null,
    val withdrawalDelay: Int? = null
) : Parcelable

@Keep
@Parcelize
data class Specification(
    val dimension: Dimension
) : Parcelable

@Keep
@Parcelize
data class Dimension(
    val length: Int?,
    val height: Int?,
    val width: Int?
) : Parcelable

@Keep
@Parcelize
data class Address(
    val type: String?,
    val streetAddress: String?,
    val streetAddress2: String? = null,
    val neighborhood: String?,
    val city: String?,
    val number: String?,
    val state: String?,
    val zipCode: String?,
    val contactPhone: String?,
    val note: String? = null
) : Parcelable