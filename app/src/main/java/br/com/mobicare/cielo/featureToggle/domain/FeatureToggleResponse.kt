package br.com.mobicare.cielo.featureToggle.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize


@Keep
@Parcelize
data class FeatureToggleResponse(
    val content: List<FeatureToggle> = emptyList(),
    val pageable: Pageable? = Pageable(),
    val last: Boolean? = true,
    val totalPages: Int? = null,
    val totalElements: Int? = null,
    val first: Boolean? = null,
    val sort: Sort? = Sort(),
    val numberOfElements: Int? = null,
    val size: Int? = null,
    val number: Int? = null,
    val empty: Boolean? = null
) : Parcelable

@Keep
@Parcelize
data class FeatureToggle(
    val id: String? = null,
    val featureName: String? = null,
    val system: String? = null,
    val show: Boolean? = null,
    val status: String? = null,
    val statusMessage: String? = null,
    val platform: String? = null,
    val version: String? = null
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