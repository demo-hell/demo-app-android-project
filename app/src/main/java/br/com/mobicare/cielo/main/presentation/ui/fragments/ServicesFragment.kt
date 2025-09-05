package br.com.mobicare.cielo.main.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category.CIELO_SERVICES
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.router.ActivityFragmentRouterAction
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.update.UpdateAppBottomSheet
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.component.CieloActionServiceGridWidget
import br.com.mobicare.cielo.databinding.FragmentServicesBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.showAlertAllowMe
import br.com.mobicare.cielo.interactBannersOffersNew.utils.BannerControl
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannerTypes
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUtils
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.presentation.ServicesContract
import br.com.mobicare.cielo.main.presentation.analytics.ServicesGA4
import br.com.mobicare.cielo.main.presentation.presenter.ServicesPresenter
import br.com.mobicare.cielo.mfa.FluxoNavegacaoMfaActivity
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.recebaMais.presentation.ui.fragment.UserLoanFragment
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class ServicesFragment : BaseFragment(), ServicesContract.View, AllowMeContract.View {

    val presenter: ServicesPresenter by inject {
        parametersOf(this)
    }

    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }

    private val ga4: ServicesGA4 by inject()

    private var services: Menu? = null
    private var binding: FragmentServicesBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentServicesBinding.inflate(
        inflater, container, false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screenViewTag()
        setupBanners()
    }

    private fun screenViewTag() {
        Analytics.trackScreenView(
            screenName = CIELO_SERVICES,
            screenClass = javaClass
        )
        ga4.logScreenView()
    }

    private fun setupBanners() {

        doWhenResumed {
            InteractBannersUtils.launchInteractBanner(
                bannerType = InteractBannerTypes.LEADERBOARD,
                shouldGetOffersFromApi = false,
                frame = R.id.frameInteractLeaderboardBannersOffers,
                fragmentActivity = requireActivity(),
                bannerControl = BannerControl.LeaderboardServices,
                onSuccess =  {
                    doWhenResumed {
                        if (isAdded){
                            binding?.apply {
                                frameInteractLeaderboardBannersOffers.visible()
                            }
                        }
                    }
                }
            )
        }
    }

    private fun loadMenu() {
        presenter.getAvailableServices()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        configureToolbarActionListener?.changeTo(title = getString(R.string.text_services_navigation_label))
        loadMenu()
    }

    override fun onPause() {
        super.onPause()
        presenter.onDestroy()
    }

    override fun showLoading() {
        if (isAttached()) {
            binding?.apply {
                actionsWidgetView.gone()
                frameServicesProgress.root.visible()
            }
        }
    }

    override fun hideLoading() {
        if (isAttached())
            binding?.frameServicesProgress?.root?.gone()
    }

    override fun showAvailableServices(menuElements: List<Menu>) {
        visibleWidgetMenu()
        if (menuElements.isNotEmpty()) {
            binding?.actionsWidgetView?.apply {
                setMenu(menuElements.first())
                setOnItemListener(object :
                    CieloActionServiceGridWidget.OnItemListener {
                    override fun onItemSelected(item: Menu) {
                        when {
                            item.code == Router.APP_ANDROID_TAP_PHONE -> checkAllowMe(item)
                            item.code == Router.APP_ANDROID_RECEBA_MAIS &&
                                    Router.actions[Router.APP_ANDROID_RECEBA_MAIS]
                                            is ActivityFragmentRouterAction -> receiveMore(item)
                            item.code == Router.APP_ANDROID_MFA && Router.actions[Router.APP_ANDROID_MFA]
                                    is ActivityFragmentRouterAction -> mfaToken(item)
                            else -> router(item)
                        }

                        ga4.logServiceClick(contentName = item.name ?: EMPTY)
                        ga4.logServiceSelectContent(contentName = item.name ?: EMPTY)
                    }
                })
            }
        }
    }

    private fun checkAllowMe(service: Menu) {
        services = service
        allowMePresenter.collect(
            allowMePresenter.init(requireContext()),
            requireActivity(),
            mandatory = true
        )
    }

    override fun successCollectToken(result: String) {
        services?.let { service ->
            router(service)
        }
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        requireActivity().showAlertAllowMe(errorMessage)
    }

    override fun getSupportFragmentManagerInstance(): FragmentManager {
        return childFragmentManager
    }

    private fun receiveMore(route: Menu) {
        Router.navigateTo(
            context = requireContext(),
            route = route,
            params = (Router.actions[Router.APP_ANDROID_RECEBA_MAIS] as ActivityFragmentRouterAction).bundle?.apply {
                putBoolean(UserLoanFragment.RECEBA_MAIS_USER_FROM_BANNER, false)
            },
            actionListener = object : Router.OnRouterActionListener {
                override fun actionNotFound(action: Menu) {
                    UpdateAppBottomSheet.newInstance().show(childFragmentManager, tag)
                }
            })
    }

    private fun mfaToken(route: Menu) {
        Router.navigateTo(context = requireContext(),
            route = route, params =
            (Router.actions[Router.APP_ANDROID_MFA]
                    as ActivityFragmentRouterAction).bundle?.apply {
                this.putBoolean(FluxoNavegacaoMfaActivity.SERVICES_CALL_MFA, true)
            }, actionListener = object : Router.OnRouterActionListener {
                override fun actionNotFound(action: Menu) {
                    UpdateAppBottomSheet.newInstance().show(childFragmentManager, tag)
                }
            })
    }

    private fun router(route: Menu) {
        Router.navigateTo(context = requireContext(),
            route = route,
            actionListener = object : Router.OnRouterActionListener {
                override fun actionNotFound(action: Menu) {
                    UpdateAppBottomSheet.newInstance().show(childFragmentManager, tag)
                }
            })
    }

    private fun visibleWidgetMenu() {
        if (isAttached()) {
            binding?.apply {
                errorHandlerServicesMenu.gone()
                actionsWidgetView.visible()
            }
        }
    }

    override fun showError(error: ErrorMessage?) {
        if (isAttached()) {
            binding?.apply {
                errorHandlerServicesMenu.visible()
                errorHandlerServicesMenu.configureActionClickListener {
                    loadMenu()
                }
            }
        }
    }

    override fun logout(msg: ErrorMessage?) {
        if (isAttached()) {
            SessionExpiredHandler.userSessionExpires(requireContext())
        }
    }
}