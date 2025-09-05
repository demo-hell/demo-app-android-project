package br.com.mobicare.cielo.suporteTecnico.presentation.viewModel.utils

import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.FIVE
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.TWENTY
import br.com.mobicare.cielo.accessManager.OTP
import br.com.mobicare.cielo.commons.constants.ERROR
import br.com.mobicare.cielo.commons.constants.HTTP_422_UNPROCESSABLE_ENTITY
import br.com.mobicare.cielo.commons.constants.HTTP_422_UNPROCESSABLE_ENTITY_MESSAGE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.recebaMais.domain.Block
import br.com.mobicare.cielo.suporteTecnico.data.UserOwnerSupportResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosMachine
import br.com.mobicare.cielo.taxaPlanos.domain.TerminalsResponse

object SuporteTecnicoFactory {
    val userOwnerDocumentAuthorized =
        UserOwnerSupportResponse(
            companyName = EMPTY,
            contacts = listOf(),
            addresses = listOf(),
            blocks =
                listOf(
                    Block(
                        codeType = ONE,
                        nameType = "nameType",
                        codeReason = ONE,
                        descriptionReason = "descriptionReason",
                        dateBeginBlocked = "dateBeginBlocked",
                        nameRequestorBlocked = "nameRequestorBlocked",
                    ),
                ),
        )

    val userOwnerDocumentNotAuthorized =
        UserOwnerSupportResponse(
            companyName = EMPTY,
            contacts = listOf(),
            addresses = listOf(),
            blocks =
                listOf(
                    Block(
                        codeType = FIVE,
                        nameType = "nameType",
                        codeReason = TWENTY,
                        descriptionReason = "descriptionReason",
                        dateBeginBlocked = "dateBeginBlocked",
                        nameRequestorBlocked = "nameRequestorBlocked",
                    ),
                ),
        )

    val onlyMachinesDigitalsResponse =
        TerminalsResponse(
            rentalEquipments = true,
            terminals =
                listOf(
                    TaxaPlanosMachine(
                        model = "ECOMMERCE",
                        logicalNumber = "10157105",
                        logicalNumberDigit = "7",
                        description = "COMERCIO ELETRONICO",
                        commercialDescription = "E-COMMERCE",
                        technology = "ECOMMERCE",
                        replacementAllowed = false,
                    ),
                    TaxaPlanosMachine(
                        model = "TEF DEDICADO",
                        logicalNumber = "41021461",
                        logicalNumberDigit = "3",
                        description = "POS VIRTUAL",
                        commercialDescription = EMPTY,
                        technology = "TEF",
                        replacementAllowed = false,
                    ),
                ),
        )

    val machinesDigitalsAndPhysicalsResponse =
        TerminalsResponse(
            rentalEquipments = true,
            terminals =
                listOf(
                    TaxaPlanosMachine(
                        model = "VX 510 6MB PCI",
                        logicalNumber = "00037853",
                        logicalNumberDigit = "9",
                        description = "VX 510 POSWEB M06 PCI",
                        commercialDescription = "VX510",
                        technology = "DIAL",
                        replacementAllowed = true,
                    ),
                    TaxaPlanosMachine(
                        model = "LIO",
                        logicalNumber = "00037893",
                        logicalNumberDigit = "5",
                        description = "TERMINAL CIELO LIO - PLUS",
                        commercialDescription = "LIO V1",
                        technology = "LIO",
                        replacementAllowed = false,
                    ),
                    TaxaPlanosMachine(
                        model = "ECOMMERCE",
                        logicalNumber = "10157105",
                        logicalNumberDigit = "7",
                        description = "COMERCIO ELETRONICO",
                        commercialDescription = "E-COMMERCE",
                        technology = "ECOMMERCE",
                        replacementAllowed = false,
                    ),
                    TaxaPlanosMachine(
                        model = "TEF DEDICADO",
                        logicalNumber = "41021461",
                        logicalNumberDigit = "3",
                        description = "POS VIRTUAL",
                        commercialDescription = EMPTY,
                        technology = "TEF",
                        replacementAllowed = false,
                    ),
                ),
        )

    val resultErrorCode422 =
        CieloDataResult.APIError(
            CieloAPIException(
                actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
                newErrorMessage =
                    NewErrorMessage(
                        title = ERROR,
                        httpCode = HTTP_422_UNPROCESSABLE_ENTITY,
                        message = HTTP_422_UNPROCESSABLE_ENTITY_MESSAGE,
                        brokenServiceUrl = EMPTY,
                        flagErrorCode = OTP,
                        actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
                        mfaErrorCode = EMPTY,
                        listErrorServer = listOf(),
                    ),
            ),
        )
}
