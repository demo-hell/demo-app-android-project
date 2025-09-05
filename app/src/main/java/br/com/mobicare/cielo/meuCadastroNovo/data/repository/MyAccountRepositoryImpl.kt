package br.com.mobicare.cielo.meuCadastroNovo.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.meuCadastroNovo.data.datasource.remote.MyAccountRemoteDataSource
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.GetAdditionalInfoFields
import br.com.mobicare.cielo.meuCadastroNovo.domain.repository.MyAccountRepository

class MyAccountRepositoryImpl(
    private val remoteDataSource: MyAccountRemoteDataSource
) : MyAccountRepository {
    override suspend fun postUserDataValidation(
        email: String?,
        password: String?,
        passwordConfirmation: String?,
        cellphone: String?
    ): CieloDataResult<String> {
        return remoteDataSource.postUserDataValidation(
            email,
            password,
            passwordConfirmation,
            cellphone
        )
    }

    override suspend fun putUserUpdateData(
        email: String?,
        password: String?,
        passwordConfirmation: String?,
        cellphone: String?,
        faceIdToken: String
    ): CieloDataResult<String> {
        return remoteDataSource.putUserUpdateData(
            email,
            password,
            passwordConfirmation,
            cellphone,
            faceIdToken
        )
    }

    override suspend fun getAdditionalFieldsInfo(): CieloDataResult<GetAdditionalInfoFields> {
        return remoteDataSource.getAdditionalFieldsInfo()
    }

    override suspend fun putAdditionalInfo(
        timeOfDay: String?,
        typeOfCommunication: ArrayList<String>,
        contactPreference: String?,
        pcdType: String?
    ): CieloDataResult<String> {
        return remoteDataSource.putAdditionalInfo(
            timeOfDay, typeOfCommunication, contactPreference, pcdType
        )
    }
}