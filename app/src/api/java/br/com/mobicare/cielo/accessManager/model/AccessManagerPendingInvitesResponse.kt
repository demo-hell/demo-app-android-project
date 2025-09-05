package br.com.mobicare.cielo.accessManager.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class AccessManagerPendingInvitesResponse(
        val summary: PendingInvitesSummary,
        val pagination: PendingInvitesPagination,
        val items: List<PendingInviteItem>
) : Parcelable

@Keep
@Parcelize
data class PendingInviteItem(
        val id: String,
        val cpf: String? = null,
        val email: String,
        val companyName: String,
        val identificationNumberPrefix: String,
        val merchantId: String,
        val role: String,
        val expiresOn: String,
        val expired: Boolean,
        val expiresOnFormatted: String
) : Parcelable

@Keep
@Parcelize
data class PendingInvitesPagination(
        val pageNumber: Long,
        val pageSize: Long,
        val totalElements: Long,
        val firstPage: Boolean,
        val lastPage: Boolean,
        val numPages: Long
) : Parcelable

@Keep
@Parcelize
data class PendingInvitesSummary(
        val totalQuantity: Long
) : Parcelable