package br.com.mobicare.cielo.mfa.router.userWithP2

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.biometricToken.BiometricTokenNavigationFlowActivity
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.enums.EnrollmentType
import br.com.mobicare.cielo.commons.utils.setFullHeight
import br.com.mobicare.cielo.databinding.LayoutMfaTokenConfigurationBinding
import br.com.mobicare.cielo.extensions.*
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.idOnboarding.router.IDOnboardingRouter
import br.com.mobicare.cielo.mfa.analytics.MfaAnalytics
import br.com.mobicare.cielo.mfa.analytics.MfaAnalytics.Companion.ANALYTICS_MFA_CONFIG_DEVICE
import br.com.mobicare.cielo.mfa.router.MfaRouterContract
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MfaTokenConfigurationBottomSheet : BottomSheetDialogFragment(),
    MfaTokenConfigurationContract.View, AllowMeContract.View, Animator.AnimatorListener {

    val presenter: MfaTokenConfigurationPresenter by inject {
        parametersOf(this)
    }

    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }

    private val analytics: MfaAnalytics by inject()

    private val isUseSecurityHash: Boolean? by lazy {
        FeatureTogglePreference.instance.getFeatureTogle(
            FeatureTogglePreference.SECURITY_HASH
        )
    }

    private var binding: LayoutMfaTokenConfigurationBinding? = null

    private var callback: (() -> Unit) = {}
    private var listenerMfa: MfaRouterContract.View? = null

    private var _isResend = false
    private var lottieActivate = true

    companion object {
        private const val ANIMATION_LOADING_PATH = "lottie/lottie_loading_flui.json"
        private const val ARG_PARAM_ENROLLMENT_TYPE = "ARG_PARAM_ENROLLMENT_TYPE"
        private const val ARG_PARAM_IS_RESEND = "ARG_PARAM_IS_RESEND"

        fun onCreate(
            listener: MfaRouterContract.View, type: String? = null, isResend: Boolean = false
        ) = MfaTokenConfigurationBottomSheet().apply {
            listenerMfa = listener
            arguments = Bundle().apply {
                this.putString(ARG_PARAM_ENROLLMENT_TYPE, type)
                this.putBoolean(ARG_PARAM_IS_RESEND, isResend)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = LayoutMfaTokenConfigurationBinding.inflate(
        inflater, container, false
    ).also {
        binding = it
        setupBottomSheet()
    }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListener()
        getInformation()
        getAllowMe(_isResend)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : BottomSheetDialog(requireContext(), theme) {
            override fun onBackPressed() {
                dismiss()
                listenerMfa?.bottomSheetConfiguringMfaDismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        analytics.logScreenView(ANALYTICS_MFA_CONFIG_DEVICE, this.javaClass)
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupBottomSheet() {
        isCancelable = false
        dialog?.setOnShowListener {
            val bottomSheet = dialog?.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout

            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)

                setFullHeight(bottomSheet)
                behavior.disableShapeAnimations()
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = ZERO

                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_DRAGGING)
                            behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }
                })
            }
        }
    }

    private fun setupListener() {
        binding?.ivBack?.setOnClickListener {
            listenerMfa?.bottomSheetConfiguringMfaDismiss()
            dismiss()
        }
    }

    private fun getInformation() {
        _isResend = arguments?.getBoolean(ARG_PARAM_IS_RESEND, false) ?: false
    }

    private fun getAllowMe(isResendCall: Boolean = false) {
        _isResend = isResendCall
        isUseSecurityHash?.let { isUse ->
            if (isUse) allowMePresenter.collect(
                mAllowMeContextual = allowMePresenter.init(requireContext()),
                context = requireActivity(),
                mandatory = false
            )
            else callApi()
        }
    }

    private fun callApi(fingerPrint: String = EMPTY) {
        if (_isResend.not())
            arguments?.getString(ARG_PARAM_ENROLLMENT_TYPE)?.let { type ->
                if (type == EnrollmentType.ENROLLMENT.name)
                    presenter.enrollment(fingerPrint)
                else
                    presenter.challenge(fingerPrint)
            }
        else presenter.resendMfa(fingerPrint)
    }

    private fun setupAnimation(lottieAnimationView: LottieAnimationView?) {
        lottieAnimationView?.apply {
            setAnimation(ANIMATION_LOADING_PATH)
            playAnimation()
        }
    }

    private fun genericErrorMfa(error: ErrorMessage?) {
        requireActivity().apply {
            genericError(
                error = error,
                onFirstAction = {
                    presenter.retry()
                },
                onSecondAction = {
                    if (isAttached())
                        backToHome()
                },
                onSwipeAction = {
                    if (isAttached()) {
                        backToHome()
                        finish()
                    }
                },
                isErrorMFA = true
            )
        }
    }

    private fun startAnimation(lottieAnimationView: LottieAnimationView?) {
        lottieAnimationView?.apply {
            repeatCount = ValueAnimator.INFINITE
            addAnimatorListener(this@MfaTokenConfigurationBottomSheet)
            setupAnimation(lottieAnimationView)
        }
    }

    private fun finalAnimation(lottieAnimationView: LottieAnimationView?) {
        lottieAnimationView?.apply {
            cancelAnimation()
            repeatCount = ZERO
            setupAnimation(lottieAnimationView)
        }
    }

    private fun showOnboardingID() {
        IDOnboardingRouter(
            activity = requireActivity(),
            showLoadingCallback = {},
            hideLoadingCallback = {}
        ).showOnboarding()
    }

    private fun finish() {
        dismiss()
        listenerMfa?.bottomSheetConfiguringMfaDismiss()
    }

    override fun onAnimationStart(p0: Animator) = Unit

    override fun onAnimationCancel(p0: Animator) = Unit

    override fun onAnimationRepeat(p0: Animator) = Unit

    override fun onAnimationEnd(p0: Animator) {
        val lottieAnimationView = if (lottieActivate)
            binding?.lottieAnimationActivate
        else
            binding?.lottieAnimation

        lottieAnimationView?.apply {
            if (repeatCount != ValueAnimator.INFINITE)
                callback.invoke()
        }
    }

    override fun showUserNeedToFinishP2(error: ErrorMessage?) {
        if (isAttached())
            requireActivity().apply {
                finishP2(
                    onFirstAction = {
                        if (isAttached())
                            backToHome()
                    },
                    onSecondAction = {
                        if (isAttached())
                            showOnboardingID()
                    },
                    onSwipeAction = {
                        if (isAttached()) {
                            backToHome()
                            finish()
                        }
                    },
                    error
                )
            }
    }

    override fun showDifferentDevice() {
        if (isAttached())
            getAllowMe(isResendCall = true)
    }

    override fun onResendMfaLoading(isLoading: Boolean) {
        lottieActivate = false
        binding?.containerActivate?.gone()
        binding?.lottieAnimation?.visible()

        if (isLoading) startAnimation(binding?.lottieAnimation)
        else finalAnimation(binding?.lottieAnimation)
    }

    override fun onSuccessResendMfa() {
        callback = {
            requireActivity().startActivity<BiometricTokenNavigationFlowActivity>()
        }
    }

    override fun onErrorResendMfa(error: ErrorMessage?) {
        callback = {
            genericErrorMfa(error)
        }
    }

    override fun onErrorRefreshToken(error: ErrorMessage?) {
        callback = {
            genericErrorMfa(error)
        }
    }

    override fun onConfiguringMfaLoading(isLoading: Boolean) {
        lottieActivate = true
        binding?.lottieAnimation?.gone()
        binding?.containerActivate?.visible()
        if (isLoading) startAnimation(binding?.lottieAnimationActivate)
        else finalAnimation(binding?.lottieAnimationActivate)
    }

    override fun onShowSuccessConfiguringMfa() {
        callback = {
            listenerMfa?.onShowSuccessConfiguringMfa(true)
        }
    }

    override fun onErrorConfiguringMfa(error: ErrorMessage?) {
        callback = {
            genericErrorMfa(error)
        }
    }

    override fun successCollectToken(result: String) {
        callApi(result)
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        requireActivity().errorAllowMe(isMandatory = mandatory, message = errorMessage, onNotMandatoryAction = {
            callApi(result ?: EMPTY)
        })
    }

    override fun getSupportFragmentManagerInstance() = childFragmentManager

    override fun isAttached() = isAdded && activity != null && view != null
}