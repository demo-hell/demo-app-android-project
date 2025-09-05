package br.com.mobicare.cielo.accessManager.home

import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.accessManager.model.AccessManagerUser
import br.com.mobicare.cielo.accessManager.model.Profile
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.idOnboarding.model.IDOnboardingCustomerSettingsResponse
import br.com.cielo.libflue.util.EMPTY
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class AccessManagerHomePresenterTest {

    @Mock
    private lateinit var view: AccessManagerHomeContract.View
    @Mock
    private lateinit var userPreferences: UserPreferences
    @Mock
    private lateinit var featureTogglePreference: FeatureTogglePreference
    @Mock
    private lateinit var repository: AccessManagerRepository

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()
    private lateinit var presenter: AccessManagerHomePresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        presenter = AccessManagerHomePresenter(
            repository,
            view,
            uiScheduler,
            ioScheduler,
            userPreferences,
            featureTogglePreference
        )
    }

    @Test
    fun `Call getCustomUsers is when feature toggle and custom profile enabled is false`() {
        //Given
        val customProfileEnabled = false
        doReturn(false).whenever(featureTogglePreference)
            .getFeatureTogle(FeatureTogglePreference.PERFIL_PERSONALIZADO)

        // When
        presenter.getCustomUsers(customProfileEnabled)

        // Then
        verify(view, never()).showLoading()
        verify(view, never()).hideLoading()
        verify(view, never()).showCustomUsers(any())
        verify(view, never()).showError(any())
    }

    @Test
    fun `Call getCustomUsers is when feature toggle and custom profile enabled is true and call API success`() {
        //Given
        val successObservable = Observable.just(mockResponse())
        val customProfileEnabled = true
        doReturn(true).whenever(featureTogglePreference)
            .getFeatureTogle(FeatureTogglePreference.PERFIL_PERSONALIZADO)

        doReturn(successObservable).whenever(repository)
            .getCustomUsersWithRole(true, EMPTY)

        // When
        presenter.getCustomUsers(customProfileEnabled)

        // Then
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showCustomUsers(any())
        verify(view, never()).showError()
    }

    @Test
    fun `Call getCustomUsers is when feature toggle  and custom profile enabled is true and call API fails`() {
        //Given
        val customProfileEnabled = true
        val captor = argumentCaptor<ErrorMessage>()
        val exception = RetrofitException(message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500)

        val errorObservable = Observable.error<RetrofitException>(exception)

        doReturn(true).whenever(featureTogglePreference)
            .getFeatureTogle(FeatureTogglePreference.PERFIL_PERSONALIZADO)

        doReturn(errorObservable).whenever(repository)
            .getCustomUsersWithRole(true, EMPTY)

        // When
        presenter.getCustomUsers(customProfileEnabled)

        // Then
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view, never()).showCustomUsers(any())
        verify(view).showError(captor.capture())
    }

    @Test
    fun `Call getCustomerSettings is when call API success`() {
        //Given
        val successObservable = Observable.just(mockResponseSettings())

        doReturn(successObservable).whenever(repository)
            .getIdOnboardingCustomerSettings()

        // When
        presenter.getCustomerSettings()

        // Then
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).getCustomUsers(any())
        verify(view, never()).showError()
    }

    @Test
    fun `Call getCustomerSettings is when call API fails`() {
        //Given
        val captor = argumentCaptor<ErrorMessage>()
        val exception = RetrofitException(message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500)

        val errorObservable = Observable.error<RetrofitException>(exception)

        doReturn(errorObservable).whenever(repository)
            .getIdOnboardingCustomerSettings()

        // When
        presenter.getCustomerSettings()

        // Then
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view, never()).getCustomUsers(any())
        verify(view).showError(captor.capture())
    }

    private fun mockResponse(): List<AccessManagerUser> {
        return listOf(
            AccessManagerUser(
                id = "id",
                cpf = "cpf",
                name = "name",
                Profile(
                    id = "id",
                    name = "name",
                    roles = listOf("a,b,c"),
                    custom = false,
                    p2Eligible = true,
                    legacy = false,
                    admin = true
                ),
                inWhitelist = true,
                mainRole = "mainRole",
                status = "status",
                email = "email",
                cellphone = "cellphone",
                statusToken = "statusToken"
            )
        )
    }

    private fun mockResponseSettings(): IDOnboardingCustomerSettingsResponse {
        return IDOnboardingCustomerSettingsResponse(
            allowedEmailDomains = arrayOf("1","2","3"),
            passwordExpirationDays = EMPTY,
            allowedCellphoneValidationChannels = arrayOf("SMS","WHATSAPP"),
            foreignFlowAllowed = false,
            customProfileEnabled = true
            )
    }
}