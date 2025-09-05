package br.com.mobicare.cielo.simulator.simulation.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.simulator.simulation.domain.model.PaymentType
import br.com.mobicare.cielo.simulator.simulation.domain.model.Product
import br.com.mobicare.cielo.simulator.simulation.domain.model.SimulatorProducts
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PaymentTypeResponse(
    val productCode: Int? = null,
    val fastRepay: Boolean? = null,
    val productDescription: String? = null
) : Parcelable

fun PaymentTypeResponse.toPaymentType() = PaymentType(productCode, fastRepay, productDescription)

@Keep
@Parcelize
data class ProductResponse(
    val cardBrandCode: String? = null,
    val cardBrandName: String? = null,
    val paymentTypes: List<PaymentTypeResponse>? = null
) : Parcelable

fun ProductResponse.toProduct() =
    Product(cardBrandCode, cardBrandName, paymentTypes?.map { it.toPaymentType() })

@Keep
@Parcelize
data class SimulatorProductsResponse(
    val merchant: String? = null,
    val products: List<ProductResponse>? = null
) : Parcelable

fun SimulatorProductsResponse.toSimulatorProducts() =
    SimulatorProducts(merchant, products?.map { it.toProduct() })
