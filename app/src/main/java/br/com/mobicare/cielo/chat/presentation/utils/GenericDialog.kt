package br.com.mobicare.cielo.chat.presentation.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chat.domains.EnumFeatures
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.extensions.activity
import kotlinx.android.synthetic.main.fragment_client_webview_common.*
import kotlinx.android.synthetic.main.generic_dialog.*

class GenericDialog : Dialog {
    private var enumFeatures: EnumFeatures? = null
    private var icon : String? = ""
    private var ec : String? = ""
    private var token : String? = ""
    private var title : String? = ""

    private var dialog: GenericDialog? = null
    private var currentActivity: Activity? = null
    lateinit var path: String

    constructor(context: Context, enumFeatures: EnumFeatures?, title: String?, icon: String?, ec: String?, token: String?, path: String) : super(context, android.R.style.Theme_Holo_Light_NoActionBar) {
        this.enumFeatures = enumFeatures
        this.icon = icon
        this.ec = ec
        this.token = token
        this.title = title
        this.path = path
        this.currentActivity = context.activity()
    }

    constructor(context: Context?, enumFeatures: EnumFeatures?, title: String?, icon: String?) :
            super(context!!, android.R.style.Theme_Holo_Light_NoActionBar) {
        this.enumFeatures = enumFeatures
        this.icon = icon
        this.title = title
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.generic_dialog)

        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        configWebView()
        configBtnRight()
        configTitle()
        setChatWebView()
    }


    private fun configTitle() {
        if (this.title?.isEmpty()!!) {
            val textView = findViewById(R.id.txtTitle) as TextView
            textView.text = this.title
            ChatApollo.configFont(textView, currentActivity!!)
        }
    }

    private fun configBtnRight() {
        btnRight.setOnClickListener{ v ->
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPICON),
                action = listOf("$path/   AssitenteDigitalCielo"),
                label = listOf("Encerrar Avaliação Do Chat Cielo")
            )
            this.dismiss()
            dialog = null;
        }
    }


    private fun configWebView() {
        val webSettings = webView!!.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webView!!.setWebViewClient(ClientWebView(currentActivity!!, webView!!))
    }

    fun setChatWebView() {
        if (enumFeatures!!.equals(EnumFeatures.RATING)) {
            webView!!.loadUrl(BuildConfig.URL_CHAT_AVALIACAO + "?chat_node=" + ClientWebView.chatNode)
        }
    }

    fun showDialog() {
        if (dialog == null || dialog!!.ownerActivity?.equals(this.currentActivity) == false) {
            dialog = GenericDialog(context, enumFeatures, title, icon, ec, token, path)
        }
        show(this.currentActivity)
    }

    private fun show(activity: Activity?) {
        currentActivity = activity

        if (currentActivity != null && currentActivity!!.isFinishing) {
            return
        }

        if (!dialog!!.isShowing && !currentActivity!!.isFinishing) {
            Analytics.trackScreenView(
                screenName = "$path/AvaliacaoAssitenteDigitalCielo",
                screenClass = activity?.javaClass
            )
            dialog!!.show()
            return
        }

        if (dialog!!.window!!.decorView.visibility == View.GONE) {
            dialog!!.window!!.decorView.visibility = View.VISIBLE
        }
    }

}
