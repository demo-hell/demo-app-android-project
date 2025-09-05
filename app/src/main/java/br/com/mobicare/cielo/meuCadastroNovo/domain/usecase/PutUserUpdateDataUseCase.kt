package br.com.mobicare.cielo.meuCadastroNovo.domain.usecase

import br.com.mobicare.cielo.meuCadastroNovo.domain.repository.MyAccountRepository

class PutUserUpdateDataUseCase (
    private val repository: MyAccountRepository
) {
    suspend operator fun invoke(
        email: String?,
        password: String?,
        passwordConfirmation: String?,
        cellphone: String?,
        faceIdToken: String
    ) = repository.putUserUpdateData(
        email, password, passwordConfirmation, cellphone, faceIdToken
    )
}

