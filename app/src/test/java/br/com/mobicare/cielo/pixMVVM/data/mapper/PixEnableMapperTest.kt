package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.pixMVVM.domain.model.PixEnable
import br.com.mobicare.cielo.pixMVVM.utils.PixTransactionsFactory
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PixEnableMapperTest {
    private val response = PixTransactionsFactory.Enable.response

    private val expectedResult =
        PixEnable(
            refund = false,
            cancelSchedule = false,
            requestAnalysis = false,
        )

    @Test
    fun `it should map PixEnableResponse to entity correctly`() {
        val result = response.toEntity()

        assertThat(result).isEqualTo(expectedResult)
    }
}
