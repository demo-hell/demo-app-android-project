package br.com.mobicare.cielo.selfieChallange.data.model.request

data class SelfieChallengeRequest(
    var operation: String? = null,
    var photoFileContentBase64: String? = null,
    var imageFileType: String? = null,
    var jwtFileContent: String? = null)
