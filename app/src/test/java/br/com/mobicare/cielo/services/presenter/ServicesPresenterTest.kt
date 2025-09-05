package br.com.mobicare.cielo.services.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggle
import br.com.mobicare.cielo.main.MenuRepository
import br.com.mobicare.cielo.main.domain.AppMenuResponse
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.domain.MenuTarget
import br.com.mobicare.cielo.main.presentation.ServicesContract
import br.com.mobicare.cielo.main.presentation.presenter.APP_ANDROID_SERVICES
import br.com.mobicare.cielo.main.presentation.presenter.ServicesPresenter
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import java.util.Calendar

const val ACCESS_TOKEN_MOCK = "123456"
const val ICON_MOCK = "https://digitaldev.hdevelo.com.br/menu/static/assets/img/income.png"
const val NAME_MOCK = "Início"
const val APP_ANDROID_ARV = "APP_ANDROID_ARV"
const val NAME_ITEM_MOCK = "Antecipe suas Vendas"
const val ICON_ITEM_MOCK = "https://digitalhml.hdevelo.com.br/menu/static/assets/img/clock.png"
const val SHORT_ICON_ITEM_MOCK =
    "https://digitalhml.hdevelo.com.br/menu/static/assets/img/short_calendar_clock.png"

private val allFeatureToggles = listOf(
    FeatureToggle(
        featureName = FeatureTogglePreference.POSTECIPADO,
        show = true,
        status = "activated",
        statusMessage = null
    )
)

class ServicesPresenterTest {

    private val userPreferences: UserPreferences = mock(UserPreferences::class.java)
    private val servicesView = mock(ServicesContract.View::class.java)
    private val appMenuRepository = mock(MenuRepository::class.java)
    private val featureTogglePreference = mock(FeatureTogglePreference::class.java)
    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    lateinit var presenter: ServicesPresenter

    @Before
    fun setup() {
        presenter = ServicesPresenter(
            servicesView,
            uiScheduler,
            ioScheduler,
            appMenuRepository,
            userPreferences,
            featureTogglePreference
        )
    }

    @Test
    fun `When the return of the menu request is 500 it shows the error bottomSheet`() {

        whenever(userPreferences.token).thenReturn(ACCESS_TOKEN_MOCK)

        val exception = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )

        val errorObservable = Observable.error<RetrofitException>(exception)
        doReturn(errorObservable).whenever(appMenuRepository).getMenu(ACCESS_TOKEN_MOCK)

        presenter.getAvailableServices()

        verify(servicesView).showLoading()
        verify(servicesView).hideLoading()
        verify(servicesView).showError()

    }

    @Test
    fun `When the return of the menu request is 401 it logout`() {
        whenever(userPreferences.token).thenReturn(ACCESS_TOKEN_MOCK)

        val exception = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 401
        )

        val errorObservable = Observable.error<RetrofitException>(exception)
        doReturn(errorObservable).whenever(appMenuRepository).getMenu(ACCESS_TOKEN_MOCK)

        presenter.getAvailableServices()

        verify(servicesView).showLoading()
        verify(servicesView).hideLoading()
        verify(servicesView).logout()

    }

    @Test
    fun `When the return of the menu request is success it is sees list items`() {
        val listPrivileges: List<String> = listOf("", "")

        val listItem: List<Menu> =
            listOf(
                Menu(
                    code = APP_ANDROID_ARV,
                    icon = ICON_ITEM_MOCK,
                    items = null,
                    name = NAME_ITEM_MOCK,
                    showIcons = true,
                    shortIcon = SHORT_ICON_ITEM_MOCK,
                    privileges = listPrivileges,
                    show = true,
                    showItems = true,
                    menuTarget = MenuTarget(external = false, type = "", mail = "", url = "")
                )
            )

        val listMenu: List<Menu> = listOf(
            Menu(
                code = APP_ANDROID_SERVICES,
                icon = ICON_MOCK,
                items = listItem,
                name = NAME_MOCK,
                showIcons = true,
                shortIcon = null,
                privileges = listPrivileges,
                show = true,
                showItems = true,
                menuTarget = MenuTarget(external = false, type = "", mail = "", url = "")
            )
        )

        val long = Calendar.getInstance().timeInMillis
        val appMenuResponse = AppMenuResponse(long, listMenu)
        val returnSuccess = Observable.just(appMenuResponse)
        doReturn(allFeatureToggles).whenever(featureTogglePreference).getAllFeatureToggles()
        doReturn(ACCESS_TOKEN_MOCK).whenever(userPreferences).token
        doReturn(returnSuccess).whenever(appMenuRepository).getMenu(ACCESS_TOKEN_MOCK)

        presenter.getAvailableServices()

        verify(servicesView).showLoading()
        verify(servicesView).hideLoading()
        verify(servicesView).showAvailableServices(listMenu)
    }

}