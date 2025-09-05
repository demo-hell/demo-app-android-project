package br.com.mobicare.cielo.chat.presentation.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.widget.TextView
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chat.domains.EnumFeatures
import br.com.mobicare.cielo.chat.domains.EnumIconsDialogGeneric
import br.com.mobicare.cielo.chat.domains.ParamsUrl
import br.com.mobicare.cielo.chat.presentation.utils.ChatApollo
import br.com.mobicare.cielo.chat.presentation.utils.ClientWebView
import br.com.mobicare.cielo.chat.presentation.utils.GenericDialog
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import kotlinx.android.synthetic.main.chat_dialog.*

class ChatDialog : Dialog, YesNoDialogInterface {

    private var currentActivity: Activity
    private var enumFeatures = EnumFeatures.CHAT
    private var ec = ""
    private var token = ""
    lateinit var yesNoDialogInterface: YesNoDialogInterface
    lateinit var dialog: GenericDialog

    constructor(activity: Activity, enumFeatures: EnumFeatures, path: String) : super(activity as Context, android.R.style.Theme_Holo_Light_NoActionBar) {
        this.enumFeatures = enumFeatures
        this.currentActivity = activity
        Companion.path = path
    }

    constructor(activity: Activity, enumFeatures: EnumFeatures, ec: String, token: String, path: String) : super(activity as Context, android.R.style.Theme_Holo_Light_NoActionBar) {
        this.enumFeatures = enumFeatures
        this.ec = ec
        this.token = token
        this.currentActivity = activity
        Companion.path = path
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(onSaveInstanceState())
        this.setContentView(R.layout.chat_dialog)

        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        configWebView()
        chooseFeature(enumFeatures)
        configClose()
        configMinimize()
        configTitle()
        yesNoDialogInterface = this
        webV = webView

        webView.setOnKeyListener(object : DialogInterface.OnKeyListener, View.OnKeyListener {
            override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                return false
            }

            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event!!.getAction() === KeyEvent.ACTION_DOWN) {
                    ChatDialog.webV!!.visibility = View.GONE
                    val webView = v as WebView
                    webView.goBack()
                }

                return false
            }

        })
    }

    override fun onStart() {
        super.onStart()
        setChatWebView()
    }

    private fun configClose() {
        btnClose.setOnClickListener { v ->

            if (Utils.isNetworkAvailable(currentActivity)) {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, Category.TAPICON),
                    action = listOf("$path$ASSISTENTE_CIELO"),
                    label = listOf("Encerrar Chat Cielo")
                )

                val title = context.getString(R.string.title_alert)
                val message = context.getString(R.string.message_alert)
                ChatApollo.showAlertConfirm(currentActivity, message, title, yesNoDialogInterface,
                        context.getString(R.string.close), context.getString(R.string.cancel), "$path$ASSISTENTE_CIELO")
            } else {
                currentActivity.showMessage(currentActivity.getString(R.string.title_error_wifi_subtitle),
                        title = currentActivity.getString(R.string.title_error_wifi_title))
            }


        }
    }


    private fun configMinimize() {
        btnMinimize.setOnClickListener {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAPICON),
                action = listOf("$path$ASSISTENTE_CIELO"),
                label = listOf("Minimizar Chat Cielo")
            )
            this.hide()
        }
    }

    private fun chooseFeature(enumFeatures: EnumFeatures) {
        val feature = enumFeatures.desc
        if (feature.equals(EnumFeatures.CHAT.desc, ignoreCase = true)) {
            setChatWebView()
        }
    }

    private fun configWebView() {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        webView.webViewClient = ClientWebView(currentActivity, webView)
    }

    private fun setChatWebView() {
        val userObj: UserObj? = MenuPreference.instance.getUserObj()

        val url = if (userObj != null && !ec.isEmpty() && token.isNotEmpty())
            BuildConfig.CHAT_URL + ParamsUrl.token.desc + token + ParamsUrl.merchant.desc + ec + "&nome=" + userObj?.name?.replace(" ", "_") + "&email=" + userObj?.email
         else
            BuildConfig.CHAT_URL

        webView.loadUrl(url)
    }



    override fun onDialogPositiveClick() {
        dismiss()

        webView.loadUrl("about:blank")

        if (!ClientWebView.url.contains("xgen")) {
            dialog = GenericDialog(currentActivity, EnumFeatures.RATING, context.getString(R.string.assistant_rating), EnumIconsDialogGeneric.CLOSE.desc, ec, token, path)
            dialog.showDialog()
        }
    }

    override fun onDialogNegativeClick() {}

    private fun configTitle() {
        val textView = findViewById(R.id.txtTitle) as TextView
        ChatApollo.configFont(textView, currentActivity)
    }

    companion object {
        var dialog: ChatDialog? = null
        const val ASSISTENTE_CIELO = "/AssistenteCielo"
        lateinit var path: String
        var webV: WebView?= null

        fun showDialog(activity: Activity, enumFeatures: EnumFeatures, ec: String, token: String, path: String) {
            if (dialog == null || !dialog!!.currentActivity.equals(activity)) {
                dialog = ChatDialog(activity, enumFeatures, ec, token, path)
            }
            show(activity)
            Handler().postDelayed({
                //doSomethingHere()
                webV!!.visibility = View.VISIBLE
            }, 2000)
            sendGaScreenView(activity)
        }

        fun showDialog(activity: Activity, enumFeatures: EnumFeatures, path: String) {
            if (dialog == null || !dialog!!.currentActivity.equals(activity)) {
                dialog = ChatDialog(activity, enumFeatures, path)
            }
            show(activity)
            Handler().postDelayed({
                //doSomethingHere()
                webV!!.visibility = View.VISIBLE
            }, 2000)
            sendGaScreenView(activity)
        }

        fun show(activity: Activity) {
            if (activity != null && activity.isFinishing) {
                return
            }

            if (!activity.isFinishing) {
                Analytics.trackScreenView(
                    screenName = "$path${ASSISTENTE_CIELO}",
                    screenClass = activity.javaClass
                )
                dialog?.show()
                return
            }

            if (dialog?.window?.decorView?.visibility == View.GONE) {
                dialog?.window?.decorView?.visibility = View.VISIBLE
            }
            sendGaScreenView(activity)
        }

        //region GaFirebase

        private fun sendGaScreenView(activity: Activity) {
            Analytics.trackScreenView(
                screenName = "/chat",
                screenClass = activity.javaClass
            )
        }

        //endregion
    }


}