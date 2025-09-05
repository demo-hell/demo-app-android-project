package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixAllowsChangeValueEnum
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQRCodeType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQrCodeOperationType
import br.com.mobicare.cielo.pixMVVM.utils.PixQRCodeFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class PixQRCodeUtilsTest {
    @Test
    fun `getTitleToolbar returns change title when PixType is CHANGE`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.CHANGE)
        val result = PixQRCodeUtils.getTitleToolbar(qrCode)
        assertEquals(R.string.pix_qr_code_payment_summary_toolbar_title_change, result)
    }

    @Test
    fun `getTitleToolbar returns withdrawal title when PixType is WITHDRAWAL`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.WITHDRAWAL)
        val result = PixQRCodeUtils.getTitleToolbar(qrCode)
        assertEquals(R.string.pix_qr_code_payment_summary_toolbar_title_withdrawal, result)
    }

    @Test
    fun `getTitleToolbar returns default title when PixType is OTHER`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.TRANSFER)
        val result = PixQRCodeUtils.getTitleToolbar(qrCode)
        assertEquals(R.string.pix_qr_code_payment_summary_toolbar_title_default, result)
    }

    @Test
    fun `amount returns change amount when PixType is CHANGE`() {
        val qrCode =
            PixQRCodeFactory.pixDecodeQRCode.copy(
                pixType = PixQrCodeOperationType.CHANGE,
                originalAmount = 100.0,
            )

        val result = PixQRCodeUtils.getPaymentAmount(qrCode)
        assertEquals(100.0, result, 0.0)
    }

    @Test
    fun `amount returns withdraw amount when PixType is WITHDRAWAL`() {
        val qrCode =
            PixQRCodeFactory.pixDecodeQRCode.copy(
                pixType = PixQrCodeOperationType.WITHDRAWAL,
                withDrawAmount = 200.0,
            )

        val result = PixQRCodeUtils.getPaymentAmount(qrCode)
        assertEquals(200.0, result, 0.0)
    }

    @Test
    fun `amount returns final amount when PixType is OTHER`() {
        val qrCode =
            PixQRCodeFactory.pixDecodeQRCode.copy(
                pixType = PixQrCodeOperationType.TRANSFER,
                finalAmount = 300.0,
            )

        val result = PixQRCodeUtils.getPaymentAmount(qrCode)
        assertEquals(300.0, result, 0.0)
    }

    @Test
    fun `amount returns zero when amounts are null`() {
        val qrCode =
            PixQRCodeFactory.pixDecodeQRCode.copy(
                pixType = PixQrCodeOperationType.TRANSFER,
                finalAmount = null,
            )

        val result = PixQRCodeUtils.getPaymentAmount(qrCode)
        assertEquals(0.0, result, 0.0)
    }

    @Test
    fun `isAllowedChangeValue returns true when allowed for change`() {
        val qrCode =
            PixQRCodeFactory.pixDecodeQRCode.copy(
                type = PixQRCodeType.STATIC,
                pixType = PixQrCodeOperationType.CHANGE,
                modalityAltChange = PixAllowsChangeValueEnum.ALLOWED,
            )

        val result = PixQRCodeUtils.isAllowedChangePaymentValue(qrCode)
        assertEquals(true, result)
    }

    @Test
    fun `isAllowedChangeValue returns false when not allowed for change`() {
        val qrCode =
            PixQRCodeFactory.pixDecodeQRCode.copy(
                type = PixQRCodeType.STATIC,
                pixType = PixQrCodeOperationType.CHANGE,
                modalityAlteration = PixAllowsChangeValueEnum.NOT_ALLOWED,
            )

        val result = PixQRCodeUtils.isAllowedChangePaymentValue(qrCode)
        assertEquals(false, result)
    }

    @Test
    fun `isAllowedChangeValue returns true when allowed for withdrawal`() {
        val qrCode =
            PixQRCodeFactory.pixDecodeQRCode.copy(
                type = PixQRCodeType.STATIC,
                pixType = PixQrCodeOperationType.WITHDRAWAL,
                modalityAltWithDraw = PixAllowsChangeValueEnum.ALLOWED,
            )

        val result = PixQRCodeUtils.isAllowedChangePaymentValue(qrCode)
        assertEquals(true, result)
    }

    @Test
    fun `isAllowedChangeValue returns false when not allowed for withdrawal`() {
        val qrCode =
            PixQRCodeFactory.pixDecodeQRCode.copy(
                type = PixQRCodeType.STATIC,
                pixType = PixQrCodeOperationType.WITHDRAWAL,
                modalityAltChange = PixAllowsChangeValueEnum.NOT_ALLOWED,
            )

        val result = PixQRCodeUtils.isAllowedChangePaymentValue(qrCode)
        assertEquals(false, result)
    }

    @Test
    fun `isAllowedChangeValue returns true when allowed for other`() {
        val qrCode =
            PixQRCodeFactory.pixDecodeQRCode.copy(
                type = PixQRCodeType.STATIC,
                pixType = PixQrCodeOperationType.TRANSFER,
                modalityAltChange = PixAllowsChangeValueEnum.ALLOWED,
            )

        val result = PixQRCodeUtils.isAllowedChangePaymentValue(qrCode)
        assertEquals(true, result)
    }

    @Test
    fun `isAllowedChangeValue returns false when not allowed for other`() {
        val qrCode =
            PixQRCodeFactory.pixDecodeQRCode.copy(
                type = PixQRCodeType.STATIC,
                pixType = PixQrCodeOperationType.TRANSFER,
                modalityAlteration = PixAllowsChangeValueEnum.NOT_ALLOWED,
            )

        val result = PixQRCodeUtils.isAllowedChangePaymentValue(qrCode)
        assertEquals(false, result)
    }

    @Test
    fun `isAllowedChangeValue returns false when type is DYNAMIC_COBV`() {
        val qrCode =
            PixQRCodeFactory.pixDecodeQRCode.copy(
                type = PixQRCodeType.DYNAMIC_COBV,
                pixType = PixQrCodeOperationType.TRANSFER,
                modalityAltChange = PixAllowsChangeValueEnum.ALLOWED,
            )

        val result = PixQRCodeUtils.isAllowedChangePaymentValue(qrCode)
        assertEquals(false, result)
    }

    @Test
    fun `isAllowedChangeChangeValue returns true when PixType is CHANGE and modalityAltChange is ALLOWED`() {
        val qrCode =
            PixQRCodeFactory.pixDecodeQRCode.copy(
                pixType = PixQrCodeOperationType.CHANGE,
                modalityAltChange = PixAllowsChangeValueEnum.ALLOWED,
            )
        val result = PixQRCodeUtils.isAllowedChangeChangeValue(qrCode)
        assertEquals(true, result)
    }

    @Test
    fun `isAllowedChangeChangeValue returns false when PixType is CHANGE and modalityAltChange is NOT_ALLOWED`() {
        val qrCode =
            PixQRCodeFactory.pixDecodeQRCode.copy(
                pixType = PixQrCodeOperationType.CHANGE,
                modalityAltChange = PixAllowsChangeValueEnum.NOT_ALLOWED,
            )
        val result = PixQRCodeUtils.isAllowedChangeChangeValue(qrCode)
        assertEquals(false, result)
    }

    @Test
    fun `isAllowedChangeChangeValue returns false when PixType is not CHANGE`() {
        val qrCode =
            PixQRCodeFactory.pixDecodeQRCode.copy(
                pixType = PixQrCodeOperationType.WITHDRAWAL,
                modalityAltChange = PixAllowsChangeValueEnum.ALLOWED,
            )
        val result = PixQRCodeUtils.isAllowedChangeChangeValue(qrCode)
        assertEquals(false, result)
    }

    @Test
    fun `getBankName returns ispbChangeName when PixType is CHANGE`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.CHANGE, ispbChangeName = "Bank A")
        val result = PixQRCodeUtils.getBankName(qrCode)
        assertEquals("Bank A", result)
    }

    @Test
    fun `getBankName returns ispbWithDrawName when PixType is WITHDRAWAL`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.WITHDRAWAL, ispbWithDrawName = "Bank B")
        val result = PixQRCodeUtils.getBankName(qrCode)
        assertEquals("Bank B", result)
    }

    @Test
    fun `getBankName returns participantName when PixType is OTHER`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.TRANSFER, participantName = "Bank C")
        val result = PixQRCodeUtils.getBankName(qrCode)
        assertEquals("Bank C", result)
    }

    @Test
    fun `getAgentMode returns modalityChangeAgent when PixType is CHANGE`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.CHANGE, modalityChangeAgent = "Agent A")
        val result = PixQRCodeUtils.getAgentMode(qrCode)
        assertEquals("Agent A", result)
    }

    @Test
    fun `getAgentMode returns modalityWithDrawAgent when PixType is WITHDRAWAL`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.WITHDRAWAL, modalityWithDrawAgent = "Agent B")
        val result = PixQRCodeUtils.getAgentMode(qrCode)
        assertEquals("Agent B", result)
    }

    @Test
    fun `getAgentMode returns null when PixType is OTHER`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.TRANSFER)
        val result = PixQRCodeUtils.getAgentMode(qrCode)
        assertEquals(null, result)
    }

    @Test
    fun `getAgentWithdrawalIspb returns ispbChange when PixType is CHANGE`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.CHANGE, ispbChange = 123456)
        val result = PixQRCodeUtils.getAgentWithdrawalIspb(qrCode)
        assertEquals("123456", result)
    }

    @Test
    fun `getAgentWithdrawalIspb returns ispbWithDraw when PixType is WITHDRAWAL`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.WITHDRAWAL, ispbWithDraw = 654321)
        val result = PixQRCodeUtils.getAgentWithdrawalIspb(qrCode)
        assertEquals("654321", result)
    }

    @Test
    fun `getAgentWithdrawalIspb returns null when PixType is OTHER`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.TRANSFER)
        val result = PixQRCodeUtils.getAgentWithdrawalIspb(qrCode)
        assertEquals(null, result)
    }

    @Test
    fun `getPaymentBillingDetail returns correct details when all fields are present`() {
        val qrCode =
            PixQRCodeFactory.pixDecodeQRCode.copy(
                originalAmount = 100.0,
                interest = 10.0,
                penalty = 5.0,
                abatement = 2.0,
                discount = 3.0,
            )
        val result = PixQRCodeUtils.getPaymentBillingDetail(qrCode)
        assertEquals(5, result.size)
        assertEquals("R\$ 100,00", result[0].value)
        assertEquals("R\$ 10,00", result[1].value)
        assertEquals("R\$ 5,00", result[2].value)
        assertEquals("R\$ 2,00", result[3].value)
        assertEquals("R\$ 3,00", result[4].value)
    }

    @Test
    fun `getPaymentBillingDetail returns correct details when some fields are null`() {
        val qrCode =
            PixQRCodeFactory.pixDecodeQRCode.copy(
                originalAmount = 100.0,
                interest = null,
                penalty = 5.0,
                abatement = null,
                discount = 3.0,
            )
        val result = PixQRCodeUtils.getPaymentBillingDetail(qrCode)
        assertEquals(5, result.size)
        assertEquals("R\$ 100,00", result[0].value)
        assertEquals("-", result[1].value)
        assertEquals("R\$ 5,00", result[2].value)
        assertEquals("-", result[3].value)
        assertEquals("R\$ 3,00", result[4].value)
    }

    @Test
    fun `getPaymentBillingDetail returns correct details when all fields are null`() {
        val qrCode =
            PixQRCodeFactory.pixDecodeQRCode.copy(
                originalAmount = null,
                interest = null,
                penalty = null,
                abatement = null,
                discount = null,
            )
        val result = PixQRCodeUtils.getPaymentBillingDetail(qrCode)
        assertEquals(5, result.size)
        assertEquals("-", result[0].value)
        assertEquals("-", result[1].value)
        assertEquals("-", result[2].value)
        assertEquals("-", result[3].value)
        assertEquals("-", result[4].value)
    }

    @Test
    fun `getPaymentDetailsData returns correct details for charge type`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(type = PixQRCodeType.DYNAMIC_COBV)
        val result = PixQRCodeUtils.getPaymentDetailsData(qrCode)
        assertEquals(3, result.size)
        assertEquals(R.string.pix_qr_code_payment_summary_payment_destination_title, result[0].titleRes)
        assertEquals(R.string.pix_qr_code_payment_summary_payment_debtor_title, result[1].titleRes)
        assertEquals(R.string.pix_qr_code_payment_summary_payment_transaction_title, result[2].titleRes)
    }

    @Test
    fun `getPaymentDetailsData returns correct details for change type`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.CHANGE)
        val result = PixQRCodeUtils.getPaymentDetailsData(qrCode)
        assertEquals(2, result.size)
        assertEquals(R.string.pix_qr_code_payment_summary_payment_destination_title, result[0].titleRes)
        assertEquals(R.string.pix_qr_code_payment_summary_payment_transaction_title, result[1].titleRes)
    }

    @Test
    fun `getPaymentDetailsData returns correct details for withdrawal type`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.WITHDRAWAL)
        val result = PixQRCodeUtils.getPaymentDetailsData(qrCode)
        assertEquals(2, result.size)
        assertEquals(R.string.pix_qr_code_payment_summary_payment_destination_title, result[0].titleRes)
        assertEquals(R.string.pix_qr_code_payment_summary_payment_transaction_title, result[1].titleRes)
    }

    @Test
    fun `getPaymentDetailsData returns correct details for other type`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.TRANSFER)
        val result = PixQRCodeUtils.getPaymentDetailsData(qrCode)
        assertEquals(1, result.size)
        assertEquals(R.string.pix_qr_code_payment_summary_payment_destination_title, result[0].titleRes)
    }

    @Test
    fun `isPixTypeChangeOrWithdrawal returns true when PixType is CHANGE`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.CHANGE)
        val result = PixQRCodeUtils.isPixTypeChangeOrWithdrawal(qrCode)
        assertEquals(true, result)
    }

    @Test
    fun `isPixTypeChangeOrWithdrawal returns true when PixType is WITHDRAWAL`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.WITHDRAWAL)
        val result = PixQRCodeUtils.isPixTypeChangeOrWithdrawal(qrCode)
        assertEquals(true, result)
    }

    @Test
    fun `isPixTypeChangeOrWithdrawal returns false when PixType is OTHER`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.TRANSFER)
        val result = PixQRCodeUtils.isPixTypeChangeOrWithdrawal(qrCode)
        assertEquals(false, result)
    }

    @Test
    fun `isPixTypeCharge returns true when type is DYNAMIC_COBV`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(type = PixQRCodeType.DYNAMIC_COBV)
        val result = PixQRCodeUtils.isPixTypeCharge(qrCode)
        assertEquals(true, result)
    }

    @Test
    fun `isPixTypeCharge returns false when type is STATIC`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(type = PixQRCodeType.STATIC)
        val result = PixQRCodeUtils.isPixTypeCharge(qrCode)
        assertEquals(false, result)
    }

    @Test
    fun `isPixTypeChange returns true when PixType is CHANGE`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.CHANGE)
        val result = PixQRCodeUtils.isPixTypeChange(qrCode)
        assertEquals(true, result)
    }

    @Test
    fun `isPixTypeChange returns false when PixType is not CHANGE`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.WITHDRAWAL)
        val result = PixQRCodeUtils.isPixTypeChange(qrCode)
        assertEquals(false, result)
    }

    @Test
    fun `isPixTypeWithdrawal returns true when PixType is WITHDRAWAL`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.WITHDRAWAL)
        val result = PixQRCodeUtils.isPixTypeWithdrawal(qrCode)
        assertEquals(true, result)
    }

    @Test
    fun `isPixTypeWithdrawal returns false when PixType is not WITHDRAWAL`() {
        val qrCode = PixQRCodeFactory.pixDecodeQRCode.copy(pixType = PixQrCodeOperationType.CHANGE)
        val result = PixQRCodeUtils.isPixTypeWithdrawal(qrCode)
        assertEquals(false, result)
    }
}
