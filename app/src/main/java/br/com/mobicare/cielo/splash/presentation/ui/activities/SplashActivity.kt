package br.com.mobicare.cielo.splash.presentation.ui.activities

import android.Manifest
import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.LOGIN_PASSO_1
import br.com.mobicare.cielo.commons.analytics.LOGIN_SPLASH
import br.com.mobicare.cielo.commons.data.clients.local.userPreferences
import br.com.mobicare.cielo.commons.ui.widget.AppUnavailableBottomSheetFragment
import br.com.mobicare.cielo.databinding.ActivitySplashBinding
import br.com.mobicare.cielo.databinding.BottomSheetCieloPermissionDeniedBinding
import br.com.mobicare.cielo.deeplink.DeepLinkActivity
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.featureToggle.FeatureToggleContract
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.BLOQUEIO_APP_INDISPONIVEL
import br.com.mobicare.cielo.featureToggle.presenter.FeatureTogglePresenter
import br.com.mobicare.cielo.openFinance.presentation.manager.newShare.conclusion.OpenFinanceFlowConclusionActivity
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.splash.presentation.presenter.SplashPresenter
import br.com.mobicare.cielo.splash.presentation.ui.SplashContract
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdk
import kotlinx.android.synthetic.main.activity_splash.progressBar_splash
import kotlinx.android.synthetic.main.activity_splash.textview_splash
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class SplashActivity : AppCompatActivity(), SplashContract.View, FeatureToggleContract.View {

    private val presenterFeatureToggle: FeatureTogglePresenter by inject {
        parametersOf(this)
    }
    private val mPresenter: SplashPresenter by inject {
        parametersOf(this)
    }

    private var _binding: ActivitySplashBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var rationalePostNotificationRequested by userPreferences(USER_DENIED_POST_NOTIFICATION, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (BuildConfig.DEBUG) {
            LocalBroadcastManager.getInstance(this)
                .sendBroadcast(Intent(Intent.ACTION_MY_PACKAGE_REPLACED))
        }
        binding.lottieView.addAnimatorListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                presenterFeatureToggle.callAPI()
            }

            override fun onAnimationEnd(animation: Animator) {
                if (ContextCompat.checkSelfPermission(
                        this@SplashActivity,
                        NOTIFICATIONS_PERMISSION
                    ) == PackageManager.PERMISSION_GRANTED || rationalePostNotificationRequested
                ) {
                    proceed()
                } else {
                    requestRuntimeNotificationPermission()
                }
            }

            override fun onAnimationCancel(animation: Animator) {
                //do nothing
            }

            override fun onAnimationRepeat(animation: Animator) {
                //do nothing
            }
        })
    }

    private val notificationRequestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted && !rationalePostNotificationRequested) {
            requestRuntimeNotificationPermission()
        } else {
            if (isGranted) {
                SFMCSdk.requestSdk { sdk ->
                    sdk.mp {
                        it.pushMessageManager.enablePush()
                    }
                }
            }
            proceed()
        }
    }

    private fun requestRuntimeNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (shouldShowRequestPermissionRationale(NOTIFICATIONS_PERMISSION)) {
                val cieloBottomSheet = CieloContentBottomSheet.create(
                    headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                        title = getString(R.string.want_deny_notify_permission),
                        onCloseTap = {
                            proceed()
                        },
                        onSlideDismiss = {
                            proceed()
                        }
                    ),
                    contentLayoutRes = R.layout.bottom_sheet_cielo_permission_denied,
                    onContentViewCreated = { view, _ ->
                        BottomSheetCieloPermissionDeniedBinding.bind(view).apply {
                            tvMessage.apply {
                                val htmlText = getString(R.string.post_notification_benefits).trimIndent()
                                text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT)
                            }
                        }
                    },
                    mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                        title = getString(R.string.retry_permission),
                        startEnabled = true,
                        onTap = { cieloBottomSheet ->
                            cieloBottomSheet.dismiss()
                            notificationRequestPermissionLauncher.launch(NOTIFICATIONS_PERMISSION)
                            rationalePostNotificationRequested = true
                        }
                    ),
                    secondaryButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                        title = getString(R.string.deny_permission),
                        startEnabled = true,
                        onTap = { cieloBottomSheet ->
                            cieloBottomSheet.dismiss()
                            proceed()
                            rationalePostNotificationRequested = true
                        }
                    )
                )
                cieloBottomSheet.isCancelable = false
                cieloBottomSheet.show(supportFragmentManager, TAG_BOTTOM_SHEET_NOTIFICATION)
            } else {
                notificationRequestPermissionLauncher.launch(NOTIFICATIONS_PERMISSION)
            }
        } else {
            proceed()
        }
    }

    override fun onStart() {
        super.onStart()
        mPresenter.callAPI()
    }

    private fun proceed() {
        if (!mPresenter.checkDeepLink(intent, true)) {
            nextActivity()
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter.onResume()
        presenterFeatureToggle.onResume()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.onPause()
        presenterFeatureToggle.onPause()
    }

    override fun showProgress() {
        progressBar_splash?.visible()
    }

    override fun hideProgress() {
        progressBar_splash?.gone()
    }

    override fun showError(error: String?) {
        textview_splash?.text = EMPTY
    }

    override fun startDeepLinkActivity(token: String?) {
        token?.run {
            val deepLinkIntent = DeepLinkActivity.newIntent(
                this@SplashActivity,
                this
            )
            startActivity(deepLinkIntent)
        }
    }

    override fun changeActivity(activity: Class<*>?) {
        mPresenter.let { presenter ->
            if (presenter.checkDeepLink(intent, false).not()) {
                val startIntent = Intent(this, activity)
                startIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(startIntent)
                this.finish()
            }
        }
    }

    override fun changeActivityToFlowOPF() {
        if (mPresenter.checkDeepLink(intent, false).not()) {
            startActivity(Intent(this, OpenFinanceFlowConclusionActivity::class.java))
        }
    }

    override fun onFeatureToogleSuccess() {}

    override fun onFeatureToogleError() {}

    private fun nextActivity() {
        Analytics.trackScreenView(
            screenName = LOGIN_SPLASH,
            screenClass = this.javaClass
        )
        Analytics.trackScreenView(
            screenName = LOGIN_PASSO_1,
            screenClass = this.javaClass
        )

        if (FeatureTogglePreference.instance.isActivate(BLOQUEIO_APP_INDISPONIVEL)) {
            AppUnavailableBottomSheetFragment().show(
                supportFragmentManager,
                "${this.javaClass.simpleName}#${AppUnavailableBottomSheetFragment::class.java.simpleName}"
            )
        } else
            mPresenter.callNextActivity()
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val NOTIFICATIONS_PERMISSION = Manifest.permission.POST_NOTIFICATIONS
        private const val TAG_BOTTOM_SHEET_NOTIFICATION = "Rationale Notification Request"
        private const val USER_DENIED_POST_NOTIFICATION = "userDeniedPostNotification"
    }
}
