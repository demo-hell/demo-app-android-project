package br.com.mobicare.cielo.pixMVVM.presentation.transfer.models

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PixTransferStoreTest {

    private val emptyStore = PixTransferStore()

    private val balance = 100.0

    @Test
    fun `validateAmount should return false when amount is zero`() {
        val store = emptyStore.copy(amount = 0.0)

        assertFalse(store.validateAmount(balance))
    }

    @Test
    fun `validateAmount should return false when amount exceeds the balance`() {
        val store = emptyStore.copy(amount = 100.01)

        assertFalse(store.validateAmount(balance))
    }

    @Test
    fun `validateAmount should return true when amount is valid`() {
        val store = emptyStore.copy(amount = 10.0)

        assertTrue(store.validateAmount(balance))
    }

    @Test
    fun `validateAmount should return true when balance is null`() {
        val store = emptyStore.copy(amount = 100.01)

        assertTrue(store.validateAmount())
    }

}