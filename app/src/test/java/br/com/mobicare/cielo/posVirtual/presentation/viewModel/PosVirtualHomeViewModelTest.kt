package br.com.mobicare.cielo.posVirtual.presentation.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualProductId
import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualStatus
import br.com.mobicare.cielo.posVirtual.presentation.home.PosVirtualHomeViewModel
import br.com.mobicare.cielo.posVirtual.presentation.home.utils.PosVirtualProductClickAction
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PosVirtualHomeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private lateinit var viewModel: PosVirtualHomeViewModel

    private val posVirtualProducts = PosVirtualFactory.Eligibility.posVirtualProducts
    private val expectedEnabledProducts = posVirtualProducts.filter { it.status == PosVirtualStatus.SUCCESS }
    private val expectedNotEnabledProducts = posVirtualProducts.filter { it.status != PosVirtualStatus.SUCCESS }

    @Before
    fun setup() {
        viewModel = PosVirtualHomeViewModel()
    }

    @Test
    fun `it should filter enabled e not enabled products correctly on buildMenuItems call`() = runTest {
        // when
        viewModel.run {
            setProductList(posVirtualProducts)
            buildMenuItems()
        }

        // then
        dispatcherRule.advanceUntilIdle()

        viewModel.enabledProductsLiveData.value?.let {
            assertThat(it.size).isEqualTo(1)
            assertThat(it).isEqualTo(expectedEnabledProducts)
        }

        viewModel.notEnabledProductsLiveData.value?.let {
            assertThat(it.size).isEqualTo(3)
            assertThat(it).isEqualTo(expectedNotEnabledProducts)
        }
    }

    @Test
    fun `it should route to RequestDetails action when product status is pending on routeAction call`() {
        // given
        val onePendingProduct = posVirtualProducts.first { it.status == PosVirtualStatus.PENDING }

        // when
        viewModel.routeAction(onePendingProduct) {
            // then
            assertThat(it).isInstanceOf(PosVirtualProductClickAction.RequestDetails::class.java)
            assertThat((it as PosVirtualProductClickAction.RequestDetails).product).isEqualTo(onePendingProduct)
        }
    }

    @Test
    fun `it should route to RequestDetails action when product status is canceled on routeAction call`() {
        // given
        val oneCanceledProduct = posVirtualProducts.first { it.status == PosVirtualStatus.CANCELED }

        // when
        viewModel.routeAction(oneCanceledProduct) {
            // then
            assertThat(it).isInstanceOf(PosVirtualProductClickAction.RequestDetails::class.java)
            assertThat((it as PosVirtualProductClickAction.RequestDetails).product).isEqualTo(oneCanceledProduct)
        }
    }

    @Test
    fun `it should route to RequestDetails action when product status is failed on routeAction call`() {
        // given
        val oneFailedProduct = posVirtualProducts.first { it.status == PosVirtualStatus.FAILED }

        // when
        viewModel.routeAction(oneFailedProduct) {
            // then
            assertThat(it).isInstanceOf(PosVirtualProductClickAction.RequestDetails::class.java)
            assertThat((it as PosVirtualProductClickAction.RequestDetails).product).isEqualTo(oneFailedProduct)
        }
    }

    @Test
    fun `it should route to TapOnPhone action when product status is success on routeAction call`() {
        // given
        val tapOnPhoneProduct = posVirtualProducts
            .first { it.id == PosVirtualProductId.TAP_ON_PHONE }
            .copy(status = PosVirtualStatus.SUCCESS)

        // when
        viewModel.routeAction(tapOnPhoneProduct) {
            // then
            assertThat(it).isInstanceOf(PosVirtualProductClickAction.TapOnPhone::class.java)
        }
    }

    @Test
    fun `it should route to SuperLink action when product status is success on routeAction call`() {
        // given
        val superLinkProduct = posVirtualProducts
            .first { it.id == PosVirtualProductId.SUPERLINK_ADDITIONAL }
            .copy(status = PosVirtualStatus.SUCCESS)

        // when
        viewModel.routeAction(superLinkProduct) {
            // then
            assertThat(it).isInstanceOf(PosVirtualProductClickAction.SuperLink::class.java)
        }
    }

    @Test
    fun `it should route to Pix action when product status is success on routeAction call`() {
        // given
        val pixProduct = posVirtualProducts
            .first { it.id == PosVirtualProductId.PIX }
            .copy(status = PosVirtualStatus.SUCCESS)

        // when
        viewModel.routeAction(pixProduct) {
            // then
            assertThat(it).isInstanceOf(PosVirtualProductClickAction.Pix::class.java)
            assertThat((it as PosVirtualProductClickAction.Pix).logicalNumber).isEqualTo(pixProduct.logicalNumber)
        }
    }

    @Test
    fun `it should route to UnavailableOption action when product id is invalid on routeAction call`() {
        // given
        val invalidProduct = posVirtualProducts
            .first()
            .copy(status = PosVirtualStatus.SUCCESS, id = null)

        // when
        viewModel.routeAction(invalidProduct) {
            // then
            assertThat(it).isInstanceOf(PosVirtualProductClickAction.UnavailableOption::class.java)
        }
    }

}