package br.com.mobicare.cielo.commons.ui

import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.OnCommonActivityFragmentStatusListener
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.extensions.isAvailable

open class BaseFragment : Fragment(), IAttached {

    var configureToolbarActionListener: ConfigureToolbarActionListener? = null
    protected var fragmentStatusListener: OnCommonActivityFragmentStatusListener? = null

    interface ConfigureToolbarActionListener {
        fun changeTo(
            @ColorRes colorResId: Int = -1,
            @ColorRes statusBarColor: Int = -1,
            title: String,
            subtitle: String = "",
            isHome: Boolean = false
        )
    }

    override fun isAttached(): Boolean {
        return view != null && this.isAdded && activity.isAvailable()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCommonActivityFragmentStatusListener) {
            this.fragmentStatusListener = context
        }

        if (context is ConfigureToolbarActionListener) {
            this.configureToolbarActionListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.fragmentStatusListener = null
        this.configureToolbarActionListener = null
    }

    fun View?.visible() {
        this?.visibility = View.VISIBLE
    }

    fun View?.isVisible() = this?.visibility == View.VISIBLE

    fun View?.invisible() {
        this?.visibility = View.INVISIBLE
    }

    fun View?.gone() {
        this?.visibility = View.GONE
    }

    fun baseLogout(isLoginScreen: Boolean = true) {
        context?.let {
            SessionExpiredHandler.userSessionExpires(
                context = it,
                closeOpenActivities = true,
                isLoginScreen = isLoginScreen
            )
        }
            ?: activity?.finish()
    }

    fun verifySessionError(error: ErrorMessage?) {
        if (error?.logout == true) {
            baseLogout()
        }
    }

    protected fun changeStatusBarColor(@ColorRes colorResId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            context?.let {
                activity?.window?.statusBarColor = ContextCompat.getColor(it, colorResId)
            }

            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    protected fun setAppearanceLightStatusBar(isAppearanceLightStatusBars: Boolean = true) {
        activity?.apply {
            val windowInsetsController = WindowCompat.getInsetsController(
                window,
                window.decorView
            )
            windowInsetsController?.isAppearanceLightStatusBars = isAppearanceLightStatusBars
        }
    }
}
