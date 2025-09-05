package br.com.mobicare.cielo.splash.domain.entities

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Configuration(
    val id: String? = null,
    val key: String? = null,
    val value: String? = null
) : Parcelable

@Keep
@Parcelize
data class Sort(
    val unsorted: Boolean? = null,
    val sorted: Boolean? = null,
    val empty: Boolean? = null
) : Parcelable

@Keep
@Parcelize
data class Pageable(
    val sort: Sort? = Sort(),
    val pageNumber: Int? = null,
    val pageSize: Int? = null,
    val offset: Int? = null,
    val unpaged: Boolean? = null,
    val paged: Boolean? = null
) : Parcelable
