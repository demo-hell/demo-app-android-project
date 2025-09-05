package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.LONG_TIME_NO_UTC
import br.com.mobicare.cielo.commons.utils.parseToZonedDateTime
import br.com.mobicare.cielo.extensions.clearDate
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferResult
import br.com.mobicare.cielo.pixMVVM.utils.PixTransactionsFactory
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PixTransferResultMapperTest {

    private val response = PixTransactionsFactory.TransferResult.response

    private val expectedResult = PixTransferResult(
        endToEndId = "E01027058202302081005sirsWeaVmgI",
        transactionCode = "fe785da1-dd5c-43ab-8402-96441a040968",
        transactionDate = "2024-01-22T18:57:35.624".clearDate().parseToZonedDateTime(LONG_TIME_NO_UTC),
        transactionStatus = PixTransactionStatus.EXECUTED,
        schedulingDate = "2024-01-22T18:57:35.624".clearDate().parseToZonedDateTime(LONG_TIME_NO_UTC),
        schedulingCode = "123"
    )

    @Test
    fun `it should map response to entity correctly`() {
        val result = response.toEntity()

        assertThat(result).isEqualTo(expectedResult)
    }

}