package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.parseFromOffsetToZonedDateTime
import br.com.mobicare.cielo.pixMVVM.domain.model.PixAccountBalance
import br.com.mobicare.cielo.pixMVVM.utils.PixAccountBalanceFactory
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PixAccountBalanceMapperTest {

    private val response = PixAccountBalanceFactory.pixAccountBalanceResponse.copy(
        balanceAvailableGlobal = 1456.87,
        timeOfRequest = "2023-12-05T10:08:30.000-03:00"
    )

    private val expectedResult = PixAccountBalance(
        currentBalance = response.balanceAvailableGlobal,
        timeOfRequest = response.timeOfRequest?.parseFromOffsetToZonedDateTime()
    )

    @Test
    fun `it should map response to entity correctly`() {
        val result = response.toEntity()

        assertThat(result.currentBalance).isEqualTo(expectedResult.currentBalance)
        assertThat(result.timeOfRequest.toString()).isEqualTo(expectedResult.timeOfRequest.toString())
    }

}