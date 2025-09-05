package br.com.mobicare.cielo.meuCadastroNovo.utils

import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.GetAdditionalInfoFields

sealed class AdditionalInfoUiState {
    class GetSuccess(val additionalFields: GetAdditionalInfoFields): AdditionalInfoUiState()
    class GetError(): AdditionalInfoUiState()
    class UpdateSuccess(): AdditionalInfoUiState()
    class UpdateError(): AdditionalInfoUiState()
}