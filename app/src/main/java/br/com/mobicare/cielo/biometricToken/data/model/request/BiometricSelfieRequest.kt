package br.com.mobicare.cielo.biometricToken.data.model.request

data class BiometricSelfieRequest(
    var imageFileType: String? = null,
    var photoFileContentBase64: String? = null,
    var jwtFileContent: String? = null)
