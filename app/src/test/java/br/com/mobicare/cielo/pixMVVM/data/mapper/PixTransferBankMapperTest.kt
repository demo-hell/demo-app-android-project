package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferBank
import br.com.mobicare.cielo.pixMVVM.utils.PixTransactionsFactory
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PixTransferBankMapperTest {

    private val response = PixTransactionsFactory.TransferBanks.response

    private val expectedResult = listOf(
        PixTransferBank(
            code = 1,
            ispb = "60701190",
            shortName = "Banco do Brasil",
            name = "Banco do Brasil"
        ),
        PixTransferBank(
            code = 237,
            ispb = "60746948",
            shortName = "Bradesco",
            name = "Bradesco"
        )
    )

    @Test
    fun `it should map response to entity correctly`() {
        val result = response.toEntity()

        assertThat(result).isEqualTo(expectedResult)
    }

}