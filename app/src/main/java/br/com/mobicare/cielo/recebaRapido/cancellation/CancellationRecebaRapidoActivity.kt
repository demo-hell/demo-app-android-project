package br.com.mobicare.cielo.recebaRapido.cancellation

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.newRecebaRapido.analytics.RAGA4
import br.com.mobicare.cielo.taxaPlanos.TAXA_PLANOS_PLAN
import kotlinx.android.synthetic.main.activity_cancellation_receba_rapido.*
import org.koin.android.ext.android.inject

class CancellationRecebaRapidoActivity : BaseLoggedActivity(), CancellationRRListener {

    private var listener: CancelationRRActionListener? = null
    private var planName: String? = ""

    private val ga4: RAGA4 by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancellation_receba_rapido)
        setupToolbar(toolbar as Toolbar, getString(R.string.toolbar_receba_rapido))
        init()
    }

    override fun onResume() {
        super.onResume()
        ga4.logDisplayContent(
            screenName = RAGA4.SCREEN_VIEW_RA_CANCEL,
            contentComponent = RAGA4.WHY_YOU_WANT_TO_CANCEL,
            contentType = GoogleAnalytics4Values.MODAL
        )
    }

    private fun init() {
        textViewDone?.setOnClickListener {
            textViewDone?.text?.toString()?.let { label ->
                listener?.callButtonDone(label)
            }
        }
        buttonBackFinish?.setOnClickListener {
            gaSendButton(buttonBackFinish.getText())
            listener?.callButtonFinish(buttonBackFinish.getText())
        }
    }
    override fun setActionListener(listener: CancelationRRActionListener) {
        this.listener = listener
    }

    override fun showButtonDone(isShow: Boolean) {
        if (isShow) textViewDone?.visible() else textViewDone?.gone()
    }

    override fun setTextButtonFinish(text: String) {
        buttonBackFinish?.setText(text)
    }

    override fun setTextButtonDone(text: String) {
        textViewDone?.text = text
    }

    override fun getPlanName(): String {
        planName = intent.extras?.getString(TAXA_PLANOS_PLAN)
        return planName ?: ""
    }

    override fun showToolbarBackButton(isShow: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(isShow)
    }

    override fun enableFirstButton(enable: Boolean) {
        textViewDone?.isEnabled = enable
    }

    override fun showContent() {
        progressBar?.gone()
        errorView?.gone()
        constraintLayoutContent?.visible()
    }

    override fun showLoading() {
        constraintLayoutContent?.gone()
        errorView?.gone()
        progressBar?.visible()
    }

    override fun showError() {
        constraintLayoutContent?.gone()
        progressBar?.gone()
        errorView?.visible()
        errorView?.configureActionClickListener {
            listener?.buttonRetry(errorView.errorButton?.getText() ?: "")
        }
    }

    private fun gaSendButton(labelButton: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.CANCELAR_RR),
            action = listOf(Action.CLICK, Label.BOTAO),
            label = listOf(labelButton, planName ?: "")
        )
    }
}

interface CancellationRRListener {

    fun setActionListener(listener: CancelationRRActionListener)
    fun showButtonDone(isShow: Boolean)
    fun setTextButtonFinish(text: String)
    fun setTextButtonDone(text: String)
    fun showToolbarBackButton(isShow: Boolean)
    fun enableFirstButton(enable: Boolean)
    fun showContent()
    fun showLoading()
    fun showError()
    fun getPlanName(): String
}

interface CancelationRRActionListener {
    fun callButtonDone(buttonLabel: String = "") {}
    fun callButtonFinish(buttonLabel: String = "") {}
    fun buttonRetry(buttonLabel: String = "") {}
}