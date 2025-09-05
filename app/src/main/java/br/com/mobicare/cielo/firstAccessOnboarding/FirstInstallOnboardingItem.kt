package br.com.mobicare.cielo.firstAccessOnboarding

import androidx.annotation.DrawableRes

data class FirstInstallOnboardingItem(
    var title: String? = null,
    var subtitle: String? = null,
    @DrawableRes var image: Int? = null
)