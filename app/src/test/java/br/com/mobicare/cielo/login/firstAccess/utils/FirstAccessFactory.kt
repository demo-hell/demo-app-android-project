package br.com.mobicare.cielo.login.firstAccess.utils

import br.com.mobicare.cielo.commons.constants.ERROR_NOT_BOOTING
import br.com.mobicare.cielo.commons.constants.HTTP_500_ERROR
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.login.firstAccess.data.model.request.FirstAccessPayIdRequest
import br.com.mobicare.cielo.login.firstAccess.data.model.request.FirstAccessRegistrationRequest
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessResponse
import com.google.gson.Gson

object FirstAccessFactory {

    const val mockCpf = "27386141084"
    const val mockEmail = "aduabdasl@cielo.com.br"
    const val mockPassword = "040621"
    const val mockPasswordConfirmation = "040621"
    const val mockNumberEc = "2014158970"
    const val errorMessage = "Caro cliente, você deve solicitar o acesso ao Proprietário ou Administrador do Estabelecimento."

    private val firstAccessResponseJson = """
        {
          "tokenExpirationInMinutes": 1440,
          "email": "e****@c****.com.br"
        }
    """.trimIndent()

    val firstAccessResponse: FirstAccessResponse = Gson().fromJson(
        firstAccessResponseJson,
        FirstAccessResponse::class.java
    )

    fun getNotBootingError() = CieloAPIException.notBootingError(
        response = null,
        HTTP_500_ERROR,
        ActionErrorTypeEnum.HTTP_ERROR,
        NewErrorMessage().apply { flagErrorCode = ERROR_NOT_BOOTING }
    )

    fun getFirstAccessResponseSuccess() = FirstAccessResponse(
        email = "e****@c****.com.br",
        tokenExpirationInMinutes = 1440
    )

    val firstAccessRegistrationRequest = FirstAccessRegistrationRequest(
    pid = FirstAccessPayIdRequest(merchantId = mockNumberEc),
    cpf = mockCpf,
    email = mockEmail,
    password = mockPassword,
    passwordConfirmation = mockPasswordConfirmation
    )
}