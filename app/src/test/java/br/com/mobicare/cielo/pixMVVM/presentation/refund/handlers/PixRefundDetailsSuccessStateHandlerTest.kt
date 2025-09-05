package br.com.mobicare.cielo.pixMVVM.presentation.refund.handlers

import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetail
import br.com.mobicare.cielo.pixMVVM.presentation.refund.utils.PixRefundDetailUiState
import org.junit.Assert.assertEquals
import org.junit.Test

class PixRefundDetailsSuccessStateHandlerTest {

    private val stateHandler = PixRefundDetailSuccessStateHandler()

    @Test
    fun `it should return PixRefundDetailUiState_StatusExecuted`() {
        // given
        val data = PixRefundDetail(transactionStatus = PixTransactionStatus.EXECUTED)

        // when
        val result = stateHandler(data)

        // then
        assertEquals(PixRefundDetailUiState.StatusExecuted, result)
    }

    private fun runStatusPendingAssertion(transactionStatus: PixTransactionStatus) {
        // given
        val data = PixRefundDetail(transactionStatus = transactionStatus)

        // when
        val result = stateHandler(data)

        // then
        assertEquals(PixRefundDetailUiState.StatusPending, result)
    }

    @Test
    fun `it should return PixRefundDetailUiState_StatusPending`() {
        runStatusPendingAssertion(PixTransactionStatus.PENDING)
        runStatusPendingAssertion(PixTransactionStatus.PROCESSING)
    }

    private fun runStatusNotExecutedAssertion(transactionStatus: PixTransactionStatus) {
        // given
        val data = PixRefundDetail(transactionStatus = transactionStatus)

        // when
        val result = stateHandler(data)

        // then
        assertEquals(PixRefundDetailUiState.StatusNotExecuted, result)
    }

    @Test
    fun `it should return PixRefundDetailUiState_StatusNotExecuted`() {
        runStatusNotExecutedAssertion(PixTransactionStatus.NOT_EXECUTED)
        runStatusNotExecutedAssertion(PixTransactionStatus.CANCELLED)
        runStatusNotExecutedAssertion(PixTransactionStatus.FAILED)
    }

}