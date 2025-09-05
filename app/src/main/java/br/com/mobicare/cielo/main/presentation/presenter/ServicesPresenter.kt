package br.com.mobicare.cielo.main.presentation.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.MenuBase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.main.MenuRepository
import br.com.mobicare.cielo.main.presentation.ServicesContract
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

const val APP_ANDROID_SERVICES = "APP_ANDROID_SERVICES"

class ServicesPresenter(
    private val servicesView: ServicesContract.View,
    val uiScheduler: Scheduler,
    val ioScheduler: Scheduler,
    private val appMenuRepository: MenuRepository,
    private val userPreferences: UserPreferences,
    featureTogglePreference: FeatureTogglePreference
) : ServicesContract.Presenter, MenuBase(featureTogglePreference) {

    private var disposable = CompositeDisposable()

    override fun getAvailableServices() {
        disposable.add(
            appMenuRepository.getMenu(userPreferences.token)
                ?.observeOn(uiScheduler)
                ?.subscribeOn(ioScheduler)
                ?.doOnSubscribe {
                    servicesView.showLoading()
                }
                ?.subscribe({ menuResponse ->
                    servicesView.hideLoading()

                    menuResponse?.run {
                        processMenuResult(
                            servicesMenu = this.menu.filter { it.code == APP_ANDROID_SERVICES },
                            onProcessed = { menus ->
                                servicesView.showAvailableServices(menus)
                            }
                        )
                    } ?: servicesView.showError()
                }, { othersMenuError ->
                    servicesView.hideLoading()
                    val error = APIUtils.convertToErro(othersMenuError)
                    if (error.logout)
                        servicesView.logout()
                    else
                        servicesView.showError()
                })!!
        )
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onDestroy() {
        disposable.dispose()
    }
}