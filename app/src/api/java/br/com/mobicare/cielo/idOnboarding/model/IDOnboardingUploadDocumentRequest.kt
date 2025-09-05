package br.com.mobicare.cielo.idOnboarding.model

data class IDOnboardingUploadDocumentRequest (
    var type: String? = "",
    var imageFileType: String? = "JPG",
    var frontContentBase64: String? = null,
    var backContentBase64: String? = null
)