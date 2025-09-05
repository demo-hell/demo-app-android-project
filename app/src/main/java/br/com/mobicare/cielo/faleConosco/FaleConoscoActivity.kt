package br.com.mobicare.cielo.faleConosco

import android.os.Bundle
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chat.presentation.utils.ChatApollo
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.HOME_LOGADA
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.WhatsApp.WHATSAPP_MESSAGE_TEXT
import br.com.mobicare.cielo.commons.helpers.AppHelper
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.databinding.FaleConoscoActivityBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.CENTRAL_AJUDA_WHATSAPP
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.CHAT

class FaleConoscoActivity : BaseActivity() {

    private lateinit var binding: FaleConoscoActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FaleConoscoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.toolbar.root, "Fale conosco")
        configureViews()
        configureListeners()
    }

    private fun configureViews() {

        val isShowWhatsApp = FeatureTogglePreference
            .instance
            .getFeatureTogle(CENTRAL_AJUDA_WHATSAPP)
        val isShowChat = FeatureTogglePreference
            .instance
            .getFeatureTogle(CHAT)

        if (isShowWhatsApp) binding.whatsAppLayout.visible() else binding.whatsAppLayout.gone()
        if (isShowChat) binding.chatLayout.visible() else binding.chatLayout.gone()
    }

    private fun configureListeners() {
        binding.whatsAppLayout.setOnClickListener {
            gaButtonEvent("abrir whatsapp")
            openWhatsApp()
        }
        binding.chatLayout.setOnClickListener {
            gaButtonEvent("abrir chat")
            openChat()
        }
    }

    private fun openChat() {
        ChatApollo.callChat(this)
    }

    private fun openWhatsApp() {
        AppHelper.showWhatsAppMessage(this, "+${getString(R.string.contact_cielo_general_doubts_whatsapp_number)}", WHATSAPP_MESSAGE_TEXT)
    }

    private fun gaButtonEvent(status: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, HOME_LOGADA),
                action = listOf(Action.HEADER),
                label = listOf(Label.ICONE, status)
            )
        }
    }

}