package br.com.mobicare.cielo.mainbottomnavigation.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domain.repository.PosVirtualWhiteListRepository
import br.com.mobicare.cielo.deeplink.model.DeepLinkModel
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.lgpd.domain.repository.LgpdRepository
import br.com.mobicare.cielo.main.MenuRepository
import br.com.mobicare.cielo.main.UserInformationRepository
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.main.presentation.presenter.ERROR_BUSINESS
import br.com.mobicare.cielo.main.presentation.presenter.MainBottomNavigationPresenter
import br.com.mobicare.cielo.main.presentation.presenter.SUCCESS
import br.com.mobicare.cielo.main.presentation.ui.MainBottomNavigationContract
import br.com.mobicare.cielo.merchant.data.MerchantRepositoryImpl
import br.com.mobicare.cielo.mfa.MfaRepository
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Response
import java.util.concurrent.Callable

const val EMPTY = ""
const val MKT_CLOUD_EXTERNAL_LINk = "http://cloud.comvc.stelo.com.br/unused"

class MainBottomNavigationPresenterTest {

    private val mainBottomNavigationView =
        Mockito.mock(MainBottomNavigationContract.View::class.java)
    private val userInformationRepository = Mockito.mock(UserInformationRepository::class.java)
    private val menuRepository = Mockito.mock(MenuRepository::class.java)
    private val mfaRepository = Mockito.mock(MfaRepository::class.java)
    private val lgpdRepository = Mockito.mock(LgpdRepository::class.java)
    private val merchantRepository = Mockito.mock(MerchantRepositoryImpl::class.java)
    private val userPreferences = Mockito.mock(UserPreferences::class.java)
    private val featureTogglePreference = Mockito.mock(FeatureTogglePreference::class.java)
    private val posVirtualWhiteListRepository =
        Mockito.mock(PosVirtualWhiteListRepository::class.java)
    private val menuPreference = Mockito.mock(MenuPreference::class.java)
    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    lateinit var presenter: MainBottomNavigationPresenter

    @Before
    fun setup() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { h: Callable<Scheduler?>? -> Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { h: Scheduler? -> Schedulers.trampoline() }

        presenter = MainBottomNavigationPresenter(
            mainBottomNavigationView,
            uiScheduler,
            ioScheduler,
            userInformationRepository,
            posVirtualWhiteListRepository,
            menuRepository,
            mfaRepository,
            lgpdRepository,
            merchantRepository,
            userPreferences,
            featureTogglePreference,
            menuPreference
        )
    }

    @Test
    fun `return error code 420 in response and call method errorPermissionRegister`() {
        val responseBody = EMPTY
            .toResponseBody("application/json".toMediaTypeOrNull())
        val response: Response<Void> = Response.error(ERROR_BUSINESS, responseBody)


        val returnSuccess = Observable.just(response)
        doReturn(returnSuccess).whenever(merchantRepository).sendPermisionRegister()

        presenter.sendPermisionRegister()

        verify(mainBottomNavigationView).errorPermissionRegister()

    }

    @Test
    fun `return code 204 in response and call method successPermissionRegister`() {
        val response: Response<String> = Response.success(SUCCESS, EMPTY)

        val returnSuccess = Observable.just(response)
        doReturn(returnSuccess).whenever(merchantRepository).sendPermisionRegister()

        presenter.sendPermisionRegister()

        verify(mainBottomNavigationView).sucessPermissionRegister()
    }

    @Test
    fun `return error code 420 in exception and call method errorPermissionRegister`() {
        val exception = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = ERROR_BUSINESS
        )

        val errorObservable = Observable.error<RetrofitException>(exception)
        doReturn(errorObservable).whenever(merchantRepository).sendPermisionRegister()

        presenter.sendPermisionRegister()

        verify(mainBottomNavigationView).errorPermissionRegister()

    }

    @Test
    fun `return error code 500 in exception and call method errorGeneric`() {
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
        doReturn(errorObservable).whenever(merchantRepository).sendPermisionRegister()

        presenter.sendPermisionRegister()

        verify(mainBottomNavigationView).errorGeneric(any())
    }

    @Test
    fun `call the startDeeplinkFlow method when it has a saved id`() {
        doReturn(
            DeepLinkModel(
                id = "2",
                params = hashMapOf()
            )
        ).whenever(userPreferences).deepLinkModel

        presenter.checkDeeplink()

        verify(mainBottomNavigationView).startDeeplinkFlow(any())
    }

    @Test
    fun `don't call the startDeeplinkFlow method when don't have id saved`() {
        doReturn(null).whenever(userPreferences).deepLinkModel

        presenter.checkDeeplink()

        verify(mainBottomNavigationView, never()).startDeeplinkFlow(any())
    }

}