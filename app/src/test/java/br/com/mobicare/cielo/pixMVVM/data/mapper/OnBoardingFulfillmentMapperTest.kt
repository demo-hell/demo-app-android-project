package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.pixMVVM.data.model.response.OnBoardingFulfillmentResponse
import br.com.mobicare.cielo.pixMVVM.domain.enums.BlockType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixDocumentType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.ProfileType
import br.com.mobicare.cielo.pixMVVM.domain.model.OnBoardingFulfillment
import org.junit.Assert.assertEquals
import org.junit.Test

class OnBoardingFulfillmentMapperTest {

    private val onBoardingFulfillmentResponse = OnBoardingFulfillmentResponse(
        eligible = false,
        profileType = "FREE_MOVEMENT",
        settlementActive = true,
        enabled = true,
        status = "ACTIVE",
        blockType = "BANK_DOMICILE",
        pixAccount = OnBoardingFulfillmentResponse.PixAccount(
            pixId = "1234",
            bank = "bank",
            agency = "001",
            account = "0001",
            accountDigit = "1",
            dockAccountId = "54321",
            isCielo = true,
            bankName = "bankName"
        ),
        settlementScheduled = OnBoardingFulfillmentResponse.SettlementScheduled(
            enabled = true,
            list = listOf("08:00", "10:00", "12:00")
        ),
        document = "999.999.999/0001-99",
        documentType = "J"
    )

    private val expected = OnBoardingFulfillment(
        isEligible = false,
        profileType = ProfileType.FREE_MOVEMENT,
        isSettlementActive = true,
        isEnabled = true,
        status = PixStatus.ACTIVE,
        blockType = BlockType.BANK_DOMICILE,
        pixAccount = OnBoardingFulfillment.PixAccount(
            pixId = "1234",
            bank = "bank",
            agency = "001",
            account = "0001",
            accountDigit = "1",
            dockAccountId = "54321",
            isCielo = true,
            bankName = "bankName"
        ),
        settlementScheduled = OnBoardingFulfillment.SettlementScheduled(
            isEnabled = true,
            list = listOf("08:00", "10:00", "12:00")
        ),
        document = "999.999.999/0001-99",
        documentType = PixDocumentType.CNPJ
    )

    @Test
    fun `it should map response to entity correctly`() {
        // when
        val result = onBoardingFulfillmentResponse.toEntity()

        // then
        assertEquals(expected, result)
    }

}