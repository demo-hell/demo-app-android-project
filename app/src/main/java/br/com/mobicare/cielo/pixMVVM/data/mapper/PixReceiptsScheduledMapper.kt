package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.SIMPLE_DATE_INTERNATIONAL
import br.com.mobicare.cielo.commons.utils.parseToLocalDate
import br.com.mobicare.cielo.commons.utils.toCalendar
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixReceiptsScheduledResponse
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixExtractReceiptType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixReceiptsScheduled

fun PixReceiptsScheduledResponse.toEntity() =
    PixReceiptsScheduled(
        last = last,
        totalItemsPage = totalItemsPage,
        lastSchedulingIdentifierCode = lastSchedulingIdentifierCode.orEmpty(),
        lastNextDateTimeScheduled = lastNextDateTimeScheduled?.toCalendar(SIMPLE_DATE_INTERNATIONAL),
        items =
            items
                ?.map { item ->
                    PixReceiptsScheduled.Item(
                        title = item.title.orEmpty(),
                        yearMonth = item.yearMonth.orEmpty(),
                        receipts =
                            item.receipts
                                ?.map { receipt ->
                                    PixReceiptsScheduled.Item.Receipt(
                                        transactionStatus = receipt.transactionStatus.orEmpty(),
                                        finalAmount = receipt.finalAmount,
                                        payeeName = receipt.payeeName.orEmpty(),
                                        payeeDocumentNumber = receipt.payeeDocumentNumber.orEmpty(),
                                        payeeBankName = receipt.payeeBankName.orEmpty(),
                                        schedulingCode = receipt.schedulingCode.orEmpty(),
                                        schedulingDate = receipt.scheduledDate?.parseToLocalDate(),
                                        type = PixExtractReceiptType.parsePixExtractReceiptType(receipt.type),
                                    )
                                },
                    )
                },
    )
