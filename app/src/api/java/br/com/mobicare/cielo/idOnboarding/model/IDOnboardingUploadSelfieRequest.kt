package br.com.mobicare.cielo.idOnboarding.model

data class IDOnboardingUploadSelfieRequest (
    var imageFileType: String = "JPG",
    var photoFileContentBase64: String? = null,
    var jwtFileContent: String? = null
)