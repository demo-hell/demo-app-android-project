package br.com.mobicare.cielo.accessManager.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class AccessManagerPendingForeignUsersResponse(
        val summary: ForeignUsersSummary? = null,
        val pagination: ForeignUsersPagination? = null,
        val items: List<ForeignUsersItem>? = null
) : Parcelable

@Keep
@Parcelize
data class ForeignUsersItem(
        val id: String? = null,
        val cpf: String? = null,
        val name: String? = null,
        val email: String? = null,
        val companyName: String? = null,
        val identificationNumberPrefix: String? = null,
        val merchantId: String? = null,
        val profile: Profile? = null,
        val role: String? = null,
        val expiresOn: String? = null,
        val expired: Boolean? = null,
        val expiresOnFormatted: String? = null,
        val sendToRevisionDateTime: String? = null
) : Parcelable

@Keep
@Parcelize
data class ForeignUsersPagination(
        val pageNumber: Long? = null,
        val pageSize: Long? = null,
        val totalElements: Long? = null,
        val firstPage: Boolean? = null,
        val lastPage: Boolean? = null,
        val numPages: Long? = null
) : Parcelable

@Keep
@Parcelize
data class ForeignUsersSummary(
        val totalQuantity: Long? = null
) : Parcelable