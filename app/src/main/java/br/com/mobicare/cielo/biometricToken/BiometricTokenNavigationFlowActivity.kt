package br.com.mobicare.cielo.biometricToken

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.biometricToken.analytics.BiometricTokenGA4.Companion.SCREEN_VIEW_BIOMETRIC_ERROR
import br.com.mobicare.cielo.biometricToken.analytics.BiometricTokenGA4.Companion.SCREEN_VIEW_BIOMETRIC_RERUN
import br.com.mobicare.cielo.biometricToken.analytics.BiometricTokenGA4.Companion.SCREEN_VIEW_BIOMETRIC_TIPS_SELFIE
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_IS_LOGIN_FLOW
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_SCREEN_NAME
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_SCREEN_NAME_EXCEPTION
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_SCREEN_NAME_GENERIC_ERROR
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_SELFIE_SDK
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_TOKEN_SELFIE_SCREEN
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_USERNAME
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.messageError
import br.com.mobicare.cielo.databinding.ActivityBiometricTokenNavigationFlowBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible

class BiometricTokenNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {

    private var binding: ActivityBiometricTokenNavigationFlowBinding? = null
    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null
    private val isSelfie: Boolean by lazy {
        intent?.extras?.getBoolean(ARG_BIOMETRIC_TOKEN_SELFIE_SCREEN) ?: false
    }
    private val selfieSDK: String by lazy {
        intent?.extras?.getString(ARG_BIOMETRIC_SELFIE_SDK) ?: EMPTY
    }
    private val userName: String by lazy {
        intent?.extras?.getString(ARG_BIOMETRIC_USERNAME) ?: EMPTY
    }
    private val screenName: String by lazy {
        intent?.extras?.getString(ARG_BIOMETRIC_SCREEN_NAME) ?: SCREEN_VIEW_BIOMETRIC_TIPS_SELFIE
    }
    private val screenNameException: String by lazy {
        intent?.extras?.getString(ARG_BIOMETRIC_SCREEN_NAME_EXCEPTION) ?: SCREEN_VIEW_BIOMETRIC_RERUN
    }
    private val screenNameGenericError: String by lazy {
        intent?.extras?.getString(ARG_BIOMETRIC_SCREEN_NAME_GENERIC_ERROR) ?: SCREEN_VIEW_BIOMETRIC_ERROR
    }
    private val isLoginFlow: Boolean? by lazy {
        intent?.extras?.getBoolean(BiometricTokenConstants.ARG_BIOMETRIC_IS_LOGIN_FLOW, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBiometricTokenNavigationFlowBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        bundle = savedInstanceState

        val destination = if (isSelfie)
            R.id.biometricTokenSelfieFragment
        else
            R.id.biometricTokenHomeFragment

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph_biometric_token)
        navGraph.setStartDestination(destination)
        navController.setGraph(
            navGraph,
            bundleOf(
                ARG_BIOMETRIC_SELFIE_SDK to selfieSDK,
                ARG_BIOMETRIC_USERNAME to userName,
                ARG_BIOMETRIC_SCREEN_NAME to screenName,
                ARG_BIOMETRIC_SCREEN_NAME_EXCEPTION to screenNameException,
                ARG_BIOMETRIC_SCREEN_NAME_GENERIC_ERROR to screenNameGenericError,
                ARG_BIOMETRIC_IS_LOGIN_FLOW to isLoginFlow
            )
        )
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun showAnimatedLoading(message: Int?) {
        binding?.apply {
            loadingAnimatedProgress.showAnimationStart(message)
            loadingProgress.gone()
            containerView.gone()
            errorView.gone()
        }
    }

    override fun showLoading(
        isShow: Boolean
    ) {
        binding?.apply {
            loadingAnimatedProgress.hideAnimationStart()
            if (isShow) loadingProgress.startAnimation(R.string.wait_animated_loading_start_message, false)
            else loadingProgress.gone()
        }
    }

    override fun hideAnimatedLoading() {
        binding?.apply {
            containerView.visible()
            loadingAnimatedProgress.hideAnimationStart()
        }
    }

    override fun showCustomBottomSheet(
        @DrawableRes image: Int?,
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
        lifecycleScope.launchWhenResumed {
            bottomSheetGenericFlui(
                image = image ?: R.drawable.ic_generic_error_image,
                title = title ?: getString(R.string.generic_error_title),
                subtitle = message ?: getString(R.string.error_generic),
                nameBtn1Bottom = bt1Title ?: EMPTY,
                nameBtn2Bottom = bt2Title ?: getString(R.string.ok),
                txtTitleStyle = TxtTitleStyle.TXT_TITLE_DARK_BLUE,
                txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_FLUI_BOTTOM_SHEET,
                statusBtnFirst = bt1Title != null,
                btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE_TXT_BLUE,
                isCancelable = isCancelable,
                isFullScreen = false,
                isPhone = isPhone
            ).apply {
                onClick =
                    object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {

                        override fun onBtnFirst(dialog: Dialog) {
                            if (bt1Callback?.invoke() != true) dismiss()
                        }

                        override fun onBtnSecond(dialog: Dialog) {
                            if (bt2Callback?.invoke() != true) dismiss()
                        }

                        override fun onSwipeClosed() {
                            closeCallback?.invoke()
                        }

                        override fun onCancel() {
                            closeCallback?.invoke()
                        }
                    }
            }.show(
                supportFragmentManager,
                getString(R.string.bottom_sheet_generic)
            )
        }
    }

    override fun showError(error: ErrorMessage?) {
        hideSoftKeyboard()
        binding?.apply {
            containerView.gone()
            loadingAnimatedProgress.hideAnimationStart()

            errorView.run {
                visible()
                cieloErrorMessage = messageError(error, this@BiometricTokenNavigationFlowActivity)
                errorButton?.setText(getString(R.string.back))
                cieloErrorTitle = getString(R.string.generic_error_title)
                errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
                configureActionClickListener {
                    navigationListener?.onRetry()
                }
            }
        }
    }
}