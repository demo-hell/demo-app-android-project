package br.com.mobicare.cielo.pixMVVM.presentation.refund.handlers

import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundReceipts
import br.com.mobicare.cielo.pixMVVM.presentation.refund.utils.PixRefundReceiptsUiState
import org.junit.Assert.assertEquals
import org.junit.Test

class PixRefundReceiptsSuccessStateHandlerTest {

    private val refundReceipts = PixRefundReceipts(
        totalAmountPossibleReversal = 10.0,
        totalItemsPage = 1,
        receipts = listOf(
            PixRefundReceipts.ReceiptItem(
                amount = 5.0,
                finalAmount = 5.0
            )
        )
    )

    private val stateHandler = PixRefundReceiptsSuccessStateHandler()

    @Test
    fun `it should return PixRefundReceiptsUiState_FullyRefunded`() {
        val result = stateHandler(
            refundReceipts = refundReceipts.copy(totalAmountPossibleReversal = 0.0),
            isExpiredReversal = false
        )

        assertEquals(PixRefundReceiptsUiState.FullyRefunded, result)
    }

    @Test
    fun `it should return PixRefundReceiptsUiState_PartiallyRefunded`() {
        val result = stateHandler(
            refundReceipts = refundReceipts,
            isExpiredReversal = false
        )

        assertEquals(PixRefundReceiptsUiState.PartiallyRefunded, result)
    }

    @Test
    fun `it should return PixRefundReceiptsUiState_PartiallyRefundedButExpired`() {
        val result = stateHandler(
            refundReceipts = refundReceipts,
            isExpiredReversal = true
        )

        assertEquals(PixRefundReceiptsUiState.PartiallyRefundedButExpired, result)
    }

    @Test
    fun `it should return PixRefundReceiptsUiState_NotRefunded`() {
        val result = stateHandler(
            refundReceipts = refundReceipts.copy(
                totalAmountPossibleReversal = 15.0,
                totalItemsPage = 0,
                receipts = emptyList()
            ),
            isExpiredReversal = false
        )

        assertEquals(PixRefundReceiptsUiState.NotRefunded, result)
    }

    @Test
    fun `it should return PixRefundReceiptsUiState_NotRefundedButExpired`() {
        val result = stateHandler(
            refundReceipts = refundReceipts.copy(
                totalAmountPossibleReversal = 15.0,
                totalItemsPage = 0,
                receipts = emptyList()
            ),
            isExpiredReversal = true
        )

        assertEquals(PixRefundReceiptsUiState.NotRefundedButExpired, result)
    }

}