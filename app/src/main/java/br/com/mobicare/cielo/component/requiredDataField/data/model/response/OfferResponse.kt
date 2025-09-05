package br.com.mobicare.cielo.component.requiredDataField.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class OfferResponse(
    val offer: Offer? = null,
    val required: Required? = null,
) : Parcelable

@Keep
@Parcelize
data class Offer(
    val agreements: List<Agreement>? = null,
    val description: String? = null,
    val expirationDate: String? = null,
    val id: String? = null,
    val products: List<Product>? = null,
    val settlementTerm: Int? = null,
    val validity: Int? = null,
    val itemsConfigurations: List<String>? = null
) : Parcelable

@Keep
@Parcelize
data class Agreement(
    val code: String? = null,
    val description: String? = null,
    val isMandatory: Boolean? = null,
    val status: String? = null,
    val terms: List<Term>? = null
) : Parcelable

@Keep
@Parcelize
data class Product(
    val brands: List<Brand>? = null,
    val description: String? = null,
    val id: String? = null,
    val name: String? = null,
    val note: String? = null,
    val reference: String? = null,
    val settlementTerm: Int? = null,
    val validity: Int? = null
) : Parcelable

@Keep
@Parcelize
data class Term(
    val description: String? = null,
    val url: String? = null,
    val version: String? = null
) : Parcelable

@Keep
@Parcelize
data class Brand(
    val code: String? = null,
    val conditions: List<Condition>? = null,
    val imgSource: String? = null,
    val name: String? = null
) : Parcelable

@Keep
@Parcelize
data class Condition(
    val flexibleTermPaymentMDR: Double? = null,
    val installments: List<Installment>? = null,
    val label: String? = null,
    val mdr: Double? = null,
    val rateContractedRR: Double? = null,
    val type: String? = null
) : Parcelable

@Keep
@Parcelize
data class Installment(
    val flexibleTermPaymentMDR: Double? = null,
    val installment: Int? = null,
    val mdr: Double? = null,
    val rateContractedRR: Double? = null
) : Parcelable

@Keep
@Parcelize
data class Required(
    val addressFields: List<Field>? = null,
    val companyFields: List<Field>? = null,
    val individualFields: List<Field>? = null,
    val phoneFields: List<Field>? = null
) : Parcelable

@Keep
@Parcelize
data class Field(
    val format: String? = null,
    val id: String? = null,
    val label: String? = null,
    val placeholder: String? = null
) : Parcelable