package br.com.mobicare.cielo.meuCadastroNovo.domain.usecase

import br.com.mobicare.cielo.meuCadastroNovo.domain.repository.MyAccountRepository

class PostUserValidateDataUseCase(
    private val repository: MyAccountRepository
) {
    suspend operator fun invoke(
        email: String?,
        password: String?,
        passwordConfirmation: String?,
        cellphone: String?
    ) = repository.postUserDataValidation(
        email, password, passwordConfirmation, cellphone
    )
}