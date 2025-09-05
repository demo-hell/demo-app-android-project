package br.com.mobicare.cielo.main.presentation.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.webkit.URLUtil
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.adicaoEc.presentation.ui.SHOW_IMPERSONATE_EC_BOTTOMSHEET
import br.com.mobicare.cielo.balcaoRecebiveis.fragment.BalcaoRecebiveisBottomSheet
import br.com.mobicare.cielo.changeEc.activity.MainImpersonateBottomSheetDialog
import br.com.mobicare.cielo.chat.presentation.utils.ChatApollo
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.HOME_LOGADA_SCREEN_VIEW
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_LGPD_ELEGIBLE
import br.com.mobicare.cielo.commons.constants.FIVE
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.helpers.BiometricHelper
import br.com.mobicare.cielo.commons.router.ActivityFragmentRouterAction
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkRouter
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.fragment.LockedProfileFragment
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.addInFrame
import br.com.mobicare.cielo.commons.utils.addWithAppearFromBottomAnimation
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.commons.utils.fingerprint.isAndroidVersionOorOMR1
import br.com.mobicare.cielo.commons.utils.fingerprint.isFingerprintSequentialID
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.showGenericSnackBar
import br.com.mobicare.cielo.commons.warning.PriorityWarningModal
import br.com.mobicare.cielo.commons.warning.WarningModalContract
import br.com.mobicare.cielo.databinding.ActivityMainBottomNavigationBinding
import br.com.mobicare.cielo.deeplink.model.DeepLinkModel
import br.com.mobicare.cielo.extrato.presentation.ui.fragments.ExtratoFragment
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleModal
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4
import br.com.mobicare.cielo.home.presentation.main.ui.fragment.HomeFragment
import br.com.mobicare.cielo.home.presentation.main.ui.fragment.OnboardingStatusListener
import br.com.mobicare.cielo.home.presentation.main.ui.fragment.OthersMenuFragment
import br.com.mobicare.cielo.home.presentation.main.ui.fragment.TurboRegistrationListener
import br.com.mobicare.cielo.idOnboarding.router.IDOnboardingRouter
import br.com.mobicare.cielo.internaluser.InternalUserActivity
import br.com.mobicare.cielo.lgpd.LgpdActivity
import br.com.mobicare.cielo.lgpd.domain.entity.LgpdElegibilityEntity
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.domain.MenuTarget
import br.com.mobicare.cielo.main.presentation.OnLogoutProceedCallback
import br.com.mobicare.cielo.main.presentation.createSessionTimeoutDialog
import br.com.mobicare.cielo.main.presentation.presenter.MainBottomNavigationPresenter
import br.com.mobicare.cielo.main.presentation.ui.MainBottomNavigationContract
import br.com.mobicare.cielo.main.presentation.ui.MainBottomNavigationFragmentListener
import br.com.mobicare.cielo.main.presentation.ui.fragments.ServicesFragment
import br.com.mobicare.cielo.main.presentation.util.MessageChannelMainBottomNavigationEnum
import br.com.mobicare.cielo.main.presentation.util.MessageChannelMainBottomNavigationEnum.UPDATE_APP_AFTER_IMPERSONATING
import br.com.mobicare.cielo.me.MeResponse
import br.com.mobicare.cielo.merchant.domain.entity.ResponseDebitoContaEligible
import br.com.mobicare.cielo.meusrecebimentosnew.calculationview.MeusRecebimentosFragmentNew
import br.com.mobicare.cielo.mfa.FluxoNavegacaoMfaActivity
import br.com.mobicare.cielo.mfa.MfaOtpErrorHandleActivity
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.OnbordingCancelSheetFragment
import br.com.mobicare.cielo.newLogin.NewLoginActivity
import br.com.mobicare.cielo.newLogin.clearDataByUserKeep
import br.com.mobicare.cielo.newLogin.onboardfirstaccess.OnBoardFirstAccessActivity
import br.com.mobicare.cielo.biometricNotification.ui.BiometricNotificationBottomSheetFragment
import br.com.mobicare.cielo.openFinance.presentation.OpenFinanceHolderMainActivity
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.login.firstAccess.presentation.ui.WithoutEstablishmentBottomSheet
import br.com.mobicare.cielo.openFinance.presentation.manager.newShare.conclusion.OpenFinanceFlowConclusionActivity
import br.com.mobicare.cielo.recebaMais.domain.Offer
import br.com.mobicare.cielo.recebaMais.presentation.ui.dialog.RecebaMaisBottomSheetFragment
import br.com.mobicare.cielo.recebaMais.presentation.ui.fragment.UserLoanFragment
import br.com.mobicare.cielo.recebaMais.presentation.ui.fragment.UserLoanFragment.Companion.RECEBA_MAIS_OFFER
import br.com.mobicare.cielo.research.ResearchContract
import br.com.mobicare.cielo.research.ResearchPresenter
import br.com.mobicare.cielo.research.domains.entities.ResearchResponse
import br.com.mobicare.cielo.turboRegistration.analytics.TurboRegistrationAnalytics
import br.com.mobicare.cielo.webView.presentation.WebViewContainerActivity
import br.com.mobicare.cielo.webView.utils.FLOW_NAME_PARAM
import br.com.mobicare.cielo.webView.utils.URL_PARAM
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdk
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val PASSWORD_EXTRA_PARAM = "PASSWORD_EXTRA_PARAM"
const val ERROR_CODE_OTP = "ERROR_CODE"
const val CAME_FROM_INTERNAL_USER_SCREEN = "CAME_FROM_INTERNAL_USER_SCREEN"
private const val RECEIVABLES_POSITION = ONE
private const val SALES_POSITION = TWO

class MainBottomNavigationActivity : BaseActivity(),
    BaseFragment.ConfigureToolbarActionListener,
    MainBottomNavigationContract.View,
    HomeFragment.OnOfferGetListener,
    ResearchContract.ResearchView,
    MainBottomNavigationFragmentListener,
    RecebaMaisBottomSheetFragment.CreditSimulationListener,
    OnBoardFirstAccessActivity.CallProcedeUserInformation,
    WarningModalContract.View,
    TurboRegistrationListener,
    MainBottomNavigationContract.Listener {

    private var mImpersonateBottomSheet: MainImpersonateBottomSheetDialog? = null
    private var cameFromInternalUserScreen = false
    private var isImpersonate = false
    private var mIDOnboardingRouter: IDOnboardingRouter? = null
    private val deeplinkRouter: DeeplinkRouter by inject()
    private val presenterResearch: ResearchPresenter by inject {
        parametersOf(this)
    }
    private val mainBottomNavigationPresenter: MainBottomNavigationPresenter by inject {
        parametersOf(this)
    }

    private var onboardingStatusListener: OnboardingStatusListener? = null

    private var userObj: UserObj? = null

    private var meusRecebimentosFragmentNew: BaseFragment = MeusRecebimentosFragmentNew()
    private var extratoFragment: BaseFragment = ExtratoFragment()

    private val servicesFragment = ServicesFragment().apply {
        this.configureToolbarActionListener = this@MainBottomNavigationActivity
    }

    private val othersMenuFragment = OthersMenuFragment().apply {
        this.configureToolbarActionListener = this@MainBottomNavigationActivity
    }

    private var hostedFragments: MutableList<BaseFragment> = emptyList<BaseFragment>().toMutableList()

    private val logoutBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            this@MainBottomNavigationActivity.logout()
        }
    }
    private val navigateToReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.run {
                val indexToNavigate = this.getIntExtra(HOME_INDEX_KEY, HOME_INDEX)
                loadBottomNavigationItem(indexToNavigate)
            }
        }
    }
    private val homeSessionExpiredBroadcastReceiver: BroadcastReceiver = object :
        BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            this@MainBottomNavigationActivity.createSessionTimeoutDialog(object :
                OnLogoutProceedCallback {
                override fun logout() {
                    this@MainBottomNavigationActivity.logout()
                }
            })
        }
    }
    private val homeHideSpecificCardBroadcastReceiver: BroadcastReceiver = object :
        BroadcastReceiver() {
        override fun onReceive(context: Context?, receivedIntent: Intent?) {
            val frameToHide = receivedIntent?.extras?.getInt(FRAME_ID_TO_HIDE)
            frameToHide?.run {
                val layoutToHide = findViewById<FrameLayout>(this)
                layoutToHide?.visibility = View.GONE
            }
        }
    }
    private val impersonateBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mainBottomNavigationPresenter.getUserInformation(true)
        }
    }
    private val errorMfaTokenReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.run {
                if (context !is MainBottomNavigationActivity) {
                    mfaOtpError(this.getStringExtra(ERROR_CODE_OTP) ?: EMPTY)
                }
            }
        }
    }

    private val binding: ActivityMainBottomNavigationBinding by lazy {
        ActivityMainBottomNavigationBinding.inflate(layoutInflater)
    }

    private val ga4: HomeGA4 by inject()

    private var receiverChannel: Job? = null

    private var updateToolbarHome: (() -> Unit)? = null
    private fun mfaOtpError(errorCode: String) {
        val intent = Intent(this, MfaOtpErrorHandleActivity::class.java).apply {
            putExtra(ERROR_CODE_OTP, errorCode)
        }
        startActivity(intent)
    }

    companion object {
        const val HOME_LOGOUT_ACTION = "br.com.cielo.actions.home.logout"
        const val HOME_SESSION_EXPIRED_ACTION = "br.com.cielo.actions.home.sessionExpiredAction"
        const val HOME_HIDE_CARDS = "br.com.cielo.actions.home.hideHomeCard"
        const val FRAME_ID_TO_HIDE = "br.com.cielo.actions.home.frameIdToHide"
        const val IMPERSONATE_USER_ACTION = "IMPERSONATE_USER_ACTION"
        const val NAVIGATE_TO_ACTION = "br.com.cielo.actions.home.navigateTo"
        const val HOME_INDEX_KEY = "br.com.cielo.actions.home.indexKey"
        const val HOME_INDEX = 0
        const val RECEBIVEIS_INDEX = 1
        const val VENDAS_INDEX = 2
        const val SERVICOS_INDEX = 3
        const val OUTROS_INDEX = 4
        const val CLOSE_OPEN_ACTIVITIES = "br.com.cielo.main.closeOpenActivitiesKey"
        private const val DELAY_ONE_SECOND = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        userObj = mainBottomNavigationPresenter.getUserObj()
        if (userObj?.isLockedProfile == true) {
            meusRecebimentosFragmentNew = LockedProfileFragment.newInstance(R.string.text_values_received_navigation_label, userObj?.mainRole ?: EMPTY)
            extratoFragment = LockedProfileFragment.newInstance(R.string.home_minhas_vendas_label, userObj?.mainRole ?: EMPTY)
        } else {
            val showReceivablesWeb = FeatureTogglePreference.instance.getFeatureTogle(FeatureTogglePreference.SHOW_RECEIVABLES_WEB)

            if (showReceivablesWeb.not()) meusRecebimentosFragmentNew = MeusRecebimentosFragmentNew()
            extratoFragment = ExtratoFragment()
        }

        hostedFragments.apply {
            add(HomeFragment().apply {
                this.mainBottomCreditSimulationListener = this@MainBottomNavigationActivity
                this.onOfferGetListener = this@MainBottomNavigationActivity
                onboardingStatusListener = this
                onSuccessTurboRegistration = this@MainBottomNavigationActivity
            })
            userObj?.doWhenRoleAvailable(roleName = UserObj.RoleControlResources.RECEIVABLES) {
                add(meusRecebimentosFragmentNew)
            }
            userObj?.doWhenRoleAvailable(roleName = UserObj.RoleControlResources.SALES) {
                add(extratoFragment)
            }
            add(servicesFragment)
            add(othersMenuFragment)
        }

        checkIfContainsADeeplinkId()
        setupActiveMerchant()
        setupBottomNavigation()
        setupHomeReceivers()
        loadIntentParams()
        setOnTabSelectedListener()
        registerReceiveChannel()
    }

    private fun checkIfContainsADeeplinkId() {
        mainBottomNavigationPresenter.checkDeeplink()
    }

    override fun startDeeplinkFlow(deepLinkModel: DeepLinkModel) {
        deeplinkRouter.startDeeplinkFlow(this, deepLinkModel)
    }

    override fun startMktDeeplink(mktDeeplinkUrl: String) {
        if (URLUtil.isValidUrl(mktDeeplinkUrl)) {
            Intent(Intent.ACTION_VIEW, Uri.parse(mktDeeplinkUrl)).let { intent ->
                val uri = Uri.parse(mktDeeplinkUrl)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(uri, contentResolver.getType(uri))
                startActivity(intent)
            }
        }
    }

    override fun startDeeplinkOpenFinance() {
        val intent = Intent(this, OpenFinanceHolderMainActivity::class.java)
        startActivity(intent)
    }

    override fun startDeeplinkConclusionShareOPF() {
        val intent = Intent(this, OpenFinanceFlowConclusionActivity::class.java)
        startActivity(intent)
    }

    private fun setupActiveMerchant() {
        UserPreferences.getInstance().userInformation?.activeMerchant?.let { merchant ->
            val establishmentName = merchant.tradingName
            val establishmentEC =
                if (merchant.id.isEmpty()) "" else getString(R.string.ec_value_x, merchant.id)
            setupToolbar(
                binding.toolbarHome as Toolbar,
                toolbarTitle = establishmentName
                    ?: getString(R.string.text_home_navigation_label),
                toolbarSubtitle = establishmentEC,
                withMainMenuEnabled = false
            )
        }
    }

    override fun showMfaOnboarding() {
        startActivity<FluxoNavegacaoMfaActivity>()
    }

    override fun showLGPD(elegible: LgpdElegibilityEntity) {
        startActivity<LgpdActivity>(
            ARG_PARAM_LGPD_ELEGIBLE to elegible
        )
    }

    override fun onStart() {
        super.onStart()
        mainBottomNavigationPresenter.onResume()
    }

    override fun onResume() {
        super.onResume()
        mIDOnboardingRouter?.onResume()
        mIDOnboardingRouter?.activity = this
        mainBottomNavigationPresenter.onResume()
        updateHostedFragments()
    }

    override fun onPause() {
        super.onPause()
        mIDOnboardingRouter?.onPause()
        mainBottomNavigationPresenter.onPause()
    }

    private fun setupHomeReceivers() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            logoutBroadcastReceiver,
            IntentFilter(HOME_LOGOUT_ACTION)
        )
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(
                homeSessionExpiredBroadcastReceiver,
                IntentFilter(HOME_SESSION_EXPIRED_ACTION)
            )
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(
                homeHideSpecificCardBroadcastReceiver,
                IntentFilter(HOME_HIDE_CARDS)
            )
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(
                impersonateBroadcastReceiver,
                IntentFilter(IMPERSONATE_USER_ACTION)
            )
        LocalBroadcastManager.getInstance(this).registerReceiver(
            navigateToReceiver,
            IntentFilter(NAVIGATE_TO_ACTION)
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
            errorMfaTokenReceiver,
            IntentFilter(BaseLoggedActivity.MFA_TOKEN_ERROR_ACTION)
        )
        mainBottomNavigationPresenter.getUserInformation()
    }

    private fun setupBottomNavigation() {
        binding.homeBottomNavigation.apply {
            titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
            accentColor = ContextCompat.getColor(this@MainBottomNavigationActivity, R.color.blue)
            inactiveColor = ContextCompat.getColor(this@MainBottomNavigationActivity, R.color.grey_8A99A8)
            addItem(
                AHBottomNavigationItem(R.string.text_home_navigation_label, R.drawable.ic_bottom_navigation_home, R.color.grey_8A99A8)
            )
            userObj?.doWhenRoleAvailable(roleName = UserObj.RoleControlResources.RECEIVABLES) {
                addItem(
                    AHBottomNavigationItem(
                        R.string.text_values_received_navigation_label,
                        R.drawable.ic_bottom_navigation_receipt,
                        R.color.grey_8A99A8
                    )
                )
            }
            userObj?.doWhenRoleAvailable(roleName = UserObj.RoleControlResources.SALES) {
                addItem(
                    AHBottomNavigationItem(R.string.text_sales_navigation_label, R.drawable.ic_bottom_navigation_sales, R.color.grey_8A99A8)
                )
            }
            addItem(
                AHBottomNavigationItem(R.string.text_services_navigation_label, R.drawable.ic_bottom_navigation_services, R.color.grey_8A99A8)
            )
            addItem(
                AHBottomNavigationItem(R.string.text_others_navigation_label, R.drawable.ic_bottom_navigation_more, R.color.grey_8A99A8)
            )
        }
    }

    fun showImpersonate() {
        if (binding.homeBottomNavigation.currentItem == 0) {
            if (cameFromInternalUserScreen) {
                startActivity<InternalUserActivity>()
                finish()
            } else {
                if (mImpersonateBottomSheet == null ||
                    mImpersonateBottomSheet?.isVisible == false
                ) {
                    showImpersonateBottomSheet()
                }
            }
        }
    }

    private fun showImpersonateBottomSheet() {
        if (mImpersonateBottomSheet?.isVisible == true) {
            dismissImpersonateBottomSheet()
            showImpersonateBottomSheet()
        } else {
            mImpersonateBottomSheet = MainImpersonateBottomSheetDialog()
            mImpersonateBottomSheet?.setUpdateToolbarHome {
                updateToolbarHome?.invoke()
            }
            mImpersonateBottomSheet?.setUpdateBottomNavigation(::updateHostedFragments)
            mImpersonateBottomSheet?.show(this.supportFragmentManager, MainImpersonateBottomSheetDialog::class.java.simpleName)
        }
    }

    private fun dismissImpersonateBottomSheet() {
        if (mImpersonateBottomSheet?.isVisible == true) {
            mImpersonateBottomSheet?.dismiss()
            mImpersonateBottomSheet = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterHomeReceivers()
        mainBottomNavigationPresenter.onDestroy()
        SessionExpiredHandler.sessionCalled = false
        dismissImpersonateBottomSheet()
        cancelReceiver()
    }

    private fun unregisterHomeReceivers() {
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(logoutBroadcastReceiver)
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(homeSessionExpiredBroadcastReceiver)
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(homeHideSpecificCardBroadcastReceiver)
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(impersonateBroadcastReceiver)
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(navigateToReceiver)
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(errorMfaTokenReceiver)
    }

    private fun loadIntentParams() {
        intent?.extras?.run {
            getByteArray(PASSWORD_EXTRA_PARAM)?.let {
                if (BiometricHelper.canAuthenticateWithBiometrics(applicationContext) && (isAndroidVersionOorOMR1() && isFingerprintSequentialID(
                        applicationContext
                    ).not()) || isAndroidVersionOorOMR1().not()
                ) {
                    Handler(mainLooper).postDelayed({
                        welcomeInfoNotification(it)
                    }, DELAY_ONE_SECOND)
                }
            }
            cameFromInternalUserScreen = getBoolean(CAME_FROM_INTERNAL_USER_SCREEN)
            Analytics.Update.updateUserType(isInternal = cameFromInternalUserScreen)

            if (cameFromInternalUserScreen) {
                LocalBroadcastManager.getInstance(this@MainBottomNavigationActivity)
                    .sendBroadcast(Intent(IMPERSONATE_USER_ACTION))
            }
            if (containsKey(SHOW_IMPERSONATE_EC_BOTTOMSHEET)) {
                if (getBoolean(SHOW_IMPERSONATE_EC_BOTTOMSHEET)) {
                    loadBottomNavigationItem(HOME_INDEX)
                    showImpersonateBottomSheet()
                } else {
                    dismissImpersonateBottomSheet()
                }
                remove(SHOW_IMPERSONATE_EC_BOTTOMSHEET)
            }
        }
    }

    override fun changeTo(
        colorResId: Int,
        statusBarColor: Int,
        title: String,
        subtitle: String,
        isHome: Boolean
    ) {
        if (statusBarColor != -1)
            changeStatusBarColor(statusBarColor)
        else
            changeStatusBarColor(R.color.colorPrimaryDark)
        binding.toolbarHome.configure(isHome, title, colorResId)
    }

    override fun onBackPressed() {
        showOptionDialogMessage(dialogTitle = getString(R.string.text_user_logout_info)) {
            this.setMessage(getString(R.string.text_logout_back_button_message))
            this.setBtnRight(getString(R.string.text_yes_label))
            this.setBtnLeft(getString(R.string.text_no_label))
            this.setOnclickListenerRight {
                logout()
            }
        }
    }

    override fun onUserInformationsResponse(userInformations: MeResponse?, isImpersonate: Boolean) {
        val title = userInformations?.merchant?.tradingName
        if (userInformations?.merchant?.id.isNullOrEmpty()){
            val fragmentWithoutEc = WithoutEstablishmentBottomSheet.create(this)
            addWithAppearFromBottomAnimation(fragmentWithoutEc)
        }

        userInformations?.merchant?.id?.let { ec_number ->
            mainBottomNavigationPresenter.sendTokenFCM(ec_number)
            presenterResearch.getResearch(ec_number)
            title?.let {
                updateToolbarTitle(title, getString(R.string.ec_value_x, ec_number))
            }
        } ?: kotlin.run { title?.let { updateToolbarTitle(title) } }
        logGaNewLogin(userInformations)
        verifyNeedsOnboarding(userInformations, isImpersonate)
        setMarketingCloudContactKey(userInformations)
    }

    private fun setMarketingCloudContactKey(userInformations: MeResponse?) {
        userInformations?.identity?.cpf?.let { cpf ->
            SFMCSdk.requestSdk { sdk ->
                sdk.identity.setProfileId(cpf)
            }
        }
    }

    override fun verifyNeedsOnboarding(userInformations: MeResponse?, isImpersonate: Boolean) {
        onboardingStatusListener?.onboardingStatusUpdated()
        if (isImpersonate && userInformations?.onboardingRequired == true) {
            showOnboardingRouter()
        }
        updateHostedFragments()
    }

    override fun showOnboardingRouter() {
        mIDOnboardingRouter = IDOnboardingRouter(
            activity = this,
            showLoadingCallback = {},
            hideLoadingCallback = {}
        ).showOnboarding()
    }

    private fun logGaNewLogin(meResponse: MeResponse?) {
        meResponse?.activeMerchant?.id?.apply {
            startSessionLogin()
            UserPreferences.getInstance().keepEC(this)
        }
        //provisório
        meResponse?.identity?.cpf?.let {
            UserPreferences.getInstance().currentUserLogged?.cpf = it
        }
    }

    private fun startSessionLogin() {
        if (isAttached()) {
            Analytics.trackScreenView(
                screenName = HOME_LOGADA_SCREEN_VIEW,
                screenClass = this.javaClass
            )
        }
        mainBottomNavigationPresenter.balcaoRecebiveisElegibility()
    }

    override fun callOnboardFirstAccess() {
        OnBoardFirstAccessActivity.create(this, this)
    }

    override fun getContext() = baseContext

    override fun loadBottomNavigationItem(itemIndex: Int) {
        if (isAttached()) {
            if (itemIndex == HOME_INDEX) {
                this@MainBottomNavigationActivity.hostedFragments[itemIndex] = HomeFragment().apply {
                    this.mainBottomCreditSimulationListener = this@MainBottomNavigationActivity
                    this.onOfferGetListener = this@MainBottomNavigationActivity
                    onboardingStatusListener = this
                    onSuccessTurboRegistration = this@MainBottomNavigationActivity
                }
            }
            hostedFragments[itemIndex].addInFrame(supportFragmentManager, R.id.frameMainContainer)
            binding.homeBottomNavigation.currentItem = itemIndex
        }
    }

    override fun showCancelOnboarding() {
        this.hideSoftKeyboard()
        val ftsucessBS = OnbordingCancelSheetFragment.newInstance()
        ftsucessBS.show(this.supportFragmentManager, "OnbordingCancelSheetFragment")
    }

    override fun showBannerBalcaoRecebiveis() {
        val isToggle =
            FeatureTogglePreference.instance.getFeatureTogle(FeatureTogglePreference.BALCAO_RECEBIVEIS)
        val isSharePreferences = UserPreferences.getInstance().isBannerStatusBalcaoRebevies
        if (isToggle && !isSharePreferences) {
            this.hideSoftKeyboard()
            val ftsucessBS = BalcaoRecebiveisBottomSheet.newInstance()
            ftsucessBS.show(supportFragmentManager, "BalcaoRecebiveisBottomSheet")
        }
    }

    private fun setOnTabSelectedListener() {
        binding.homeBottomNavigation.apply {
            setOnTabSelectedListener { position, _ ->
                val showReceivablesWeb =
                    FeatureTogglePreference.instance.getFeatureTogle(FeatureTogglePreference.SHOW_RECEIVABLES_WEB)
                val showSalesWeb =
                    FeatureTogglePreference.instance.getFeatureTogle(FeatureTogglePreference.SHOW_SALES_WEB)

                val intent = Intent(this@MainBottomNavigationActivity, WebViewContainerActivity::class.java)
                if (position == RECEIVABLES_POSITION && showReceivablesWeb) {
                    intent.putExtra(URL_PARAM, BuildConfig.RECEBIVEIS_URL)
                    intent.putExtra(FLOW_NAME_PARAM, "Recebíveis")
                    startActivity(intent)
                    false
                } else if (position == SALES_POSITION && showSalesWeb) {
                    intent.putExtra(URL_PARAM, BuildConfig.SALES_URL)
                    intent.putExtra(FLOW_NAME_PARAM, "Vendas")
                    startActivity(intent)
                    false
                } else {
                    gaMenuBottomNavigation(hostedFragments[position])
                    hostedFragments[position].addInFrame(supportFragmentManager, R.id.frameMainContainer)
                    true
                }
            }
            currentItem = ZERO
        }
    }

    private fun welcomeInfoNotification(fingerprintData: ByteArray) {
        if (UserPreferences.getInstance()
            .isShowBiometricNotification(BiometricHelper.canAuthenticateWithBiometrics(this))
            && UserPreferences.getInstance().isCalledBiometricNotificationByLogin
        ) {
            val fragment = BiometricNotificationBottomSheetFragment.create(fingerprintData)
            addWithAppearFromBottomAnimation(fragment)
        }
    }

    override fun onOfferGetSuccess(offer: Offer?, recebaMaisToken: String) {
        (Router.actions[Router.APP_ANDROID_RECEBA_MAIS] as ActivityFragmentRouterAction)
            .bundle = Bundle().apply {
            this.putParcelable(RECEBA_MAIS_OFFER, offer)
        }
    }

    override fun saveDataResearch(researchResponse: ResearchResponse?) {
        UserPreferences.getInstance().saveResearchData(researchResponse)
    }

    override fun callProcedeUserInformation() {
        mainBottomNavigationPresenter.procedeUserInformation()
    }

    fun logout() {
        UserPreferences.getInstance().clearDataByUserKeep()
        ChatApollo.logout()
        this.startActivity(Intent(this, NewLoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        finish()
    }

    private fun gaMenuBottomNavigation(bottomSelectedFragment: BaseFragment) {
    val fragmentName = bottomSelectedFragment::class.java.simpleName

    when (bottomSelectedFragment) {
        is HomeFragment -> gaSendMenuBottomItem(
            fragmentName,
            getString(R.string.text_home_navigation_label)
        )

        is MeusRecebimentosFragmentNew -> gaSendMenuBottomItem(
            fragmentName,
            getString(R.string.text_values_received_navigation_label)
        )

        is ExtratoFragment -> gaSendMenuBottomItem(
            fragmentName,
            getString(R.string.text_sales_navigation_label)
        )

        is ServicesFragment -> gaSendMenuBottomItem(
            fragmentName,
            getString(R.string.text_services_navigation_label)
        )

        is OthersMenuFragment -> gaSendMenuBottomItem(
            fragmentName,
            getString(R.string.text_others_navigation_label)
        )

        is LockedProfileFragment -> {
            when (bottomSelectedFragment.title) {
                R.string.text_values_received_navigation_label -> gaSendMenuBottomItem(
                    fragmentName,
                    getString(R.string.text_values_received_navigation_label)
                )

                R.string.text_sales_navigation_label -> gaSendMenuBottomItem(
                    fragmentName,
                    getString(R.string.text_sales_navigation_label)
                )
            }
        }
    }
}

private fun gaSendMenuBottomItem(nameFragment: String, status: String) {
    if (isAttached()) {
        ga4.logServiceFooterSelectContent(
            screenName = nameFragment,
            contentComponent = HomeGA4.MENU_FOOTER,
            contentName = status
        )
    }
}

    override fun onStartCreditSimulation(offer: Offer?) {
        (Router.actions[Router.APP_ANDROID_RECEBA_MAIS] as ActivityFragmentRouterAction)
            .bundle?.putBoolean(UserLoanFragment.RECEBA_MAIS_USER_FROM_BANNER, true)
        offer?.run {
            (Router.actions[Router.APP_ANDROID_RECEBA_MAIS] as ActivityFragmentRouterAction)
                .bundle?.putParcelable(UserLoanFragment.RECEBA_MAIS_OFFER, this)
        }
        Router.navigateTo(this, Menu(
            Router.APP_ANDROID_RECEBA_MAIS, "", listOf(),
            getString(R.string.text_receba_mais_title), false, "",
            listOf(), show = false, showItems = false, menuTarget = MenuTarget(
                false,
                type = "", mail = "", url = ""
            )
        ), object : Router.OnRouterActionListener {
            override fun actionNotFound(action: Menu) {
            }
        })
    }

    override fun bannerDebitoContaEligible() {
        mainBottomNavigationPresenter.debitoEmContaElegibility()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            this.intent.putExtras(it)
        }
        loadIntentParams()
    }

    override fun onShowWarningModal(modal: FeatureToggleModal, isImpersonate: Boolean) {
        this.isImpersonate = isImpersonate
        val warningModal = PriorityWarningModal.create(modal, this)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(warningModal, this.localClassName)
        transaction.commitAllowingStateLoss()
    }

    override fun onShowOtherWarning() {
        onLoadOtherInformation(isImpersonate)
    }

    override fun onLoadOtherInformation(isImpersonate: Boolean) {
        mainBottomNavigationPresenter.showCancelOnboard()

        if (isImpersonate) {
            loadBottomNavigationItem(HOME_INDEX)
            mainBottomNavigationPresenter.checkLgpd()
        } else {
            mainBottomNavigationPresenter.procedureAfterLgpd()
        }
    }

    override fun onLogout() {
        baseLogout()
    }

    fun setUpdateToolbarHome(value: () -> Unit) {
        updateToolbarHome = value
    }

    private fun updateMainBottomAfterImpersonating() {
        updateToolbarHome?.invoke()
        updateHostedFragments()
    }

    private fun updateHostedFragments() {
        userObj = mainBottomNavigationPresenter.getUserObj()
        if (userObj?.isLockedProfile == true) {
            meusRecebimentosFragmentNew = LockedProfileFragment.newInstance(R.string.text_values_received_navigation_label, userObj?.mainRole ?: EMPTY)
            extratoFragment = LockedProfileFragment.newInstance(R.string.home_minhas_vendas_label, userObj?.mainRole ?: EMPTY)
        } else {
           // meusRecebimentosFragmentNew = MeusRecebimentosFragmentNew()
            extratoFragment = ExtratoFragment()
        }

        hostedFragments.forEachIndexed { index, baseFragment ->
            when {
                (index == ONE && baseFragment is MeusRecebimentosFragmentNew
                        || index == ONE && baseFragment is LockedProfileFragment)
                        && userObj?.isRoleAvailable(roleName = UserObj.RoleControlResources.RECEIVABLES.roleName) == true -> {
                    hostedFragments[index] = meusRecebimentosFragmentNew
                }

                (index == TWO && baseFragment is ExtratoFragment
                        || index == TWO && baseFragment is LockedProfileFragment)
                        && userObj?.isRoleAvailable(roleName = UserObj.RoleControlResources.SALES.roleName) == true -> {
                    hostedFragments[index] = extratoFragment
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun registerReceiveChannel() {
        receiverChannel = ChannelMainBottomNavigationActivity.channel.receiveAsFlow()
            .onEach { message ->
                when (message) {
                    UPDATE_APP_AFTER_IMPERSONATING -> {
                        runOnUiThread {
                            updateMainBottomAfterImpersonating()
                        }
                    }
                }
            }
            .catch { cause -> cause.message.logFirebaseCrashlytics() }
            .launchIn(GlobalScope)
    }

    private fun cancelReceiver() {
        receiverChannel?.cancel()
    }

    object ChannelMainBottomNavigationActivity {
        val channel = Channel<MessageChannelMainBottomNavigationEnum>(Channel.BUFFERED)
    }

    override fun onSuccessTurboRegistration() {
        TurboRegistrationAnalytics.displayContentRegistrationFinishInAnalysis()
        showGenericSnackBar(binding.homeBottomNavigation, getString(R.string.warning_analysis_state), R.drawable.ic_warning_filled, FIVE)
    }

    override fun callLogout(){
        logout()
    }
}