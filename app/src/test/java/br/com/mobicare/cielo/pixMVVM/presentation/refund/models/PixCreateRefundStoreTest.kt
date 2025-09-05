package br.com.mobicare.cielo.pixMVVM.presentation.refund.models

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PixCreateRefundStoreTest {

    private val store = PixCreateRefundStore(
        otpCode = "123456",
        fingerprint = "fingerprint",
        amount = 100.0,
        idEndToEnd = "idEndToEnd",
        idTx = "idTx"
    )

    @Test
    fun `validate should return true when data is filled`() {
        assertTrue(store.validate())
    }

    @Test
    fun `validate should return false when any mandatory field is null`() {
        assertFalse(store.copy(otpCode = null).validate())
        assertFalse(store.copy(fingerprint = null).validate())
        assertFalse(store.copy(amount = null).validate())
        assertFalse(store.copy(idEndToEnd = null).validate())
        assertFalse(store.copy(idTx = null).validate())
    }

}