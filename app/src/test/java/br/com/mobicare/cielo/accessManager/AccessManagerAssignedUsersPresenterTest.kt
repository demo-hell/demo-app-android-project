package br.com.mobicare.cielo.accessManager

import br.com.mobicare.cielo.accessManager.assignedUsers.AccessManagerAssignedUsersContract
import br.com.mobicare.cielo.accessManager.assignedUsers.AccessManagerAssignedUsersPresenter
import br.com.mobicare.cielo.accessManager.model.AccessManagerUnlinkUserResponse
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.me.MeResponse
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

const val unlinkUserResponse =
    "{\n \"message\": \"Vínculo entre usuário e raiz CNPJ removido com sucesso\"\n}"
const val OTP = "0000"
const val userId = "1"

val reason = UnlinkUserReason.values().random()

class AccessManagerAssignedUsersPresenterTest {
    @Mock
    lateinit var view: AccessManagerAssignedUsersContract.View

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var meResponse: MeResponse

    @Mock
    lateinit var accessManagerRepository: AccessManagerRepository

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()
    private lateinit var presenter: AccessManagerAssignedUsersPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = AccessManagerAssignedUsersPresenter(
            accessManagerRepository,
            view,
            uiScheduler,
            ioScheduler,
            userPreferences
        )
        whenever(userPreferences.userInformation).thenReturn(meResponse)
    }

    @Test
    fun `When unlinkUser is called, user is unlinked from EC`() {
        val unlinkUserResponse =
            Gson().fromJson(unlinkUserResponse, AccessManagerUnlinkUserResponse::class.java)
        val successResponse = Observable.just(unlinkUserResponse)
        doReturn(successResponse).whenever(accessManagerRepository)
            .unlinkUser(userId, reason, OTP)

        presenter.unlinkUser(userId, reason, OTP)

        val captor = argumentCaptor<String>()
        val captorError = argumentCaptor<ErrorMessage>()
        verify(view).onUserUnlinked(captor.capture())
        verify(view, never()).showError(captorError.capture())
        Assert.assertSame(captor.firstValue, userId)
    }

    @Test
    fun `When unlinkUser is called with invalid data, showError is called`() {
        val errorMessage = ErrorMessage().apply {
            httpStatus = 420
        }
        doAnswer {
            observableException(errorMessage)
        }.whenever(accessManagerRepository)
            .unlinkUser(any(), any(), any())

        presenter.unlinkUser(userId, reason, OTP)

        val captorError = argumentCaptor<ErrorMessage>()
        verify(view).showError(captorError.capture())
    }

    @Test
    fun `When logged user is master, it can't be removed`() {
        whenever(userPreferences.userInformation?.id).thenReturn(userId)
        whenever(userPreferences.userInformation?.roles).thenReturn(
            listOf(
                UserObj.ADMIN,
                UserObj.MASTER
            )
        )

        val result = presenter.canUserBeRemoved(userId)

        Assert.assertFalse(result)
    }

    @Test
    fun `When logged user is admin but not master, it can be removed`() {
        whenever(userPreferences.userInformation?.id).thenReturn(userId)
        whenever(userPreferences.userInformation?.roles).thenReturn(listOf(UserObj.ADMIN))

        val result = presenter.canUserBeRemoved(userId)

        Assert.assertTrue(result)
    }

    private fun observableException(errorMessage: ErrorMessage): Observable<RetrofitException> {
        val exception = RetrofitException(
            null,
            null,
            APIUtils.createResponse(
                errorMessage
            ),
            RetrofitException.Kind.HTTP,
            null,
            null,
            errorMessage.httpStatus
        )

        return Observable.error(exception)
    }
}