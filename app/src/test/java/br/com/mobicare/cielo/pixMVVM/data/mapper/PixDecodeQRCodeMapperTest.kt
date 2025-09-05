package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.pixMVVM.domain.enums.PixAllowsChangeValueEnum
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixOwnerType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQRCodeType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQrCodeOperationType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixDecodeQRCode
import br.com.mobicare.cielo.pixMVVM.utils.PixQRCodeFactory
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PixDecodeQRCodeMapperTest {
    private val response = PixQRCodeFactory.pixDecodeQRCodeResponse

    private val expectedResult =
        PixDecodeQRCode(
            endToEndId = "E010270582024080712199JODC2ZMdhd",
            type = PixQRCodeType.STATIC,
            pixType = PixQrCodeOperationType.TRANSFER,
            participant = 1027058,
            participantName = "CIELO IP S.A.",
            revision = null,
            receiverName = "MOCK RAZAO LTDA",
            receiverTradingName = "",
            receiverPersonType = PixOwnerType.LEGAL_PERSON,
            receiverDocument = "62.993.482/0001-85",
            idTx = "CIELO20240802000000000139",
            payerName = "",
            payerDocument = "",
            city = "SAO PAULO",
            address = "",
            state = "",
            zipCode = "",
            originalAmount = 0.00,
            interest = null,
            penalty = null,
            discount = null,
            abatement = null,
            finalAmount = 0.00,
            withDrawAmount = null,
            changeAmount = null,
            allowsChange = null,
            expireDate = null,
            dueDate = "",
            daysAfterDueDate = null,
            creationDate = null,
            decodeDate = null,
            url = "",
            reusable = null,
            branch = "1",
            accountType = "4",
            accountNumber = "34728200016",
            key = "+5583988556622",
            keyType = "PHONE",
            category = "",
            additionalData = "",
            payerType = null,
            modalityAlteration = PixAllowsChangeValueEnum.ALLOWED,
            description = "",
            ispbWithDraw = null,
            ispbWithDrawName = "",
            modalityAltWithDraw = PixAllowsChangeValueEnum.NOT_ALLOWED,
            modalityWithDrawAgent = "",
            ispbChange = null,
            ispbChangeName = "",
            modalityAltChange = PixAllowsChangeValueEnum.NOT_ALLOWED,
            modalityChangeAgent = "",
            status = null,
            qrCode = "00020101021126360014br.gov.bcb.pix0114+55839885566225204000053039865802BR5915MOCK RAZAO LTDA6009SAO PAULO62290525CIELO20240802000000000139630412BF",
            isSchedulable = true,
        )

    @Test
    fun `it should map response to entity correctly`() {
        val result = response.toEntity()
        assertThat(result).isEqualTo(expectedResult)
    }
}
