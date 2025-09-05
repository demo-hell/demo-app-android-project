package br.com.mobicare.cielo.webView.presentation

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import br.com.cielo.cieloframeworkwebview.presentation.WebViewHandler
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.databinding.WebviewContainerBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.domain.MenuTarget
import br.com.mobicare.cielo.main.presentation.OnLogoutProceedCallback
import br.com.mobicare.cielo.main.presentation.createSessionTimeoutDialog
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.webView.utils.FLOW_NAME_PARAM
import br.com.mobicare.cielo.webView.utils.IS_FROM_INTERNAL_FLOW
import br.com.mobicare.cielo.webView.utils.URL_PARAM
import br.com.mobicare.cielo.webView.utils.WebViewSharedConfiguration
import org.koin.android.ext.android.inject

class WebViewContainerActivity : BaseActivity() {
    private var binding: WebviewContainerBinding? = null
    private val sharedConfiguration: WebViewSharedConfiguration by inject()
    private var webViewHandlerInstance: WebViewHandler? = null

    private val url: String
        get() = intent?.getStringExtra(URL_PARAM) ?: EMPTY

    private val isExternalFlow: Boolean
        get() = intent?.getBooleanExtra(IS_FROM_INTERNAL_FLOW, false) ?: false

    private val flowName: String
        get() = intent?.getStringExtra(FLOW_NAME_PARAM) ?: EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WebviewContainerBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        configureToolbar()
        configureAndStartWebView()

        onBackPressedDispatcher.addCallback(this, getOnBackCallback())
    }

    private fun configureToolbar(isVisible: Boolean? = false) {
        runOnUiThread {
            binding?.toolbarInclude?.apply {
                root.visible(isVisible ?: isExternalFlow)
                setupToolbar(toolbarMain, flowName)
            }
        }
    }

    private fun configureAndStartWebView() {
        binding?.apply {
            webViewContainer.webView?.let { webView ->
                webViewHandlerInstance = sharedConfiguration.setupWebView(
                    flowName,
                    url,
                    webView,
                    onShowToolbar = {
                        configureToolbar(true)
                    },
                    onPageFinished = {
                        pbLoading.gone()
                        webViewContainer.visible()
                    },
                    onGeneralError = {
                        webView.gone()

                        errorLayout.visible()
                        errorLayout.errorButton?.setOnClickListener {
                            finish()
                        }
                    },
                    onExpiredToken = {
                        createSessionTimeoutDialog(
                            object :
                                OnLogoutProceedCallback {
                                override fun logout() {
                                    SessionExpiredHandler.userSessionExpires(
                                        this@WebViewContainerActivity,
                                        true
                                    )

                                    finishAffinity()
                                }
                            }
                        )
                    },
                    onFinishFlow = {
                        this@WebViewContainerActivity.finish()
                    },
                    onNavigateToNativeFlow = { flowId, params, shouldCloseWebView ->
                        if (shouldCloseWebView) finish()
                        Router.navigateTo(this@WebViewContainerActivity, mountFlow(flowId), null, params)
                    }
                ).start()
            }
        }
    }

    private fun getOnBackCallback(): OnBackPressedCallback {
        return object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webViewHandlerInstance?.canGoBack() == true) {
                    webViewHandlerInstance?.goBack()
                } else {
                    finish()
                }
            }
        }
    }

    private fun mountFlow(flowId: String): Menu {
        return Menu(
            code = flowId,
            icon = EMPTY,
            items = listOf(),
            name = EMPTY,
            showIcons = false,
            shortIcon = EMPTY,
            privileges = listOf(),
            show = false,
            showItems = false,
            menuTarget = MenuTarget(),
        )
    }

    override fun onDestroy() {
        sharedConfiguration.finishWebView()
        super.onDestroy()
    }
}
