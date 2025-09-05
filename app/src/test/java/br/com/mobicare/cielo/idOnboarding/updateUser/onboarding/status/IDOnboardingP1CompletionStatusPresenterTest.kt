package br.com.mobicare.cielo.idOnboarding.updateUser.onboarding.status

import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP1
import br.com.mobicare.cielo.idOnboarding.enum.IDOnboardingComeBackEnum
import br.com.mobicare.cielo.idOnboarding.model.IDOnboardingStatusResponse
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class IDOnboardingP1CompletionStatusPresenterTest {

    private val userStatus = IDOnboardingStatusResponse.UserStatus(
        policy1ValidationFlow = IDOnboardingStatusResponse.UserStatus.Policy1ValidationFlow(
            p1Validation = IDOnboardingStatusResponse.UserStatus.Policy1ValidationFlow.P1Validation(
                responseOn = "test",
                validated = true
            )
        )
    )

    private val statusResponse = IDOnboardingStatusResponse(userStatus = userStatus)

    private val userStatusBlocked = IDOnboardingStatusResponse.UserStatus(
        policy1ValidationFlow = IDOnboardingStatusResponse.UserStatus.Policy1ValidationFlow(
            p1Validation = IDOnboardingStatusResponse.UserStatus.Policy1ValidationFlow.P1Validation(
                responseOn = "test",
                validated = false
            )
        )
    )

    private val statusResponseBlocked = IDOnboardingStatusResponse(userStatus = userStatusBlocked)

    @Mock
    lateinit var view: IDOnboardingP1CompletionStatusContract.View

    private lateinit var presenter: IDOnboardingP1CompletionStatusPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = IDOnboardingP1CompletionStatusPresenter(
            view
        )
    }

    @Test
    fun `When the client has already started P1, it is not blocked and is not expired`() {
        val captor = argumentCaptor<IDOnboardingComeBackEnum>()
        presenter.onProcessP1CompletionStatus(
            IDOCheckpointP1.CPF_NAME_VALIDATED,
            30L,
            statusResponse
        )

        verify(view).onStarted(captor.capture())

        verify(view, never()).onBlocked(any())
        verify(view, never()).onDidNotStart(any())
        verify(view, never()).onStartedAndIsBlocked(any())
        verify(view, never()).onDidNotStartAndIsBlocked(any())
        verify(view, never()).onDidNotStartAndInTheLastDays(any())

        assertEquals(IDOnboardingComeBackEnum.HOME, captor.firstValue)
    }

    @Test
    fun `When the client has already started P1, it is not blocked and the deadline is expired`() {
        val captor = argumentCaptor<IDOnboardingComeBackEnum>()
        presenter.onProcessP1CompletionStatus(
            IDOCheckpointP1.CPF_NAME_VALIDATED,
            0L,
            statusResponse
        )

        verify(view).onStartedAndIsBlocked(captor.capture())

        verify(view, never()).onBlocked(any())
        verify(view, never()).onStarted(any())
        verify(view, never()).onDidNotStart(any())
        verify(view, never()).onDidNotStartAndIsBlocked(any())
        verify(view, never()).onDidNotStartAndInTheLastDays(any())

        assertEquals(IDOnboardingComeBackEnum.LOGOUT, captor.firstValue)
    }

    @Test
    fun `When the client has already started P1, it is blocked`() {
        val captor = argumentCaptor<IDOnboardingComeBackEnum>()
        presenter.onProcessP1CompletionStatus(
            IDOCheckpointP1.CPF_NAME_VALIDATED,
            20L,
            statusResponseBlocked
        )

        verify(view).onBlocked(captor.capture())

        verify(view, never()).onStarted(any())
        verify(view, never()).onDidNotStart(any())
        verify(view, never()).onStartedAndIsBlocked(any())
        verify(view, never()).onDidNotStartAndIsBlocked(any())
        verify(view, never()).onDidNotStartAndInTheLastDays(any())

        assertEquals(IDOnboardingComeBackEnum.BLOCKED, captor.firstValue)
    }

    @Test
    fun `When the client has not started P1, it is not locked and is not timed out`() {
        val captor = argumentCaptor<IDOnboardingComeBackEnum>()
        presenter.onProcessP1CompletionStatus(
            IDOCheckpointP1.CPF_NAME_VALIDATED,
            20L,
            statusResponseBlocked
        )

        verify(view).onBlocked(captor.capture())

        verify(view, never()).onStarted(any())
        verify(view, never()).onDidNotStart(any())
        verify(view, never()).onDidNotStartAndInTheLastDays(any())
        verify(view, never()).onDidNotStartAndIsBlocked(any())
        verify(view, never()).onDidNotStartAndInTheLastDays(any())

        assertEquals(IDOnboardingComeBackEnum.BLOCKED, captor.firstValue)
    }

    @Test
    fun `When the client has not started P1, it is not blocked and has more than 5 days to expire`() {
        val captor = argumentCaptor<IDOnboardingComeBackEnum>()
        presenter.onProcessP1CompletionStatus(
            IDOCheckpointP1.NONE,
            10L,
            statusResponse
        )

        verify(view).onDidNotStart(captor.capture())

        verify(view, never()).onStarted(any())
        verify(view, never()).onBlocked(any())
        verify(view, never()).onStartedAndIsBlocked(any())
        verify(view, never()).onDidNotStartAndIsBlocked(any())
        verify(view, never()).onDidNotStartAndInTheLastDays(any())

        assertEquals(IDOnboardingComeBackEnum.HOME, captor.firstValue)
    }

    @Test
    fun `When the client has not started P1, it is not blocked and has 5 days for the deadline to expire`() {
        val captor = argumentCaptor<IDOnboardingComeBackEnum>()
        presenter.onProcessP1CompletionStatus(
            IDOCheckpointP1.NONE,
            5L,
            statusResponse
        )

        verify(view).onDidNotStartAndInTheLastDays(captor.capture())

        verify(view, never()).onStarted(any())
        verify(view, never()).onBlocked(any())
        verify(view, never()).onDidNotStart(any())
        verify(view, never()).onStartedAndIsBlocked(any())
        verify(view, never()).onDidNotStartAndIsBlocked(any())

        assertEquals(IDOnboardingComeBackEnum.DIALOG, captor.firstValue)
    }

    @Test
    fun `When the client has not started P1, it is not locked and has less than 5 days to expire`() {
        val captor = argumentCaptor<IDOnboardingComeBackEnum>()
        presenter.onProcessP1CompletionStatus(
            IDOCheckpointP1.NONE,
            3L,
            statusResponse
        )

        verify(view).onDidNotStartAndInTheLastDays(captor.capture())

        verify(view, never()).onStarted(any())
        verify(view, never()).onBlocked(any())
        verify(view, never()).onDidNotStart(any())
        verify(view, never()).onStartedAndIsBlocked(any())
        verify(view, never()).onDidNotStartAndIsBlocked(any())

        assertEquals(IDOnboardingComeBackEnum.DIALOG, captor.firstValue)
    }

    @Test
    fun `When the client has not started P1, it is not blocked and the deadline has expired`() {
        val captor = argumentCaptor<IDOnboardingComeBackEnum>()
        presenter.onProcessP1CompletionStatus(
            IDOCheckpointP1.NONE,
            0L,
            statusResponse
        )

        verify(view).onDidNotStartAndIsBlocked(captor.capture())

        verify(view, never()).onStarted(any())
        verify(view, never()).onBlocked(any())
        verify(view, never()).onDidNotStart(any())
        verify(view, never()).onStartedAndIsBlocked(any())
        verify(view, never()).onDidNotStartAndInTheLastDays(any())

        assertEquals(IDOnboardingComeBackEnum.LOGOUT, captor.firstValue)
    }

    @Test
    fun `When the client has not started P1 and it is blocked`() {
        val captor = argumentCaptor<IDOnboardingComeBackEnum>()
        presenter.onProcessP1CompletionStatus(
            IDOCheckpointP1.NONE,
            0L,
            statusResponseBlocked
        )

        verify(view).onBlocked(captor.capture())

        verify(view, never()).onStarted(any())
        verify(view, never()).onDidNotStart(any())
        verify(view, never()).onDidNotStartAndIsBlocked(any())
        verify(view, never()).onStartedAndIsBlocked(any())
        verify(view, never()).onDidNotStartAndInTheLastDays(any())

        assertEquals(IDOnboardingComeBackEnum.BLOCKED, captor.firstValue)
    }
}