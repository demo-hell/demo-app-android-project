package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler

import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixRefundReceiptsUiResult
import br.com.mobicare.cielo.pixMVVM.utils.PixRefundsFactory
import br.com.mobicare.cielo.pixMVVM.utils.PixTransactionsFactory
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PixRefundReceiptsResultHandlerTest {

    private val refundReceiptsEntity = PixRefundsFactory.RefundReceipts.entity
    private val transferDetailEntity = PixTransactionsFactory.TransferDetail.entity

    private val handler = PixRefundReceiptsResultHandler()

    @Test
    fun `it should return PixRefundReceiptsUiResult_CanBeRefunded result`() {
        val result = handler.invoke(
            refundReceiptsEntity.copy(totalAmountPossibleReversal = 10.0),
            transferDetailEntity.copy(expiredReversal = false)
        )

        assertThat(result).isInstanceOf(PixRefundReceiptsUiResult.CanBeRefunded::class.java)
    }

    @Test
    fun `it should return PixRefundReceiptsUiResult_CannotBeRefunded result when reversal is expired`() {
        val result = handler.invoke(
            refundReceiptsEntity.copy(totalAmountPossibleReversal = 10.0),
            transferDetailEntity.copy(expiredReversal = true)
        )

        assertThat(result).isInstanceOf(PixRefundReceiptsUiResult.CannotBeRefunded::class.java)
    }

    @Test
    fun `it should return PixRefundReceiptsUiResult_CannotBeRefunded result when no amount to refund is available`() {
        val result = handler.invoke(
            refundReceiptsEntity.copy(totalAmountPossibleReversal = 0.0),
            transferDetailEntity.copy(expiredReversal = false)
        )

        assertThat(result).isInstanceOf(PixRefundReceiptsUiResult.CannotBeRefunded::class.java)
    }

}