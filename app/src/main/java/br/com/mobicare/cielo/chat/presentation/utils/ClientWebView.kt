package br.com.mobicare.cielo.chat.presentation.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Message
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.handleSslError

/**
 * Created by gustavon on 27/09/17.
 */
class ClientWebView(private val activity: Activity, private val webView: WebView) : WebViewClient() {


    var dialogWebView: LoadingDialog = LoadingDialog(activity.baseContext)

    @SuppressLint("NewApi")
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        return false
    }

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        if(!activity.isFinishing){
            dialogWebView.showDialog(activity)
        }
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        if(!activity.isFinishing){
            dialogWebView.dismissDialog()
        }

        if (url.contains("xgen") && !url.contains("LoadInteraction")) {
            this.webView.loadUrl("javascript:(function() { " +
                    "document.getElementById('topo').style.visibility = \"hidden\"" +
                    "})()")

            this.webView.loadUrl("javascript:(function removeBar() {" +
                    " var theBar = document.getElementsByClassName('barra');" +
                    " for(var i=0; i < theBar.length; i++) {\n" +
                    "     theBar[i].style.visibility = 'hidden'\n" +
                    " }" +
                    "})()")

            this.webView.loadUrl("javascript:(function() {" +
                    " removeBar();" +
                    " }" +
                    "})()")

            this.webView.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName('aviso_cielo')[0].style.color = \"black\"" +
                    "})()")

            this.webView.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName('aviso_cielo')[0].style.background = \"white\"" +
                    "})()")

        }

        val node = "chat_node="
        if (url.contains(node)) {
            chatNode = url.substring(url.indexOf(node) + node.length, url.length)
        }
    }

    override fun onFormResubmission(view: WebView, dontResend: Message, resend: Message) {
        super.onFormResubmission(view, dontResend, resend)
    }
    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
        view?.handleSslError(handler, activity)
        if(!activity.isFinishing){
            dialogWebView.dismissDialog()
        }
        view?.loadUrl("about:blank")
        Toast.makeText(activity, activity?.resources?.getString(R.string.error_message), Toast.LENGTH_LONG).show()

    }


    companion object {
        var chatNode = "bemvindo"
        var url = ""
    }

}