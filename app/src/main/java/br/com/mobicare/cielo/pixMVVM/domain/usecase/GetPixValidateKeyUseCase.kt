package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.pixMVVM.domain.repository.PixKeysRepository

class GetPixValidateKeyUseCase(private val repository: PixKeysRepository) {

    suspend operator fun invoke(key: String, keyType: String) =
        repository.getValidateKey(key, keyType)

}