package br.com.mobicare.cielo.posVirtual.data.mapper

import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualProductId
import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualStatus
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PosVirtualMapperTest {

    private val posVirtualResponse = PosVirtualFactory.Eligibility.posVirtualResponse

    @Test
    fun `it should map response to entity correctly on toEntity call`() {
        // when
        val posVirtualEntity = posVirtualResponse.toEntity()

        // then
        posVirtualEntity.let {
            assertThat(it.status).isEqualTo(PosVirtualStatus.SUCCESS)
            assertThat(it.merchantId).isEqualTo(posVirtualResponse.merchantId)
            assertThat(it.products?.size).isEqualTo(posVirtualResponse.products?.size)
        }
    }

    @Test
    fun `it should map product response to product entity correctly on toEntity call`() {
        // given
        val pixProductResponse = posVirtualResponse.products?.first { it.id == PosVirtualProductId.PIX.name }

        // when
        val pixProductEntity = pixProductResponse?.toEntity()

        // then
        pixProductEntity?.let { entity ->
            assertThat(entity.status).isEqualTo(PosVirtualStatus.SUCCESS)
            assertThat(entity.id).isEqualTo(PosVirtualProductId.PIX)
            assertThat(entity.logicalNumber).isEqualTo(pixProductResponse.logicalNumber)
        }
    }

    @Test
    fun `it should return the same list size when mapping product list response to product list entity on toEntityList call`() {
        // given
        val productListResponse = posVirtualResponse.products

        // when
        val productListEntity = productListResponse?.toEntityList()

        // then
        assertThat(productListEntity?.size).isEqualTo(productListResponse?.size)
    }

}