package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.LONG_TIME_WITH_MILLIS_NO_UTC
import br.com.mobicare.cielo.commons.utils.parseToZonedDateTime
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixRefundReceiptsResponse
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundReceipts

fun PixRefundReceiptsResponse.toEntity() = PixRefundReceipts(
    totalAmountPossibleReversal = totalAmountPossibleReversal,
    totalItemsPage = totalItemsPage,
    receipts = items.firstOrNull()?.receipts?.map { it.toEntity() }
)

fun PixRefundReceiptsResponse.ReceiptItem.toEntity() = PixRefundReceipts.ReceiptItem(
    idAccount = idAccount,
    idEndToEnd = idEndToEnd,
    idEndToEndOriginal = idEndToEndOriginal,
    transactionDate = transactionDate?.parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
    transactionType = PixTransactionType.find(transactionType),
    transactionStatus = PixTransactionStatus.find(transactionStatus),
    reversalCode = reversalCode,
    reversalReason = reversalReason,
    tariffAmount = tariffAmount,
    amount = amount,
    finalAmount = finalAmount,
    idAdjustment = idAdjustment,
    transactionCode = transactionCode
)