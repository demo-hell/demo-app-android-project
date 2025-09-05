package br.com.mobicare.cielo.mySales.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Pagination(
    val pageNumber: Long? = null,
    val pageSize: Int? = null,
    val totalElements: Int? = null,
    val firstPage: Boolean? = null,
    val lastPage: Boolean? = null,
    val numPages: Int? = null
) : Parcelable