package br.com.mobicare.cielo.selfieChallange.utils

import java.io.Serializable

data class SelfieChallengeParams(
    val isForeign : Boolean = false,
    val cameraSDK: SelfieCameraSDK? = null,
    val username: String? = null,
    val operation: SelfieOperation
) : Serializable
