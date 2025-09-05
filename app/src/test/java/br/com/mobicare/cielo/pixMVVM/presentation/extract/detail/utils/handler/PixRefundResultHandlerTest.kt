package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler

import br.com.mobicare.cielo.pixMVVM.domain.enums.PixExtractType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixRefundUiResult
import br.com.mobicare.cielo.pixMVVM.utils.PixRefundsFactory
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PixRefundResultHandlerTest {

    private val entity = PixRefundsFactory.RefundDetail.entity

    private val handler = PixRefundResultHandler()

    @Test
    fun `it should return PixRefundUiResult_RefundReceived result`() {
        val result = handler.invoke(
            entity.copy(
                transactionType = PixExtractType.REVERSAL_CREDIT,
            )
        )

        assertThat(result).isInstanceOf(PixRefundUiResult.RefundReceived::class.java)
    }

    @Test
    fun `it should return PixRefundUiResult_RefundSentCompleted result`() {
        val result = handler.invoke(
            entity.copy(
                transactionType = PixExtractType.REVERSAL_DEBIT,
                transactionStatus = PixTransactionStatus.EXECUTED
            )
        )

        assertThat(result).isInstanceOf(PixRefundUiResult.RefundSentCompleted::class.java)
    }

    @Test
    fun `it should return PixRefundUiResult_RefundSentPending result`() {
        val result = handler.invoke(
            entity.copy(
                transactionType = PixExtractType.REVERSAL_DEBIT,
                transactionStatus = PixTransactionStatus.PENDING
            )
        )

        assertThat(result).isInstanceOf(PixRefundUiResult.RefundSentPending::class.java)
    }

    @Test
    fun `it should return PixRefundUiResult_RefundSentFailed result`() {
        val result = handler.invoke(
            entity.copy(
                transactionType = PixExtractType.REVERSAL_DEBIT,
                transactionStatus = PixTransactionStatus.FAILED
            )
        )

        assertThat(result).isInstanceOf(PixRefundUiResult.RefundSentFailed::class.java)
    }


}