package br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.onboarding

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Action.VOLTAR
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_MESSAGE_INFO_ACTIVITY
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_TITLE_INFO_ACTIVITY
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.PAGAMENTO_POR_LINK
import br.com.mobicare.cielo.pagamentoLink.delivery.InfoActivity
import kotlinx.android.synthetic.main.onboarding_loggi_step_03.*
import org.jetbrains.anko.startActivity

class OnboardingLoggiStep03 : BaseFragment(), CieloNavigationListener {

    private var cieloNavigation: CieloNavigation? = null

    companion object {
        fun newInstance(bundle: Bundle) = OnboardingLoggiStep03().apply { this.arguments = bundle }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.onboarding_loggi_step_03, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar(getString(R.string.onboarding_name))
            this.cieloNavigation?.showButton(false)
            this.cieloNavigation?.showContent(true)
        }
    }

    override fun onHelpButtonClicked() {
        requireContext().startActivity<InfoActivity>(
                ARG_PARAM_TITLE_INFO_ACTIVITY to R.string.text_title_info_como_funciona_loggi,
                ARG_PARAM_MESSAGE_INFO_ACTIVITY to R.string.text_loggi_information)
    }

    override fun onBackButtonClicked(): Boolean {
        gaSendSteps(VOLTAR)
        return super.onBackButtonClicked()
    }

    fun initView() {
        btnNextOnboarding.isEnabled = true
        txtEscolherOutraModalidade.paintFlags = txtEscolherOutraModalidade.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG
        logicalThirdPage()
    }

    fun logicalThirdPage() {

        checkBoxSaveUserData.setOnCheckedChangeListener { buttonView, isChecked ->
            //checkbox status
            checkBoxSaveUserData.background = if (isChecked) ContextCompat.getDrawable(requireContext(), R.drawable.sl_checkbox_check) else ContextCompat.getDrawable(requireContext(), R.drawable.sl_checkbox_uncheck)
            //button status
            btnNextOnboarding.isEnabled = if (checkBoxSaveUserData.isChecked) true else false
            //button background
            btnNextOnboarding.background = if (checkBoxSaveUserData.isChecked) ContextCompat.getDrawable(requireContext(), R.drawable.sl_next_btn_enable) else ContextCompat.getDrawable(requireContext(), R.drawable.sl_next_btn_disable)
            //text backbround
            appCompatTextView4.setTextColor(if (checkBoxSaveUserData.isChecked) ContextCompat.getColor(requireContext(), R.color.color_017CEB) else ContextCompat.getColor(requireContext(), R.color.color_5A646E))
            gaSendSteps("checkbox")
        }
        btnNextOnboarding.setOnClickListener {
            gaSendSteps("continuar")
            findNavController().navigate(OnboardingLoggiStep03Directions.actionOnboardingLoggiStep03ToCollectAddressFragment())
        }

        txtEscolherOutraModalidade.setOnClickListener {
            gaSendSteps("escolher outra modalidade")
            findNavController().navigate(OnboardingLoggiStep03Directions.actionOnboardingLoggiStep03ToFormaEnvioFragment())
        }

    }

    private fun gaSendSteps(buttonType: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.BOTAO, "entrega loggi"),
                label = listOf(buttonType, "solicite", "clicado")
            )
        }
    }
}