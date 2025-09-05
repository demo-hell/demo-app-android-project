package br.com.mobicare.cielo.mfa.merchantstatus.challenge

import android.app.Dialog
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
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUBCATEGORY_NAME
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.mfa.MfaAccount
import br.com.mobicare.cielo.mfa.onbordingSuccess.OnboardingSuccessBottomSheetFragment
import br.com.mobicare.cielo.mfa.resume.InsertCNPJBottomSheetFragment
import br.com.mobicare.cielo.mfa.resume.InsertCNPJBottomSheetFragmentContract
import br.com.mobicare.cielo.mfa.selecioneBanco.SELECT_BANK_MFA
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.dialog.BottomSheetGenericFragment
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import kotlinx.android.synthetic.main.fragment_selecione_banco_mfa.*
import kotlinx.android.synthetic.main.layout_item_conta_corrente.view.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val JURIDICA = "JURIDICA"

class MerchantValidateChallengeFragment : BaseFragment(), MerchantValidateChallengeView,
        CieloNavigationListener {

    private var listAccounts: List<MfaAccount>? = null
    private var cieloNavigation: CieloNavigation? = null
    private val presenter: MerchantValidateChallengePresenterImpl by inject {
        parametersOf(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_selecione_banco_mfa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        textViewInfo.text = getString(R.string.text_bank_challenge_mfa_info)
        configureListeners()
        configureNavigation()
        presenter.getMfaBanks()
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar(getString(R.string.toolbar_text_bank_challenge_mfa))
            this.cieloNavigation?.setTextButton(getString(R.string.esqueci_usuario_button_enviar))
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
                    presenter.selectedItem(mfaAccount)
                    this@MerchantValidateChallengeFragment.cieloNavigation?.enableButton(true)
                }
            }
        })
    }

    override fun onButtonClicked(labelButton: String) {
        presenter.sendMFABankChallenge()
    }

    override fun onRetry() {
        presenter.getMfaBanks()
    }

    override fun onHelpButtonClicked() {
        findNavController()
                .navigate(MerchantValidateChallengeFragmentDirections
                        .actionMerchantValidateChallengeFragmentToFaqQuestionsFragment2())
        this.cieloNavigation?.showContent(true)
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
            contentLayout?.addView(this.createRadioButton(it))
        }
        this.radioGroupView?.addView(contentLayout)
    }

    override fun showLoading() {
        this.cieloNavigation?.showLoading(true)
    }

    override fun showError(errorMessage: ErrorMessage) {
        cieloNavigation?.showError(errorMessage)
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

    override fun showBankChallengeActive() {
        val bottomSheet = BottomSheetGenericFragment
                .newInstance(
                        getString(R.string.text_bank_challenge_mfa_toolbar_success),
                        R.drawable.ic_08,
                        getString(R.string.text_bank_challenge_mfa_title_success),
                        getString(R.string.text_bank_challenge_mfa_subtitle_success),
                        getString(R.string.incomint_fast_cancellation_back_button),
                        statusBtnClose = false,
                        statusBtnOk = true,
                        statusViewLine = true,
                )
        bottomSheet.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
            override fun onBtnOk(dialog: Dialog) {
                requireActivity().finish()
            }
        }
        bottomSheet.isCancelable = false
        bottomSheet.show(this.childFragmentManager, bottomSheet::class.java.simpleName)
    }

    override fun showNotEligibleUser() {
        cieloNavigation?.showIneligibleUser(getString(R.string.text_message_service_ineligible))
    }

    override fun showBankChallengePending() {
        val bottomSheet = OnboardingSuccessBottomSheetFragment().apply {
            this.listener =
                    object : OnboardingSuccessBottomSheetFragment.OnboardingSuccessListener {
                        override fun onButtonClicked() {
                            requireActivity().finish()
                        }

                        override fun onSwipeClosed() {
                            requireActivity().finish()
                        }
                    }
        }
        bottomSheet.subTitleFirst = getString(R.string.text_merchant_status_mfa_pending)
        bottomSheet.buttonText = getString(R.string.text_close)
        bottomSheet.show(
                requireActivity().supportFragmentManager,
                OnboardingSuccessBottomSheetFragment::class.java.simpleName
        )
    }

    override fun showBlocked() {
        val bottomSheet = BottomSheetGenericFragment
                .newInstance(
                        getString(R.string.text_bank_mfa_status_blocked_toolbar),
                        R.drawable.ic_07,
                        getString(R.string.text_bank_mfa_status_blocked_toolbar),
                        getString(R.string.text_bank_mfa_status_blocked_title),
                        getString(R.string.incomint_fast_cancellation_back_button),
                        statusBtnClose = false,
                        statusBtnOk = true,
                        statusViewLine = true,
                )
        bottomSheet.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
            override fun onBtnOk(dialog: Dialog) {
                requireActivity().finish()
            }
        }
        bottomSheet.isCancelable = false
        bottomSheet.show(this.childFragmentManager, bottomSheet::class.java.simpleName)
    }

    override fun onBusinessError(error: ErrorMessage) {
        this.cieloNavigation?.showError(
                getString(R.string.text_title_generic_error),
                getString(R.string.business_error),
                getString(R.string.ok),
                R.drawable.ic_generic_error_image
        ) {
            requireActivity().finish()
        }
    }

    private fun createRadioButton(account: MfaAccount): View {
        val radioButton = CieloRegularTextOutlineSelectRadioButton(this.requireContext())
        val layout = LayoutInflater.from(requireContext())
                .inflate(R.layout.layout_item_conta_corrente, null, false) as ConstraintLayout
        layout.nomeBancoTextView?.text = account.bankName
        layout.agenciaTextView?.text = account.agency
        layout.contaCorrenteTextView?.text = "${account.account}-${account.accountDigit}"
        ImageUtils.loadImage(
                layout?.bandeiraImageView,
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
        (radioButton.layoutParams as LinearLayout.LayoutParams).setMargins(0, 16, 0, 16)
        radioButton.requestLayout()
        return radioButton
    }

    private fun insertCNPJ(mfaAccount: MfaAccount) {
        InsertCNPJBottomSheetFragment.create(mfaAccount, object : InsertCNPJBottomSheetFragmentContract {
            override fun verifyData(mfaAccount: MfaAccount) {
                findNavController()
                        .navigate(
                                MerchantValidateChallengeFragmentDirections
                                        .actionMerchantValidateChallengeFragmentToResumeBankAndCNPJFragment(mfaAccount, false)
                        )
            }

            override fun dismiss() {
                listAccounts?.let {
                    show(it)
                }?: run {
                    presenter.getMfaBanks()
                }
            }

        }).show(childFragmentManager, SELECT_BANK_MFA)
    }
}