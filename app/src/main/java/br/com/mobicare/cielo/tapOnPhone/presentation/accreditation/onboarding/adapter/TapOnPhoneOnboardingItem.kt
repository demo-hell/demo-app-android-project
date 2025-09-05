package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.onboarding.adapter

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TapOnPhoneOnboardingItem(
    var title: String? = null,
    var subtitle: String? = null,
    @DrawableRes var image: Int? = null
) : Parcelable
