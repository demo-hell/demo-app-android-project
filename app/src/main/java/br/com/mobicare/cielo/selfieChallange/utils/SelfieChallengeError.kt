package br.com.mobicare.cielo.selfieChallange.utils

import java.io.Serializable

data class SelfieChallengeError(
    var type: SelfieErrorEnum,
    var message: String? = null,
    var errorCode: String? = null
): Serializable