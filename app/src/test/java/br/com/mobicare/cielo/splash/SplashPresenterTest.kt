package br.com.mobicare.cielo.splash


import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.meuCadastroNovo.domain.Solution
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference
import br.com.mobicare.cielo.splash.data.managers.SplashRepository
import br.com.mobicare.cielo.splash.domain.entities.Configuration
import br.com.mobicare.cielo.splash.presentation.presenter.SplashPresenter
import br.com.mobicare.cielo.splash.presentation.ui.SplashContract
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class SplashPresenterTest {

    @Mock
    lateinit var view: SplashContract.View

    @Mock
    lateinit var repository: SplashRepository

    @Mock
    lateinit var preferences: ConfigurationPreference

    @Mock
    lateinit var featureTogglePreference: FeatureTogglePreference

    private lateinit var presenter: SplashPresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    private val configResponseJson = "[{\"key\":\"SOS_PHONE_TO_CALL\",\"value\":\"+5521987420150\"},{\"key\":\"NOT_A_CUSTOMER_URL\",\"value\":\"http://www.cielo.com.br/portal/cielo/seja-um-cliente-cielo.html\"}]"

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = spy(SplashPresenter(
            view,
            repository,
            uiScheduler,
            ioScheduler,
            preferences,
            featureTogglePreference
        )
        )
    }

    @Test
    fun `success configs request`() {
        val listConfigurationType = object : TypeToken<List<Configuration>>() {}.type
        val response: List<Configuration> = Gson().fromJson(configResponseJson, listConfigurationType)

        val successObservable = Observable.just(response)
        doReturn(successObservable).whenever(repository).getConfig()

        presenter.callAPI()

        verify(view).showProgress()
        verify(preferences).saveConfig(response)
        verify(view).hideProgress()
        verify(view, never()).showError(any())
        verify(presenter, never()).callNextActivity()
    }

    @Test
    fun `error config request`() {
        val exception = RetrofitException(
            message = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )
        val errorObservable = Observable.error<RetrofitException>(exception)
        doReturn(errorObservable).whenever(repository).getConfig()

        presenter.callAPI()

        verify(view).showProgress()
        verify(preferences, never()).saveConfig(any())
        verify(view).hideProgress()
        verify(view).showError(any())
        verify(presenter).callNextActivity()
    }

}