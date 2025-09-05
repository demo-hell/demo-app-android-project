package br.com.mobicare.cielo.meuCadastroNovo.domain.usecase

import br.com.mobicare.cielo.meuCadastroNovo.domain.repository.MyAccountRepository

class PutAdditionalInfoUseCase(
    private val repository: MyAccountRepository
) {
    suspend operator fun invoke(
        timeOfDay: String?,
        typeOfCommunication: ArrayList<String>,
        contactPreference: String?,
        pcdType: String?
    ) = repository.putAdditionalInfo(
        timeOfDay, typeOfCommunication, contactPreference, pcdType
    )
}