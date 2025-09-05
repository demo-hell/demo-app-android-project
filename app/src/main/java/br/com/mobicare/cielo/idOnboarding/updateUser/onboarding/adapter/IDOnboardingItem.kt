package br.com.mobicare.cielo.idOnboarding.updateUser.onboarding.adapter

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IDOnboardingItem(
    var title: String? = null,
    var subtitle: String? = null,
    var buttonText: String? = null,
    var isShowButton: Boolean = false,
    @DrawableRes var image: Int? = null
) : Parcelable
