package br.com.mobicare.cielo.openFinance.utils

import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.TEN
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidBalanceResponse
import br.com.mobicare.cielo.openFinance.data.model.request.ChangeOrRenewShareRequest
import br.com.mobicare.cielo.openFinance.data.model.request.ConfirmShareRequest
import br.com.mobicare.cielo.openFinance.data.model.request.ConsentIdRequest
import br.com.mobicare.cielo.openFinance.data.model.request.CreateShareRequest
import br.com.mobicare.cielo.openFinance.data.model.request.EndShareRequest
import br.com.mobicare.cielo.openFinance.data.model.request.GivenUpShareRequest
import br.com.mobicare.cielo.openFinance.data.model.request.RejectConsentRequest
import br.com.mobicare.cielo.openFinance.data.model.request.UpdateShareRequest
import br.com.mobicare.cielo.openFinance.data.model.response.ConsentResponse
import br.com.mobicare.cielo.openFinance.data.model.response.Creditor
import br.com.mobicare.cielo.openFinance.data.model.response.CreditorAccount
import br.com.mobicare.cielo.openFinance.data.model.response.Detail
import br.com.mobicare.cielo.openFinance.data.model.response.DetainerResponse
import br.com.mobicare.cielo.openFinance.data.model.response.Document
import br.com.mobicare.cielo.openFinance.data.model.response.Items
import br.com.mobicare.cielo.openFinance.data.model.response.Loggeduser
import br.com.mobicare.cielo.openFinance.data.model.response.Pagination
import br.com.mobicare.cielo.openFinance.data.model.response.Payment
import br.com.mobicare.cielo.openFinance.data.model.response.SharedDataConsentsResponse
import br.com.mobicare.cielo.openFinance.data.model.response.Summary
import br.com.mobicare.cielo.openFinance.domain.model.AuthorizationServer
import br.com.mobicare.cielo.openFinance.domain.model.Brand
import br.com.mobicare.cielo.openFinance.domain.model.ChangeOrRenewShare
import br.com.mobicare.cielo.openFinance.domain.model.ConfirmShare
import br.com.mobicare.cielo.openFinance.domain.model.ConsentDetail
import br.com.mobicare.cielo.openFinance.domain.model.CreateShare
import br.com.mobicare.cielo.openFinance.domain.model.DeadLine
import br.com.mobicare.cielo.openFinance.domain.model.GivenUpShare
import br.com.mobicare.cielo.openFinance.domain.model.InfoDetailsShare
import br.com.mobicare.cielo.openFinance.domain.model.Permission
import br.com.mobicare.cielo.openFinance.domain.model.Permissions
import br.com.mobicare.cielo.openFinance.domain.model.PixMerchantListResponse
import br.com.mobicare.cielo.openFinance.domain.model.ResourceGroup
import br.com.mobicare.cielo.openFinance.domain.model.TermsOfUse
import br.com.mobicare.cielo.openFinance.domain.model.UpdateShare
import br.com.mobicare.cielo.openFinance.domain.model.UserInformation
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.RENEW_SHARE

object OpenFinanceFactory {

    val successResponsePixMerchant: List<PixMerchantListResponse>
        get() = listOf(
            PixMerchantListResponse("1", "mock 1", "0000", null, null, null),
            PixMerchantListResponse("2", "mock 2", "1111", null, null, null),
            PixMerchantListResponse("3", "mock 3", "2222", null, null, null)
        )

    val successResponseUserCardBalance: PrepaidBalanceResponse
        get() = PrepaidBalanceResponse("BRL", 1000.00)

    val successResponseConsent: ConsentResponse
        get() = ConsentResponse(EMPTY, EMPTY, EMPTY, EMPTY)

    val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    val resultEmpty = CieloDataResult.Empty()

    val resultErrorNotFound = CieloDataResult.APIError(
        CieloAPIException(
            actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
            httpStatusCode = NetworkConstants.HTTP_STATUS_404
        )
    )

    val consentIdRequest = ConsentIdRequest(EMPTY)
    val rejectConsentRequest = RejectConsentRequest(
        EMPTY,
        OpenFinanceConstants.REJECTED_USER_DETAIL,
        OpenFinanceConstants.REJECTED_USER
    )

    val successReponseDetainerResume: DetainerResponse
        get() = DetainerResponse(
            apiVersion = "v3",
            companyName = "CIELO S.A.",
            status = "REJECTED",
            consentId = "urn:cielo:b7328abe-bbb7-4346-b1b5-dd01cd543160",
            creationDate = "2024-01-02T10:08:32",
            creditor = Creditor(
                name = "Fernando Fernandes",
                personType = "PESSOA_NATURAL",
                document = Document(
                    identification = "56376560709",
                    type = "CPF"
                )
            ),
            expirationDate = "2024-01-02T10:13:32",
            issuer = "https://api-open-finance-h.cielo.com.br/of-auth/realms/cielo-sbx",
            loggedUser = Loggeduser(
                document = Document(
                    identification = "93109924099",
                    type = "CPF"
                )
            ),
            payment = Payment(
                type = "PIX",
                amount = 1.05,
                currency = "BRL",
                date = "2024-01-02",
                detail = Detail(
                    localInstrument = "INIC",
                    proxy = "35234972000125",
                    creditorAccount = CreditorAccount(
                        account = "12345678",
                        agency = "0001"
                    ),
                ),
                issuer = "",
                schedule = null
            )
        )

    val successResponseSharedDataConsent: SharedDataConsentsResponse
        get() = SharedDataConsentsResponse(summary, pagination, listItems)

    val summary = Summary(ZERO, ZERO, "10", "3", "2", "1")
    val pagination = Pagination(ZERO, ZERO, ZERO, true, false, ZERO)
    val items = Items(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY)
    val listItems = listOf(
        items,
        items,
        items
    )

    val successConsentDetail = ConsentDetail(
        consentSatus = "ACTIVE",
        flow = "RECEIVING",
        consentId = "urn:cielo:3e3e3e3e-3e3e-3e3e-3e3e-3e3e3e3e3e3e",
        shareId = "65e9d659aee09a7ff8984b41",
        brand = "CIELO",
        organizationId = "d96d6def-7f8f-5623-b15a-0bb5910920d1",
        loggedUser = "31215646330",
        userName = "Fernando Fernandes",
        merchantId = "2040633272",
        document = "56376560709",
        alteration = false,
        renovation = false,
        cancelation = false,
        permissions = listOf(
            Permissions(
                permissionCode = "PAGAMENTO",
                displayName = "Pagamento",
                permissionDescription = "Permite que o usuário realize pagamentos"
            )
        ),
        deadLine = DeadLine(
            total = ONE,
            type = "HOURS",
            expirationDate = "2024-01-02T10:13:32"
        ),
        confirmationDateTime = "2024-01-02T10:08:32",
        expirationDateTime = "2024-01-02T10:13:32",
        lastUpdateDateTime = "2024-01-02T10:08:32",
        authorizationServerId = "bchdvcscbnjsxncbnvb",
        logoUri = "https://api-open-finance-h.cielo.com.br/of-auth/realms/cielo-sbx"
    )

    val consentId = "urn:cielo:3e3e3e3e-3e3e-3e3e-3e3e-3e3e3e3e3e3e"

    val successBrands = List(TEN) { Brand(EMPTY, emptyList()) }

    const val TEST_BANK = "Test Bank"
    const val TEST_ANOTHER_BANK = "Another Bank"
    const val NONEXISTENT = "Nonexistent"

    val successCreateShare = CreateShare(
        authorizationServer = AuthorizationServer(EMPTY, EMPTY, EMPTY, EMPTY),
        deadLines = listOf(DeadLine(ONE, EMPTY, EMPTY)),
        shareId = EMPTY,
        resourceGroups = listOf(ResourceGroup(EMPTY, EMPTY, listOf(
            Permission(EMPTY, EMPTY, false, EMPTY)))),
        userInformation = UserInformation(EMPTY, EMPTY)
    )

    val requestCreateShare = CreateShareRequest(EMPTY, EMPTY, null)

    val requestUpdateShare = UpdateShareRequest(
        dataPermissions = emptyList(),
        deadLine = DeadLine(ONE, EMPTY, EMPTY),
        redirectUri = BuildConfig.URL_OPF_CALLBACK
    )

    val responseUpdateShare = UpdateShare(
        redirectUri = EMPTY
    )

    val deadline = DeadLine(ONE, EMPTY, EMPTY)

    val responseTermsOfUse = TermsOfUse(EMPTY)

    val requestConfirmShare = ConfirmShareRequest("123", EMPTY, EMPTY)
    val requestGivenUpShare = GivenUpShareRequest(EMPTY, EMPTY, EMPTY)

    val responseConfirmShare = ConfirmShare(EMPTY, EMPTY, EMPTY, EMPTY)
    val responseGivenUpShare = GivenUpShare(EMPTY, EMPTY, EMPTY)
    val authorizationCodeSample = "123"

    val infoDetailsShare = InfoDetailsShare(
        EMPTY,
        EMPTY,
        EMPTY,
        EMPTY,
        deadline
    )

    val jsonStringInfoDetailsShare = "{\n" +
            "“function”:””,\n" +
            "“consentId”:””,\n" +
            "“shareId”:””,\n" +
            "“flow”:””,\n" +
            "“deadline”: {\n" +
            "\t“total”:ONE,\n" +
            "\t“type”:””,\n" +
            "\t“expirationDate”:””\n" +
            "\t}\n" +
            "}"

    val requestChangeOrRenewShare = ChangeOrRenewShareRequest(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY)
    val responseChangeOrRenewShare = ChangeOrRenewShare(EMPTY, EMPTY, EMPTY, EMPTY)

    val endShareRequest = EndShareRequest(null, null, RENEW_SHARE, EMPTY, EMPTY, EMPTY)
}