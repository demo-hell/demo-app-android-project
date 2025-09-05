package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.LONG_TIME_WITH_MILLIS_NO_UTC
import br.com.mobicare.cielo.commons.utils.parseToZonedDateTime
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundCreated
import br.com.mobicare.cielo.pixMVVM.utils.PixRefundsFactory
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PixRefundCreatedMapperTest {

    private val response = PixRefundsFactory.RefundCreated.response

    private val expectedResult = PixRefundCreated(
        idEndToEndReturn = "string",
        idEndToEndOriginal = "string",
        transactionDate = "2023-05-09T07:05:31.901".parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
        idAdjustment = "string",
        transactionCode = "string",
        transactionStatus = PixTransactionStatus.NOT_EXECUTED,
        idTx = "string"
    )

    @Test
    fun `it should map response to entity correctly`() {
        val result = response.toEntity()

        assertThat(result).isEqualTo(expectedResult)
    }

}