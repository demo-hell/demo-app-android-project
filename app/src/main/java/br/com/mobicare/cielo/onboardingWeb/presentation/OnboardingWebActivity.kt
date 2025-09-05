package br.com.mobicare.cielo.onboardingWeb.presentation

import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.databinding.ActivityOnboardingWebBinding
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.analytics.DatadogEvent
import br.com.mobicare.cielo.commons.constants.OnboardingWeb
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.onboardingWeb.bridges.UnicoSdkBridge

class OnboardingWebActivity :  BaseActivity() {
    private var binding: ActivityOnboardingWebBinding? = null
    private var bundle: Bundle? = null
    private lateinit var datadogEvent: DatadogEvent
    private lateinit var unicoSdkBridge: UnicoSdkBridge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        datadogEvent = DatadogEvent(this.applicationContext, UserPreferences.getInstance())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        binding = ActivityOnboardingWebBinding.inflate(layoutInflater)

        savedInstanceState?.let{
            this.bundle = it
        }

        binding?.apply{
            setContentView(root)
        }

        val webview = findViewById<OnboardingWebview>(R.id.webview)

        webview.getWebview().loadUrl(BuildConfig.CREDECIAMENTO_URL)

        unicoSdkBridge = UnicoSdkBridge(applicationContext, this, webview.getWebview())
        val javascriptUnicoSdk = unicoSdkBridge.JavascriptInterface()

        webview.setCustomJavascriptBridge(javascriptUnicoSdk, OnboardingWeb.BRIDGE_NAME)
    }

    override fun onPause() {
        super.onPause()
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento:OnboardingWebActivity pausada - App pode estar indo para o segundo plano",
            key = "Novo Credenciamento: AppBackground",
            value = "OnboardingWebActivity onPause"
        )
    }

    override fun onResume() {
        super.onResume()
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento:OnboardingWebActivity retomada - App voltou ao primeiro plano",
            key = "Novo Credenciamento: AppForeground",
            value = "OnboardingWebActivity onResume"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento:OnboardingWebActivity destruída - App pode estar sendo fechado",
            key = "Novo Credenciamento: AppClose",
            value = "OnboardingWebActivity onDestroy"
        )
    }

}