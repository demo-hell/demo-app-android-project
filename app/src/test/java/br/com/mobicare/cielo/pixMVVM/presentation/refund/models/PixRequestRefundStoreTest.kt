package br.com.mobicare.cielo.pixMVVM.presentation.refund.models

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PixRequestRefundStoreTest {

    private val store = PixRequestRefundStore(amount = 100.0)
    private val currentBalance = 500.0
    private val availableAmountToRefund = 120.0

    @Test
    fun `validateAmount should return true`() {
        assertTrue(store.validateAmount(availableAmountToRefund))
        assertTrue(store.validateAmount(availableAmountToRefund, currentBalance))
    }

    @Test
    fun `validateAmount should return false when amount is zero`() {
        val storeWithNoAmount = store.copy(amount = 0.0)

        assertFalse(storeWithNoAmount.validateAmount(availableAmountToRefund))
    }

    @Test
    fun `validateAmount should return false when amount exceeds availableAmountToRefund`() {
        val storeWithExceededAmountToRefund = store.copy(amount = 120.01)

        assertFalse(
            storeWithExceededAmountToRefund.validateAmount(availableAmountToRefund, currentBalance)
        )
    }

    @Test
    fun `validateAmount should return false when amount exceeds currentBalance`() {
        val storeWithExceededAmountForBalance = store.copy(amount = 500.01)
        val availableAmountToRefundGreaterThanBalance = 600.0

        assertFalse(
            storeWithExceededAmountForBalance.validateAmount(
                availableAmountToRefundGreaterThanBalance,
                currentBalance
            )
        )
    }

}