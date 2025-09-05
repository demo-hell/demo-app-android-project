package br.com.mobicare.cielo.commons.utils.token.presentation

import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import org.koin.standalone.KoinComponent

class HandlerValidationToken : KoinComponent {
    private var handlerValidationTokenView: HandlerValidationTokenView? = null

    fun getToken(supportFragmentManager: FragmentManager, callbackToken: CallbackToken) {
        val tag = this::class.java.simpleName
        supportFragmentManager.findFragmentByTag(tag)?.let {
            supportFragmentManager.beginTransaction().remove(it).commit()
        }
        handlerValidationTokenView = HandlerValidationTokenView.onCreate(callbackToken)
        handlerValidationTokenView?.show(supportFragmentManager, tag)
    }

    fun playAnimationSuccess(
        @StringRes message: Int = R.string.text_animation_loading_success,
        callbackAnimationSuccess: CallbackAnimationSuccess
    ) {
        handlerValidationTokenView?.playAnimationSuccess(message, callbackAnimationSuccess)
    }

    fun playAnimationError(
        error: NewErrorMessage? = null,
        callbackAnimationError: CallbackAnimationError
    ) {
        handlerValidationTokenView?.playAnimationError(error, callbackAnimationError)
    }

    fun hideAnimation(isDelay: Boolean = true, callbackStopAnimation: CallbackStopAnimation) {
        handlerValidationTokenView?.hideAnimation(isDelay, callbackStopAnimation)
    }

    interface CallbackToken {
        fun onSuccess(token: String)
        fun onError()
    }

    interface CallbackAnimationSuccess {
        fun onSuccess()
    }

    interface CallbackStopAnimation {
        fun onStop() {}
    }

    interface CallbackAnimationError {
        fun onClose() {}
        fun onBack() {}
        fun onTryAgain() {}
    }
}