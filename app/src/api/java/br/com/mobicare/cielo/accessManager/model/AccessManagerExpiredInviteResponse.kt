package br.com.mobicare.cielo.accessManager.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class AccessManagerExpiredInviteResponse(
    val items: List<Item>? = null,
    val pagination: Pagination,
    val summary: Summary? = null
) : Parcelable

@Keep
@Parcelize
data class Item(
    val cpf: String? = null,
    val email: String? = null,
    val expired: Boolean? = null,
    val expiresOn: String? = null,
    val expiresOnFormatted: String? = null,
    val id: String? = null,
    val role: String? = null,
    val profile: Profile? = null
) : Parcelable

@Keep
@Parcelize
data class Pagination(
    val firstPage: Boolean,
    val lastPage: Boolean,
    val numPages: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Int? = null
) : Parcelable

@Keep
@Parcelize
data class Summary(
    val totalAmount: Int? = null,
    val totalQuantity: Int? = null
) : Parcelable