package br.com.mobicare.cielo.component.onboarding.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BaseOnboardingPage(
    @StringRes val title: Int? = null,
    @StringRes val subtitle: Int? = null,
    @DrawableRes val image: Int? = null
) : Parcelable