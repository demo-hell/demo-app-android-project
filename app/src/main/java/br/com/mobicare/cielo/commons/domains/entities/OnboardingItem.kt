package br.com.mobicare.cielo.commons.domains.entities

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OnboardingItem(
    var title: String? = null,
    var subtitle: String? = null,
    @DrawableRes var image: Int? = null,
    var isGif: Boolean = false
) : Parcelable