package br.com.mobicare.cielo.commons.utils.dialog

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.mfaErrorHandler
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper.MfaOtp
import br.com.mobicare.cielo.commons.utils.setFullHeight
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationTokenViewModel
import br.com.mobicare.cielo.commons.utils.token.utils.UiTokenState
import br.com.mobicare.cielo.databinding.FragmentBottomSheetValidationTokenBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.pix.constants.DEFAULT_OTP
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.get

class BottomSheetValidationTokenFragment : BottomSheetDialogFragment(),
    Animator.AnimatorListener {

    private val viewModel: HandlerValidationTokenViewModel by lazy {
        get<HandlerValidationTokenViewModel>()
    }

    private val ANIMATION_LOADING_PATH = "lottie/validate_token_loading.json"
    private val ANIMATION_SUCCESS_PATH = "lottie/validate_token_success.json"
    private val ANIMATION_ERROR_PATH = "lottie/validate_token_error.json"

    private var isValidateSuccess = true
    private var error: ErrorMessage? = null
    private var callbackValidateToken: BottomSheetValidationTokenWrapper.CallbackValidateToken? = null
    private var callbackMfaOtp: MfaOtp? = null

    private var binding: FragmentBottomSheetValidationTokenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentBottomSheetValidationTokenBinding.inflate(
        inflater, container, false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        initLottie()
    }

    private fun setupObservers() {
        viewModel.uiTokenState.observe(viewLifecycleOwner) {
            when (it) {
                is UiTokenState.Success -> callbackMfaOtp?.onResult(it.token)
                is UiTokenState.Error -> callbackMfaOtp?.onResult(DEFAULT_OTP)
                is UiTokenState.ConfigureToken -> onConfigureToken(it.error)
            }
        }
    }

    private fun onConfigureToken(error: NewErrorMessage) {
        dismiss()
        mfaErrorHandler(error, requireContext())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        configureBottomSheet(dialog)
        return dialog
    }

    private fun configureBottomSheet(dialog: Dialog) {
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                setFullHeight(bottomSheet)

                isCancelable = false
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = ZERO
                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                            behavior.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }
                })
            }
        }
    }

    private fun initLottie() {
        binding?.lottieAnimationToken?.apply {
            clearAnimation()
            setAnimation(ANIMATION_LOADING_PATH)
            repeatCount = ValueAnimator.INFINITE
            addAnimatorListener(this@BottomSheetValidationTokenFragment)
            playAnimation()
        }
    }

    override fun onAnimationRepeat(animation: Animator) = Unit
    override fun onAnimationCancel(animation: Animator) = Unit
    override fun onAnimationStart(animation: Animator) = Unit

    override fun onAnimationEnd(animation: Animator) {
        if (binding?.lottieAnimationToken?.repeatCount != ValueAnimator.INFINITE) {
            if (isValidateSuccess) {
                callbackValidateToken?.callbackTokenSuccess()
            } else {
                callbackValidateToken?.callbackTokenError()
            }
            binding?.lottieAnimationToken?.clearAnimation()
            dismiss()
        }
    }

    fun playAnimationSuccess(
        callbackValidateToken:
        BottomSheetValidationTokenWrapper.CallbackValidateToken?
    ) {
        this.callbackValidateToken = callbackValidateToken
        isValidateSuccess = true
        binding?.apply {
            textViewContent.gone()
            textViewTitle.text = getString(R.string.token_validating_success)
            lottieAnimationToken.apply {
                cancelAnimation()
                clearAnimation()
                repeatCount = ZERO
                setAnimation(ANIMATION_SUCCESS_PATH)
                playAnimation()
            }
        }
    }

    fun playAnimationError(
        error: ErrorMessage?,
        callbackValidateToken: BottomSheetValidationTokenWrapper.CallbackValidateToken?
    ) {
        this.callbackValidateToken = callbackValidateToken
        isValidateSuccess = false
        this.error = error
        binding?.apply {
            textViewContent.gone()
            textViewTitle.text = getString(R.string.token_validating_error)
            lottieAnimationToken.apply {
                cancelAnimation()
                clearAnimation()
                repeatCount = ZERO
                setAnimation(ANIMATION_ERROR_PATH)
                playAnimation()
            }
        }
    }

    fun generateOtp(callback: MfaOtp) {
        callbackMfaOtp = callback
        viewModel.getToken()
    }

    override fun onDestroyView() {
        binding = null
        callbackMfaOtp = null
        super.onDestroyView()
    }
}