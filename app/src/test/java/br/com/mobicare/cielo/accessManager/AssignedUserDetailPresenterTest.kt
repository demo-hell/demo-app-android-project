package br.com.mobicare.cielo.accessManager

import br.com.mobicare.cielo.accessManager.assignedUsers.details.AssignedUserDetailContract
import br.com.mobicare.cielo.accessManager.assignedUsers.details.AssignedUserDetailPresenter
import br.com.mobicare.cielo.accessManager.model.AccessManagerCustomProfileResponse
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.mainbottomnavigation.presenter.EMPTY
import br.com.mobicare.cielo.me.MeResponse
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class AssignedUserDetailPresenterTest {
    @Mock
    lateinit var view: AssignedUserDetailContract.View

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    private lateinit var featureTogglePreference: FeatureTogglePreference

    @Mock
    lateinit var meResponse: MeResponse

    @Mock
    private val response: List<AccessManagerCustomProfileResponse>?= mock()

    @Mock
    lateinit var accessManagerRepository: AccessManagerRepository

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()
    private lateinit var presenter: AssignedUserDetailPresenter
    private val custom = "CUSTOM"
    private val active = "ACTIVE"

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = AssignedUserDetailPresenter(
            accessManagerRepository,
            view,
            uiScheduler,
            ioScheduler,
            userPreferences,
            featureTogglePreference
        )
        whenever(userPreferences.userInformation).thenReturn(meResponse)
    }

    @Test
    fun `When assignRole is with animation, role is assigned`() {
        val responseBody = EMPTY
            .toResponseBody("application/json".toMediaTypeOrNull())
        val response = retrofit2.Response.success(200, responseBody)

        val returnSuccess = Observable.just(response)
        doReturn(returnSuccess).whenever(accessManagerRepository)
            .assignRole(listOf(userId), UserObj.ADMIN, OTP)

        presenter.assignRole(userId, UserObj.ADMIN, OTP)

        val captorError = argumentCaptor<ErrorMessage>()
        verify(view).onRoleAssigned()
        verify(view, never()).showError(captorError.capture())
    }

    @Test
    fun `When assignRole is without animation, role is assigned`() {
        val responseBody = EMPTY
            .toResponseBody("application/json".toMediaTypeOrNull())
        val response = retrofit2.Response.success(200, responseBody)

        val returnSuccess = Observable.just(response)
        doReturn(returnSuccess).whenever(accessManagerRepository)
            .assignRole(listOf(userId), UserObj.ADMIN, OTP)

        presenter.assignRole(userId, UserObj.ADMIN, OTP)

        val captorError = argumentCaptor<ErrorMessage>()
        verify(view).onRoleAssigned()
        verify(view, never()).showError(captorError.capture())
    }

    @Test
    fun `When assignRole is called with invalid data, showError is called`() {
        val errorMessage = ErrorMessage().apply {
            httpStatus = 420
        }
        doAnswer {
            observableException(errorMessage)
        }.whenever(accessManagerRepository)
            .assignRole(listOf(userId), UserObj.ADMIN, OTP)

        presenter.assignRole(userId, UserObj.ADMIN, OTP)

        val captorError = argumentCaptor<ErrorMessage>()
        verify(view, never()).onRoleAssigned()
        verify(view).showError(captorError.capture())
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

    @Test
    fun `Call getCustomActiveProfiles is when feature toggle is true and custom profile enabled is false`() {
        //Given
        val customProfileEnabled = false
        doReturn(true).whenever(featureTogglePreference)
            .getFeatureTogle(FeatureTogglePreference.PERFIL_PERSONALIZADO)

        // When
        presenter.getCustomActiveProfiles(customProfileEnabled)

        // Then
        verify(view, never()).showCustomProfiles(any())
        verify(view, never()).showErrorProfile()
    }

    @Test
    fun `Call getCustomActiveProfiles is when feature toggle and custom profile enabled is false`() {
        //Given
        val customProfileEnabled = false
        doReturn(false).whenever(featureTogglePreference)
            .getFeatureTogle(FeatureTogglePreference.PERFIL_PERSONALIZADO)

        // When
        presenter.getCustomActiveProfiles(customProfileEnabled)

        // Then
        verify(view, never()).showCustomProfiles(any())
        verify(view, never()).showErrorProfile()
    }

    @Test
    fun `Call getCustomUsers is when feature toggle and custom profile enabled is true and call API success`() {
        //Given
        val customProfileEnabled = true
        doReturn(true).whenever(featureTogglePreference)
            .getFeatureTogle(FeatureTogglePreference.PERFIL_PERSONALIZADO)

        val returnSuccess = Observable.just(response)
        doReturn(returnSuccess).whenever(accessManagerRepository)
            .getProfiles(custom, active)

        // When
        presenter.getCustomActiveProfiles(customProfileEnabled)

        // Then
        verify(view).showCustomProfiles(any())
        verify(view, never()).showErrorProfile()
    }

    @Test
    fun `Call getCustomActiveProfiles is when feature toggle and custom profile enabled is true and call API fails`() {
        //Given
        val customProfileEnabled = true
        doReturn(true).whenever(featureTogglePreference)
            .getFeatureTogle(FeatureTogglePreference.PERFIL_PERSONALIZADO)
        val errorMessage = ErrorMessage().apply {
            httpStatus = 420
        }
        doAnswer {
            observableException(errorMessage)
        }.whenever(accessManagerRepository)
            .getProfiles(custom, active)

        // When
        presenter.getCustomActiveProfiles(customProfileEnabled)

        // Then
        verify(view, never()).showCustomProfiles(any())
        verify(view).showErrorProfile()
    }
}