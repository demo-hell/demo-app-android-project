package br.com.mobicare.cielo.pedidos.tracking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class OrderAffiliationDetail(
        val id: Long? = null,
        val amount: Double? = null,
        val customer: Customer? = null,
        val payments: List<Payment>? = null,
        val shipments: List<Shipment>? = null,
        val tracking: Tracking? = null
)

data class Customer(
        val name: String? = null,
        val address: Address? = null
)

data class Address(
        val streetAddress: String? = null,
        val streetAddress2: String? = null,
        val neighborhood: String? = null,
        val number: String? = null,
        val city: String? = null,
        val state: String? = null,
        val zipCode: String? = null
)

data class Payment(
        val amount: Double? = null,
        val type: String? = null,
        val typeDescription: String? = null
)

data class Shipment(
        val carrier: String? = null,
        val items: List<Item>? = null
)

data class Item(
        val name: String? = null,
        val quantity: String? = null,
        val price: String? = null,
        val status: String? = null,
        val statusDescription: String? = null
)

@Parcelize
data class Tracking(
        val status: String? = null,
        val description: String? = null,
        val steps: List<Step>? = null
) : Parcelable

@Parcelize
data class Step(
        val description: String? = null,
        val lastUpdated: String? = null
) : Parcelable
