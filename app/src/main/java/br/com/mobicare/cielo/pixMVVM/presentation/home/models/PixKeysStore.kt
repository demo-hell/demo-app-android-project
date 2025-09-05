package br.com.mobicare.cielo.pixMVVM.presentation.home.models

import br.com.mobicare.cielo.pixMVVM.data.model.response.PixKeysResponse

data class PixKeysStore(
    val keys: List<PixKeysResponse.KeyItem>? = null,
    val masterKey: PixKeysResponse.KeyItem? = null
)
