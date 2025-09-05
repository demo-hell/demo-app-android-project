package br.com.mobicare.cielo.posVirtual.presentation.onboarding.adapter

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PosVirtualOnboardingItem(
    var title: String? = null,
    var subtitle: String? = null,
    @DrawableRes var image: Int? = null
) : Parcelable