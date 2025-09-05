package br.com.mobicare.cielo.simulator.simulation.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PaymentType(
    val productCode: Int? = null,
    val fastRepay: Boolean? = null,
    val productDescription: String? = null
): Parcelable

@Keep
@Parcelize
data class Product(
    val cardBrandCode: String? = null,
    val cardBrandName: String? = null,
    val paymentTypes: List<PaymentType>? = null
): Parcelable

@Keep
@Parcelize
data class SimulatorProducts(
    val merchant: String? = null,
    val products: List<Product>? = null
): Parcelable
