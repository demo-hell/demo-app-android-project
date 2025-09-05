package br.com.mobicare.cielo.pix.ui.extract.details.views.helpers

import br.com.mobicare.cielo.pix.enums.PixQRCodeOperationTypeEnum
import br.com.mobicare.cielo.pix.enums.PixTransferTypeEnum
import br.com.mobicare.cielo.pix.ui.extract.detail.views.helpers.CreditAmountDataHelper
import br.com.mobicare.cielo.pix.ui.extract.details.utils.PixExtractDetailsFactory
import org.junit.Assert.*
import org.junit.Test

class CreditAmountDataHelperTest {

    private val response = PixExtractDetailsFactory.transferDetailsResponse

    @Test
    fun `it should be a completely executed settlement`() {
        val result = CreditAmountDataHelper(response)

        assertTrue(result.isSettlementCompletelyExecuted)
        assertFalse(result.isSettlementPartiallyExecuted)
    }

    @Test
    fun `it should be a partially executed settlement`() {
        val settlementPartiallyExecutedResponse = response.copy(
            settlement = response.settlement?.copy(
                settlementIdEndToEnd = null
            )
        )

        val result = CreditAmountDataHelper(settlementPartiallyExecutedResponse)

        assertTrue(result.isSettlementPartiallyExecuted)
        assertFalse(result.isSettlementCompletelyExecuted)
    }

    private fun runSettlementProcessingAssertionTest(status: String?, expected: Boolean) {
        val settlementProcessingResponse = response.copy(
            settlement = response.settlement?.copy(
                settlementTransactionStatus = status
            )
        )

        val result = CreditAmountDataHelper(settlementProcessingResponse)

        assertEquals(expected, result.isSettlementProcessing)
    }

    @Test
    fun `it should be a processing settlement`() {
        runSettlementProcessingAssertionTest("PROCESSING", true)
        runSettlementProcessingAssertionTest("PENDING", true)
        runSettlementProcessingAssertionTest("NOT_EXECUTED", true)
        runSettlementProcessingAssertionTest("SENT_WITH_ERROR", true)

        runSettlementProcessingAssertionTest("EXECUTED", false)
        runSettlementProcessingAssertionTest("SCHEDULED", false)
        runSettlementProcessingAssertionTest("SCHEDULED_EXECUTED", false)
        runSettlementProcessingAssertionTest("CANCELLED", false)
        runSettlementProcessingAssertionTest("FAILED", false)
        runSettlementProcessingAssertionTest("REVERSAL_EXECUTED", false)
        runSettlementProcessingAssertionTest(null, false)
    }

    private fun runFeeProcessingAssertionTest(status: String?, expected: Boolean) {
        val feeProcessingResponse = response.copy(
            fee = response.fee?.copy(
                feeTransactionStatus = status
            )
        )

        val result = CreditAmountDataHelper(feeProcessingResponse)

        assertEquals(expected, result.isFeePendingOrProcessing)
    }

    @Test
    fun `it should be a processing fee`() {
        runFeeProcessingAssertionTest("PROCESSING", true)
        runFeeProcessingAssertionTest("PENDING", true)

        runFeeProcessingAssertionTest("NOT_EXECUTED", false)
        runFeeProcessingAssertionTest("SENT_WITH_ERROR", false)
        runFeeProcessingAssertionTest("EXECUTED", false)
        runFeeProcessingAssertionTest("SCHEDULED", false)
        runFeeProcessingAssertionTest("SCHEDULED_EXECUTED", false)
        runFeeProcessingAssertionTest("CANCELLED", false)
        runFeeProcessingAssertionTest("FAILED", false)
        runFeeProcessingAssertionTest("REVERSAL_EXECUTED", false)
        runFeeProcessingAssertionTest(null, false)
    }

    private fun runTransferOperationTypeAssertionTest(code: Int?, expected: Boolean) {
        val transferOperationTypeResponse = response.copy(
            pixType = PixQRCodeOperationTypeEnum.TRANSFER.name,
            transferType = code
        )

        val result = CreditAmountDataHelper(transferOperationTypeResponse)

        assertEquals(expected, result.isTransferOperationType)
    }

    @Test
    fun `it should be a transfer operation type`() {
        runTransferOperationTypeAssertionTest(PixTransferTypeEnum.MANUAL.code, true)
        runTransferOperationTypeAssertionTest(PixTransferTypeEnum.CHAVE.code, true)
        runTransferOperationTypeAssertionTest(PixTransferTypeEnum.QR_CODE_ESTATICO.code, true)
        runTransferOperationTypeAssertionTest(PixTransferTypeEnum.QR_CODE_DINAMICO.code, true)

        runTransferOperationTypeAssertionTest(PixTransferTypeEnum.NONE.code, false)
        runTransferOperationTypeAssertionTest(PixTransferTypeEnum.SERVICO_DE_INICIACAO_DE_TRANSACAO_DE_PAGAMENTO.code, false)
        runTransferOperationTypeAssertionTest(null, false)
    }

    private fun runChangeOperationTypeAssertionTest(code: Int?, expected: Boolean) {
        val changeOperationTypeResponse = response.copy(
            pixType = PixQRCodeOperationTypeEnum.CHANGE.name,
            transferType = code
        )

        val result = CreditAmountDataHelper(changeOperationTypeResponse)

        assertEquals(expected, result.isChangeOperationType)
    }

    @Test
    fun `it should be a change operation type`() {
        runChangeOperationTypeAssertionTest(PixTransferTypeEnum.QR_CODE_ESTATICO.code, true)
        runChangeOperationTypeAssertionTest(PixTransferTypeEnum.QR_CODE_DINAMICO.code, true)

        runChangeOperationTypeAssertionTest(PixTransferTypeEnum.MANUAL.code, false)
        runChangeOperationTypeAssertionTest(PixTransferTypeEnum.CHAVE.code, false)
        runChangeOperationTypeAssertionTest(PixTransferTypeEnum.NONE.code, false)
        runChangeOperationTypeAssertionTest(PixTransferTypeEnum.SERVICO_DE_INICIACAO_DE_TRANSACAO_DE_PAGAMENTO.code, false)
    }

    private fun runNetAmountClickableAssertionTest(
        settlementIdEndToEnd: String?,
        settlementTransactionCode: String?,
        expected: Boolean
    ) {
        val settlementNetAmountClickableResponse = response.copy(
            settlement = response.settlement?.copy(
                settlementIdEndToEnd = settlementIdEndToEnd,
                settlementTransactionCode = settlementTransactionCode,
            )
        )

        val result = CreditAmountDataHelper(settlementNetAmountClickableResponse)

        assertEquals(expected, result.isNetAmountClickable)
    }

    @Test
    fun `it should check if net amount is clickable`() {
        runNetAmountClickableAssertionTest(
            settlementIdEndToEnd = "SOME_ID",
            settlementTransactionCode = "SOME_CODE",
            expected = true
        )
        runNetAmountClickableAssertionTest(
            settlementIdEndToEnd = null,
            settlementTransactionCode = "SOME_CODE",
            expected = false
        )
        runNetAmountClickableAssertionTest(
            settlementIdEndToEnd = "SOME_ID",
            settlementTransactionCode = null,
            expected = false
        )
        runNetAmountClickableAssertionTest(
            settlementIdEndToEnd = null,
            settlementTransactionCode = null,
            expected = false
        )
    }

    private fun runFeeClickableAssertionTest(
        feeIdEndToEnd: String?,
        feeTransactionCode: String?,
        expected: Boolean
    ) {
        val feeClickableResponse = response.copy(
            fee = response.fee?.copy(
                feeIdEndToEnd = feeIdEndToEnd,
                feeTransactionCode = feeTransactionCode,
            )
        )

        val result = CreditAmountDataHelper(feeClickableResponse)

        assertEquals(expected, result.isFeeClickable)
    }

    @Test
    fun `it should check if fee is clickable`() {
        runFeeClickableAssertionTest(
            feeIdEndToEnd = "SOME_ID",
            feeTransactionCode = "SOME_CODE",
            expected = true
        )
        runFeeClickableAssertionTest(
            feeIdEndToEnd = null,
            feeTransactionCode = "SOME_CODE",
            expected = false
        )
        runFeeClickableAssertionTest(
            feeIdEndToEnd = "SOME_ID",
            feeTransactionCode = null,
            expected = false
        )
        runFeeClickableAssertionTest(
            feeIdEndToEnd = null,
            feeTransactionCode = null,
            expected = false
        )
    }

}