package br.com.mobicare.cielo.pixMVVM.presentation.key.models

import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferBank
import br.com.mobicare.cielo.pixMVVM.presentation.key.enums.PixBankAccountType
import br.com.mobicare.cielo.pixMVVM.presentation.key.enums.PixBeneficiaryType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PixBankAccountStoreTest {

    private val emptyStore = PixBankAccountStore()

    @Test
    fun `validateBank should return false when bank is null`() {
        val store = emptyStore.copy(bank = null)

        assertFalse(store.validateBank)
    }

    @Test
    fun `validateBank should return true when bank is not null`() {
        val store = emptyStore.copy(bank = PixTransferBank())

        assertTrue(store.validateBank)
    }

    @Test
    fun `validateAccountType should return false when bankAccountType is null`() {
        val store = emptyStore.copy(bankAccountType = null)

        assertFalse(store.validateAccountType)
    }

    @Test
    fun `validateAccountType should return true when bankAccountType is not null`() {
        val store = emptyStore.copy(bankAccountType = PixBankAccountType.CHECKING_ACCOUNT)

        assertTrue(store.validateAccountType)
    }

    @Test
    fun `validateAccountData should return false when at least one of beneficiary fields is null`() {
        val store = emptyStore.copy(
            bankBranchNumber = "001",
            bankAccountNumber = null,
            bankAccountDigit = null
        )

        assertFalse(store.validateAccountData)
    }

    @Test
    fun `validateAccountData should return true when beneficiary fields are not null`() {
        val store = emptyStore.copy(
            bankBranchNumber = "001",
            bankAccountNumber = "12345",
            bankAccountDigit = "6"
        )

        assertTrue(store.validateAccountData)
    }

    @Test
    fun `validateDocument should return false when beneficiaryType is CPF and documentNumber is invalid`() {
        val invalidCpf = "12345678901"

        val store = emptyStore.copy(
            beneficiaryType = PixBeneficiaryType.CPF,
            documentNumber = invalidCpf
        )

        assertFalse(store.validateDocument)
    }

    @Test
    fun `validateDocument should return true when beneficiaryType is CPF and documentNumber is valid`() {
        val validCpf = "71826877045"

        val store = emptyStore.copy(
            beneficiaryType = PixBeneficiaryType.CPF,
            documentNumber = validCpf
        )

        assertTrue(store.validateDocument)
    }

    @Test
    fun `validateDocument should return false when beneficiaryType is CNPJ and documentNumber is invalid`() {
        val invalidCnpj = "1234567890123"

        val store = emptyStore.copy(
            beneficiaryType = PixBeneficiaryType.CNPJ,
            documentNumber = invalidCnpj
        )

        assertFalse(store.validateDocument)
    }

    @Test
    fun `validateDocument should return true when beneficiaryType is CNPJ and documentNumber is valid`() {
        val validCnpj = "01063751000110"

        val store = emptyStore.copy(
            beneficiaryType = PixBeneficiaryType.CNPJ,
            documentNumber = validCnpj
        )

        assertTrue(store.validateDocument)
    }

    @Test
    fun `validateRecipient should return false when recipientName is null`() {
        val store = emptyStore.copy(recipientName = null)

        assertFalse(store.validateRecipient)
    }

    @Test
    fun `validateRecipient should return false when recipientName is empty`() {
        val store = emptyStore.copy(recipientName = "")

        assertFalse(store.validateRecipient)
    }

    @Test
    fun `validateRecipient should return true when recipientName is not empty`() {
        val store = emptyStore.copy(recipientName = "John Doe")

        assertTrue(store.validateRecipient)
    }

}