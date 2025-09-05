package br.com.mobicare.cielo.commons.ui.fragment

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.AUTOCADASTRO_NAO_CLIENTE
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.login.analytics.LoginAnalytics
import kotlinx.android.synthetic.main.fragment_client_webview_common.*

class CommonWebviewClientFragment : BaseFragment() {

    private val loginAnalytics = LoginAnalytics()
    companion object {
        fun create(url: String): CommonWebviewClientFragment =
                CommonWebviewClientFragment().apply {
                    this.mainUrl = url
                }
    }


    var mainUrl: String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_client_webview_common,
                container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //GA
        Analytics.trackScreenView(AUTOCADASTRO_NAO_CLIENTE)
        loginAnalytics.logFirstAccessCommonClientScreenViewGa(this.javaClass)
        webCommonContent.settings.javaScriptEnabled = true
        webCommonContent.settings.domStorageEnabled = true
        webCommonContent.loadUrl(mainUrl ?: "")
        webCommonContent.webViewClient = (object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                frameCommonWebviewProgress?.let {
                    frameCommonWebviewProgress.visibility = View.VISIBLE
                }
            }

            override fun onPageFinished(view: WebView?, url: String) {
                frameCommonWebviewProgress?.let {
                    frameCommonWebviewProgress.visibility = View.GONE
                }
            }

            override fun onReceivedSslError(view: WebView,
                                            handler: SslErrorHandler, error: SslError) {

                val baseActivity = requireActivity() as BaseActivity
                baseActivity.showOptionDialogMessage(dialogTitle =
                getString(R.string.text_webview_ssl_warning_title)) {
                    this.setMessage(getString(R.string.text_webview_ssl_message))
                    this.setBtnLeft(getString(R.string.cancelar))
                    this.setBtnRight(getString(R.string.dialog_btn_continuar))
                    this.setOnclickListenerLeft {
                        if (isAdded) {
                            view.loadUrl("about:blank")
                            Toast.makeText(requireActivity(), "Houve um erro de conexão, tente novamente mais tarde", Toast.LENGTH_LONG).show()
                            val baseActivity = requireActivity() as BaseActivity
                            baseActivity.finish()
                        }
                    }
                    this.setOnclickListenerRight {
                        handler.proceed()
                    }
                }

            }
        })


    }
}