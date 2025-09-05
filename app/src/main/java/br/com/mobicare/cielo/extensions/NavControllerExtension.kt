package br.com.mobicare.cielo.extensions

import androidx.navigation.NavController
import androidx.navigation.NavDirections

fun NavController.safeNavigate(destination: NavDirections) =
    currentDestination?.getAction(destination.actionId)
        ?.let { navigate(destination) }