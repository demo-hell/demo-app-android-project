package br.com.mobicare.cielo.pixMVVM.presentation.account.rules

import br.com.mobicare.cielo.pixMVVM.presentation.account.enums.PixReceiptMethod
import br.com.mobicare.cielo.pixMVVM.presentation.account.ui.receiptMethod.rules.ShouldEmptyBalanceRule
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

class ShouldEmptyBalanceRuleTest {

    private val balanceOf100 = 100.0
    private val cieloAccountReceiptMethod = PixReceiptMethod.CIELO_ACCOUNT

    @Test
    fun `it should return true when activeReceiptMethod is CIELO_ACCOUNT and has balance`() {
        val rule = ShouldEmptyBalanceRule(balanceOf100)

        val result = rule(cieloAccountReceiptMethod)

        assertTrue(result)
    }

    @Test
    fun `it should return false when activeReceiptMethod is not CIELO_ACCOUNT`() {
        val rule = ShouldEmptyBalanceRule(balanceOf100)

        val resultForTransferBySale = rule(PixReceiptMethod.TRANSFER_BY_SALE)
        val resultForScheduledTransfer = rule(PixReceiptMethod.SCHEDULED_TRANSFER)

        assertFalse(resultForTransferBySale)
        assertFalse(resultForScheduledTransfer)
    }

    @Test
    fun `it should return false when balance is zero`() {
        val rule = ShouldEmptyBalanceRule(0.0)

        val result = rule(cieloAccountReceiptMethod)

        assertFalse(result)
    }

    @Test
    fun `it should return false when balance is null`() {
        val rule = ShouldEmptyBalanceRule(null)

        val result = rule(cieloAccountReceiptMethod)

        assertFalse(result)
    }

}