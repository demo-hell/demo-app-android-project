package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.LONG_TIME_WITH_MILLIS_NO_UTC
import br.com.mobicare.cielo.commons.utils.parseToLocalDateTimeOrNull
import br.com.mobicare.cielo.commons.utils.parseToZonedDateTime
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixTransferDetailResponse
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixFeeType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQrCodeOperationType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransferOrigin
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransferType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail

fun PixTransferDetailResponse.toEntity() = PixTransferDetail(
    agentMode = agentMode,
    agentWithdrawalIspb = agentWithdrawalIspb,
    amount = amount,
    changeAmount = changeAmount,
    creditParty = creditParty?.toEntity(),
    debitParty = debitParty?.toEntity(),
    errorCode = errorCode,
    errorMessage = errorMessage,
    errorType = errorType,
    finalAmount = finalAmount,
    idAccount = idAccount,
    idAccountType = idAccountType,
    idAdjustment = idAdjustment,
    idEndToEnd = idEndToEnd,
    merchantNumber = merchantNumber,
    originChannel = originChannel,
    payerAnswer = payerAnswer,
    pixType = PixQrCodeOperationType.find(pixType),
    purchaseAmount = purchaseAmount,
    tariffAmount = tariffAmount,
    transactionCode = transactionCode,
    transactionCodeOriginal = transactionCodeOriginal,
    transactionDate = transactionDate?.parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
    transactionStatus = PixTransactionStatus.find(transactionStatus),
    transactionType = PixTransactionType.find(transactionType),
    transferType = PixTransferType.find(transferType),
    transactionReversalDeadline = transactionReversalDeadline?.parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
    expiredReversal = expiredReversal,
    idTx = idTx,
    transferOrigin = PixTransferOrigin.find(transferOrigin),
    credit = credit?.toEntity(),
    fee = fee?.toEntity(),
    settlement = settlement?.toEntity(),
    type = PixType.find(type),
    enable = enable?.toEntity()
)

fun PixTransferDetailResponse.TransferParty.toEntity() =
    PixTransferDetail.TransferParty(
        bankAccountNumber = bankAccountNumber,
        bankAccountType = bankAccountType,
        bankBranchNumber = bankBranchNumber,
        bankName = bankName,
        ispb = ispb,
        key = key,
        name = name,
        nationalRegistration = nationalRegistration,
    )

fun PixTransferDetailResponse.Credit.toEntity() =
    PixTransferDetail.Credit(
        originChannel = originChannel,
        creditTransactionDate = creditTransactionDate.parseToLocalDateTimeOrNull(LONG_TIME_WITH_MILLIS_NO_UTC),
        creditAmount = creditAmount,
        creditFinalAmount = creditFinalAmount,
        creditIdEndToEnd = creditIdEndToEnd,
        creditTransactionCode = creditTransactionCode,
    )

fun PixTransferDetailResponse.Fee.toEntity() =
    PixTransferDetail.Fee(
        feeIdEndToEnd = feeIdEndToEnd,
        feeTax = feeTax,
        feePaymentDate = feePaymentDate?.parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
        feeTransactionStatus = PixTransactionStatus.find(feeTransactionStatus),
        feeTransactionCode = feeTransactionCode,
        feeType = PixFeeType.find(feeType),
    )

fun PixTransferDetailResponse.Settlement.toEntity() =
    PixTransferDetail.Settlement(
        settlementIdEndToEnd = settlementIdEndToEnd,
        settlementDate = settlementDate?.parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
        settlementTransactionStatus = PixTransactionStatus.find(settlementTransactionStatus),
        settlementTransactionCode = settlementTransactionCode,
        settlementFinalAmount = settlementFinalAmount,
    )
