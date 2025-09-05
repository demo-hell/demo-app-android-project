package br.com.mobicare.cielo.accessManager.domain.usecase

import br.com.mobicare.cielo.accessManager.domain.repository.NewAccessManagerRepository

class PostAssignRoleUseCase(
    private val repository: NewAccessManagerRepository
) {
    suspend operator fun invoke(
        usersId: List<String>,
        role: String,
        otpCode: String
    ) = repository.postAssignRole(usersId, role, otpCode)
}