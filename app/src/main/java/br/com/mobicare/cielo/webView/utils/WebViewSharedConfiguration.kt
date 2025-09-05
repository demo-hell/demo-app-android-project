package br.com.mobicare.cielo.webView.utils

import android.content.Context
import android.os.Bundle
import br.com.cielo.cieloframeworkwebview.presentation.Parameters.CacheStorageParams
import br.com.cielo.cieloframeworkwebview.presentation.Parameters.LocalStorageParams
import br.com.cielo.cieloframeworkwebview.presentation.Parameters.UICallbacks
import br.com.cielo.cieloframeworkwebview.presentation.Parameters.WebView
import br.com.cielo.cieloframeworkwebview.presentation.Parameters.WebViewCallbacks
import br.com.cielo.cieloframeworkwebview.presentation.WebViewHandler
import br.com.mobicare.cielo.commons.data.clients.local.MfaUserInformation
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.FileUtils
import br.com.mobicare.cielo.mfa.token.CieloMfaTokenGenerator
import br.com.mobicare.cielo.webView.analytics.WebViewContainerAnalytics
import br.com.mobicare.cielo.webView.presentation.WebViewContainerViewModel
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking as ga4

class WebViewSharedConfiguration(private val context: Context) : KoinComponent {
    private val viewModel: WebViewContainerViewModel by inject()
    private val cieloMfaTokenGenerator: CieloMfaTokenGenerator by inject()
    private val userPreferences: UserPreferences by inject()
    private val mfaUser: MfaUserInformation by inject()
    private var webViewHandlerInstance: WebViewHandler? = null
    private val analytics: WebViewContainerAnalytics by inject()

    private val seed = mfaUser.getMfaUser(userPreferences.userName)?.mfaSeed

    fun setupWebView(
        flowName: String,
        url: String,
        webView: android.webkit.WebView,
        onShowToolbar: (() -> Unit)? = null,
        onExpiredToken: (() -> Unit),
        onPageFinished: (() -> Unit),
        onGeneralError: (() -> Unit),
        onFinishFlow: (() -> Unit)? = null,
        onNavigateToNativeFlow: ((flowId: String, params: Bundle, shouldCloseWebView: Boolean) -> Unit)? = null
    ): WebViewHandler {
        return WebViewHandler(
            webViewParams = WebView(
                context = context,
                webViewContainer = webView,
                url = url
            ),
            webViewCallbacks = WebViewCallbacks(
                onPageFinished = { success ->
                    if (success) analytics.logScreenSuccess(flowName)

                    onPageFinished.invoke()
                },
                onTagFlow = { eventName, eventsMap, eventsList ->
                    ga4.trackEvent(
                        eventName = eventName,
                        eventsMap = eventsMap,
                        eventsList = eventsList,
                    )
                },
                onExpiredToken = {
                    onExpiredToken()
                },
                onGeneralError = { errorMessage ->
                    analytics.logException(flowName, errorMessage)

                    onGeneralError()
                },
                onGettingOtpCode = { callbackName ->
                    cieloMfaTokenGenerator.getOtpCode(seed)?.let { otpCode ->
                        webViewHandlerInstance?.sendInfoToWebViewPage(callbackName, otpCode)
                    }
                },
                onCloseScreen = {
                    onFinishFlow?.invoke()
                },
                onNavigateToNativeFlow = { flowId, params, shouldCloseWebView ->
                    onNavigateToNativeFlow?.invoke(flowId, params, shouldCloseWebView)
                }
            ),
            uiCallbacks = UICallbacks(
                onShowToolbar = {
                    onShowToolbar?.invoke()
                },
                onShowShareComponent = { content, contentType, fileName ->
                    val tempFile = FileUtils(context).convertBase64ToFile(
                        base64String = content,
                        fileName = fileName,
                        fileType = contentType.type
                    )

                    FileUtils(context).startShare(tempFile)
                }
            ),
            cacheParams = CacheStorageParams(
                params = viewModel.getWebViewCacheParams()
            ),
            localParams = LocalStorageParams(
                params = viewModel.getWebViewLocalParams()
            )
        ).also {
            webViewHandlerInstance = it
        }
    }

    fun finishWebView() {
        webViewHandlerInstance?.finish()
    }
}