package br.com.mobicare.cielo.commons.utils.token.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.mfaErrorHandler
import br.com.mobicare.cielo.commons.utils.getNewErrorMessage
import br.com.mobicare.cielo.commons.utils.setupBottomSheet
import br.com.mobicare.cielo.commons.utils.token.utils.UiTokenState
import br.com.mobicare.cielo.databinding.LayoutHandlerValidationTokenViewBinding
import br.com.mobicare.cielo.pix.constants.DEFAULT_OTP
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val DELAY: Long = 4500

class HandlerValidationTokenView : BottomSheetDialogFragment() {
    private var binding: LayoutHandlerValidationTokenViewBinding? = null
    private val viewModel: HandlerValidationTokenViewModel by viewModel()

    private var callbackToken: HandlerValidationToken.CallbackToken? = null
    private var startTime = 0L

    companion object {
        fun onCreate(listener: HandlerValidationToken.CallbackToken) =
            HandlerValidationTokenView().apply {
                callbackToken = listener
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = LayoutHandlerValidationTokenViewBinding.inflate(
        inflater, container, false
    ).also {
        binding = it
        setupView()
    }.root

    private fun setupView() {
        isCancelable = false
        setupBottomSheet(
            dialog = dialog,
            isFullScreen = true,
            isShowShadow = false,
            disableShapeAnimations = true
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playAnimation()
        getToken()
    }

    private fun getToken() {
        observe()
        viewModel.getToken()
    }

    private fun observe() {
        viewModel.uiTokenState.observe(viewLifecycleOwner) {
            when (it) {
                is UiTokenState.Success -> callbackToken?.onSuccess(it.token)
                is UiTokenState.Default -> callbackToken?.onSuccess(DEFAULT_OTP)
                is UiTokenState.ConfigureToken -> onConfigureToken(it.error)
            }
        }
    }

    private fun onConfigureToken(error: NewErrorMessage) {
        dismiss()
        mfaErrorHandler(error, requireContext())
    }

    private fun playAnimation() {
        startTime = System.currentTimeMillis()
        binding?.loadingViewFlui?.startAnimation(message = R.string.text_animation_loading)
    }

    fun hideAnimation(
        isDelay: Boolean,
        callbackStopAnimation: HandlerValidationToken.CallbackStopAnimation
    ) {
        if (isDelay)
            finishAnimation {
                stop(callbackStopAnimation)
            }
        else
            stop(callbackStopAnimation)
    }

    private fun stop(callbackStopAnimation: HandlerValidationToken.CallbackStopAnimation) {
        binding?.loadingViewFlui?.hideAnimationStart()
        callbackStopAnimation.onStop()
        dismiss()
    }

    fun playAnimationSuccess(
        @StringRes message: Int,
        callbackAnimationSuccess: HandlerValidationToken.CallbackAnimationSuccess
    ) {
        finishAnimation {
            binding?.loadingViewFlui?.showAnimationSuccess(
                message = message,
                onAction = {
                    callbackAnimationSuccess.onSuccess()
                    dismiss()
                })
        }
    }

    fun playAnimationError(
        error: NewErrorMessage?,
        callbackAnimationError: HandlerValidationToken.CallbackAnimationError
    ) {
        finishAnimation {
            val message =
                requireContext().getNewErrorMessage(error, R.string.text_animation_loading_error)
            binding?.loadingViewFlui?.showAnimationError(
                message = message,
                onActionTryAgain = {
                    callbackAnimationError.onTryAgain()
                    dismiss()
                }, onActionBack = {
                    callbackAnimationError.onBack()
                    dismiss()
                }, onActionClose = {
                    callbackAnimationError.onClose()
                    dismiss()
                })
        }
    }

    private fun finishAnimation(onAction: () -> Unit) {
        val elapsedTime = System.currentTimeMillis() - startTime
        val delayMillis = DELAY - elapsedTime
        if (elapsedTime >= DELAY)
            onAction.invoke()
        else
            Handler(Looper.getMainLooper()).postDelayed({
                onAction.invoke()
            }, delayMillis)
    }
}