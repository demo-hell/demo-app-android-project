package br.com.mobicare.cielo.pagamentoLink.domain


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CreateLinkBodyRequest(
        val type: String,
        val name: String,
        val price: Double,
        val shipping: Shipping,
        val weight: Int? = null,
        val description: String? = null,
        val quantity: Int? = null,
        var softDescriptor: String? = null,
        var expiration: String? = null,
        var finalRecurrentExpiration: String? = null,
        var sku: String? = null,
        var maximumInstallment: Int? = null
) : Parcelable

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

@Parcelize
data class Specification(
        val dimension: Dimension
) : Parcelable

@Parcelize
data class Dimension(
        val length: Int?,
        val height: Int?,
        val width: Int?
) : Parcelable

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