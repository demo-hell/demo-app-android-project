package br.com.mobicare.cielo.commons.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.core.view.postDelayed
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import kotlinx.android.synthetic.main.view_message_progress.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.schedule

class MessageProgressView(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    init {
        inflate(context, R.layout.view_message_progress, this)
    }

    fun showLoading(@StringRes message: Int? = null, vararg messageArgs: String) {
        imgSuccess?.gone()
        progressBar?.visible()

        message?.let {
            tvMessage?.visible()
            tvMessage?.text = context.getString(it, *messageArgs)
        } ?: tvMessage?.gone()

        this.visible()
    }

    fun hideLoading(@StringRes successMessage: Int? = null, successCallback: (() -> Unit)? = null, vararg messageArgs: String) {
        if (successMessage != null) {
            imgSuccess?.visible()
            progressBar?.gone()

            tvMessage?.visible()
            tvMessage?.text = context.getString(successMessage, *messageArgs)

            postDelayed(SUCCESS_STATUS_TIME) {
                if (isLaidOut) {
                    this@MessageProgressView?.gone()
                    successCallback?.invoke()
                }
            }
        } else {
            this?.gone()
        }
    }

    companion object {
        const val SUCCESS_STATUS_TIME = 2000L
    }

}

fun MessageProgressView?.hideLoading(@StringRes successMessage: Int? = null, successCallback: (() -> Unit)? = null, vararg messageArgs: String) {
    this?.hideLoading(successMessage, successCallback, *messageArgs)
        ?: successCallback?.invoke()
}