package br.com.mobicare.cielo.home.presentation.main.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity.Companion.NOT_CAME_FROM_HELP_CENTER
import br.com.mobicare.cielo.cieloFarol.data.model.response.CieloFarolResponse
import br.com.mobicare.cielo.cieloFarol.presentation.CieloFarolAdapter
import br.com.mobicare.cielo.cieloFarol.presentation.CieloFarolViewModel
import br.com.mobicare.cielo.cieloFarol.utils.analytics.CieloFarolAnalytics
import br.com.mobicare.cielo.cieloFarol.utils.uiState.FarolUiState.Empty
import br.com.mobicare.cielo.cieloFarol.utils.uiState.FarolUiState.Error
import br.com.mobicare.cielo.cieloFarol.utils.uiState.FarolUiState.Success
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.HOME_LOGADA_SCREEN_VIEW
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUBCATEGORY_NAME
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.URL_WEBSITE_CIELO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.helpers.AppHelper
import br.com.mobicare.cielo.commons.router.BUNDLE_TO_ROUTER
import br.com.mobicare.cielo.commons.router.FRAGMENT_TO_ROUTER
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.router.RouterFragmentInActivity
import br.com.mobicare.cielo.commons.router.TITLE_ROUTER_FRAGMENT
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.AppsFlyerConstants.AF_SCREEN_NAME
import br.com.mobicare.cielo.commons.utils.AppsFlyerConstants.AF_SCREEN_VIEW
import br.com.mobicare.cielo.commons.utils.AppsFlyerUtil
import br.com.mobicare.cielo.commons.utils.EVENT_LOGIN
import br.com.mobicare.cielo.commons.utils.SCREEN_CURRENT_PATH
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.addInFrame
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.commons.utils.initCountDownResearch
import br.com.mobicare.cielo.commons.utils.recycler.CircleIndicatorItemDecoration
import br.com.mobicare.cielo.commons.utils.remove
import br.com.mobicare.cielo.contactCielo.analytics.ContactCieloAnalytics.trackClickButtonOnBottomSheetContactCielo
import br.com.mobicare.cielo.contactCielo.analytics.ContactCieloAnalytics.trackWhatsappButtonClick
import br.com.mobicare.cielo.contactCielo.domain.ContactCieloViewModel
import br.com.mobicare.cielo.contactCielo.utils.BottomSheetContactCieloWhatsappList
import br.com.mobicare.cielo.databinding.HomeFragmentBinding
import br.com.mobicare.cielo.databinding.ItemVerifyStatusUserBinding
import br.com.mobicare.cielo.databinding.LayoutMfaEcStatusAlertBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.showAlertAllowMe
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.ACCESS_MANAGER
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.ID_ONBOARDING
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics.Companion.CHANGE_EC
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics.Companion.ERROR_ON_OPEN_HELP_CENTER_TAG
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics.Companion.HELP
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics.Companion.MODAL_RECEBA_MAIS_TAG
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics.Companion.NOTIFICATIONS
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics.Companion.PLANS_AND_FEES
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4.Companion.RECEIVABLES
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4.Companion.SALES
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4.Companion.SALES_AND_RECEIVABLES
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4.Companion.SCREEN_VIEW_HOME
import br.com.mobicare.cielo.home.presentation.arv.ui.customview.ArvCardAlertHomeFragment
import br.com.mobicare.cielo.home.presentation.main.BannersContract
import br.com.mobicare.cielo.home.presentation.main.HomeServiceContract
import br.com.mobicare.cielo.home.presentation.main.HomeStatusPrepago
import br.com.mobicare.cielo.home.presentation.main.MenuContract
import br.com.mobicare.cielo.home.presentation.main.presenter.FeeAndPlansHomePresenter
import br.com.mobicare.cielo.home.presentation.main.presenter.HomePresenter
import br.com.mobicare.cielo.home.presentation.main.ui.fragment.home.adapters.HomeFeeAndPlansAdapter
import br.com.mobicare.cielo.home.presentation.main.ui.fragment.home.adapters.HomeServicesAdapter
import br.com.mobicare.cielo.home.presentation.meusrecebimentonew.MeusRecebimentosHomeFragmentNew
import br.com.mobicare.cielo.home.presentation.postecipado.presentation.PostecipadoHomeSummaryFragment
import br.com.mobicare.cielo.home.presentation.produtos.ui.fragment.ProdutosHomeFragment
import br.com.mobicare.cielo.idOnboarding.updateUser.homeCard.IDOnboardingHomeCardStatusEnum
import br.com.mobicare.cielo.idOnboarding.updateUser.homeCard.IDOnboardingHomeCardStatusEnum.APPROVED_DOCUMENTS
import br.com.mobicare.cielo.idOnboarding.updateUser.homeCard.IDOnboardingHomeCardStatusEnum.DATA_ANALYSIS
import br.com.mobicare.cielo.idOnboarding.updateUser.homeCard.IDOnboardingHomeCardStatusEnum.NONE
import br.com.mobicare.cielo.interactBannersOffersNew.utils.BannerControl
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannerTypes.LEADERBOARD
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannerTypes.RECTANGLE
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersUtils.launchInteractBanner
import br.com.mobicare.cielo.lighthouse.ui.activities.LightHouseActivityConciliador
import br.com.mobicare.cielo.login.domain.UserStatusPrepago
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ADMIN
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.domain.MenuTarget
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.main.presentation.ui.MainBottomNavigationFragmentListener
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import br.com.mobicare.cielo.meuCadastroNovo.domain.Brand
import br.com.mobicare.cielo.meusCartoes.domains.entities.SituationType
import br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.MyCreditCardsFragment
import br.com.mobicare.cielo.mfa.merchantstatus.FluxoNavegacaoMerchantStatusMfaActivity
import br.com.mobicare.cielo.migration.presentation.ui.activity.MigrationActivity
import br.com.mobicare.cielo.minhasVendas.fragments.online.adapter.MinhasVendasRecebiveisPageAdapter
import br.com.mobicare.cielo.mySales.presentation.ui.MySalesHomeCardFragment
import br.com.mobicare.cielo.notification.list.ListNotificationActivity
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.recebaMais.domain.Offer
import br.com.mobicare.cielo.recebaMais.presentation.presenter.UserLoanContract
import br.com.mobicare.cielo.recebaMais.presentation.presenter.UserLoanPresenter
import br.com.mobicare.cielo.recebaMais.presentation.ui.dialog.RecebaMaisBottomSheetFragment
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import br.com.mobicare.cielo.turboRegistration.RegistrationUpdateNavigationFlowActivity
import br.com.mobicare.cielo.turboRegistration.analytics.TurboRegistrationAnalytics
import br.com.mobicare.cielo.turboRegistration.presentation.TurboRegistrationViewModel
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationResource
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationStepError
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

interface OnboardingStatusListener {
    fun onboardingStatusUpdated()
}

fun interface TurboRegistrationListener {
    fun onSuccessTurboRegistration()
}

class HomeFragment : BaseFragment(),
    BannersContract.View,
    HomeStatusPrepago,
    UserLoanContract.View,
    UserLoanContract.View.Banner,
    MenuContract.View,
    OnboardingStatusListener,
    HomeServiceContract.View,
    FeeAndPlansHomeContract.View,
    AllowMeContract.View {

    private var userVerifyStatusBinding: ItemVerifyStatusUserBinding? = null
    private var mfaAlertBinding: LayoutMfaEcStatusAlertBinding? = null
    private var binding: HomeFragmentBinding? = null
    private var homeFeeAndPlansAdapter: HomeFeeAndPlansAdapter? = null
    private var canLogEvents = false
    private val analytics: HomeAnalytics by inject()
    private val ga4: HomeGA4 by inject()
    private val cieloFarolAnalytics: CieloFarolAnalytics by inject()
    private var homeMenu: Menu? = null
    private val cieloFarolViewModel: CieloFarolViewModel by viewModel()
    private val turboRegistrationViewModel: TurboRegistrationViewModel by viewModel()
    private var isLockedProfile = false
    private var turboRegistrationActivityLauncher: ActivityResultLauncher<Intent>? = null
    var onSuccessTurboRegistration: TurboRegistrationListener? = null
    private var notificationQuantity: Int = ZERO

    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }

    private val homePresenter: HomePresenter by inject {
        parametersOf(this, this)
    }

    private val feeAndPlansPresenter: FeeAndPlansHomePresenter by inject {
        parametersOf(this)
    }

    private var mainBottomNavigationFragmentListener: MainBottomNavigationFragmentListener? = null
    var mainBottomCreditSimulationListener: RecebaMaisBottomSheetFragment.CreditSimulationListener? =
        null

    private val userLoanPresenter: UserLoanPresenter by inject {
        parametersOf(this, true)
    }

    var onOfferGetListener: OnOfferGetListener? = null

    private val userObj: UserObj? by lazy {
        homePresenter.getUserObj()
    }

    private val contactCieloViewModel: ContactCieloViewModel by sharedViewModel()

    interface OnOfferGetListener {
        fun onOfferGetSuccess(offer: Offer?, recebaMaisToken: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Analytics.trackScreenView(
            screenName = HOME_LOGADA_SCREEN_VIEW,
            screenClass = this.javaClass
        )
        turboRegistrationActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                onSuccessTurboRegistration?.onSuccessTurboRegistration()
                binding?.cardRequiresUpdate?.gone()
            }
        }
        return HomeFragmentBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
            mfaAlertBinding = LayoutMfaEcStatusAlertBinding.bind(it.root)
            userVerifyStatusBinding = binding?.userStatus
        }.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.let {
            if (it is MainBottomNavigationFragmentListener) {
                mainBottomNavigationFragmentListener = it
            }

            if (it is RecebaMaisBottomSheetFragment.CreditSimulationListener) {
                mainBottomCreditSimulationListener = it
            }

            if (it is OnOfferGetListener) {
                onOfferGetListener = it
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mainBottomNavigationFragmentListener = null
        mainBottomCreditSimulationListener = null
    }

    override fun onStart() {
        super.onStart()
        homePresenter.onResume()
        feeAndPlansPresenter.onResume()

        logScreenView()
        setupHeader()
        setupResearchCountdown()
        loadScreenData()
        hideBanner()
        verifyOnboardingStatus()
        verifyUserMigration()
        showSallesAndIncommingCards()
        showPostecipadoContainer()
        showArvAlertContainer()
    }

    private fun hideWhatsappButton() {
        binding?.whatsappContainer?.gone()
        binding?.btnWhatsapp?.gone()
    }

    private fun showWhatsappButton() {
        binding?.whatsappContainer?.visible()
        binding?.btnWhatsapp?.visible()
    }

    private fun logScreenView() {
        if (isAttached()) {
            analytics.logScreenView(HomeAnalytics.SCREEN_VIEW_HOME, this.javaClass)
            analytics.logHomeScreenViewAppsFlyer(requireContext())
        }
    }

    private fun setupFeeRecyclerView() {
        homeFeeAndPlansAdapter = HomeFeeAndPlansAdapter(requireContext(), arrayListOf(), true)

        binding?.rvFees?.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = homeFeeAndPlansAdapter
            addItemDecoration(CircleIndicatorItemDecoration(context))
        }
    }

    private fun setupListeners() {
        binding?.btFeeAndPlans?.setOnClickListener {
            Router.navigateTo(
                requireContext(),
                Menu(
                    Router.APP_ANDROID_RATES, EMPTY,
                    listOf(),
                    getString(R.string.txp_header),
                    false,
                    EMPTY,
                    listOf(),
                    show = false,
                    showItems = false,
                    menuTarget = MenuTarget()
                )
            )

            analytics.logScreenActions(
                actionName = PLANS_AND_FEES,
                labelName = HomeAnalytics.SEE_MORE_BRANDS
            )
        }

        binding?.helpContainer?.setOnClickListener {
            openHelpCenter()
            logHeaderEvents(HELP)
        }

        contactCieloViewModel.contactInfoSource.observe(viewLifecycleOwner) { contactsCieloWhatsappList ->
            if (contactsCieloWhatsappList.isNullOrEmpty()) {
                hideWhatsappButton()
            } else {
                showWhatsappButton()
                binding?.whatsappContainer?.setOnClickListener {
                    trackWhatsappButtonClick()
                    BottomSheetContactCieloWhatsappList.show(this@HomeFragment, contactsCieloWhatsappList) { contactCieloWhatsapp, _, _ ->
                        trackClickButtonOnBottomSheetContactCielo(contactCieloWhatsapp)
                        AppHelper.showWhatsAppMessage(requireActivity(), getString(contactCieloWhatsapp.whatsappNumber), EMPTY)
                    }
                }
            }
        }

        binding?.notificationContainer?.setOnClickListener {
            ga4.logBellAlertClick(notificationQuantity)
            openNotificationsList()
            logHeaderEvents(NOTIFICATIONS)
        }

        binding?.ecSwitchButton?.setOnClickListener {
            showImpersonate()
            logHeaderEvents(CHANGE_EC)
            ga4.logHomeEcSwitchButtonClick(this.javaClass)
        }

        binding?.switchECClickArea?.setOnClickListener {
            showImpersonate()
        }

        userVerifyStatusBinding?.btVsEnvio?.setOnClickListener {
            Intent(context, RouterFragmentInActivity::class.java).let {
                it.putExtra(
                    TITLE_ROUTER_FRAGMENT,
                    getString(R.string.global_account_choose_digital_button)
                )
                it.putExtra(FRAGMENT_TO_ROUTER, MyCreditCardsFragment::class.java.canonicalName)
                it.putExtra(BUNDLE_TO_ROUTER, Bundle().apply {
                    putString(SCREEN_CURRENT_PATH, Action.HOME_PATH)
                })
            }.let {
                requireContext().startActivity(it)
            }
        }

        binding?.viewPagerSales?.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == ZERO && canLogEvents.not()) return

                canLogEvents = true
                ga4.logServiceSelectContent(
                    contentName = SALES_AND_RECEIVABLES,
                    contentComponent = if (position == ZERO) SALES else RECEIVABLES
                )
            }
        })

        binding?.btCieloFarol?.setOnClickListener {
            cieloFarolAnalytics.logCieloFarolAccessButtonClick()

            requireActivity().startActivity<LightHouseActivityConciliador>()
        }

        binding?.includeLockedProfile?.btnAccessWebsite?.setOnClickListener {
            Utils.openBrowser(requireActivity(), URL_WEBSITE_CIELO)
        }
    }

    private fun logHeaderEvents(labelName: String) {
        analytics.logScreenActions(actionName = HomeAnalytics.HEADER, labelName = labelName)
    }

    private fun setupOffers() {
        if (MenuPreference.instance.showOffer()) {
            ProdutosHomeFragment().addInFrame(childFragmentManager, R.id.frameProdutos)
        } else {
            ProdutosHomeFragment().remove(childFragmentManager)
        }
    }

    private fun setupHeader() {
        MenuPreference.instance.let { menuPreference ->
            val establishmentName = getString(
                R.string.hello_user_header_home,
                menuPreference.getEstablishment()?.tradeName.orEmpty()
            )
            val establishmentEC = if (menuPreference.getEC()
                    .isNullOrEmpty()
            ) EMPTY else getString(R.string.ec_number_header_home, menuPreference.getEC())

            binding?.tvClientNameHeader?.text = establishmentName
            binding?.tvClientEcHeader?.text = establishmentEC
            configureToolbarActionListener?.changeTo(
                title = establishmentName ?: getString(R.string.text_home_navigation_label),
                subtitle = establishmentEC,
                isHome = true
            )
        }
        turboRegistrationViewModel.verifyUserNeedsToUpdateDocuments()
    }

    private fun setupResearchCountdown() {
        requireActivity().initCountDownResearch(
            requireContext(),
            childFragmentManager, Action.HOME_INICIO
        )
    }

    private fun loadScreenData() {
        feeAndPlansPresenter.getBrands()
        homePresenter.loadUserStatus()
        homePresenter.getMerchantMFAStatus()
        setupFarolObserver()
        registrationObserver()
        cieloFarolViewModel.getCieloFarol()
    }

    private fun showPostecipadoContainer() {
        doWhenResumed {
            getSupportFragmentManagerInstance().beginTransaction().apply {
                replace(R.id.postecipadoSummaryContainer, PostecipadoHomeSummaryFragment.newInstance())
                commit()
            }
        }
    }

    private fun showArvAlertContainer() {
        doWhenResumed {
            getSupportFragmentManagerInstance().beginTransaction().apply {
                replace(R.id.arvAlertContainer, ArvCardAlertHomeFragment.newInstance())
                commit()
            }
        }
    }

    @SuppressLint("NewApi")
    private fun hideBanner() {
        if (isAttached()) {
            userVerifyStatusBinding?.apply {
                pbVsProgress.visible()
                tvVsAtualizando.visible()
                btVsEnvio.gone()
                tvVsSubtitle.gone()
                tvVsTitle.gone()
                ivVs.gone()
                cvVs.setCardBackgroundColor(resources.getColor(R.color.color_ffffff_security))
                cvVs.visible()
            }
        }
    }

    override fun hideFeesAndPlans() {
        binding?.apply {
            tvTaxTitle.gone()
            rvFees.gone()
            btFeeAndPlans.gone()
            dividerView2.gone()
            clContainerTaxPerBrand.gone()
        }
    }

    private fun initInteractBanners() {
        homePresenter.checkFeatureToggleInteractBanner()
    }

    override fun showInteractBanner() {
        doWhenResumed {
            launchInteractBanner(
                bannerType = LEADERBOARD,
                frame = R.id.frameInteractLeaderboardBannersOffers,
                fragmentActivity = requireActivity(),
                bannerControl = BannerControl.LeaderboardHome,
                onSuccess = {
                    doWhenResumed {
                        if (isAdded) {
                            binding?.apply {
                                dividerView2.visible()
                                frameInteractLeaderboardBannersOffers.visible()

                                getRectangleBannerOffer()
                            }
                        }
                    }
                })
        }
    }

    private fun getRectangleBannerOffer() {
        doWhenResumed {
            launchInteractBanner(
                bannerType = RECTANGLE,
                frame = R.id.frameInteractRectangleBannersOffers,
                fragmentActivity = requireActivity(),
                shouldGetOffersFromApi = false,
                bannerControl = BannerControl.RectangleHome,
                onSuccess = {
                    doWhenResumed {
                        if (isAdded) {
                            binding?.apply {
                                dividerView2.visible()
                                frameInteractRectangleBannersOffers.visible()
                            }
                        }
                    }
                })
        }
    }

    private fun verifyOnboardingStatus() {
        try {
            val isIdOnboardingEnabled =
                FeatureTogglePreference.instance.getFeatureTogle(ID_ONBOARDING)

            if (isIdOnboardingEnabled) {
                MenuPreference.instance.getUserObj()?.run {
                    when {
                        mainRole == ADMIN && !onboardingRequired -> {
                            val isAccessManagerEnabled = FeatureTogglePreference.instance
                                .getFeatureTogle(ACCESS_MANAGER)

                            if (isAccessManagerEnabled && UserPreferences.getInstance().hasAccessManagerFirstView.not())
                                showIdOnboardingHomeStatusCard(APPROVED_DOCUMENTS)
                        }

                        onboardingRequired -> homePresenter.verifyOnboardingCardStatus()
                        else -> showIdOnboardingHomeStatusCard(NONE)
                    }
                }
            }
            homePresenter.checkProfileType()
        } catch (assertionError: java.lang.AssertionError) {
            UserPreferences.getInstance().deleteAccessManager()
        } catch (ex: Exception) {
            ex.message?.logFirebaseCrashlytics()
        }
    }

    private fun verifyUserMigration() {
        if (UserPreferences.getInstance().isBannerMigration.not()) {
            homePresenter.vericationIfUserMigration()
        }
    }

    override fun showSallesAndIncommingCards() {
        val featureTogglePreference = FeatureTogglePreference.instance
        val showCardMySales = featureTogglePreference.getFeatureToggleObject(FeatureTogglePreference.MINHAS_VENDAS)?.show ?: false
        val showCardMyReceivables = featureTogglePreference.getFeatureToggleObject(FeatureTogglePreference.MEUS_RECEBIMENTOS)?.show ?: false
        binding?.apply {
            if (isAttached()) {
                val titlesArray = resources.getStringArray(R.array.view_pager_sales_home).toList()
                val allowedFragments = mutableSetOf<Pair<Fragment, String>>().apply {
                    userObj?.doWhenRoleAvailable(roleName = UserObj.RoleControlResources.SALES) {
                        add(Pair(MySalesHomeCardFragment.newInstance(), titlesArray[ZERO]).takeIf { showCardMySales } ?: return@doWhenRoleAvailable)
                    }
                    userObj?.doWhenRoleAvailable(roleName = UserObj.RoleControlResources.RECEIVABLES) {
                        add(Pair(MeusRecebimentosHomeFragmentNew.newInstance(), titlesArray[ONE]).takeIf { showCardMyReceivables } ?: return@doWhenRoleAvailable)
                    }
                }

                if (allowedFragments.isEmpty()) {
                    containerSales.gone()
                    return
                }

                val adapter = viewPagerSales.adapter

                if (adapter == null) {
                    viewPagerSales.adapter = MinhasVendasRecebiveisPageAdapter(requireActivity(), allowedFragments)
                }

                TabLayoutMediator(tabLayoutSales, viewPagerSales) { tab, position ->
                    tab.text = allowedFragments.elementAt(position).second
                }.attach()
            }
        }
    }

    private fun showImpersonate() {
        (activity as? MainBottomNavigationActivity)?.showImpersonate()
    }

    private fun openNotificationsList() {
        this@HomeFragment.context?.startActivity<ListNotificationActivity>()
    }

    private fun openHelpCenter() {
        Router.navigateTo(requireContext(), Menu(
            Router.APP_ANDROID_HELP_CENTER, EMPTY, listOf(),
            getString(R.string.text_body_dirf_02), false, EMPTY,
            listOf(), show = false, showItems = false, menuTarget = MenuTarget(
                false,
                type = EMPTY, mail = EMPTY, url = EMPTY
            )
        ), object : Router.OnRouterActionListener {

            override fun actionNotFound(action: Menu) {
                FirebaseCrashlytics
                    .getInstance()
                    .recordException(Throwable(ERROR_ON_OPEN_HELP_CENTER_TAG))
            }
        })
    }

    override fun onboardingStatusUpdated() {
        verifyOnboardingStatus()
    }

    override fun showIdOnboardingHomeStatusCard(status: IDOnboardingHomeCardStatusEnum) {
        binding?.apply {
            if (status == NONE) {
                cardIdOnboardingStatus.setOnClickListener {}
                cardIdOnboardingStatus.gone()
            } else {
                MenuPreference.instance.getUserObj()?.mainRole?.let {
                    cardIdOnboardingStatus.mainRole = it
                }
                cardIdOnboardingStatus.status = status
                cardIdOnboardingStatus.visible()
                analytics.logScreenViewCard(status)
                ga4.statusCardID(status)

                cardIdOnboardingStatus.setOnClickListener {
                    analytics.logOnClickCardSendDocuments(status)

                    when (status) {
                        APPROVED_DOCUMENTS -> openOthersMenu()
                        DATA_ANALYSIS -> return@setOnClickListener
                        else -> mainBottomNavigationFragmentListener?.showOnboardingRouter()
                    }
                }
            }
        }

    }

    private fun openOthersMenu() {
        ga4.logIDHomeCardApprovedDocumentsSignUp()
        activity?.let {
            LocalBroadcastManager.getInstance(it)
                .sendBroadcast(
                    Intent(
                        MainBottomNavigationActivity
                            .NAVIGATE_TO_ACTION
                    ).apply {
                        this.putExtra(
                            MainBottomNavigationActivity.HOME_INDEX_KEY,
                            MainBottomNavigationActivity.OUTROS_INDEX
                        )
                    })
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotification()
        homePresenter.checkProfileType()
        homePresenter.loadMenu()
        initInteractBanners()
        ga4.logHomeScreenView(this.javaClass)
    }

    override fun onPause() {
        homePresenter.onDestroy()
        feeAndPlansPresenter.onDestroy()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        turboRegistrationActivityLauncher = null
    }

    @SuppressLint("NewApi", "ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactCieloViewModel.retrieveContactSourceInfo()
        if (FeatureTogglePreference.instance.getFeatureTogle(FeatureTogglePreference.RECEBA_MAIS)) {
            UserPreferences.getInstance().token.run {
                userLoanPresenter.listOffers(this)
            }
        }
        AppsFlyerUtil.send(
            requireContext(),
            event = AF_SCREEN_VIEW,
            obj = Pair(AF_SCREEN_NAME, SCREEN_VIEW_HOME)
        )
        setupFeeRecyclerView()
        setupListeners()
        setupOffers()
        initInteractBanners()
        setupMainBottomNavigationActivity()
    }

    private fun setupMainBottomNavigationActivity() {
        (activity as? MainBottomNavigationActivity)?.setUpdateToolbarHome(::setupHeader)
    }

    private fun setupNotificationAccessibility(notifications: Int) {
        binding?.notificationContainer?.contentDescription =
            getString(
                R.string.accessibility_button_description_pattern,
                getString(R.string.text_notification_title_list)
            ) +
                    getString(
                        R.string.notification_content_description,
                        resources.getQuantityString(
                            R.plurals.not_read_notifications_home,
                            notifications, notifications
                        )
                    )
    }

    override fun showFeePerBrand(brands: ArrayList<Brand>, isError: Boolean) {
        homeFeeAndPlansAdapter?.apply {
            updateInfo(brands, isError, false) {
                reload()
                feeAndPlansPresenter.getBrands()
            }
        }
    }

    override fun onServiceClick(homeService: Menu) {
        when (homeService.code) {
            Router.APP_ANDROID_TAP_PHONE, Router.APP_ANDROID_POS_VIRTUAL -> checkAllowMe(homeService)
            else -> Router.navigateTo(
                requireContext(),
                homeService
            )
        }
        ga4.logServiceSelectContent(contentName = homeService.name ?: EMPTY, contentComponent = HomeGA4.CONTENT_COMPONENT_MAIN_SERVICES)
    }

    private fun checkAllowMe(homeService: Menu) {
        homeMenu = homeService
        allowMePresenter.collect(
            allowMePresenter.init(requireContext()),
            requireActivity(),
            mandatory = true
        )
    }

    override fun successCollectToken(result: String) {
        homeMenu?.let { menu ->
            Router.navigateTo(
                requireContext(),
                menu
            )
        }
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        requireActivity().showAlertAllowMe(errorMessage)
    }

    override fun getSupportFragmentManagerInstance(): FragmentManager {
        return childFragmentManager
    }

    override fun showLoading() {
        binding?.progressFragmentsContainer?.visible()
    }

    override fun hideLoading() {
        binding?.progressFragmentsContainer?.gone()
    }

    override fun showMenu(menu: List<Menu>) {
        if (isAttached() && menu.isNotEmpty()) {
            binding?.containerHomeServices.visible()
            setupMainServices(mainServicesMenuMapping(menu))
        }
    }

    private fun mainServicesMenuMapping(menu: List<Menu>): List<Menu> {
        return menu.map {
            when (it.code) {
                Router.APP_ANDROID_PAYMENT_LINK -> it.copy(
                    name = getString(R.string.home_services_super_link)
                )

                Router.APP_ANDROID_POS_VIRTUAL -> it.copy(
                    name = getString(R.string.home_services_pos_virtual)
                )

                Router.APP_ANDROID_TAP_PHONE -> it.copy(
                    name = getString(R.string.home_services_tap_on_phone)
                )

                Router.APP_ANDROID_PIX -> it.copy(
                    name = getString(R.string.home_services_pix)
                )

                Router.APP_ANDROID_REFUNDS -> it.copy(
                    name = getString(R.string.home_services_refunds)
                )

                else -> it
            }
        }
    }

    private fun setupMainServices(mainServices: List<Menu>) {
        binding?.recyclerviewServicesHome?.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = HomeServicesAdapter(mainServices, this@HomeFragment)
        }
    }

    private fun loadNotification() {
        val isNotificationToggle =
            FeatureTogglePreference
                .instance
                .getFeatureTogle(FeatureTogglePreference.NOTIFICATION_BOX)
        if (isNotificationToggle) {
            homePresenter.loadNotification { quantity ->
                notificationQuantity = quantity
                ga4.logBellAlertShown(quantity)
                if (quantity > ZERO) {
                    binding?.textNotificationCount?.apply {
                        visible()
                        text = quantity.toString()
                    }
                }
                setupNotificationAccessibility(quantity)
            }
        }
    }

    override fun loadBannerMigration() {
        requireActivity().startActivity<MigrationActivity>()
    }

    override fun showLoadingMerchantStatusChallengeMFA() {
        mfaAlertBinding?.progressMFAStatusAlert.visible()
    }

    override fun hideLoadingMerchantStatusChallengeMFA() {
        mfaAlertBinding?.progressMFAStatusAlert.gone()
    }

    override fun showMerchantStatusChallengeMFA(merchantStatusMFA: String) {
        mfaAlertBinding?.apply {
            val text = SpannableString(
                HtmlCompat
                    .fromHtml(
                        getString(R.string.mfa_status_home_label),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
            )
            textViewStatusAlert.setText(text, TextView.BufferType.SPANNABLE)

            constraintLayoutMFAStatusAlert.setOnClickListener {
                val intent = Intent(context, FluxoNavegacaoMerchantStatusMfaActivity::class.java)
                intent.putExtra(
                    FluxoNavegacaoMerchantStatusMfaActivity
                        .MERCHANT_STATUS_MFA, merchantStatusMFA
                )
                startActivity(intent)
            }
            constraintLayoutMFAStatusAlert.visible()
        }
    }

    override fun showMerchantStatusPendingChallengeMFA() {
        mfaAlertBinding?.apply {
            textViewStatusAlert.text = getString(R.string.mfa_pending_status_home_label)
            constraintLayoutMFAStatusAlert.visible()
        }
    }

    override fun showMerchantStatusErroPennyDropAndNotEligibleChallengeMFA(message: Int) {
        mfaAlertBinding?.apply {
            val text = SpannableString(
                HtmlCompat
                    .fromHtml(
                        getString(message),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
            )

            textViewStatusAlert.setText(text, TextView.BufferType.SPANNABLE)
            constraintLayoutMFAStatusAlert.setOnClickListener {
                requireActivity().startActivity<CentralAjudaSubCategoriasEngineActivity>(
                    ConfigurationDef.TAG_KEY_HELP_CENTER to ConfigurationDef.TAG_HELP_CENTER_MFA,
                    ARG_PARAM_SUBCATEGORY_NAME to getString(R.string.text_token),
                    NOT_CAME_FROM_HELP_CENTER to true
                )
            }
            constraintLayoutMFAStatusAlert.visible()
        }
    }

    override fun hideMerchantStatusChallengeMFA() {
        mfaAlertBinding?.constraintLayoutMFAStatusAlert.gone()
    }

    @SuppressLint("ResourceAsColor")
    override fun loadCardPrepago(data: UserStatusPrepago?) {
        userVerifyStatusBinding?.apply {
            constraintUserPrepaidStatus.visible()
            if (isAdded && isVisible) {
                pbVsProgress.invisible()
                tvVsAtualizando.invisible()
                btVsEnvio.visible()
                tvVsSubtitle.visible()
                tvVsTitle.visible()
                ivVs.visible()
                cvVs.setCardBackgroundColor(resources.getColor(R.color.purple))

                data?.apply {
                    when {
                        (type == SituationType.LICENSE_AGREEMENT_OK.name) -> {
                            tvVsTitle.text = getString(R.string.ls_tv_vs_title)
                            tvVsSubtitle.text = getString(R.string.ls_tv_vs_subtitle)
                            btVsEnvio.setText(getString(R.string.ls_tv_vs_btn))

                            pbVsProgress.invisible()
                            tvVsAtualizando.invisible()
                            btVsEnvio.visible()
                            tvVsSubtitle.visible()
                            tvVsTitle.visible()
                            ivVs.visible()
                            cvVs.setCardBackgroundColor(resources.getColor(R.color.purple))
                        }

                        (type == SituationType.DOCUMENT_READ_PROBLEM.name) -> {
                            tvVsTitle.text = getString(R.string.dc_tv_vs_title)
                            tvVsSubtitle.text = getString(R.string.dc_tv_vs_subtitle)
                            btVsEnvio.setText(getString(R.string.dc_tv_vs_btn))

                            pbVsProgress.invisible()
                            tvVsAtualizando.invisible()
                            btVsEnvio.visible()
                            tvVsSubtitle.visible()
                            tvVsTitle.visible()
                            ivVs.visible()
                            cvVs.setCardBackgroundColor(resources.getColor(R.color.purple))
                        }

                        else -> {
                            cvVs.gone()
                        }
                    }
                }
            }
        }
    }

    override fun errorLoadStatus() {
        if (isAttached()) {
            userVerifyStatusBinding?.cvVs?.gone()
        }
    }

    private fun openRecebaMais(offer: Offer?, recebaMaisUserToken: String) {
        onOfferGetListener?.onOfferGetSuccess(offer, recebaMaisUserToken)

        offer?.run {
            if (!UserPreferences.getInstance().isRecebaMaisChecked) {
                val recebaMaisSF = RecebaMaisBottomSheetFragment
                    .newInstance(offer).apply {
                        this.creditSimulationListener = mainBottomCreditSimulationListener
                    }

                val transaction = childFragmentManager.beginTransaction()
                transaction.add(recebaMaisSF, MODAL_RECEBA_MAIS_TAG)
                transaction.commitAllowingStateLoss()
            }
        }
    }

    override fun availableOffersToShow(offer: Offer?) {
        if (isAttached()) {
            if (FeatureTogglePreference.instance
                    .getFeatureTogle(FeatureTogglePreference.RECEBA_MAIS)
            ) {
                openRecebaMais(
                    offer,
                    UserPreferences.getInstance().token
                )
            }
        }
    }

    private fun setupFarolObserver() {
        cieloFarolViewModel.farolUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Success -> checkIfShouldShowCieloFarol(state.data)
                is Error, Empty -> hideCieloFarol()
            }
        }
    }

    private fun registrationObserver() {
        turboRegistrationViewModel.eligibility.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegistrationResource.Success -> onEligibilityReceived(state.data)
                else -> binding?.cardRequiresUpdate.gone()
            }
        }
    }

    private fun onEligibilityReceived(eligible: Boolean) {
        binding?.cardRequiresUpdate?.visible(eligible)
        if (eligible) {
            val tripleTitleDescAnalytics: Triple<String, String, String> =
                when (UserPreferences.getInstance().turboRegistrationErrorStep) {
                    RegistrationStepError.MONTHLY_INCOME.ordinal -> if (UserPreferences.getInstance().isLegalEntity) {
                        Triple(
                            getString(R.string.step_error_monthly_invoice_title),
                            getString(R.string.step_error_monthly_invoice_description),
                            getString(R.string.step_error_monthly_invoice_title_analytics)
                        )
                    } else {
                        Triple(
                            getString(R.string.step_error_monthly_income_title),
                            getString(R.string.step_error_monthly_income_description),
                            getString(R.string.step_error_monthly_income_title_analytics)
                        )
                    }

                    RegistrationStepError.BUSINESS_SECTOR.ordinal -> Triple(
                        getString(R.string.step_error_business_sector_title),
                        getString(R.string.step_error_business_sector_description),
                        getString(R.string.step_error_business_sector_title_analytics)
                    )

                    RegistrationStepError.BANK.ordinal -> Triple(
                        getString(R.string.step_error_bank_title),
                        getString(R.string.step_error_bank_description),
                        getString(R.string.step_error_bank_title_analytics)
                    )

                    else -> Triple(
                        getString(R.string.finish_your_registration),
                        getString(R.string.message_registration_update),
                        getString(R.string.finish_your_registration_analytics)
                    )
                }
            TurboRegistrationAnalytics.displayContentCardHomeAlert(tripleTitleDescAnalytics.third)
            binding?.apply {
                tvTitle?.text = tripleTitleDescAnalytics.first
                tvMessage?.text = tripleTitleDescAnalytics.second
                btFinishRegistration?.setOnClickListener {
                    TurboRegistrationAnalytics.clickEventFinishRegistration(tripleTitleDescAnalytics.third)
                    val intent = Intent(requireContext(), RegistrationUpdateNavigationFlowActivity::class.java)
                    turboRegistrationActivityLauncher?.launch(intent)
                }
            }
        }
    }

    private fun checkIfShouldShowCieloFarol(farol: CieloFarolResponse) {
        if (farol.bestDayOfWeek.isNullOrEmpty() || farol.bestTime.isNullOrEmpty() || farol.averageTicketAmount.isNullOrEmpty() || farol.insightText.isNullOrEmpty()) {
            hideCieloFarol()
            return
        }

        showCieloFarol(farol)
    }

    private fun hideCieloFarol() {
        binding?.apply {
            containerCieloFarol.gone()
            dividerView3.gone()
        }
    }

    private fun showCieloFarol(farol: CieloFarolResponse) {
        binding?.apply {
            rvCieloFarol?.apply {
                val pillInfo = listOf(farol.bestDayOfWeek, farol.bestTime, farol.averageTicketAmount)

                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )

                rvCieloFarol.adapter = CieloFarolAdapter(pillInfo)

                if (onFlingListener == null) PagerSnapHelper().attachToRecyclerView(this)
                addItemDecoration(CircleIndicatorItemDecoration(context))
            }

            if (farol.insightText.isNullOrEmpty().not()) {
                tvCieloFarolDescription?.text = farol.insightText
                tvCieloFarolDescription?.visible()
            }

            containerCieloFarol?.visible()
        }
    }

    override fun showLockedProfileScreen(userObj: UserObj?) {
        binding?.apply {
            includeLockedProfile?.root?.visible(userObj?.isLockedProfile ?: false)
            customProfileIndicator?.root.visible(userObj?.isCustomRole ?: false)
            customProfileIndicator?.tvCustomProfileIndicatorDesc?.fromHtml(
                R.string.custom_profile_indicator_desc_text,
                userObj?.customRoleName ?: EMPTY
            )
            this@HomeFragment.isLockedProfile = userObj?.isLockedProfile == true
            binding?.linearMainHomeContent.visible(isVisible = isLockedProfile.not())
        }
    }
}