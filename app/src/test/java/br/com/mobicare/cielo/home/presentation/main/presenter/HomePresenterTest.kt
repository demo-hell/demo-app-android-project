package br.com.mobicare.cielo.home.presentation.main.presenter

import br.com.mobicare.cielo.commons.constants.EMPTY_STRING
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.POS_VIRTUAL_BUTTON_HOME
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.POS_VIRTUAL_WHITE_LIST
import br.com.mobicare.cielo.home.presentation.main.BannersContract
import br.com.mobicare.cielo.home.presentation.main.MenuContract
import br.com.mobicare.cielo.home.utils.HomeFactory
import br.com.mobicare.cielo.idOnboarding.IDOnboardingRepository
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.main.MenuRepository
import br.com.mobicare.cielo.main.UserInformationRepository
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.meusCartoes.PrepaidRepository
import br.com.mobicare.cielo.mfa.MfaRepository
import br.com.mobicare.cielo.migration.MigrationRepository
import br.com.mobicare.cielo.notification.NotificationRepository
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class HomePresenterTest {

    @Mock
    lateinit var homeView: BannersContract.View
    @Mock
    lateinit var menuView: MenuContract.View
    @Mock
    lateinit var prepaidRepository: PrepaidRepository
    @Mock
    lateinit var migrationRepository: MigrationRepository
    @Mock
    lateinit var repositoryNotification: NotificationRepository
    @Mock
    lateinit var menuRepository: MenuRepository
    @Mock
    lateinit var mfaRepository: MfaRepository
    @Mock
    lateinit var idRepository: IDOnboardingRepository
    @Mock
    lateinit var userInformationRepository: UserInformationRepository
    @Mock
    lateinit var featureTogglePreference: FeatureTogglePreference
    @Mock
    lateinit var userPreferences: UserPreferences
    @Mock
    lateinit var menuPreference: MenuPreference

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    private val appMenuResponse = HomeFactory.appMenuResponseWithTapAndPosOptions
    private val menuListCaptor = argumentCaptor<List<Menu>>()

    private lateinit var presenter: HomePresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        presenter = HomePresenter(
            homeView = homeView,
            menuView = menuView,
            prepaidRepository = prepaidRepository,
            migrationRepository = migrationRepository,
            repositoryNotification = repositoryNotification,
            menuRepository = menuRepository,
            mfaRepository = mfaRepository,
            idRepository = idRepository,
            userInformationRepository = userInformationRepository,
            featureTogglePreference = featureTogglePreference,
            userPreferences = userPreferences,
            menuPreference = menuPreference,
            uiScheduler = uiScheduler,
            ioScheduler = ioScheduler
        )

        doReturn(EMPTY_STRING).whenever(userPreferences).token
        doReturn(true).whenever(featureTogglePreference).getFeatureTogle(POS_VIRTUAL_BUTTON_HOME)
    }

    @Test
    fun `it should keep PosVirtual and remove TapOnPhone menu button when isPosVirtualWhiteList user preference is true`() {
        // given
        doReturn(Observable.just(appMenuResponse)).whenever(menuRepository).getMenu(any())
        doReturn(true).whenever(featureTogglePreference).getFeatureTogle(POS_VIRTUAL_WHITE_LIST)
        doReturn(true).whenever(userPreferences).isPosVirtualWhiteList
        doReturn(UserObj()).whenever(menuPreference).getUserObj()

        // when
        presenter.loadMenu()

        // then
        verify(homeView).showLoading()
        verify(homeView).hideLoading()
        verify(menuView).showMenu(menuListCaptor.capture())

        menuListCaptor.firstValue.let { menuList ->
            assertTrue(menuList.any { it.code == Router.APP_ANDROID_POS_VIRTUAL })
            assertTrue(menuList.none { it.code == Router.APP_ANDROID_TAP_PHONE })
        }
    }

    @Test
    fun `it should remove PosVirtual menu button when POS_VIRTUAL_BUTTON_HOME FT is false`() {
        // given
        doReturn(Observable.just(appMenuResponse)).whenever(menuRepository).getMenu(any())
        doReturn(false).whenever(featureTogglePreference).getFeatureTogle(POS_VIRTUAL_BUTTON_HOME)
        doReturn(true).whenever(featureTogglePreference).getFeatureTogle(POS_VIRTUAL_WHITE_LIST)
        doReturn(true).whenever(userPreferences).isPosVirtualWhiteList
        doReturn(UserObj()).whenever(menuPreference).getUserObj()

        // when
        presenter.loadMenu()

        // then
        verify(homeView).showLoading()
        verify(homeView).hideLoading()
        verify(menuView).showMenu(menuListCaptor.capture())

        assertTrue(menuListCaptor.firstValue.none { it.code == Router.APP_ANDROID_POS_VIRTUAL })
    }

    @Test
    fun `it should keep PosVirtual and remove TapOnPhone menu button when POS_VIRTUAL_WHITE_LIST FT and isPosVirtualWhiteList user preference are both false`() {
        // given
        doReturn(Observable.just(appMenuResponse)).whenever(menuRepository).getMenu(any())
        doReturn(false).whenever(featureTogglePreference).getFeatureTogle(POS_VIRTUAL_WHITE_LIST)
        doReturn(false).whenever(userPreferences).isPosVirtualWhiteList
        doReturn(UserObj()).whenever(menuPreference).getUserObj()

        // when
        presenter.loadMenu()

        // then
        verify(homeView).showLoading()
        verify(homeView).hideLoading()
        verify(menuView).showMenu(menuListCaptor.capture())

        menuListCaptor.firstValue.let { menuList ->
            assertTrue(menuList.any { it.code == Router.APP_ANDROID_POS_VIRTUAL })
            assertTrue(menuList.none { it.code == Router.APP_ANDROID_TAP_PHONE })
        }
    }

    @Test
    fun `it should remove PosVirtual and keep TapOnPhone menu button when POS_VIRTUAL_WHITE_LIST FT is true and isPosVirtualWhiteList user preference is false`() {
        // given
        doReturn(Observable.just(appMenuResponse)).whenever(menuRepository).getMenu(any())
        doReturn(true).whenever(featureTogglePreference).getFeatureTogle(POS_VIRTUAL_WHITE_LIST)
        doReturn(false).whenever(userPreferences).isPosVirtualWhiteList
        doReturn(UserObj()).whenever(menuPreference).getUserObj()

        // when
        presenter.loadMenu()

        // then
        verify(homeView).showLoading()
        verify(homeView).hideLoading()
        verify(menuView).showMenu(menuListCaptor.capture())

        menuListCaptor.firstValue.let { menuList ->
            assertTrue(menuList.none { it.code == Router.APP_ANDROID_POS_VIRTUAL })
            assertTrue(menuList.any { it.code == Router.APP_ANDROID_TAP_PHONE })
        }
    }
}