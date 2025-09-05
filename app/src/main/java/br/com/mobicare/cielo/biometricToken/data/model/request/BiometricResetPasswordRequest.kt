package br.com.mobicare.cielo.biometricToken.data.model.request

data class BiometricResetPasswordRequest(
    var newPassword: String? = null,
    var newPasswordConfirmation: String? = null
)
