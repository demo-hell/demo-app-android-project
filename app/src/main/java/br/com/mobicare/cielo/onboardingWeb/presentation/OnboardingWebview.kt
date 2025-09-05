package br.com.mobicare.cielo.onboardingWeb.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.constraintlayout.widget.ConstraintLayout
import br.com.mobicare.cielo.databinding.LayoutOnboardingWebviewBinding
import br.com.mobicare.cielo.R

class OnboardingWebview : ConstraintLayout {
    var url: String? = null
    private var _binding: LayoutOnboardingWebviewBinding? = null
    private val binding get() = requireNotNull(_binding)

    init {
        this.let { View.inflate(context, R.layout.layout_onboarding_webview, it) }
    }

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setUrl(attrs)
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setUrl(attrs)
        initialize()
    }

    private fun initialize() {
        val inflater = LayoutInflater.from(context)
        _binding = LayoutOnboardingWebviewBinding.inflate(inflater, this, true)
        setWebviewSettings()

        url?.let {
            binding.shellWebview.loadUrl(it)
        }
    }

    fun getWebview(): WebView {
        return binding.shellWebview
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebviewSettings() {
        binding.shellWebview.settings.javaScriptEnabled = true
        binding.shellWebview.settings.domStorageEnabled = true
        binding.shellWebview.settings.displayZoomControls = false
        binding.shellWebview.settings.builtInZoomControls = false
        binding.shellWebview.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        binding.shellWebview.overScrollMode = View.OVER_SCROLL_NEVER
        binding.shellWebview.setBackgroundColor(Color.WHITE)
    }

    @SuppressLint("JavascriptInterface")
    fun setCustomJavascriptBridge(bridge: Any, name: String) {
        binding.shellWebview.addJavascriptInterface(bridge, name)
    }

    private fun setUrl(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.Webview)
        url = typedArray.getString(R.styleable.Webview_url).toString()
        typedArray.recycle()
    }
}