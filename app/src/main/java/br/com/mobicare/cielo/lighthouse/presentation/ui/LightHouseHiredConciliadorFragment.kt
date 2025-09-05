package br.com.mobicare.cielo.lighthouse.presentation.ui


import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.router.APP_ANDROID_MENU
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.getHeadersLightHouse
import br.com.mobicare.cielo.commons.utils.handleSslError
import br.com.mobicare.cielo.commons.utils.loadUrlWithHeaders
import br.com.mobicare.cielo.main.domain.Menu
import kotlinx.android.synthetic.main.fragment_lighthouse_hired.*
import kotlinx.android.synthetic.main.layout_error_lighthouse.*

class LightHouseHiredConciliadorFragment : BaseFragment() {

    private var menu: Menu? = null
    private var isLoadedWebView = true

    companion object{

        const val  CLOSE_CONCILIADOR = "conciliador#close"
        const val SESSION_EXPIRED = "#sessao-expirada"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadArguments()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_lighthouse_hired, container, false)
    }

    override fun onStart() {
        super.onStart()
        if (Utils.isNetworkAvailable(requireActivity())) {
            configureWebView()
        } else {
            callError()
        }
    }

    private fun loadArguments() {
        this.arguments?.let {
            this.menu = it.getParcelable(APP_ANDROID_MENU)
        }
    }

    private fun configureWebView(){
        if (!Utils.isNetworkAvailable(requireActivity())) {
            callError()
            return
        }

        var url = BuildConfig.URL_FAROL_CONCILIADOR
        this.menu?.let {
            it.menuTarget.url?.let { itUrl ->
                url = itUrl
            }
        }

        webViewLightHouseHired.loadUrlWithHeaders(url,
                webViewLightHouseHired.getHeadersLightHouse(), object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                progressVisibile()
            }

            override fun onPageFinished(view: WebView?, url: String) {
                if (isLoadedWebView) {
                    if (webViewBackPress(url)) return
                    webViewVisible()
                }
            }
                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler,
                    error: SslError?
                ) {
                    isLoadedWebView = false
                    callError()
                    view?.handleSslError(
                        handler,
                        requireContext())
                }

        })
    }

    private fun callError() {
        layoutErrorTryAgainVisible()
        configureTryAgain()
        configureClose()
    }

    private fun webViewBackPress(url: String): Boolean {
        if (url.contains(CLOSE_CONCILIADOR)) {
            activity?.onBackPressed()
            return true
        } else if (url.contains(SESSION_EXPIRED)) {
            activity?.onBackPressed()
            return true
        }
        return false
    }

    private fun configureClose() {
        if (isAdded) {
            close.visibility = View.GONE
        }
    }

    private fun layoutErrorTryAgainVisible(){

        if (isAdded) {
            webViewLightHouseHired.visibility = View.GONE
            progress.visibility = View.GONE
            layout_error.visibility = View.VISIBLE
        }
    }

    private fun webViewVisible(){

        if (isAdded) {
            if (layout_error != null && layout_error.visibility == View.VISIBLE){
                layout_error.visibility = View.GONE
            }

            progress.visibility = View.GONE
            webViewLightHouseHired.visibility = View.VISIBLE
            layout_error.visibility = View.GONE
        }
    }

    private fun progressVisibile(){
        if (isAdded) {
            progress.visibility = View.VISIBLE
            webViewLightHouseHired.visibility = View.GONE
            layout_error.visibility = View.GONE
        }
    }

    private fun configureTryAgain(){
        if (isAdded) {
            buttonTryAgain.setOnClickListener {
                isLoadedWebView = true
                configureWebView()
            }
        }
    }

}
