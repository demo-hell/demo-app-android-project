package br.com.mobicare.cielo.simulator.simulation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.simulator.simulation.domain.model.PaymentType
import br.com.mobicare.cielo.simulator.simulation.domain.usecase.GetSimulationUseCase
import br.com.mobicare.cielo.simulator.simulation.domain.usecase.GetSimulatorProductsUseCase
import br.com.mobicare.cielo.simulator.simulation.presentation.state.UiSimulatorProductState
import br.com.mobicare.cielo.simulator.simulation.presentation.state.UiSimulatorResultState
import br.com.mobicare.cielo.simulator.simulation.presentation.viewModel.SimulatorViewModel
import br.com.mobicare.cielo.simulator.util.SimulatorFactory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class SimulatorViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getSimulatorProductsUseCase = mockk<GetSimulatorProductsUseCase>(relaxed = true)
    private val getSimulationUseCase = mockk<GetSimulationUseCase>(relaxed = true)
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val resultError = SimulatorFactory.resultError

    private val context = mockk<Context>()

    private lateinit var viewModel: SimulatorViewModel

    @Before
    fun setUp() {
        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context

        coEvery { getSimulatorProductsUseCase() } returns CieloDataResult.Success(SimulatorFactory.productsSuccess)

        viewModel = SimulatorViewModel(
            getUserObjUseCase,
            getSimulatorProductsUseCase,
            getSimulationUseCase
        )
        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }

    @Test
    fun ` when get products it should set UiState as Success state`() = runTest {
        // given
        coEvery { getSimulatorProductsUseCase() } returns CieloDataResult.Success(SimulatorFactory.productsSuccess)

        // when
        viewModel = SimulatorViewModel(
            getUserObjUseCase,
            getSimulatorProductsUseCase,
            getSimulationUseCase
        )

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.simulatorProductState.value is UiSimulatorProductState.Success)
    }

    @Test
    fun `when get products it should set error state on getSimulatorProducts call result`() =
        runTest {
            // given
            coEvery { getSimulatorProductsUseCase() } returns resultError

            // when
            viewModel = SimulatorViewModel(
                getUserObjUseCase,
                getSimulatorProductsUseCase,
                getSimulationUseCase
            )

            // then
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.simulatorProductState.value is UiSimulatorProductState.Error)
        }

    @Test
    fun `when get products it should set UiState as Error state when the CieloDataResult is empty`() =
        runTest {
            // given
            coEvery { getSimulatorProductsUseCase() } returns CieloDataResult.Empty()

            // when
            viewModel = SimulatorViewModel(
                getUserObjUseCase,
                getSimulatorProductsUseCase,
                getSimulationUseCase
            )

            // then
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.simulatorProductState.value is UiSimulatorProductState)
        }

    @Test
    fun `when get simulation it should set UiState as Success state`() = runTest {
        // given
        coEvery {
            getSimulationUseCase(
                any(),
                any(),
                any(),
                any()
            )
        } returns CieloDataResult.Success(SimulatorFactory.simulationSuccess)

        // when
        viewModel.getSimulation()

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.simulatorResultState.value is UiSimulatorResultState.Success)
    }

    @Test
    fun `when get simulation error it should set UiState as Error state`() = runTest {
        // given
        coEvery { getSimulationUseCase(any(), any(), any(), any()) } returns resultError

        // when
        viewModel.getSimulation()

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.simulatorResultState.value is UiSimulatorResultState.Error)
    }

    @Test
    fun `when get simulation is empty it should set UiState as Error state`() = runTest {
        // given
        coEvery { getSimulationUseCase(any(), any(), any(), any()) } returns CieloDataResult.Empty()

        // when
        viewModel.getSimulation()

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.simulatorResultState.value is UiSimulatorResultState.Error)
    }

    @Test
    fun `when updates simulation value it should update value livedata`() = runTest {
        // given
        val previousValue = viewModel.simulationValue

        // when
        viewModel.updateValue(BigDecimal.valueOf(1))

        // then
        dispatcherRule.advanceUntilIdle()

        val currentValue = viewModel.simulationValue

        assert(currentValue == BigDecimal.valueOf(1))
        assert(currentValue != previousValue)
    }

    @Test
    fun `when updates brand selection it should update available paymentTypes`() = runTest {
        // when
        viewModel.updateSelectedBrand("1")
        val visaPaymentTypes = viewModel.availablePaymentTypes
        viewModel.updateSelectedBrand("11")
        val agiplanPaymentTypes = viewModel.availablePaymentTypes

        // then
        dispatcherRule.advanceUntilIdle()

        assert(
            visaPaymentTypes == listOf(
                PaymentType(
                    productCode = 40, fastRepay = false, productDescription = "Crédito À Vista"
                ), PaymentType(
                    productCode = 41, fastRepay = false, productDescription = "Débito"
                ), PaymentType(
                    productCode = 43, fastRepay = false, productDescription = "Parcelado loja"
                )
            )
        )

        assert(
            agiplanPaymentTypes == listOf(
                PaymentType(
                    productCode = 2, fastRepay = false, productDescription = "Parcelado loja"
                )
            )
        )
    }

    @Test
    fun `when updates brand selection it should reset selected paymentType`() = runTest {
        // given
        viewModel.updateSelectedBrand("1")
        viewModel.updateSelectedPaymentType(40)

        val previousPaymentType = viewModel.simulatorSelectedPaymentType.value

        // when
        viewModel.updateSelectedBrand("11")
        val currentSelectedPaymentType = viewModel.simulatorSelectedPaymentType.value

        // then
        dispatcherRule.advanceUntilIdle()

        assert(
            previousPaymentType == PaymentType(
                productCode = 40, fastRepay = false, productDescription = "Crédito À Vista"
            )
        )
        assert(currentSelectedPaymentType == null)
    }

    @Test
    fun `when updates payment selection it should update selected paymentType livedata`() = runTest {
        // given
        viewModel.updateSelectedBrand("1")
        viewModel.updateSelectedPaymentType(40)
        val previousPaymentType = viewModel.simulatorSelectedPaymentType.value

        // when
        viewModel.updateSelectedPaymentType(41)
        val currentSelectedPaymentType = viewModel.simulatorSelectedPaymentType.value

        // then
        dispatcherRule.advanceUntilIdle()

        assert(
            previousPaymentType == PaymentType(
                productCode = 40, fastRepay = false, productDescription = "Crédito À Vista"
            )
        )
        assert(currentSelectedPaymentType == PaymentType(
            productCode = 41, fastRepay = false, productDescription = "Débito"))
    }
}