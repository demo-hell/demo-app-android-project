package br.com.mobicare.cielo.commons.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text.FORBIDDEN_ADM
import br.com.mobicare.cielo.commons.constants.Text.MESSAGE
import br.com.mobicare.cielo.commons.constants.Text.NOT_VALIDATED
import br.com.mobicare.cielo.commons.constants.Text.ONBOARDING_REQUIRED
import br.com.mobicare.cielo.commons.constants.Text.ROLE_WITHOUT_ACCESS
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.FragmentDetector
import br.com.mobicare.cielo.commons.utils.RoleWithoutAccessHandler
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.component.CieloToolbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_main_bottom_navigation.*

abstract class BaseActivity : AppCompatActivity(), IAttached {

    interface AnimationListener {
        fun whenClose()
    }

    interface OnBackButtonListener {
        fun onBackTouched()
    }

    var animationListener: AnimationListener? = null

    var onBackButtonListener: OnBackButtonListener? = null

    private val roleWithoutAccessBroadcastReceiver: BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    ONBOARDING_REQUIRED, NOT_VALIDATED -> RoleWithoutAccessHandler.showNoAccessAlertUpdateInfo(
                        this@BaseActivity
                    )
                    FORBIDDEN_ADM -> RoleWithoutAccessHandler.showNoAccessAlertAdmin(
                        this@BaseActivity, intent.getStringExtra(MESSAGE).toString()
                    )
                    else -> RoleWithoutAccessHandler.showNoAccessAlert(this@BaseActivity)
                }
            }
        }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        supportFragmentManager.registerFragmentLifecycleCallbacks(
            FragmentDetector
                .getFragmentDetectorInstance(), false
        )

        changeStatusBarColor(R.color.colorPrimaryDark)
    }

    override fun onResume() {
        super.onResume()
        try {
            LocalBroadcastManager.getInstance(this)
                .registerReceiver(
                    roleWithoutAccessBroadcastReceiver,
                    IntentFilter(ROLE_WITHOUT_ACCESS)
                )

            LocalBroadcastManager.getInstance(this)
                .registerReceiver(
                    roleWithoutAccessBroadcastReceiver,
                    IntentFilter(ONBOARDING_REQUIRED)
                )

            LocalBroadcastManager.getInstance(this)
                .registerReceiver(
                    roleWithoutAccessBroadcastReceiver,
                    IntentFilter(FORBIDDEN_ADM)
                )
        } catch (exception: Exception) {
            exception.message?.let { error ->
                FirebaseCrashlytics.getInstance().log(error)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(roleWithoutAccessBroadcastReceiver)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackButtonListener?.apply {
                    this.onBackTouched()
                }
                onBackPressed()
                animationListener?.apply {
                    this.whenClose()
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun setupToolbar(
        toolbar: Toolbar,
        toolbarTitle: String = "",
        toolbarSubtitle: String = "",
        withMainMenuEnabled: Boolean = true
    ) {
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            this.setDisplayHomeAsUpEnabled(withMainMenuEnabled)
            this.setDisplayShowHomeEnabled(true)
            this.setDisplayShowTitleEnabled(false)

            if (toolbarTitle.isNotBlank()) {
                if (toolbar is CieloToolbar) {
                    toolbar.configure(
                        isHome = true,
                        title = toolbarTitle,
                        colorResId = -1
                    )
                } else {
                    val toolbarTitleTextView =
                        findViewById<AppCompatTextView>(R.id.textToolbarMainTitle)
                    toolbarTitleTextView?.text = toolbarTitle
                    toolbarTitleTextView?.contentDescription =
                        getString(R.string.description_title_toolbar, toolbarTitle)
                }
            }

        }
    }

    fun setupBackIcon(isShow: Boolean = true) {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(isShow)
            setDisplayShowHomeEnabled(isShow)
        }
    }

    fun updateToolbarTitle(toolbarTitle: String, toolbarSubtitle: String = "") {
        setupToolbar(
            toolbarHome as Toolbar, toolbarTitle = toolbarTitle, toolbarSubtitle = toolbarSubtitle,
            withMainMenuEnabled = false
        )
    }

    fun showErrorMessage(error: ErrorMessage, dialogTitle: String = "") {

        val dialogBuilder = AlertDialogCustom.Builder(this, dialogTitle)
            .setMessage(error.message)
            .setBtnRight(getString(R.string.ok))

        if (dialogTitle.isNotBlank()) {
            dialogBuilder.setTitle(dialogTitle)
        }

        dialogBuilder.show()
    }

    fun showErrorMessage(error: ErrorMessage, dialogTitle: String = "", onCLick: () -> Unit) {
        val dialogBuilder = AlertDialogCustom.Builder(this, dialogTitle)
            .setMessage(error.message)
            .setCancelable(false)
            .setBtnRight(getString(R.string.ok))
            .setOnclickListenerRight {
                onCLick()
            }

        if (dialogTitle.isNotBlank()) {
            dialogBuilder.setTitle(dialogTitle)
        }

        dialogBuilder.show()
    }

    fun showOptionDialogMessage(
        dialogTitle: String = "",
        dialogOptions: AlertDialogCustom.Builder.() -> Unit
    ) {
        val dialogBuilder = AlertDialogCustom.Builder(this, dialogTitle)
        dialogBuilder.dialogOptions()
        dialogBuilder.show()
    }

    protected fun changeStatusBarColor(statusBarColor: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, statusBarColor)
            window.decorView.systemUiVisibility = 0
        }
    }

    protected fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)

            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    //region IAttached

    override fun isAttached(): Boolean {
        return !isFinishing
    }

    fun baseLogout(isLoginScreen: Boolean = true) {
        SessionExpiredHandler.userSessionExpires(
            context = this,
            closeOpenActivities = true,
            isLoginScreen = isLoginScreen
        )
        finish()
    }
}