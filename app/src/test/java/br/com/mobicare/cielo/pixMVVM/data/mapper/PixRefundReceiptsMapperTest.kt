package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.LONG_TIME_WITH_MILLIS_NO_UTC
import br.com.mobicare.cielo.commons.utils.parseToZonedDateTime
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundReceipts
import br.com.mobicare.cielo.pixMVVM.utils.PixRefundsFactory
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PixRefundReceiptsMapperTest {

    private val response = PixRefundsFactory.RefundReceipts.response

    private val expectedResult = PixRefundReceipts(
        totalAmountPossibleReversal = 10.0,
        totalItemsPage = 1,
        receipts = listOf(
            PixRefundReceipts.ReceiptItem(
                idAccount = 0,
                idEndToEnd = "string",
                idEndToEndOriginal = "string",
                transactionDate = "2024-01-23T21:08:41.644".parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
                transactionType = PixTransactionType.TRANSFER_DEBIT,
                transactionStatus = PixTransactionStatus.EXECUTED,
                reversalCode = 0,
                reversalReason = "string",
                tariffAmount = 0.0,
                amount = 5.0,
                finalAmount = 5.0,
                idAdjustment = 0,
                transactionCode = "string"
            )
        )
    )

    @Test
    fun `it should map response to entity correctly`() {
        val result = response.toEntity()

        assertThat(result).isEqualTo(expectedResult)
    }

}