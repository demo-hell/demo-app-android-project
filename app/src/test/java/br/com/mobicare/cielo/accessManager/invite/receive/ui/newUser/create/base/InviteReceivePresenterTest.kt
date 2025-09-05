package br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.selfRegistration.register.SelfRegistrationRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class InviteReceivePresenterTest {
    private lateinit var view: InviteReceiveContract.View
    private lateinit var userCreateRepository: SelfRegistrationRepository
    private lateinit var accessManagerRepository: AccessManagerRepository
    private lateinit var uiScheduler: Scheduler
    private lateinit var ioScheduler: Scheduler
    private lateinit var presenter: InviteReceivePresenter

    @Before
    fun setup(){
        view = mockk(relaxed = true)
        userCreateRepository = mockk(relaxed = true)
        accessManagerRepository = mockk(relaxed = true)
        uiScheduler = Schedulers.trampoline()
        ioScheduler = Schedulers.trampoline()

        presenter = InviteReceivePresenter(
            accessManagerRepository,
            userCreateRepository,
            view,
            uiScheduler,
            ioScheduler
        )
    }

    @After
    fun teardown(){
        presenter.onPause()
    }

    @Test
    fun `acceptInviteToken should call onAcceptInviteTokenSuccess when API call succeeds`(){
        //Given
        val inviteToken = "inviteToken"

        every {
            accessManagerRepository.acceptInviteToken(inviteToken)
        } returns Observable.just(Response.success(null))

        //When
        presenter.acceptInviteToken(inviteToken)

        //Then
        verify { view.onAcceptInviteTokenSuccess() }
        verify (inverse = true) { view.onShowGenericError() }
    }

    @Test
    fun `acceptInviteToken should call onShowGenericError when API call fails`(){
        //Given
        val inviteToken = "inviteToken"
        val responseBody = EMPTY
            .toResponseBody("application/json".toMediaTypeOrNull())

        every { accessManagerRepository.acceptInviteToken(inviteToken)
        } returns Observable.just(Response.error( 500, responseBody))

        //When
        presenter.acceptInviteToken(inviteToken)

        //Then
        verify { view.onShowGenericError() }
    }

    @Test
    fun `declineInviteToken should call onDeclineInviteTokenSuccess when API call succeeds`(){
        //Given
        val inviteToken = "inviteToken"

        every {
            accessManagerRepository.declineInviteToken(inviteToken)
        } returns Observable.just(Response.success(null))

        //When
        presenter.declineInviteToken(inviteToken)

        //Then
        verify { view.onDeclineInviteTokenSuccess()}
        verify (inverse = true) { view.onShowGenericError()}
    }

    @Test
    fun `declineInviteToken should call onShowGenericError when API call fails` (){
        //Given
        val inviteToken = "inviteToken"
        val responseBody = EMPTY
            .toResponseBody("application/json".toMediaTypeOrNull())

        every {
            accessManagerRepository.declineInviteToken(inviteToken)
        } returns Observable.just(Response.error(500, responseBody))

        //When
        presenter.declineInviteToken(inviteToken)

        //Then
        verify { view.onShowGenericError() }
    }
}