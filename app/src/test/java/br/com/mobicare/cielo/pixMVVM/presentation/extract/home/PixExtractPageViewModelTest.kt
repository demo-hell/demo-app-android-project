package br.com.mobicare.cielo.pixMVVM.presentation.extract.home

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PixReceiptsTab
import br.com.mobicare.cielo.pixMVVM.domain.model.PixExtract.PixExtractReceipt
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixExtractUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixReceiptsScheduledUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.pager.viewModel.PixExtractPageViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.utils.UIPixExtractPageState
import br.com.mobicare.cielo.pixMVVM.utils.PixExtractFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.reflect.Field

@OptIn(ExperimentalCoroutinesApi::class)
class PixExtractPageViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val getPixExtractUseCase = mockk<GetPixExtractUseCase>()
    private val getPixReceiptsScheduledUseCase = mockk<GetPixReceiptsScheduledUseCase>()

    private val context = mockk<Context>()

    private lateinit var viewModel: PixExtractPageViewModel

    private val resultUserObjSuccess = CieloDataResult.Success(UserObj())
    private val resultExtractSuccess = CieloDataResult.Success(PixExtractFactory.pixExtract)
    private val resultExtractSchedulingSuccess = CieloDataResult.Success(PixExtractFactory.pixExtractScheduling)
    private val resultExtractEmptySuccess =
        CieloDataResult.Success(PixExtractFactory.pixExtractEmpty)
    private val resultExtractSchedulingEmptySuccess = CieloDataResult.Success(PixExtractFactory.pixExtractSchedulingEmpty)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Before
    fun setup() {
        viewModel =
            PixExtractPageViewModel(
                getUserObjUseCase,
                getPixExtractUseCase,
                getPixReceiptsScheduledUseCase,
            )
        viewModel.setTab(PixReceiptsTab.TRANSFER)

        coEvery { getUserObjUseCase() } returns resultUserObjSuccess

        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context
    }

    @Test
    fun `verify loadTransactions when onResume equal true and _endList equal true`() =
        runTest {
            val field: Field = PixExtractPageViewModel::class.java.getDeclaredField("endList")
            field.isAccessible = true
            field.set(viewModel, true)

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = true, isSwipe = true)

            dispatcherRule.advanceUntilIdle()

            assertEquals(ZERO, states.size)
        }

    @Test
    fun `verify loadTransactions when onResume equal true and _transactions is not empty`() =
        runTest {
            val field: Field = PixExtractPageViewModel::class.java.getDeclaredField("_transactions")
            field.isAccessible = true
            val transactions = field.get(viewModel) as ArrayList<PixExtractReceipt>
            transactions.add(PixExtractReceipt())

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = true, isSwipe = true)

            dispatcherRule.advanceUntilIdle()

            assertEquals(ZERO, states.size)
        }

    @Test
    fun `verify loadTransactions when onResume equal true and _endList equal true or false and _transactions is empty or not empty and isSwipe equal false and extract is not empty`() =
        runTest {
            coEvery {
                getPixExtractUseCase(any())
            } returns resultExtractSuccess

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = true, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.Success::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal true and _endList equal true or false and _transactions is empty or not empty and isSwipe equal false and extract is empty and filter is inactive`() =
        runTest {
            coEvery {
                getPixExtractUseCase(any())
            } returns resultExtractEmptySuccess

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = true, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.EmptyTransactions::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal true and _endList equal true or false and _transactions is empty or not empty and isSwipe equal false and extract is empty and filter is active`() =
        runTest {
            val field: Field = PixExtractPageViewModel::class.java.getDeclaredField("_filterData")
            field.isAccessible = true
            val filterData = field.get(viewModel) as MutableLiveData<*>
            filterData.value = PixExtractFactory.filterActive

            coEvery {
                getPixExtractUseCase(any())
            } returns resultExtractEmptySuccess

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = true, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.EmptyTransactionsWithActiveFilter::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal true and _endList equal true or false and _transactions is empty or not empty and isSwipe equal false and get extract is empty error`() =
        runTest {
            coEvery {
                getPixExtractUseCase(any())
            } returns resultEmpty

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = true, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.Error::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal and _endList equal true or false and _transactions is empty or not empty true and isSwipe equal false and get extract is error`() =
        runTest {
            coEvery {
                getPixExtractUseCase(any())
            } returns resultError

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = true, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.Error::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal true and _endList equal true or false and _transactions is empty or not empty and isSwipe equal true and extract is not empty`() =
        runTest {
            coEvery {
                getPixExtractUseCase(any())
            } returns resultExtractSuccess

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = true, isSwipe = true)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.HideLoadingSwipe::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.Success::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal true and _endList equal true or false and _transactions is empty or not empty and isSwipe equal true and extract is empty and filter is inactive`() =
        runTest {
            coEvery {
                getPixExtractUseCase(any())
            } returns resultExtractEmptySuccess

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = true, isSwipe = true)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.HideLoadingSwipe::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.EmptyTransactions::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal true and _endList equal true or false and _transactions is empty or not empty and isSwipe equal true and extract is empty and filter is active`() =
        runTest {
            val field: Field = PixExtractPageViewModel::class.java.getDeclaredField("_filterData")
            field.isAccessible = true
            val filterData = field.get(viewModel) as MutableLiveData<*>
            filterData.value = PixExtractFactory.filterActive

            coEvery {
                getPixExtractUseCase(any())
            } returns resultExtractEmptySuccess

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = true, isSwipe = true)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.HideLoadingSwipe::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.EmptyTransactionsWithActiveFilter::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal true and _endList equal true or false and _transactions is empty or not empty and isSwipe equal true and get extract is empty error`() =
        runTest {
            coEvery {
                getPixExtractUseCase(any())
            } returns resultEmpty

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = true, isSwipe = true)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.HideLoadingSwipe::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.Error::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal true and _endList equal true or false and _transactions is empty or not empty and isSwipe equal true and get extract is error`() =
        runTest {
            coEvery {
                getPixExtractUseCase(any())
            } returns resultError

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = true, isSwipe = true)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.HideLoadingSwipe::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.Error::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal false and extract is not empty`() =
        runTest {
            coEvery {
                getPixExtractUseCase(any())
            } returns resultExtractSuccess

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = false, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.Success::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal false and extract is empty and filter is inactive`() =
        runTest {
            coEvery {
                getPixExtractUseCase(any())
            } returns resultExtractEmptySuccess

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = false, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.EmptyTransactions::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal false and extract is empty and filter is active`() =
        runTest {
            val field: Field = PixExtractPageViewModel::class.java.getDeclaredField("_filterData")
            field.isAccessible = true
            val filterData = field.get(viewModel) as MutableLiveData<*>
            filterData.value = PixExtractFactory.filterActive

            coEvery {
                getPixExtractUseCase(any())
            } returns resultExtractEmptySuccess

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = false, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.EmptyTransactionsWithActiveFilter::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal false and get extract is empty error`() =
        runTest {
            coEvery {
                getPixExtractUseCase(any())
            } returns resultEmpty

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = false, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.Error::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal false and get extract is error`() =
        runTest {
            coEvery {
                getPixExtractUseCase(any())
            } returns resultError

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = false, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.Error::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal true and extract is not empty`() =
        runTest {
            coEvery {
                getPixExtractUseCase(any())
            } returns resultExtractSuccess

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = false, isSwipe = true)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.HideLoadingSwipe::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.Success::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal true and extract is empty and filter is inactive`() =
        runTest {
            coEvery {
                getPixExtractUseCase(any())
            } returns resultExtractEmptySuccess

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = false, isSwipe = true)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.HideLoadingSwipe::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.EmptyTransactions::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal true and extract is empty and filter is active`() =
        runTest {
            val field: Field = PixExtractPageViewModel::class.java.getDeclaredField("_filterData")
            field.isAccessible = true
            val filterData = field.get(viewModel) as MutableLiveData<*>
            filterData.value = PixExtractFactory.filterActive

            coEvery {
                getPixExtractUseCase(any())
            } returns resultExtractEmptySuccess

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = false, isSwipe = true)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.HideLoadingSwipe::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.EmptyTransactionsWithActiveFilter::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal true and get extract is empty error`() =
        runTest {
            coEvery {
                getPixExtractUseCase(any())
            } returns resultEmpty

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = false, isSwipe = true)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.HideLoadingSwipe::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.Error::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal true and get extract is error`() =
        runTest {
            coEvery {
                getPixExtractUseCase(any())
            } returns resultError

            val states = viewModel.extractState.captureValues()

            viewModel.loadTransactions(isOnResume = false, isSwipe = true)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.HideLoadingSwipe::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.Error::class.java)
        }

    @Test
    fun `verify loadMoreTransactions when extract is not empty`() =
        runTest {
            val field: Field = PixExtractPageViewModel::class.java.getDeclaredField("hasStarted")
            field.isAccessible = true
            field.set(viewModel, true)

            coEvery {
                getPixExtractUseCase(any())
            } returns resultExtractSuccess

            val states = viewModel.extractState.captureValues()

            viewModel.loadMoreTransactions()

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoadingMoreTransactions::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoadingMoreTransactions::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.Success::class.java)
        }

    @Test
    fun `verify loadMoreTransactions when extract is empty`() =
        runTest {
            val field: Field = PixExtractPageViewModel::class.java.getDeclaredField("hasStarted")
            field.isAccessible = true
            field.set(viewModel, true)

            coEvery {
                getPixExtractUseCase(any())
            } returns resultExtractEmptySuccess

            val states = viewModel.extractState.captureValues()

            viewModel.loadMoreTransactions()

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoadingMoreTransactions::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoadingMoreTransactions::class.java)
        }

    @Test
    fun `verify loadMoreTransactions when get extract is empty error`() =
        runTest {
            val field: Field = PixExtractPageViewModel::class.java.getDeclaredField("hasStarted")
            field.isAccessible = true
            field.set(viewModel, true)

            coEvery {
                getPixExtractUseCase(any())
            } returns resultEmpty

            val states = viewModel.extractState.captureValues()

            viewModel.loadMoreTransactions()

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoadingMoreTransactions::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoadingMoreTransactions::class.java)
        }

    @Test
    fun `verify loadMoreTransactions when get extract is error`() =
        runTest {
            val field: Field = PixExtractPageViewModel::class.java.getDeclaredField("hasStarted")
            field.isAccessible = true
            field.set(viewModel, true)

            coEvery {
                getPixExtractUseCase(any())
            } returns resultError

            val states = viewModel.extractState.captureValues()

            viewModel.loadMoreTransactions()

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoadingMoreTransactions::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoadingMoreTransactions::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.Error::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal true and _endList equal true or false and _transactions is empty or not empty and isSwipe equal false and receipts schedule is not empty and tab is NEW_SCHEDULES`() =
        runTest {
            coEvery {
                getPixReceiptsScheduledUseCase(any())
            } returns resultExtractSchedulingSuccess

            val states = viewModel.extractState.captureValues()

            viewModel.setTab(PixReceiptsTab.NEW_SCHEDULES)
            viewModel.loadTransactions(isOnResume = true, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.Success::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal true and _endList equal true or false and _transactions is empty or not empty and isSwipe equal false and receipts schedule is empty and tab is NEW_SCHEDULES`() =
        runTest {
            coEvery {
                getPixReceiptsScheduledUseCase(any())
            } returns resultExtractSchedulingEmptySuccess

            val states = viewModel.extractState.captureValues()

            viewModel.setTab(PixReceiptsTab.NEW_SCHEDULES)
            viewModel.loadTransactions(isOnResume = true, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.EmptyTransactions::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal true and _endList equal true or false and _transactions is empty or not empty and isSwipe equal false and get receipts schedule is empty error and tab is NEW_SCHEDULES`() =
        runTest {
            coEvery {
                getPixReceiptsScheduledUseCase(any())
            } returns resultEmpty

            val states = viewModel.extractState.captureValues()

            viewModel.setTab(PixReceiptsTab.NEW_SCHEDULES)
            viewModel.loadTransactions(isOnResume = true, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.Error::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal and _endList equal true or false and _transactions is empty or not empty true and isSwipe equal false and get receipts schedule is error and tab is NEW_SCHEDULES`() =
        runTest {
            coEvery {
                getPixReceiptsScheduledUseCase(any())
            } returns resultError

            val states = viewModel.extractState.captureValues()

            viewModel.setTab(PixReceiptsTab.NEW_SCHEDULES)
            viewModel.loadTransactions(isOnResume = true, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.Error::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal false and receipts schedule is not empty and tab is NEW_SCHEDULES`() =
        runTest {
            coEvery {
                getPixReceiptsScheduledUseCase(any())
            } returns resultExtractSchedulingSuccess

            val states = viewModel.extractState.captureValues()

            viewModel.setTab(PixReceiptsTab.NEW_SCHEDULES)
            viewModel.loadTransactions(isOnResume = false, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.Success::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal false and receipts schedule is empty and tab is NEW_SCHEDULES`() =
        runTest {
            coEvery {
                getPixReceiptsScheduledUseCase(any())
            } returns resultExtractSchedulingEmptySuccess

            val states = viewModel.extractState.captureValues()

            viewModel.setTab(PixReceiptsTab.NEW_SCHEDULES)
            viewModel.loadTransactions(isOnResume = false, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.EmptyTransactions::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal false and get receipts schedule is empty error and tab is NEW_SCHEDULES`() =
        runTest {
            coEvery {
                getPixReceiptsScheduledUseCase(any())
            } returns resultEmpty

            val states = viewModel.extractState.captureValues()

            viewModel.setTab(PixReceiptsTab.NEW_SCHEDULES)
            viewModel.loadTransactions(isOnResume = false, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.Error::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal false and get receipts schedule is error and tab is NEW_SCHEDULES`() =
        runTest {
            coEvery {
                getPixReceiptsScheduledUseCase(any())
            } returns resultError

            val states = viewModel.extractState.captureValues()

            viewModel.setTab(PixReceiptsTab.NEW_SCHEDULES)
            viewModel.loadTransactions(isOnResume = false, isSwipe = false)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.Error::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal true and receipts schedule is not empty and tab is NEW_SCHEDULES`() =
        runTest {
            coEvery {
                getPixReceiptsScheduledUseCase(any())
            } returns resultExtractSchedulingSuccess

            val states = viewModel.extractState.captureValues()

            viewModel.setTab(PixReceiptsTab.NEW_SCHEDULES)
            viewModel.loadTransactions(isOnResume = false, isSwipe = true)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.HideLoadingSwipe::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.Success::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal true and receipts schedule is empty and tab is NEW_SCHEDULES`() =
        runTest {
            coEvery {
                getPixReceiptsScheduledUseCase(any())
            } returns resultExtractSchedulingEmptySuccess

            val states = viewModel.extractState.captureValues()

            viewModel.setTab(PixReceiptsTab.NEW_SCHEDULES)
            viewModel.loadTransactions(isOnResume = false, isSwipe = true)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.HideLoadingSwipe::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.EmptyTransactions::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal true and get receipts schedule is empty error and tab is NEW_SCHEDULES`() =
        runTest {
            coEvery {
                getPixReceiptsScheduledUseCase(any())
            } returns resultEmpty

            val states = viewModel.extractState.captureValues()

            viewModel.setTab(PixReceiptsTab.NEW_SCHEDULES)
            viewModel.loadTransactions(isOnResume = false, isSwipe = true)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.HideLoadingSwipe::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.Error::class.java)
        }

    @Test
    fun `verify loadTransactions when onResume equal false and isSwipe equal true and get receipts schedule is error and tab is NEW_SCHEDULES`() =
        runTest {
            coEvery {
                getPixReceiptsScheduledUseCase(any())
            } returns resultError

            val states = viewModel.extractState.captureValues()

            viewModel.setTab(PixReceiptsTab.NEW_SCHEDULES)
            viewModel.loadTransactions(isOnResume = false, isSwipe = true)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.HideLoadingSwipe::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.Error::class.java)
        }

    @Test
    fun `verify loadMoreTransactions when receipts schedule is not empty and tab is NEW_SCHEDULES`() =
        runTest {
            val field: Field = PixExtractPageViewModel::class.java.getDeclaredField("hasStarted")
            field.isAccessible = true
            field.set(viewModel, true)

            coEvery {
                getPixReceiptsScheduledUseCase(any())
            } returns resultExtractSchedulingSuccess

            val states = viewModel.extractState.captureValues()

            viewModel.setTab(PixReceiptsTab.NEW_SCHEDULES)
            viewModel.loadMoreTransactions()

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoadingMoreTransactions::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoadingMoreTransactions::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.Success::class.java)
        }

    @Test
    fun `verify loadMoreTransactions when receipts schedule is empty and tab is NEW_SCHEDULES`() =
        runTest {
            val field: Field = PixExtractPageViewModel::class.java.getDeclaredField("hasStarted")
            field.isAccessible = true
            field.set(viewModel, true)

            coEvery {
                getPixReceiptsScheduledUseCase(any())
            } returns resultExtractSchedulingEmptySuccess

            val states = viewModel.extractState.captureValues()

            viewModel.setTab(PixReceiptsTab.NEW_SCHEDULES)
            viewModel.loadMoreTransactions()

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoadingMoreTransactions::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoadingMoreTransactions::class.java)
        }

    @Test
    fun `verify loadMoreTransactions when get receipts schedule is empty error and tab is NEW_SCHEDULES`() =
        runTest {
            val field: Field = PixExtractPageViewModel::class.java.getDeclaredField("hasStarted")
            field.isAccessible = true
            field.set(viewModel, true)

            coEvery {
                getPixReceiptsScheduledUseCase(any())
            } returns resultEmpty

            val states = viewModel.extractState.captureValues()

            viewModel.setTab(PixReceiptsTab.NEW_SCHEDULES)
            viewModel.loadMoreTransactions()

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoadingMoreTransactions::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoadingMoreTransactions::class.java)
        }

    @Test
    fun `verify loadMoreTransactions when get receipts schedule is error and tab is NEW_SCHEDULES`() =
        runTest {
            val field: Field = PixExtractPageViewModel::class.java.getDeclaredField("hasStarted")
            field.isAccessible = true
            field.set(viewModel, true)

            coEvery {
                getPixReceiptsScheduledUseCase(any())
            } returns resultError

            val states = viewModel.extractState.captureValues()

            viewModel.setTab(PixReceiptsTab.NEW_SCHEDULES)
            viewModel.loadMoreTransactions()

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assertThat(states[ZERO]).isInstanceOf(UIPixExtractPageState.ShowLoadingMoreTransactions::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPixExtractPageState.HideLoadingMoreTransactions::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPixExtractPageState.Error::class.java)
        }
}
