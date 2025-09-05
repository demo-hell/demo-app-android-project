package br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.onboarding

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
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.pagamentoLink.delivery.InfoActivity
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.PAGAMENTO_POR_LINK
import kotlinx.android.synthetic.main.onboarding_loggi_step_01.*
import org.jetbrains.anko.startActivity


/**
 * @author Enzo Teles 23/04/2020
 *
 * */
class OnboardingLoggiStep01 : BaseFragment(), CieloNavigationListener {

    private var cieloNavigation: CieloNavigation? = null

    companion object {
        fun newInstance(bundle: Bundle) = OnboardingLoggiStep01().apply { this.arguments = bundle }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.onboarding_loggi_step_01, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        configureNavigation()
        UserPreferences.getInstance().setShowOnboarding(UserPreferences.ONBOARDING.SUPERLINK, false)
    }

    fun initView() {
        btnNextOnboarding.isEnabled = false
        txtEscolherOutraModalidade.paintFlags = txtEscolherOutraModalidade.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG
        logicalFirstPage()
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar(getString(R.string.onboarding_name))
            this.cieloNavigation?.showButton(false)
            this.cieloNavigation?.setNavigationListener(this)
            this.cieloNavigation?.showContent(true)
        }
    }

    override fun onBackButtonClicked(): Boolean {
        gaSendSteps(VOLTAR)
        return super.onBackButtonClicked()
    }

    override fun onHelpButtonClicked() {
        requireContext().startActivity<InfoActivity>(
                ARG_PARAM_TITLE_INFO_ACTIVITY to R.string.text_title_info_como_funciona_loggi,
                ARG_PARAM_MESSAGE_INFO_ACTIVITY to R.string.text_loggi_information)
    }

    fun logicalFirstPage() {
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
            findNavController().navigate(OnboardingLoggiStep01Directions.actionOnboardingLoggiStep01ToOnboardingLoggiStep02())
        }

        txtEscolherOutraModalidade.setOnClickListener {
            gaSendSteps("escolher outra modalidade")
            this.arguments?.let {
                findNavController().popBackStack()
            }
        }
    }

    private fun gaSendSteps(buttonType: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.BOTAO, "entrega loggi"),
                label = listOf(buttonType, "compartilhe", "clicado")
            )
        }
    }
}