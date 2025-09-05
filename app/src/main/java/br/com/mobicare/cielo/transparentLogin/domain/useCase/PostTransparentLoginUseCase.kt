package br.com.mobicare.cielo.transparentLogin.domain.useCase

import br.com.mobicare.cielo.newLogin.domain.LoginRequest
import br.com.mobicare.cielo.transparentLogin.domain.repository.TransparentLoginRepository

class PostTransparentLoginUseCase(
    private val repository: TransparentLoginRepository
) {
    suspend operator fun invoke(
        request: LoginRequest,
        ignoreSessionExpired: String,
        akamaiSensorData: String?
    ) = repository.login(request, ignoreSessionExpired, akamaiSensorData)
}