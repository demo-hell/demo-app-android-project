package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.LONG_TIME_WITH_MILLIS_NO_UTC
import br.com.mobicare.cielo.commons.utils.parseToZonedDateTime
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixRefundDetailResponse
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixExtractType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetail

fun PixRefundDetailResponse.toEntity() =
    PixRefundDetail(
        idAccount = idAccount,
        idEndToEndOriginal = idEndToEndOriginal,
        idEndToEndReturn = idEndToEndReturn,
        transactionDate = transactionDate?.parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
        transactionType = PixExtractType.find(transactionType),
        errorType = errorType,
        transactionStatus = PixTransactionStatus.find(transactionStatus),
        creditParty = creditParty?.toEntity(),
        debitParty = debitParty?.toEntity(),
        amount = amount,
        tariffAmount = tariffAmount,
        finalAmount = finalAmount,
        reversalCode = reversalCode,
        reversalReason = reversalReason,
        idAdjustment = idAdjustment,
        transactionCode = transactionCode,
        transactionCodeOriginal = transactionCodeOriginal,
        payerAnswer = payerAnswer,
        enable = enable?.toEntity(),
    )

fun PixRefundDetailResponse.RefundParty.toEntity() =
    PixRefundDetail.RefundParty(
        ispb = ispb,
        bankName = bankName,
        nationalRegistration = nationalRegistration,
        name = name,
        bankBranchNumber = bankBranchNumber,
        bankAccountNumber = bankAccountNumber,
        bankAccountType = bankAccountType,
    )
