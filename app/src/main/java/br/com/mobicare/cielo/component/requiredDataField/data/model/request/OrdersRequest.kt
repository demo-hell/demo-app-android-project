package br.com.mobicare.cielo.component.requiredDataField.data.model.request

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class OrdersRequest(
    val registrationData: List<Field>? = null,
    val order: Order? = null,
    val type: String? = null
) : Parcelable

@Keep
@Parcelize
data class Field(
    val id: String? = null,
    val value: String? = null
) : Parcelable

@Keep
@Parcelize
data class Order(
    val agreements: List<Agreement>? = null,
    val itemsConfigurations: List<String>? = null,
    val offerId: String? = null,
    val payoutData: PayoutData? = null,
    val sessionId: String? = null
) : Parcelable

@Keep
@Parcelize
data class Agreement(
    val code: String? = null,
    val value: String? = null
) : Parcelable

@Keep
@Parcelize
data class PayoutData(
    val payoutMethod: String? = null,
    val targetBankAccount: TargetBankAccount? = null
) : Parcelable

@Keep
@Parcelize
data class TargetBankAccount(
    val accountNumber: String? = null,
    val accountType: String? = null,
    val agency: String? = null,
    val bankNumber: String? = null
) : Parcelable