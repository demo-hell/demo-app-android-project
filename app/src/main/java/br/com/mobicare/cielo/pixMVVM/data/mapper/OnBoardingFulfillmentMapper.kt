package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.pixMVVM.data.model.response.OnBoardingFulfillmentResponse
import br.com.mobicare.cielo.pixMVVM.domain.enums.BlockType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixDocumentType
import br.com.mobicare.cielo.pixMVVM.domain.enums.ProfileType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixStatus
import br.com.mobicare.cielo.pixMVVM.domain.model.OnBoardingFulfillment

fun OnBoardingFulfillmentResponse.toEntity() = OnBoardingFulfillment(
    isEligible = eligible,
    profileType = ProfileType.find(profileType),
    isSettlementActive = settlementActive,
    isEnabled = enabled,
    status = PixStatus.find(status),
    blockType = BlockType.find(blockType),
    pixAccount = pixAccount?.toEntity(),
    settlementScheduled = settlementScheduled?.toEntity(),
    document = document,
    documentType = PixDocumentType.find(documentType)
)

fun OnBoardingFulfillmentResponse.PixAccount.toEntity() = OnBoardingFulfillment.PixAccount(
    pixId = pixId,
    bank = bank,
    agency = agency,
    account = account,
    accountDigit = accountDigit,
    dockAccountId = dockAccountId,
    isCielo = isCielo,
    bankName = bankName
)

fun OnBoardingFulfillmentResponse.SettlementScheduled.toEntity() = OnBoardingFulfillment.SettlementScheduled(
    isEnabled = enabled,
    list = list
)