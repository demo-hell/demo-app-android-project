package br.com.mobicare.cielo.posVirtual.presentation.viewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class PosVirtualAccreditationOfferViewModelTest {
/*
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getPosVirtualAccreditationOffersUseCase =
        mockk<GetPosVirtualAccreditationOffersUseCase>()
    private val getAntiFraudSessionIDUseCase = mockk<GetAntiFraudSessionIDUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val context = mockk<Context>()

    private lateinit var viewModel: PosVirtualAccreditationOfferViewModel

    private val resultGetOffersSuccess =
        CieloDataResult.Success(PosVirtualFactory.OfferResponseFactory.offerResponse)
    private val resultGetOffersWithRequiredSuccess =
        CieloDataResult.Success(PosVirtualFactory.OfferResponseFactory.offerWithRequiredResponse)
    private val resultGetOffersEmptyWithRequiredSuccess =
        CieloDataResult.Success(PosVirtualFactory.OfferResponseFactory.offerEmptyWithRequiredResponse)
    private val resultUserObjSuccess = CieloDataResult.Success(UserObj())
    private val resultGetSessionIDSuccess = CieloDataResult.Success(PosVirtualFactory.sessionID)
    private val resultEmpty = CieloDataResult.Empty()
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultErrorCodeNotEligible = CieloDataResult.APIError(
        CieloAPIException(
            httpStatusCode = 420,
            actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
            newErrorMessage = NewErrorMessage(flagErrorCode = POS_VIRTUAL_ERROR_CODE_NOT_ELIGIBLE)
        )
    )
    private val resultErrorCodeActivityBranch = CieloDataResult.APIError(
        CieloAPIException(
            httpStatusCode = HTTP_UNKNOWN,
            actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
            newErrorMessage = NewErrorMessage(flagErrorCode = POS_VIRTUAL_ERROR_CODE_ACTIVITY_BRANCH)
        )
    )
    private val resultErrorCodeSuspectFraud = CieloDataResult.APIError(
        CieloAPIException(
            httpStatusCode = HTTP_UNKNOWN,
            actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
            newErrorMessage = NewErrorMessage(flagErrorCode = POS_VIRTUAL_ERROR_CODE_SUSPECT_FOR_FRAUD)
        )
    )

    @Before
    fun setup() {
        viewModel = PosVirtualAccreditationOfferViewModel(
            getPosVirtualAccreditationOffersUseCase,
            getUserObjUseCase,
            getAntiFraudSessionIDUseCase
        )

        coEvery { getUserObjUseCase() } returns resultUserObjSuccess

        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context
    }

    @Test
    fun `it should set offerState as Success`() =
        runTest {
            coEvery {
                getPosVirtualAccreditationOffersUseCase(any())
            } returns resultGetOffersSuccess

            coEvery {
                getAntiFraudSessionIDUseCase()
            } returns resultGetSessionIDSuccess

            val states = viewModel.offerState.captureValues()

            viewModel.resume()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPosVirtualAccreditationState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPosVirtualAccreditationState.Success::class.java)
        }

    @Test
    fun `it should set offerState as Success with offer not empty in cash`() =
        runTest {
            val field: Field =
                PosVirtualAccreditationOfferViewModel::class.java.getDeclaredField("_brandsTap")
            field.isAccessible = true
            field.set(viewModel, PosVirtualFactory.OfferResponseFactory.brandsTap)

            val states = viewModel.offerState.captureValues()

            viewModel.resume()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationState.HideLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPosVirtualAccreditationState.Success::class.java)
        }

    @Test
    fun `it should set offerState as OpenRequiredDataField`() =
        runTest {
            coEvery {
                getPosVirtualAccreditationOffersUseCase(any())
            } returns resultGetOffersEmptyWithRequiredSuccess

            coEvery {
                getAntiFraudSessionIDUseCase()
            } returns resultGetSessionIDSuccess

            val states = viewModel.offerState.captureValues()

            viewModel.resume()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPosVirtualAccreditationState.RequiredDataFieldError::class.java)
        }

    @Test
    fun `it should set offerState as GenericError when empty get offers and success get sessionID`() =
        runTest {

            coEvery {
                getPosVirtualAccreditationOffersUseCase(any())
            } returns resultEmpty

            coEvery {
                getAntiFraudSessionIDUseCase()
            } returns resultGetSessionIDSuccess

            val states = viewModel.offerState.captureValues()

            viewModel.resume()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPosVirtualAccreditationState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPosVirtualAccreditationState.GenericError::class.java)
        }

    @Test
    fun `it should set offerState as GenericError when error get offers and success get sessionID`() =
        runTest {

            coEvery {
                getPosVirtualAccreditationOffersUseCase(any())
            } returns resultError

            coEvery {
                getAntiFraudSessionIDUseCase()
            } returns resultGetSessionIDSuccess

            val states = viewModel.offerState.captureValues()

            viewModel.resume()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPosVirtualAccreditationState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPosVirtualAccreditationState.GenericError::class.java)
        }

    @Test
    fun `it should set offerState as UnavailableError when error get offers with errorCode=NOT_ELIGIBLE and success get sessionID`() =
        runTest {
            coEvery {
                getPosVirtualAccreditationOffersUseCase(any())
            } returns resultErrorCodeNotEligible

            coEvery {
                getAntiFraudSessionIDUseCase()
            } returns resultGetSessionIDSuccess

            val states = viewModel.offerState.captureValues()

            viewModel.resume()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPosVirtualAccreditationState.UnavailableError::class.java)
        }

    @Test
    fun `it should set offerState as UnavailableError when error get offers with errorCode=ACTIVITY_BRANCH and success get sessionID`() =
        runTest {
            coEvery {
                getPosVirtualAccreditationOffersUseCase(any())
            } returns resultErrorCodeActivityBranch

            coEvery {
                getAntiFraudSessionIDUseCase()
            } returns resultGetSessionIDSuccess

            val states = viewModel.offerState.captureValues()

            viewModel.resume()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPosVirtualAccreditationState.UnavailableError::class.java)
        }

    @Test
    fun `it should set offerState as SuspectError when error get offers with errorCode=SUSPECT_FOR_FRAUD and success get sessionID`() =
        runTest {
            coEvery {
                getPosVirtualAccreditationOffersUseCase(any())
            } returns resultErrorCodeSuspectFraud

            coEvery {
                getAntiFraudSessionIDUseCase()
            } returns resultGetSessionIDSuccess

            val states = viewModel.offerState.captureValues()

            viewModel.resume()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPosVirtualAccreditationState.SuspectError::class.java)
        }

    @Test
    fun `it should set offerState as AntiFraudError when succes get offers and empty get sessionID`() =
        runTest {
            coEvery {
                getPosVirtualAccreditationOffersUseCase(any())
            } returns resultGetOffersSuccess

            coEvery {
                getAntiFraudSessionIDUseCase()
            } returns resultEmpty

            val states = viewModel.offerState.captureValues()

            viewModel.resume()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPosVirtualAccreditationState.AntiFraudError::class.java)
        }

    @Test
    fun `it should set offerState as AntiFraudError when success get offers and error get sessionID`() =
        runTest {
            coEvery {
                getPosVirtualAccreditationOffersUseCase(any())
            } returns resultGetOffersSuccess

            coEvery {
                getAntiFraudSessionIDUseCase()
            } returns resultError

            val states = viewModel.offerState.captureValues()

            viewModel.resume()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPosVirtualAccreditationState.AntiFraudError::class.java)
        }

    @Test
    fun `it should set offerState as Success when success get offer with required not null`() =
        runTest {
            coEvery {
                getPosVirtualAccreditationOffersUseCase(any())
            } returns resultGetOffersWithRequiredSuccess

            coEvery {
                getAntiFraudSessionIDUseCase()
            } returns resultGetSessionIDSuccess

            val states = viewModel.offerState.captureValues()

            viewModel.resume()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPosVirtualAccreditationState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPosVirtualAccreditationState.Success::class.java)
        }

    @Test
    fun `it should set offerState as RequiredDataFieldError when success get offer with required not null`() =
        runTest {
            coEvery {
                getPosVirtualAccreditationOffersUseCase(any())
            } returns resultGetOffersEmptyWithRequiredSuccess

            coEvery {
                getAntiFraudSessionIDUseCase()
            } returns resultGetSessionIDSuccess

            val states = viewModel.offerState.captureValues()

            viewModel.resume()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPosVirtualAccreditationState.RequiredDataFieldError::class.java)
        }
*/
}