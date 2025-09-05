package br.com.mobicare.cielo.commons.presentation.utils

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.EMPTY_STRING
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.databinding.ActivityWebviewBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking as ga4

open class WebviewActivity : BaseActivity(), BaseActivity.OnBackButtonListener {

    private var binding: ActivityWebviewBinding? = null

    private var url: String = EMPTY_STRING
    private var title: String = EMPTY_STRING
    private var screen : String = EMPTY_STRING
    private var screenPath : String = EMPTY_STRING
    private var screenPathGA4: String = EMPTY_STRING
    private lateinit var page : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        intent?.extras?.let {
            title = it.getString(TITLE, EMPTY_STRING)
            url = it.getString(URL, EMPTY_STRING)
            screen = it.getString(SCREEN_NAME, EMPTY_STRING)
            screenPathGA4 = it.getString(SCREEN_PATH_GA4, EMPTY_STRING)
            page = it.getString(SCREEN_NAME_FIREBASE, EMPTY_STRING)
        }

        binding?.toolbar?.root?.let { setupToolbar(it, title) }
        this.onBackButtonListener = this

        trackScreen()

        binding?.webview?.settings?.javaScriptEnabled = true

        configureWebClient()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onResume() {
        super.onResume()
        sendGaScreenView()
        logScreenViewGA4()
    }

    private fun configureWebClient() {
        binding?.apply {
            webview.settings.domStorageEnabled = true

            webview.webViewClient = (object : WebViewClient() {

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    progressWebview.visible()
                }

                override fun onPageFinished(view: WebView, url: String) {
                    progressWebview.gone()

                    if (url.contains("cadastramento")) {
                        webview.loadUrl(
                            "javascript:(function() { " +
                                    "var element = document.getElementById('cabecalho');  element.style.display = 'none'" +
                                    "})()"
                        )

                        webview.loadUrl(
                            "javascript:(function() { " +
                                    "var element = document.querySelector('.breadcrumb').remove()" +
                                    "})()"
                        )

                        webview.loadUrl(
                            "javascript:(function() { " +
                                    "var element = document.querySelector('.titulo_pagina').remove()" +
                                    "})()"
                        )


                        webview.loadUrl(
                            "javascript:(function() { " +
                                    "var element = document.querySelector('.footer').remove()" +
                                    "})()"
                        )

                        webview.zoomOut()
                        webview.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                    }
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler,
                    error: SslError?
                ) {
                    view?.loadUrl("about:blank")
                    Toast.makeText(
                        this@WebviewActivity,
                        "Houve um erro de conexão, tente novamente mais tarde",
                        Toast.LENGTH_LONG
                    ).show()
                    this@WebviewActivity.finish()
                    view?.handleSslError(handler, this@WebviewActivity)
                }
            })
        }
    }

    private fun trackScreen(){
        if(url.contains("cadastramento")){
            screenPath = "CriarUsuario"
            Analytics.trackScreenView(screenPath)
        } else if (url.contains("nosso-cliente")){
            screenPath = "SejaNossoCliente"
            Analytics.trackScreenView(screenPath)
        }
    }

    override fun onStart() {
        super.onStart()
        initWebView()
    }

    open fun initWebView() {
        binding?.webview?.loadUrl(url)
    }

    override fun onBackTouched() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Action.FORMULARIO),
            action = listOf(screenPath),
            label = listOf(Label.BOTAO, String.format(Label.VOLTAR_PARA, screen)),
        )
        finish()
    }

    private fun sendGaScreenView() {
        if (isAttached()) {
            Analytics.trackScreenView(page)
        }
    }

    private fun logScreenViewGA4() = ga4.trackScreenView(screenPathGA4)

    companion object {
        const val TITLE = "title"
        const val DEFAULT_TAX = "defaultTax"
        const val PROMOTION_TAX = "promotionalTax"
        const val MAX_TAX = "maxTax"
        const val URL = "url"
        const val SCREEN_NAME = "SCREEN_NAME"
        const val SCREEN_PATH_GA4 = "SCREEN_PATH_GA4"
        const val SCREEN_NAME_FIREBASE = "GA_FIREBASE"
    }

}
