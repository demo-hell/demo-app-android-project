package br.com.mobicare.cielo.commons.utils

import android.content.Context
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import timber.log.Timber

fun WebView.open(url: String, webClient: WebViewClient) {
    configureWebView(webClient)
    this.loadUrl(url)

    Timber.tag("URL_LOAD_WEB_VIEW").i(url)
}

fun WebView.loadUrlWithHeaders(url: String, headers: MutableMap<String, String>, webClient: WebViewClient) {
    configureWebView(webClient)
    this.loadUrl(url, headers)

    Timber.tag("URL_LOAD_WEB_VIEW").i(url)
}

fun WebView.configureWebView(webClient: WebViewClient? = null) {
    this.settings.loadWithOverviewMode = true
    this.settings.javaScriptEnabled = true
    this.settings.domStorageEnabled = true
    this.webChromeClient = android.webkit.WebChromeClient()
    webClient?.let {
        this.webViewClient = webClient
    }
}

fun WebView.getHeadersLightHouse(): HashMap<String, String> {
    return hashMapOf("access-token" to UserPreferences.getInstance().token,
            "client-id" to BuildConfig.CLIENT_ID)
}

fun WebView.handleSslError(handler: SslErrorHandler?, context: Context) {
    AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.text_webview_ssl_warning_title))
            .setMessage(context.getString(R.string.text_webview_ssl_message))
            .setPositiveButton(context.getString(R.string.continuar)) { _, _ -> handler?.proceed() }
            .setNegativeButton(context.getString(R.string.cancelar)) { _, _ -> handler?.cancel() }
            .create()
            .show()
}

