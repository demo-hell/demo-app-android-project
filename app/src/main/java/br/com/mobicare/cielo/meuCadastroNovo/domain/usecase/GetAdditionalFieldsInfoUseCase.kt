package br.com.mobicare.cielo.meuCadastroNovo.domain.usecase

import br.com.mobicare.cielo.meuCadastroNovo.domain.repository.MyAccountRepository

class GetAdditionalFieldsInfoUseCase(
    private val repository: MyAccountRepository
) {
    suspend operator fun invoke() = repository.getAdditionalFieldsInfo()
}