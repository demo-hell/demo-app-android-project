package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.LONG_TIME_NO_UTC
import br.com.mobicare.cielo.commons.utils.parseToZonedDateTime
import br.com.mobicare.cielo.extensions.clearDate
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixExtractResponse
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixExtractReceiptType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixExtract

fun PixExtractResponse.toEntity(): PixExtract =
    PixExtract(
        items =
            items?.map { item ->
                PixExtract.PixExtractItem(
                    receipts =
                        item.receipts?.map {
                            PixExtract.PixExtractReceipt(
                                title = it.title,
                                amount = it.amount,
                                changeAmount = it.changeAmount,
                                date = it.date?.clearDate()?.parseToZonedDateTime(LONG_TIME_NO_UTC),
                                finalAmount = it.finalAmount,
                                idAccount = it.idAccount,
                                idAdjustment = it.idAdjustment,
                                idCorrelation = it.idCorrelation,
                                idEndToEnd = it.idEndToEnd,
                                idEndToEndOriginal = it.idEndToEndOriginal,
                                payeeName = it.payeeName,
                                payerAnswer = it.payerAnswer,
                                payerName = it.payerName,
                                pixType = it.pixType,
                                purchaseAmount = it.purchaseAmount,
                                reversalCode = it.reversalCode,
                                reversalCodeDescription = it.reversalCodeDescription,
                                tariffAmount = it.tariffAmount,
                                transactionCode = it.transactionCode,
                                transactionDate = it.transactionDate?.clearDate()?.parseToZonedDateTime(LONG_TIME_NO_UTC),
                                transactionStatus = it.transactionStatus,
                                transactionType = it.transactionType,
                                transferType = it.transferType,
                                schedulingDate = it.schedulingDate?.clearDate()?.parseToZonedDateTime(LONG_TIME_NO_UTC),
                                schedulingCode = it.schedulingCode,
                                period = it.period,
                                type = PixExtractReceiptType.parsePixExtractReceiptType(it.type),
                            )
                        } ?: emptyList(),
                    title = item.title,
                    yearMonth = item.yearMonth,
                )
            } ?: emptyList(),
        totalItemsPage = this.totalItemsPage,
    )
