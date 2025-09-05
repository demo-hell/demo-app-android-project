package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.pixMVVM.domain.enums.PixStatus
import br.com.mobicare.cielo.pixMVVM.domain.model.PixAuthorizationStatus
import br.com.mobicare.cielo.pixMVVM.utils.PixAuthorizationStatusFactory
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDateTime

class PixAuthorizationStatusMapperTest {

    private val pixAuthorizationStatusResponse = PixAuthorizationStatusFactory.responseWithPendingStatus
    private val expectedResult = PixAuthorizationStatus(
        status = PixStatus.PENDING,
        beginTime = LocalDateTime.of(
            2023,
            8,
            30,
            15,
            30,
            0,
            0
        )
    )

    @Test
    fun `it should map response to entity correctly`() {
        val result = pixAuthorizationStatusResponse.toEntity()

        assertThat(result.status).isEqualTo(expectedResult.status)
        assertThat(result.beginTime).isEqualTo(expectedResult.beginTime)
    }

}