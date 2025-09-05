package br.com.mobicare.cielo.home.presentation.main.presenter

import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.commons.utils.MenuBase
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.home.presentation.main.OthersMenuContract
import br.com.mobicare.cielo.main.data.managers.AppMenuRepository
import io.reactivex.rxkotlin.addTo

private const val APP_ANDROID_OTHERS = "APP_ANDROID_OTHERS"

class OthersMenuPresenter(
    private val othersMenuView: OthersMenuContract.View,
    private val appMenuRepository: AppMenuRepository,
    featureTogglePreference: FeatureTogglePreference
) : OthersMenuContract.Presenter, MenuBase(featureTogglePreference) {

    private val disposableHandler = CompositeDisposableHandler()

    override fun getOthersMenu(accessToken: String) {
        appMenuRepository.getMenu(accessToken)
            ?.configureIoAndMainThread()
            ?.doOnSubscribe { othersMenuView.showLoading() }
            ?.subscribe({ menuResponse ->
                othersMenuView.hideLoading()

                menuResponse?.run {
                    processMenuResult(
                        servicesMenu = this.menu.filter { it.code == APP_ANDROID_OTHERS },
                        onProcessed = { menus ->
                            othersMenuView.showOthersMenu(menus)
                        }
                    )
                }
            }, { othersMenuError ->
                othersMenuView.hideLoading()
                val error = APIUtils.convertToErro(othersMenuError)
                if (error.logout)
                    othersMenuView.logout(error)
                else
                    othersMenuView.showError(error)
            })?.addTo(disposableHandler.compositeDisposable)
    }

    override fun onResume() {
        disposableHandler.start()
    }

    override fun onDestroy() {
        disposableHandler.destroy()
    }
}