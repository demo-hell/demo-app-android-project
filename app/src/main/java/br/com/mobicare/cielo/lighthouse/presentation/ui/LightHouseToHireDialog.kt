package br.com.mobicare.cielo.lighthouse.presentation.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.ui.widget.FullScreenDialog
import br.com.mobicare.cielo.commons.utils.getHeadersLightHouse
import br.com.mobicare.cielo.commons.utils.handleSslError
import br.com.mobicare.cielo.commons.utils.loadUrlWithHeaders
import kotlinx.android.synthetic.main.dialog_lighthouse_to_hire.*
import kotlinx.android.synthetic.main.layout_error_lighthouse.*

class LightHouseToHireDialog internal constructor() : FullScreenDialog() {

    companion object {
        const val TAG: String = "LightHouseToHireDialog"
        const val CLOSE: String = "close"
        const val CLICK_ACCESS: String = "acesse-agora"

        fun create(): LightHouseToHireDialog {
            return LightHouseToHireDialog()
        }
    }

    private var isLoadedWebView = true

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_lighthouse_to_hire, container, false)
    }

    override fun onStart() {
        super.onStart()
        configureWebview()
    }

    @SuppressLint("SetJavaScriptEnabled", "newApi")
    private fun configureWebview() {

        webViewLightHouseToHire.loadUrlWithHeaders(BuildConfig.URL_FAROL,
                webViewLightHouseToHire.getHeadersLightHouse(), object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressVisibile()
            }

            override fun onPageFinished(view: WebView?, url: String) {

                if (isLoadedWebView) {
                    webViewVisible()
                    interceptInteractionsWebView(url)
                }

                super.onPageFinished(view, url)
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
                isLoadedWebView = false

                view?.url?.let { url ->
                    if (url.contains(CLOSE).not() && url.contains(CLICK_ACCESS).not()) {
                        layoutErrorTryAgainVisible()
                        configureTryAgain()
                    }
                }
                view?.handleSslError(handler, requireContext())
            }

        })

    }

    private fun interceptInteractionsWebView(url: String) {
        if (url.contains(CLOSE)) {
            isLoadedWebView = false
            dismiss()
        }
        if (url.contains(CLICK_ACCESS)) {
            isLoadedWebView = false
            dismiss()
        }
    }

    private fun progressVisibile() {
        if (layout_error != null && layout_error.visibility == View.VISIBLE) {
            layout_error.visibility = View.GONE
        }

        progress.visibility = View.VISIBLE
        webViewLightHouseToHire.visibility = View.GONE
    }

    private fun webViewVisible() {
        if (isVisible){
            progress.visibility = View.GONE
            webViewLightHouseToHire.visibility = View.VISIBLE
        }
    }

    private fun layoutErrorTryAgainVisible() {
        webViewLightHouseToHire.visibility = View.GONE
        progress.visibility = View.GONE
        layout_error.visibility = View.VISIBLE
        configureClose()
    }

    private fun configureClose() {
        close.visibility = View.VISIBLE
        close.setOnClickListener {
            isLoadedWebView = true
            dismiss()
        }
    }

    private fun configureTryAgain() {
        buttonTryAgain.setOnClickListener {
            isLoadedWebView = true
            configureWebview()
        }
    }

}