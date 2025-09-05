package br.com.mobicare.cielo.mySales.data.model.responses

import android.os.Parcelable
import br.com.mobicare.cielo.mySales.data.model.PaymentType
import br.com.mobicare.cielo.mySales.data.model.SaleCardBrand
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResultPaymentTypes(val cardBrands: List<SaleCardBrand>?,
                              val paymentTypes: List<PaymentType>?) : Parcelable
