package br.com.mobicare.cielo.eventTracking.utils

import androidx.fragment.app.Fragment

data class ScreenData(
    val title: String,
    var isEnabled: Boolean,
    val createFragment: () -> Fragment
)