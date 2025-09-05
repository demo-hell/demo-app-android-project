package br.com.mobicare.cielo.pagamentoLink.domains

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.autoAtendimento.domain.MachineInstallAddressObj
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Keep
@Parcelize
class PaymentLinkDTO(
    var typeSale: TypeSaleEnum,
    var typeSaleFrequency: TypeSalePeriodicEnum? = null,
    var responsibleDelivery: ResponsibleDeliveryEnum? = null,
    var productName: String? = null,
    var address: MachineInstallAddressObj? = null,
    var phone: String? = null,
    var dalayTime: Int? = null,
    var productDetail: ProductDetailDTO? = null,
    var costOfFreight: BigDecimal? = BigDecimal.valueOf(0.0),
    var productValue: BigDecimal? = BigDecimal.valueOf(0.0),
    var zipCode: String? = null,
    var weight: Int? = null,
    var quantity: Int? = null,
    var finalRecurrentExpiration: String? = null,
    var nameOfFreight: String? = null
) : Parcelable

@Keep
@Parcelize
class ProductDetailDTO(
        var size: String? = null,
        var height: Int? = null,
        var width: Int? = null,
        var depth: Int? = null
) : Parcelable