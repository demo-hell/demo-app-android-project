package br.com.mobicare.cielo.idOnboarding

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.IDOnboarding.ARG_PARAM_IS_LOGIN_ID
import br.com.mobicare.cielo.commons.constants.IDOnboarding.ARG_PARAM_TYPE_SEND_IMAGES
import br.com.mobicare.cielo.commons.constants.IDOnboarding.ARG_PARAM_SHOW_WARNING
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.databinding.ActivityIdOnboardingNavigationBinding
import br.com.mobicare.cielo.databinding.ToolbarMainBinding
import br.com.mobicare.cielo.extensions.*
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointUserCnpj
import br.com.mobicare.cielo.idOnboarding.model.IDOnboardingStatusResponse
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ADMIN
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.CUSTOM
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.TECHNICAL
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.pix.constants.EMPTY
import java.lang.Exception
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP2 as P2

class IDOnboardingNavigationActivity : BaseLoggedActivity(), CieloNavigation {

    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null
    private var isShowHelpMenu: Boolean = true
    private var navController: NavController? = null
    private lateinit var binding: ActivityIdOnboardingNavigationBinding
    private var bottomSheetLoading: BottomSheetFluiGenericFragment? = null

    private val isShowWarning: Boolean by lazy {
        intent?.extras?.getBoolean(ARG_PARAM_SHOW_WARNING) ?: true
    }

    private val isLogin: Boolean by lazy {
        intent?.extras?.getBoolean(ARG_PARAM_IS_LOGIN_ID) ?: false
    }

    private val typeSendDocs: Int by lazy {
        intent?.extras?.getInt(ARG_PARAM_TYPE_SEND_IMAGES) ?: 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdOnboardingNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savedInstanceState?.let {
            this.bundle = it
            it.getParcelable<IDOnboardingStatusResponse>(USER_STATUS)?.let { itStatus ->
                userStatus.onboardingStatus = itStatus
            }
        }

        IDOnboardingFlowHandler.isShowWarning = isShowWarning
        IDOnboardingFlowHandler.isLogin = isLogin

        binding.apply {
            val toolbar: ToolbarMainBinding = toolbar
            setupToolbar(toolbar.toolbarMain, EMPTY)
            setupNavigation()
        }
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController?.popBackStack()

        val navActionId: Int = when (typeSendDocs) {
            ONE -> R.id.action_to_idOnboardingPicturesSelfieGuideFragment
            TWO -> R.id.action_to_idOnboardingValidateP2PolicyFragment
            else -> when (IDOnboardingFlowHandler.checkpointUserCnpj) {
                IDOCheckpointUserCnpj.NONE -> p1Destination()
                IDOCheckpointUserCnpj.P1_VALIDATED -> {
                    showUserWithoutRole()
                    when (userStatus.onboardingStatus?.role) {
                        ADMIN -> R.id.action_to_idOnboardingAdminBenefitsFragment
                        TECHNICAL -> R.id.action_to_idOnboardingTechnicalBenefitsFragment
                        CUSTOM -> R.id.action_to_idOnboardingCustomBenefitsFragment
                        else -> R.id.action_to_idOnboardingAnalystBenefitsFragment
                    }
                }
                IDOCheckpointUserCnpj.USER_CNPJ_CHECKED -> when (IDOnboardingFlowHandler.checkpointP2) {
                    P2.NONE -> {
                        when (userStatus.onboardingStatus?.role) {
                            ADMIN -> R.id.action_to_idOnboardingAdminBenefitsFragment
                            TECHNICAL -> R.id.action_to_idOnboardingTechnicalBenefitsFragment
                            CUSTOM -> R.id.action_to_idOnboardingCustomBenefitsFragment
                            else -> R.id.action_to_idOnboardingAnalystBenefitsFragment
                        }
                    }
                    P2.DOCUMENT_PHOTO_UPLOADED -> {
                        when (userStatus.onboardingStatus?.role) {
                            ADMIN -> R.id.action_to_idOnboardingAdminBenefitsFragment
                            TECHNICAL -> R.id.action_to_idOnboardingTechnicalBenefitsFragment
                            CUSTOM -> R.id.action_to_idOnboardingCustomBenefitsFragment
                            else -> R.id.action_to_idOnboardingAnalystBenefitsFragment
                        }
                    }
                    P2.SELF_PHOTO_UPLOADED -> R.id.action_to_idOnboardingValidateP2PolicyFragment
                    P2.ALLOWME_SENT -> R.id.action_to_idOnboardingValidateP2PolicyFragment
                    P2.POLICY_2_REQUESTED -> {
                        if (userStatus.onboardingStatus?.userStatus?.foreign == true){
                             R.id.action_to_idOnboardingP2ForeignSuccessFragment
                        } else {
                            if (userStatus.onboardingStatus?.p1Flow?.deadlineRemainingDays == ZERO.toLong()){
                                R.id.action_to_idOnboardingP2SuccessFragment
                            } else {
                                backToHome()
                                return
                            }
                        }
                    }
                    P2.POLICY_2_RESPONSE -> {
                        backToHome()
                        return
                    }
                }
                IDOCheckpointUserCnpj.P2_VALIDATED -> {
                    backToHome()
                    return
                }
            }
        }

        navController?.navigate(navActionId)
    }

    private fun p1Destination(): Int {
        return if (UserPreferences.getInstance().isUserViewedIDOnboarding)
            R.id.action_to_idOnboardingP1CompletionStatusFragment
        else
            R.id.action_to_idOnboardingFragment
    }

    private fun showUserWithoutRole() {
        val canSkip = userStatus.p1Flow?.deadlineRemainingDays.orZero > 0
        doWhenResumed(
            action = {
                if (isAvailable()) {

                    val name = MenuPreference.instance.getLoginObj()?.establishment?.tradeName
                        ?: getString(R.string.Establishment)
                    val toDate = userStatus.p1Flow?.deadlineOn.dateFormatToBr()

                    IDOnboardingFlowHandler.showCustomBottomSheet(
                        this,
                        image = if (canSkip)
                            R.drawable.img_interrogacao
                        else
                            R.drawable.img_sempromos_promo,
                        title = if (canSkip)
                            getString(R.string.id_onboarding_p1_no_role_bs_title)
                        else
                            getString(R.string.id_onboarding_p1_no_role_mandatory_bs_title),
                        message = if (canSkip)
                            getString(R.string.id_onboarding_p1_no_role_bs_message, name, toDate)
                        else
                            getString(R.string.id_onboarding_p1_no_role_mandatory_bs_message, name),
                        bt2Title = getString(R.string.entendi),
                        bt2Callback = {
                            if (canSkip) {
                                backToHome()
                            } else {
                                logout()
                            }
                            false
                        },
                        closeCallback = {
                            if (canSkip) {
                                backToHome()
                            } else {
                                logout()
                            }
                        },
                        isCancelable = false
                    )
                }
            },
            errorCallback = {
                if (canSkip) {
                    backToHome()
                } else {
                    logout()
                }
            }
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        bundle?.let {
            outState.clear()
            outState.putAll(it)
        }
        outState.putParcelable(USER_STATUS, userStatus.onboardingStatus)
    }

    override fun saveData(bundle: Bundle) {
        this.bundle?.putAll(bundle) ?: run {
            this.bundle = bundle
        }
    }

    override fun getSavedData() = bundle

    override fun onSupportNavigateUp(): Boolean {
        if (Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp().not())
            this.finish()

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isShowHelpMenu)
            menuInflater.inflate(R.menu.menu_common_faq, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_help -> {
                this.navigationListener?.onHelpButtonClicked()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun getNavController() = navController

    override fun setTextToolbar(title: String) {
        if (title.isNotBlank()) {
            val toolbarTitleTextView = findViewById<AppCompatTextView>(R.id.textToolbarMainTitle)
            toolbarTitleTextView.text = title
        }
    }

    override fun clearData() {
        this.finish()
    }

    override fun onBackPressed() {
        navigationListener?.onBackButtonClicked()
        super.onBackPressed()
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        this.navigationListener = listener
    }

    override fun showLoading(
        isShow: Boolean,
        @StringRes message: Int?,
        vararg messageArgs: String
    ) {
        binding.apply {
            if (isShow) {
                messageProgressView.showLoading(message, *messageArgs)
                containerView.gone()
                errorView.gone()
            } else {
                containerView.visible()
                messageProgressView.hideLoading(
                    successMessage = message,
                    messageArgs = messageArgs
                )
            }
        }
    }

    override fun showLoading(
        isShow: Boolean,
        @DrawableRes image: Int?,
        isDocumentCapture: Boolean,
        isSuccess: Boolean?
    ) {
        if (isShow) {
            if (bottomSheetLoading == null) {
                bottomSheetLoading = image?.let {
                    bottomSheetGenericFlui(
                        image = it,
                        title = getString(R.string.id_onboarding_title_loading_send_docs),
                        subtitle = getString(R.string.id_onboarding_message_loading_send_docs),
                        nameBtn2Bottom = EMPTY,
                        statusNameTopBar = false,
                        statusBtnClose = false,
                        statusBtnSecond = false,
                        statusView2Line = false,
                        txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
                        txtTitleStyle = TxtTitleStyle.TXT_TITLE_DARK_BLUE,
                        txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
                        btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
                        btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
                        isFullScreen = true,
                        isCancelable = false,
                        isPhone = false
                    )
                }
                try {
                    bottomSheetLoading?.show(supportFragmentManager, EMPTY)
                } catch (e: Exception) {
                    myLogout()
                }
            }
        } else {
            try {
                bottomSheetLoading?.dismiss()
                bottomSheetLoading = null
                if (isSuccess == true) {
                    if (isDocumentCapture)
                        navigateToSelfie()
                    else
                        navigateToValidateP2()
                }
            } catch (e: Exception) {
                myLogout()
            }
        }
    }

    private fun navigateToSelfie() {
        val intent = Intent(applicationContext, IDOnboardingNavigationActivity::class.java)
        intent.putExtra(ARG_PARAM_TYPE_SEND_IMAGES, ONE)
        activity()?.startActivity(intent)
        finish()
    }

    private fun navigateToValidateP2() {
        val intent = Intent(applicationContext, IDOnboardingNavigationActivity::class.java)
        intent.putExtra(ARG_PARAM_TYPE_SEND_IMAGES, TWO)
        activity()?.startActivity(intent)
        finish()
    }

    private fun myLogout() {
        UserPreferences.getInstance().setShowErrorIDOnboardingP2(true)
        applicationContext?.let {
            SessionExpiredHandler.userSessionExpires(
                context = it,
                closeOpenActivities = true,
                isLoginScreen = false
            )
        } ?: activity() ?: finish()
    }

    override fun showContent(
        isShow: Boolean,
        @StringRes loadingSuccessMessage: Int?,
        loadingSuccessCallback: (() -> Unit)?,
        vararg messageArgs: String
    ) {
        binding.apply {
            containerView.visible(isShow)
            if (isShow) {
                messageProgressView.hideLoading(
                    successMessage = loadingSuccessMessage,
                    loadingSuccessCallback,
                    *messageArgs
                )
                errorView.gone()
            }
        }
    }

    override fun showErrorBottomSheet(
        textButton: String?,
        @StringRes textMessage: Int?,
        error: ErrorMessage?,
        title: String?,
        isFullScreen: Boolean
    ) {
        bottomSheetLoading?.dismiss()
        bottomSheetLoading = null
        if (isAvailable()) {
            hideSoftKeyboard()
            binding.messageProgressView.hideLoading()

            val textBtn = textButton ?: getString(R.string.ok)
            val textMessageError = textMessage ?: R.string.business_error

            bottomSheetGenericFlui(
                EMPTY,
                R.drawable.ic_07,
                getString(R.string.generic_error_title),
                messageError(error, this, textMessageError),
                getString(R.string.text_try_again_label),
                textBtn,
                statusNameTopBar = false,
                statusTitle = true,
                statusSubTitle = true,
                statusImage = true,
                statusBtnClose = false,
                statusBtnFirst = true,
                statusBtnSecond = true,
                statusView1Line = true,
                statusView2Line = false,
                txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
                txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
                txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
                btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
                btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
                isFullScreen = true
            ).apply {
                onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnFirst(dialog: Dialog) {
                        dismiss()
                        navigationListener?.onRetry()
                    }

                    override fun onBtnSecond(dialog: Dialog) {
                        dismiss()
                        navigationListener?.onClickSecondButtonError()
                    }

                    override fun onSwipeClosed() {
                        dismiss()
                        navigationListener?.onActionSwipe()
                    }
                }
            }.show(supportFragmentManager, getString(R.string.bottom_sheet_generic))
        }
    }

    override fun showError(error: ErrorMessage?) {
        hideSoftKeyboard()
        binding.apply {
            containerView.gone()
            messageProgressView.hideLoading()

            errorView.run {
                visible()
                cieloErrorMessage = messageError(error, this@IDOnboardingNavigationActivity)
                errorButton?.setText(getString(R.string.back))
                cieloErrorTitle = getString(R.string.generic_error_title)
                errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
                configureActionClickListener {
                    navigationListener?.onRetry()
                }
            }
        }
    }

    override fun showCustomBottomSheet(
        image: Int?,
        title: String?,
        message: String?,
        bt1Title: String?,
        bt2Title: String?,
        bt1Callback: (() -> Boolean)?,
        bt2Callback: (() -> Boolean)?,
        closeCallback: (() -> Unit)?,
        isCancelable: Boolean,
        isPhone: Boolean
    ) {
        if (isAvailable()) {
            IDOnboardingFlowHandler.showCustomBottomSheet(
                this,
                image,
                title,
                message,
                bt1Title,
                bt2Title,
                bt1Callback,
                bt2Callback,
                closeCallback,
                isCancelable,
                isPhone = isPhone,
            )
        }
    }

    override fun showCustomBottomSheet(
        image: Int?,
        title: Int,
        message: String?,
        bt1Title: Int?,
        bt2Title: Int?,
        bt1Callback: (() -> Boolean)?,
        bt2Callback: (() -> Boolean)?,
        closeCallback: (() -> Unit)?,
        isCancelable: Boolean,
        isFullScreen: Boolean,
        isPhone: Boolean
    ) {
        if (isAvailable()) {
            val bt1 = bt1Title?.let { getString(it) } ?: EMPTY
            val bt2 = bt2Title?.let { getString(it) } ?: EMPTY
            val msg = message ?: getString(R.string.id_onboarding_pictures_doc_guide_cnh_error_message)

            IDOnboardingFlowHandler.showCustomBottomSheet(
                this,
                image,
                getString(title),
                msg,
                bt1,
                bt2,
                bt1Callback,
                bt2Callback,
                closeCallback,
                isCancelable,
                isPhone = isPhone,
            )
        }
    }

    override fun showCustomBottomSheet(
        image: Int,
        title: Int,
        message: Int,
        bt1Title: Int?,
        bt2Title: Int?,
        bt1Show: Boolean,
        bt2Show: Boolean,
        bt1Callback: (() -> Unit)?,
        bt2Callback: (() -> Unit)?,
        closeCallback: (() -> Unit)?,
        isCancelable: Boolean,
        isFullScreen: Boolean
    ) {
        bottomSheetLoading = bottomSheetGenericFlui(
            image = image,
            title = "Enviando fotos",
            subtitle = "Suas fotos estão sendo enviadas para análise. Por favor, aguarde alguns instantes",
            nameBtn2Bottom = EMPTY,
            statusNameTopBar = false,
            statusBtnClose = false,
            statusBtnFirst = bt1Show,
            statusBtnSecond = bt2Show,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_DARK_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = isFullScreen,
            isCancelable = isCancelable,
            isPhone = false
        ).apply {
            this.onClick =
                object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnSecond(dialog: Dialog) {
                        dialog.dismiss()
                        bt2Callback?.invoke()
                    }
                }
        }

        bottomSheetLoading?.show(supportFragmentManager.beginTransaction(), null)
    }

    override fun destroyCustomBottomSheet() {
        bottomSheetLoading?.onDestroy()
    }

    override fun showHelpButton(isShow: Boolean) {
        this.isShowHelpMenu = isShow
        this.invalidateOptionsMenu()
    }

    override fun onPause() {
        navigationListener?.onPauseActivity()
        super.onPause()
    }

    private fun logout() {
        SessionExpiredHandler.userSessionExpires(this, true)
    }

    companion object {
        const val USER_STATUS = "USER_STATUS"
    }
}