package br.com.mobicare.cielo.meuCadastroNovo.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.GetAdditionalInfoFields

interface MyAccountRepository {

    suspend fun postUserDataValidation(
        email: String?,
        password: String?,
        passwordConfirmation: String?,
        cellphone: String?
    ): CieloDataResult<String>

    suspend fun putUserUpdateData(
        email: String?,
        password: String?,
        passwordConfirmation: String?,
        cellphone: String?,
        faceIdToken: String
    ): CieloDataResult<String>

    suspend fun getAdditionalFieldsInfo(): CieloDataResult<GetAdditionalInfoFields>

    suspend fun putAdditionalInfo(
        timeOfDay: String?,
        typeOfCommunication: ArrayList<String>,
        contactPreference: String?,
        pcdType: String?
    ): CieloDataResult<String>
}