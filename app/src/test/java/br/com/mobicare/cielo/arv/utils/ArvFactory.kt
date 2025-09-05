package br.com.mobicare.cielo.arv.utils

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.arv.data.model.request.ArvConfirmAnticipationRequest
import br.com.mobicare.cielo.arv.data.model.request.ArvConfirmScheduledAnticipationRequest
import br.com.mobicare.cielo.arv.data.model.request.ArvScheduledAnticipationCancelRequest
import br.com.mobicare.cielo.arv.data.model.request.CancelNegotiationType
import br.com.mobicare.cielo.arv.data.model.response.ArvConfirmAnticipationResponse
import br.com.mobicare.cielo.arv.data.model.response.ArvHistoricResponse
import br.com.mobicare.cielo.arv.data.model.response.Item
import br.com.mobicare.cielo.arv.data.model.response.Pagination
import br.com.mobicare.cielo.arv.domain.model.Acquirer
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.model.ArvBank
import br.com.mobicare.cielo.arv.domain.model.ArvBranchesContracts
import br.com.mobicare.cielo.arv.domain.model.ArvOptIn
import br.com.mobicare.cielo.arv.domain.model.ArvScheduledAnticipation
import br.com.mobicare.cielo.arv.domain.model.CardBrand
import br.com.mobicare.cielo.arv.domain.model.RateSchedules
import br.com.mobicare.cielo.arv.domain.model.Schedules
import br.com.mobicare.cielo.arv.presentation.anticipation.ArvScheduledAnticipationBankSelectFragmentArgs
import br.com.mobicare.cielo.arv.presentation.home.utils.ArvWhatsAppContactData
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.NOT_ELIGIBLE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.me.MeResponse
import br.com.mobicare.cielo.pix.constants.CIELO
import com.google.gson.Gson

object ArvFactory {
    private val meJson =
        "{\n" +
            "   \"id\":\"0b0c0000000f0000ba0000fbf00fa00c0000df0fe000e0a000f\",\n" +
            "   \"advertisingId\":\"0b0c0000000f0000ba0000fbf00fa00c0000df0fe000e0a000\",\n" +
            "   \"username\":\"Massa teste\",\n" +
            "   \"login\":\"00000000051\",\n" +
            "   \"email\":\"teste@teste.com\",\n" +
            "   \"birthDate\":\"1990-06-11\",\n" +
            "   \"identity\":{\n" +
            "      \"cpf\":\"00000000051\",\n" +
            "      \"foreigner\":false\n" +
            "   },\n" +
            "   \"phoneNumber\":\"(11) 99999-9999\",\n" +
            "   \"roles\":[\n" +
            "      \"MASTER\"\n" +
            "   ],\n" +
            "   \"merchant\":{\n" +
            "      \"id\":\"0000000005\",\n" +
            "      \"name\":\"NAME TEST\",\n" +
            "      \"tradingName\":\"TEST\",\n" +
            "      \"cnpj\":{\n" +
            "         \"rootNumber\":\"00000000\",\n" +
            "         \"number\":\"00.000.000/0001-00\"\n" +
            "      },\n" +
            "      \"receivableType\":\"Individual\",\n" +
            "      \"hierarchyLevel\":\"TEST_GROUP\",\n" +
            "      \"individual\":false,\n" +
            "      \"migrated\":true\n" +
            "   },\n" +
            "   \"activeMerchant\":{\n" +
            "      \"id\":\"2200000000\",\n" +
            "      \"name\":\"NAME TEST\",\n" +
            "      \"tradingName\":\"TEST\",\n" +
            "      \"cnpj\":{\n" +
            "         \"rootNumber\":\"00000000\",\n" +
            "         \"number\":\"00.000.000/0001-00\"\n" +
            "      },\n" +
            "      \"receivableType\":\"Individual\",\n" +
            "      \"hierarchyLevel\":\"TEST_GROUP\",\n" +
            "      \"individual\":false,\n" +
            "      \"migrated\":true\n" +
            "   },\n" +
            "   \"impersonating\":false,\n" +
            "   \"impersonationEnabled\":true,\n" +
            "   \"lastLoginDate\":\"2023-02-24T00:00:00\",\n" +
            "   \"isMigrationRequired\":false,\n" +
            "   \"onboardingRequired\":false,\n" +
            "   \"digitalId\":{\n" +
            "      \"status\":\"P2_APPROVED\",\n" +
            "      \"deadline\":\"2022-06-11\",\n" +
            "      \"mandatory\":false,\n" +
            "      \"migrated\":true,\n" +
            "      \"expired\":false,\n" +
            "      \"p1Approved\":false,\n" +
            "      \"p2Approved\":true,\n" +
            "      \"notApproved\":false\n" +
            "   }\n" +
            "}"

    val meResponse = Gson().fromJson(meJson, MeResponse::class.java)

    private val meJsonWithoutCNPJNumber =
        "{\n" +
            "   \"advertisingId\":\"0b0c0000000f0000ba0000fbf00fa00c0000df0fe000e0a000\",\n" +
            "   \"username\":\"Massa teste\",\n" +
            "   \"login\":\"00000000051\",\n" +
            "   \"email\":\"teste@teste.com\",\n" +
            "   \"birthDate\":\"1990-06-11\",\n" +
            "   \"identity\":{\n" +
            "      \"cpf\":\"00000000051\",\n" +
            "      \"foreigner\":false\n" +
            "   },\n" +
            "   \"phoneNumber\":\"(11) 99999-9999\",\n" +
            "   \"roles\":[\n" +
            "      \"MASTER\"\n" +
            "   ],\n" +
            "   \"merchant\":{\n" +
            "      \"id\":\"0000000005\",\n" +
            "      \"name\":\"NAME TEST\",\n" +
            "      \"tradingName\":\"TEST\",\n" +
            "      \"cnpj\":{\n" +
            "         \"number\":\"00.000.000/0001-00\"\n" +
            "      },\n" +
            "      \"receivableType\":\"Individual\",\n" +
            "      \"hierarchyLevel\":\"TEST_GROUP\",\n" +
            "      \"individual\":false,\n" +
            "      \"migrated\":true\n" +
            "   },\n" +
            "   \"activeMerchant\":{\n" +
            "      \"id\":\"2200000000\",\n" +
            "      \"name\":\"NAME TEST\",\n" +
            "      \"tradingName\":\"TEST\",\n" +
            "      \"cnpj\":{\n" +
            "         \"rootNumber\":\"00000000\"\n" +
            "      },\n" +
            "      \"receivableType\":\"Individual\",\n" +
            "      \"hierarchyLevel\":\"TEST_GROUP\",\n" +
            "      \"individual\":false,\n" +
            "      \"migrated\":true\n" +
            "   },\n" +
            "   \"impersonating\":false,\n" +
            "   \"impersonationEnabled\":true,\n" +
            "   \"lastLoginDate\":\"2023-02-24T00:00:00\",\n" +
            "   \"isMigrationRequired\":false,\n" +
            "   \"onboardingRequired\":false,\n" +
            "   \"digitalId\":{\n" +
            "      \"status\":\"P2_APPROVED\",\n" +
            "      \"deadline\":\"2022-06-11\",\n" +
            "      \"mandatory\":false,\n" +
            "      \"migrated\":true,\n" +
            "      \"expired\":false,\n" +
            "      \"p1Approved\":false,\n" +
            "      \"p2Approved\":true,\n" +
            "      \"notApproved\":false\n" +
            "   }\n" +
            "}"

    val meResponseWithoutCNPJNumber =
        Gson().fromJson(meJsonWithoutCNPJNumber, MeResponse::class.java)

    val anticipationWithValue = ArvAnticipation(grossAmount = 56.0)

    val ArvOptInRequestResponse = ArvOptIn(true)

    val ArvOptInDoneResponse = ArvOptIn(false)

    val anticipation = ArvAnticipation(grossAmount = 24.0, negotiationType = CIELO, initialDate = "2024-01-01", finalDate = "2026-01-01")

    val nullAnticipation = ArvAnticipation()

    val listNegotiations =
        listOf(
            Item(
                discountAmount = 2.0,
                grossAmount = 12340.0,
                modality = "test",
                negotiationDate = "2022-03-24",
            ),
        )

    val anticipationHistory =
        ArvHistoricResponse(
            items = listNegotiations,
            pagination = Pagination(pageNumber = 1),
        )

    val nullAnticipationHistory =
        ArvHistoricResponse(
            items = listOf(),
            pagination = Pagination(),
        )

    val acquirer =
        Acquirer(
            cardBrands =
                listOf(
                    CardBrand(
                        code = 1,
                        discountAmount = 0.85,
                        grossAmount = 65.29,
                        name = "AMEX",
                        netAmount = 64.33,
                    ),
                ),
            discountAmount = 0.85,
            grossAmount = 65.29,
            name = "AMEX",
            netAmount = 64.33,
        )
    val arvSingleAnticipation =
        ArvAnticipation(
            acquirers = listOf(acquirer),
            discountAmount = 1.00,
            finalDate = "2024-03-28",
            grossAmount = 65.00,
            id = "ID",
            initialDate = "2024-03-28",
            negotiationType = "CIELO",
            netAmount = 0.13,
            nominalFee = 0.00,
            standardFee = 1.40,
            token = "abc",
            effectiveFee = 0.10,
        )

    val arvSingleAnticipationFromCardHomeFlow =
        ArvAnticipation(
            acquirers = listOf(acquirer),
            discountAmount = 1.00,
            finalDate = "2024-03-28",
            grossAmount = 65.00,
            id = "ID",
            initialDate = "2024-03-28",
            negotiationType = "CIELO",
            netAmount = 0.13,
            nominalFee = 0.00,
            standardFee = 1.40,
            token = "abc",
            effectiveFee = 0.10,
            isFromCardHomeFlow = true,
        )

    val arvScheduledAnticipation =
        ArvScheduledAnticipation(
            token = EMPTY,
            rateSchedules =
                listOf(
                    RateSchedules(
                        name = "CIELO",
                        schedule = true,
                        rate = 3.0,
                        cnpjRoot = false,
                        cnpjBranch = false,
                    ),
                ),
        )

    val arvScheduledAnticipationNotHired =
        ArvScheduledAnticipation(
            token = EMPTY,
            rateSchedules =
                listOf(
                    RateSchedules(
                        name = "CIELO",
                        schedule = false,
                        rate = 3.0,
                        cnpjRoot = false,
                        cnpjBranch = false,
                    ),
                    RateSchedules(
                        name = "MARKET",
                        schedule = false,
                        rate = 3.0,
                        cnpjRoot = false,
                        cnpjBranch = false,
                    ),
                ),
        )

    val arvScheduledAnticipationCieloHired =
        ArvScheduledAnticipation(
            token = EMPTY,
            rateSchedules =
                listOf(
                    RateSchedules(
                        name = "CIELO",
                        schedule = true,
                        rate = 3.0,
                        cnpjRoot = false,
                        cnpjBranch = false,
                    ),
                    RateSchedules(
                        name = "MARKET",
                        schedule = false,
                        rate = 3.0,
                        cnpjRoot = false,
                        cnpjBranch = false,
                    ),
                ),
        )

    val arvScheduledAnticipationMarketHired =
        ArvScheduledAnticipation(
            token = EMPTY,
            rateSchedules =
                listOf(
                    RateSchedules(
                        name = "CIELO",
                        schedule = false,
                        rate = 3.0,
                        cnpjRoot = false,
                        cnpjBranch = false,
                    ),
                    RateSchedules(
                        name = "MARKET",
                        schedule = true,
                        rate = 3.0,
                        cnpjRoot = false,
                        cnpjBranch = false,
                    ),
                ),
        )

    val resultError =
        CieloDataResult.APIError(
            CieloAPIException.networkError(EMPTY),
        )

    val resultAnticipationClosedMarket: CieloDataResult<ArvAnticipation> =
        CieloDataResult.APIError(
            CieloAPIException.networkError(
                message = EMPTY,
                newErrorMessage =
                    NewErrorMessage(
                        flagErrorCode = ArvConstants.CLOSED_MARKET,
                    ),
            ),
        )

    val resultAnticipationNotEligible: CieloDataResult<ArvAnticipation> =
        CieloDataResult.APIError(
            CieloAPIException.networkError(
                message = EMPTY,
                newErrorMessage =
                    NewErrorMessage(
                        flagErrorCode = ArvConstants.MERCHANT_NOT_ELIGIBLE,
                    ),
            ),
        )

    val resultAnticipationCorporateDesk: CieloDataResult<ArvAnticipation> =
        CieloDataResult.APIError(
            CieloAPIException.networkError(
                message = EMPTY,
                newErrorMessage =
                    NewErrorMessage(
                        flagErrorCode = ArvConstants.CORPORATE_DESK,
                    ),
            ),
        )

    val resultAnticipationInvalidReceivableAmount: CieloDataResult.APIError =
        CieloDataResult.APIError(
            CieloAPIException.networkError(
                message = EMPTY,
                newErrorMessage =
                    NewErrorMessage(
                        flagErrorCode = ArvConstants.INVALID_RECEIVABLE_AMOUNT,
                    ),
            ),
        )

    val resultMfaTokenError =
        CieloDataResult.APIError(
            CieloAPIException(
                actionErrorType = ActionErrorTypeEnum.MFA_TOKEN_ERROR_ACTION,
                newErrorMessage = NewErrorMessage(flagErrorCode = Text.OTP),
            ),
        )

    val resultNotEligibleError =
        CieloDataResult.APIError(
            CieloAPIException(
                actionErrorType = ActionErrorTypeEnum.ELIGIBLE_ERROR,
                newErrorMessage = NewErrorMessage(flagErrorCode = NOT_ELIGIBLE),
            ),
        )

    val confirmAnticipationResponse =
        ArvConfirmAnticipationResponse(
            negotiationDate = "2022-03-21",
            operationNumber = "1",
            negotiationType = "negotiationType",
            status = "status",
            grossAmount = 10.0,
            discountAmount = 1.0,
            netAmount = 9.0,
            negotiationFee = 1.0,
            modality = "modality",
        )

    val confirmAnticipationRequest =
        ArvConfirmAnticipationRequest(
            token = "123",
            code = "001",
            agency = "04132",
            account = "004132",
            accountDigit = "5",
        )

    val confirmScheduledAnticipationRequest =
        ArvConfirmScheduledAnticipationRequest(
            token = "123",
            rateSchedules =
                listOf(
                    RateSchedules(
                        name = "CIELO",
                        schedule = false,
                        rate = 3.0,
                        cnpjRoot = false,
                        cnpjBranch = false,
                    ),
                ),
            domicile =
                ArvBank(
                    code = "",
                    name = "",
                    agency = "",
                    agencyDigit = "",
                    account = "",
                    accountDigit = "",
                    accountType = "",
                    businessType = "",
                ),
        )

    val cancelScheduledAnticipationRequest =
        ArvScheduledAnticipationCancelRequest(
            CancelNegotiationType.CIELO,
        )

    val emptyArvBank =
        ArvBank(
            code = "",
            name = "",
            agency = "",
            agencyDigit = "",
            account = "",
            accountDigit = "",
            accountType = "",
            businessType = "",
        )

    val emptyBrand =
        CardBrand(
            code = 0,
            discountAmount = 0.0,
            grossAmount = 0.0,
            name = "",
            netAmount = 0.0,
            isSelected = true,
        )

    val VisaBrand =
        CardBrand(
            code = 1,
            discountAmount = 0.0,
            grossAmount = 0.0,
            name = "VISA",
            netAmount = 0.0,
            isSelected = true,
        )

    val MasterBrand =
        CardBrand(
            code = 2,
            discountAmount = 0.0,
            grossAmount = 0.0,
            name = "MASTERCARD",
            netAmount = 0.0,
            isSelected = true,
        )

    val AMEXBrand =
        CardBrand(
            code = 3,
            discountAmount = 0.0,
            grossAmount = 0.0,
            name = "AMEX",
            netAmount = 0.0,
            isSelected = true,
        )

    val arvBankList = List(10) { emptyArvBank }

    val arvAllSelectedBrandsList = listOf(VisaBrand, MasterBrand, AMEXBrand)
    val arvAMEXNotSelectedBrandsList = listOf(VisaBrand, MasterBrand, AMEXBrand.copy(isSelected = false))
    val arvOnlySelectedBrandList = arvAMEXNotSelectedBrandsList.filter { it.isSelected }

    val arvBranchesContracts =
        ArvBranchesContracts(
            total = 2,
            schedules =
                listOf(
                    Schedules(
                        cnpj = "09205562000146",
                        name = "MOCK RAZAO LTDA",
                        nominalRateCielo = 3.0,
                        contractDateCielo = "2023-08-29",
                        nominalRateMarket = 3.0,
                        contractDateMarket = "2023-08-29",
                    ),
                ),
        )

    val scheduledAnticipationBankSelectFragmentArgsNotHired =
        ArvScheduledAnticipationBankSelectFragmentArgs.Builder(arvScheduledAnticipationNotHired).build()

    val scheduledAnticipationBankSelectFragmentArgsCieloHired =
        ArvScheduledAnticipationBankSelectFragmentArgs.Builder(arvScheduledAnticipationCieloHired).build()

    val scheduledAnticipationBankSelectFragmentArgsMarketHired =
        ArvScheduledAnticipationBankSelectFragmentArgs.Builder(arvScheduledAnticipationMarketHired).build()

    val arvWhatsAppContactDataJson =
        """
        {
            "phoneNumber": "551199999999",
            "message": "message"
        }
        """.trimIndent()

    val arvWhatsAppContactData =
        ArvWhatsAppContactData(
            phoneNumber = "551199999999",
            message = "message",
        )
}
