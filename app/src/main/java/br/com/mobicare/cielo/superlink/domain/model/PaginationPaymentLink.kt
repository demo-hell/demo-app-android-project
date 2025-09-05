package br.com.mobicare.cielo.superlink.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PaginationPaymentLink (
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Int,
    val firstPage: Boolean,
    val lastPage: Boolean,
    val numPages: Int
) : Parcelable