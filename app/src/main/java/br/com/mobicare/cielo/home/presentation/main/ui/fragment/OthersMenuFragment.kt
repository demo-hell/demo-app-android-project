package br.com.mobicare.cielo.home.presentation.main.ui.fragment

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.router.Router.Companion.APP_ANDROID_ACCESS_MANAGEMENT
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.databinding.ItemOthersMenuBinding
import br.com.mobicare.cielo.databinding.LinearOthersMenuBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4.Companion.SCREEN_VIEW_OTHERS
import br.com.mobicare.cielo.home.presentation.main.OthersMenuContract
import br.com.mobicare.cielo.home.presentation.main.presenter.OthersMenuPresenter
import br.com.mobicare.cielo.interactBannersOffersNew.utils.BannerControl
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannerTypes
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUtils
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ADMIN
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.mfa.router.MfaRouteHandler
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class OthersMenuFragment : BaseFragment(), OthersMenuContract.View {

    var menuItems: List<Menu>? = null

    private val MFARequiredCodeList = listOf(APP_ANDROID_ACCESS_MANAGEMENT)

    private val mfaRouteHandler: MfaRouteHandler by inject {
        parametersOf(activity ?: requireActivity())
    }

    private val othersMenuPresenter: OthersMenuPresenter by inject {
        parametersOf(this)
    }

    private val ga4: HomeGA4 by inject()

    private var binding: LinearOthersMenuBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return LinearOthersMenuBinding.inflate(inflater, container, false)
            .also {
                binding = it
            }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        doWhenResumed {
            InteractBannersUtils.launchInteractBanner(
                bannerType = InteractBannerTypes.LEADERBOARD,
                shouldGetOffersFromApi = false,
                frame = R.id.frameInteractLeaderboardBannersOffers,
                fragmentActivity = requireActivity(),
                bannerControl = BannerControl.LeaderboardOthers,
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

        this.configureToolbarActionListener?.changeTo(
            title =
            getString(R.string.text_others_navigation_label)
        )

        configMfaRouteHandler()
    }

    override fun onResume() {
        super.onResume()
        othersMenuPresenter.onResume()
        othersMenuPresenter.getOthersMenu(UserPreferences.getInstance().token)
        logScreenView()
    }

    override fun onPause() {
        super.onPause()
        othersMenuPresenter.onDestroy()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun configMfaRouteHandler() {
        mfaRouteHandler.showLoadingCallback = { show ->
            if (show) {
                showLoading()
            } else
                hideLoading()
        }
    }

    override fun showOthersMenu(othersMenuResponse: List<Menu>) {
        if (isAttached()) {
            binding?.apply {
                linearLayoutContentMenu.removeAllViews()
                linearLayoutContentMenu.visible()

                menuItems = othersMenuResponse.first().items

                val isAccessManagerEnabled = FeatureTogglePreference.instance
                    .getFeatureTogle(FeatureTogglePreference.ACCESS_MANAGER)
                if (isAccessManagerEnabled.not() || MenuPreference.instance.getUserObj()?.mainRole != ADMIN) {
                    menuItems = menuItems?.filter {
                        it.code != APP_ANDROID_ACCESS_MANAGEMENT
                    }
                }

                menuItems?.let {
                    val lastPositon = it.size - 1
                    it.forEachIndexed { index, menuItem ->
                        val isLastPositon = index == lastPositon
                        val item = ItemOthersMenuBinding.inflate(LayoutInflater.from(context))
                        item.frameMenuOthersItemContent.let { itContainer ->
                            item.imageItemMenuIcon.let { itImageItemMenuIcon ->
                                item.textItemOthersMenuLabel.let { itTextItemOthersMenu ->
                                    item.imageArrowSubItems.let { itImageArrowSubItems ->
                                        itTextItemOthersMenu.text = SpannableStringBuilder
                                            .valueOf(menuItem.name)
                                        Picasso.get().load(menuItem.shortIcon)
                                            .resize(64, 64).into(itImageItemMenuIcon)
                                        if (!menuItem.menuTarget.external && index != lastPositon) {
                                            itImageArrowSubItems.visible()
                                        } else {
                                            itImageArrowSubItems.gone()
                                        }
                                        if (isLastPositon) item.viewLineSeparator.gone()

                                        val showNewTag =
                                            (menuItem.code == APP_ANDROID_ACCESS_MANAGEMENT && UserPreferences.getInstance().hasAccessManagerFirstView.not())
                                        item.tvNewTag.visible(showNewTag)
                                    }
                                }
                            }
                            itContainer.setOnClickListener {
                                if (menuItem.code in MFARequiredCodeList) {
                                    mfaRouteHandler.runWithMfaToken {
                                        navigate(menuItem)
                                    }
                                } else {
                                    navigate(menuItem)
                                }
                            }
                        }
                        linearLayoutContentMenu.addView(item.root)
                    }
                }
            }
        }
    }

    private fun navigate(menuItem: Menu) {
        Router.navigateTo(requireContext(), menuItem)
        gaSendMenuBottomItem(menuItem.name ?: EMPTY)
    }

    override fun showLoading() {
        doWhenResumed {
            binding?.apply {
                linearLayoutContentMenu.gone()
                errorHandlerOthersMenu.gone()
                frameMainProgress.root.visible()
            }
        }
    }

    override fun hideLoading() {
        doWhenResumed {
            binding?.apply {
                linearLayoutContentMenu.visible()
                errorHandlerOthersMenu.gone()
                frameMainProgress.root.gone()
            }
        }
    }

    override fun showError(error: ErrorMessage?) {
        doWhenResumed {
            binding?.apply {
                frameMainProgress.root.gone()
                linearLayoutContentMenu.gone()
                errorHandlerOthersMenu.visible()
            }

        }
    }

    override fun logout(msg: ErrorMessage?) {
        SessionExpiredHandler.userSessionExpires(requireContext())
    }

    private fun gaSendMenuBottomItem(status: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Label.OUTROS),
                action = listOf(Label.OPCOES),
                label = listOf(Label.ITEM, status)
            )
        }
    }

    private fun logScreenView() {
        if (isAttached()) {
            ga4.logScreenView(SCREEN_VIEW_OTHERS)
        }
    }
}