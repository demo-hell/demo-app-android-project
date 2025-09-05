package br.com.mobicare.cielo.idOnboarding

import br.com.mobicare.cielo.idOnboarding.model.*

class IDOnboardingRepository(private val api: IDOnboardingApi) {

    fun getIdOnboardingStatus() =
        api.getIdOnboardingStatus()

    fun setIdOnboardingStarted() =
        api.setIdOnboardingStarted()

    fun validateCpfName(cpf: String?, name: String?) =
        api.validateCpfName(
            IDOnboardingCpfNameRequest(cpf.orEmpty(), name.orEmpty())
        )

    fun requestEmailCode(email: String?) =
        api.requestEmailCode(
            IDOnboardingSendEmailCodeRequest(email.orEmpty())
        )

    fun checkEmailCode(code: String?) =
        api.checkEmailCode(
            IDOnboardingCheckValidationCodeRequest(code.orEmpty())
        )

    fun requestPhoneCode(phoneNumber: String?, target: String) =
        api.requestPhoneCode(
            IDOnboardingSendPhoneCodeRequest(phoneNumber.orEmpty(), target)
        )

    fun checkPhoneCode(code: String?) =
        api.checkPhoneCode(
            IDOnboardingCheckValidationCodeRequest(code.orEmpty())
        )

    fun requestP1PolicyValidation() =
        api.validateP1Policy()

    fun uploadDocument(type: String?, frontBase64: String?, backBase64: String?, imageFileType: String?) =
        api.uploadDocument(
            IDOnboardingUploadDocumentRequest(
                type = type,
                frontContentBase64 = frontBase64,
                backContentBase64 = backBase64,
                imageFileType = imageFileType
            )
        )

    fun uploadSelfie(photoBase64: String?, jwt: String?) =
        api.uploadSelfie(
            IDOnboardingUploadSelfieRequest(
                photoFileContentBase64 = photoBase64,
                jwtFileContent = jwt
            )
        )

    fun sendAllowme(fingerprint: String) =
        api.sendAllowme(
            IDOnboardingSendFingerprintRequest(fingerprint)
        )

    fun validateP2Policy() =
        api.validateP2Policy()

    fun refreshToken(accessToken: String?, refreshToken: String?) =
        api.refreshToken(accessToken, refreshToken)

    fun addWhiteList() =
        api.addWhitelist()

    fun getIdOnboardingCustomerSettings() =
        api.getCustomerSettings()

    fun getStoneAgeToken() = api.getStoneAgeToken()

    fun sendForeignCellphone(cellphone: String) = api.sendForeignCellphone(IDOnboardingSendForeignCellphoneRequest(cellphone))
}