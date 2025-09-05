package br.com.mobicare.cielo.suporteTecnico.presentation.viewModel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.suporteTecnico.domain.useCase.GetMerchantEquipmentsUseCase
import br.com.mobicare.cielo.suporteTecnico.presentation.viewModel.utils.SuporteTecnicoFactory
import br.com.mobicare.cielo.suporteTecnico.utils.UiStateEquipments
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MerchantEquipmentsViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcher = TestDispatcherRule()

    private val getMerchantEquipmentsUseCase = mockk<GetMerchantEquipmentsUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private lateinit var viewModel: MerchantEquipmentsViewModel
    private val onlyMachinesDigitalsResponse = SuporteTecnicoFactory.onlyMachinesDigitalsResponse
    private val machinesDigitalsAndPhysicalsResponse = SuporteTecnicoFactory.machinesDigitalsAndPhysicalsResponse
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultError422 = SuporteTecnicoFactory.resultErrorCode422
    private val context = mockk<Context>(relaxed = true)

    @Before
    fun setup() {
        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context

        viewModel = MerchantEquipmentsViewModel(getMerchantEquipmentsUseCase, getUserObjUseCase)

        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }

    @Test
    fun `it should set the correct value to success state in the getMerchantEquipment`() =
        runTest {
            // given
            coEvery { getMerchantEquipmentsUseCase() } returns
                CieloDataResult.Success(machinesDigitalsAndPhysicalsResponse)

            // when
            viewModel.getMerchantEquipment()

            // then
            dispatcher.advanceUntilIdle()

            viewModel.merchantEquipments.value?.let { state ->
                assert(state is UiStateEquipments.Success)
            }
        }

    @Test
    fun `it should set the machines digitals value to success state in the getMerchantEquipment`() =
        runTest {
            // given
            coEvery { getMerchantEquipmentsUseCase() } returns
                CieloDataResult.Success(onlyMachinesDigitalsResponse)

            // when
            viewModel.getMerchantEquipment()

            // then
            dispatcher.advanceUntilIdle()

            assertThat(viewModel.merchantEquipments.value is UiStateEquipments.ErrorWithoutMachine)
        }

    @Test
    fun `it should set ErrorWithoutMachine state on getMerchantEquipments call result with code 422`() =
        runTest {
            // given
            coEvery { getMerchantEquipmentsUseCase() } returns resultError422

            // when
            viewModel.getMerchantEquipment()

            // then
            dispatcher.advanceUntilIdle()

            assertThat(viewModel.merchantEquipments.value is UiStateEquipments.ErrorWithoutMachine)
        }

    @Test
    fun `it should set error state on getMerchantEquipments call result`() =
        runTest {
            // given
            coEvery { getMerchantEquipmentsUseCase() } returns resultError

            // when
            viewModel.getMerchantEquipment()

            // then
            dispatcher.advanceUntilIdle()

            assert(viewModel.merchantEquipments.value is UiStateEquipments.Error)
        }

    @Test
    fun `it should set empty state on getMerchantEquipments call result`() =
        runTest {
            // given
            coEvery { getMerchantEquipmentsUseCase() } returns CieloDataResult.Empty()

            // when
            viewModel.getMerchantEquipment()

            // then
            dispatcher.advanceUntilIdle()

            assert(viewModel.merchantEquipments.value is UiStateEquipments.Empty)
        }
}
