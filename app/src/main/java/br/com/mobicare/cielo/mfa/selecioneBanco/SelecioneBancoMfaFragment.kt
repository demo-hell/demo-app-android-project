package br.com.mobicare.cielo.mfa.selecioneBanco

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.button.CieloBaseRadioButton
import br.com.cielo.libflue.button.CieloRadioGroup
import br.com.cielo.libflue.button.CieloRegularTextOutlineSelectRadioButton
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUBCATEGORY_NAME
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.mfa.MfaAccount
import br.com.mobicare.cielo.mfa.merchantstatus.challenge.JURIDICA
import br.com.mobicare.cielo.mfa.onbordingSuccess.OnboardingSuccessBottomSheetFragment
import br.com.mobicare.cielo.mfa.resume.InsertCNPJBottomSheetFragment
import br.com.mobicare.cielo.mfa.resume.InsertCNPJBottomSheetFragmentContract
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import kotlinx.android.synthetic.main.fragment_selecione_banco_mfa.*
import kotlinx.android.synthetic.main.layout_item_conta_corrente.view.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val SELECT_BANK_MFA = "SELECT_BANK_MFA"

class SelecioneBancoMfaFragment : BaseFragment(), SelecioneBancoMfaContract.View,
        CieloNavigationListener {

    var title: String? = null
    var passOrAt: String? = null
    val presenter: SelecioneBancoMfaPresenter by inject {
        parametersOf(this)
    }

    private var cieloNavigation: CieloNavigation? = null
    private var listAccounts: List<MfaAccount>? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_selecione_banco_mfa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //ga verification
        UserPreferences.getInstance().keepStatusMfa?.let { stMfa ->
            title = if (stMfa) MFA_NOVO_TOKEN else MFA_NOVO_TOKEN_TROCA
        }
        validationShowOnboarding()
        configureNavigation()
        configureListeners()
        this.presenter.load()
    }

    private fun validationShowOnboarding() {
        val isToShowMfaOnboarding = UserPreferences.getInstance()
                .isToShowOnboarding(UserPreferences.ONBOARDING.MFA)

        passOrAt = if (isToShowMfaOnboarding) MFA_PASSIVO else MFA_ATIVO

    }

    private fun insertCNPJ(mfaAccount: MfaAccount) {
        InsertCNPJBottomSheetFragment.create(mfaAccount, object : InsertCNPJBottomSheetFragmentContract {
            override fun verifyData(mfaAccount: MfaAccount) {
                findNavController()
                        .navigate(
                                SelecioneBancoMfaFragmentDirections
                                        .actionSelecioneBancoMfaFragmentToResumeBankAndCNPJFragment(mfaAccount, true)
                        )
            }

            override fun dismiss() {
                listAccounts?.let {
                    show(it)
                }?: run {
                    presenter.load()
                }
            }

        }).show(childFragmentManager, SELECT_BANK_MFA)
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar("Token")
            this.cieloNavigation?.setTextButton("Enviar")
            this.cieloNavigation?.showHelpButton(true)
            this.cieloNavigation?.showButton(true)
            this.cieloNavigation?.enableButton(false)
            this.cieloNavigation?.setNavigationListener(this)
        }
    }

    private fun configureListeners() {
        this.radioGroupView?.setRadioButtonListener(object : CieloRadioGroup.RadioButtonListener {
            override fun onItemSelected(button: CieloBaseRadioButton) {

                val mfaAccount = button.tag as MfaAccount
                if (mfaAccount.identificationNumber.isNullOrEmpty().not() && mfaAccount.legalEntity.isNullOrEmpty().not() && mfaAccount.legalEntity == JURIDICA) {
                    insertCNPJ(mfaAccount)
                } else {
                    this@SelecioneBancoMfaFragment.presenter.selectedItem(mfaAccount)
                    gaChooseRadionButton((button.tag as MfaAccount).bankName ?: "")
                }
            }
        })
    }

    override fun show(accounts: List<MfaAccount>) {
        listAccounts = accounts
        this.cieloNavigation?.showContent(true)
        val contentLayout = LinearLayout(this.context)
        contentLayout.orientation = LinearLayout.VERTICAL
        contentLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        this.radioGroupView?.removeAllViews()
        accounts.forEach {
            contentLayout.addView(this.createRadioButton(it))
        }
        this.radioGroupView?.addView(contentLayout)
    }

    override fun showLoading(isVisible: Boolean) {
        this.cieloNavigation?.showLoading(isVisible)
    }

    override fun enableNextButton(isEnabled: Boolean) {
        this.cieloNavigation?.enableButton(isEnabled)
    }

    override fun onHelpButtonClicked() {
        gaHelpButton()
        findNavController()
                .navigate(
                        SelecioneBancoMfaFragmentDirections
                                .actionSelecioneBancoMfaFragmentToCentralAjudaPerguntasFragmentMFA()
                )
        this.cieloNavigation?.showContent(true)
    }

    override fun showSuccessful() {
        gaMsgCallbackSuccess()
        val text = getString(R.string.text_status_mfa_pending)
        val buttonText = getString(R.string.text_close)
        val ftsucessBS = OnboardingSuccessBottomSheetFragment().apply {
            this.subTitleFirst = text
            this.buttonText = buttonText
            this.listener =
                    object : OnboardingSuccessBottomSheetFragment.OnboardingSuccessListener {
                        override fun onButtonClicked() {
                            this@SelecioneBancoMfaFragment.requireActivity().finish()
                        }

                        override fun onSwipeClosed() {
                            this@SelecioneBancoMfaFragment.requireActivity().finish()
                        }
                    }
        }

        ftsucessBS.show(
                requireActivity().supportFragmentManager,
                "NewBottomSheetGeneric"
        )
    }

    override fun showError(error: ErrorMessage) {
        this.cieloNavigation?.showError(error)
        gaMsgCallbackError(error)
    }

    override fun onRetry() {
        super.onRetry()
        presenter.load()
    }

    override fun showTemporarilyBlockError(error: ErrorMessage) {
        this.cieloNavigation?.showError(getString(R.string.text_view_temporarily_block_title_mfa),
                getString(R.string.text_view_temporarily_block_subtitle_mfa),
                getString(R.string.text_lgpd_saiba_mais), R.drawable.ic_07, View.OnClickListener {
            requireActivity().startActivity<CentralAjudaSubCategoriasEngineActivity>(
                    ConfigurationDef.TAG_KEY_HELP_CENTER to ConfigurationDef.TAG_HELP_CENTER_MFA,
                    ARG_PARAM_SUBCATEGORY_NAME to getString(R.string.text_token),
                    CentralAjudaSubCategoriasEngineActivity.NOT_CAME_FROM_HELP_CENTER to true)
        })
    }

    override fun showIneligible() {
        this.cieloNavigation?.showIneligibleUser(getString(R.string.text_mfa_inelegible_merchant_message))
    }

    override fun onButtonClicked(labelButton: String) {
        this.presenter.send()
        gaSendButton()
    }

    private fun createRadioButton(account: MfaAccount): View {
        val radioButton = CieloRegularTextOutlineSelectRadioButton(this.requireContext())
        val layout = LayoutInflater.from(requireContext())
                .inflate(R.layout.layout_item_conta_corrente, null, false) as ConstraintLayout
        layout.nomeBancoTextView?.text = account.bankName
        layout.agenciaTextView?.text = account.agency
        layout.contaCorrenteTextView?.text = "${account.account}-${account.accountDigit}"
        ImageUtils.loadImage(
                layout.bandeiraImageView,
                account.imgSource,
                R.drawable.ic_generic_brand
        )
        radioButton.tag = account
        radioButton.setLayout(layout)
        radioButton.doCentralizeSelectImage()
        radioButton.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        (radioButton.layoutParams as LinearLayout.LayoutParams).setMargins(0, 0, 0, 16)
        radioButton.requestLayout()
        return radioButton
    }

    //ga
    fun gaChooseRadionButton(name: String) {
        passOrAt?.let { st ->
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, MFA_NOVO_TOKEN_LOGIN),
                action = listOf(Action.ONBOARDING, st, Action.SELECAO, MFA_DOMICILIO),
                label = listOf(name)
            )
        }
    }

    private fun gaHelpButton() {
        passOrAt?.let { st ->
            title?.let {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, it),
                    action = listOf(Action.ONBOARDING, st, Action.CLIQUE, Action.ICONE),
                    label = listOf(MFA_FAQ, MFA_TELA_CONFIGURACAO)
                )
            }
        }
    }

    fun gaSendButton() {
        passOrAt?.let { st ->
            title?.let {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, it),
                    action = listOf(Action.ONBOARDING, st, Action.CLIQUE, Action.BOTAO),
                    label = listOf(Label.ENVIAR)
                )
            }
        }
    }

    private fun gaMsgCallbackError(error: ErrorMessage) {
        passOrAt?.let { st ->
            title?.let {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, it),
                    action = listOf(Action.CALLBACK, MFA_CONFIGURACAO_TOKEN, st),
                    label = listOf(ERRO, error.errorMessage, "${error.httpStatus}")
                )
            }
        }
    }

    private fun gaMsgCallbackSuccess() {
        passOrAt?.let { st ->
            title?.let {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, it),
                    action = listOf(Action.CALLBACK, MFA_CONFIGURACAO_TOKEN, st),
                    label = listOf(SUCESSO)
                )
            }
        }
    }
    //end ga
}