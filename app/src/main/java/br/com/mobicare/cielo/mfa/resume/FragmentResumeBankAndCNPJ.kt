package br.com.mobicare.cielo.mfa.resume

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUBCATEGORY_NAME
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.FormHelper
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.CNPJ_MASK_COMPLETE_FORMAT
import br.com.mobicare.cielo.mfa.MfaAccount
import br.com.mobicare.cielo.mfa.onbordingSuccess.OnboardingSuccessBottomSheetFragment
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.dialog.BottomSheetGenericFragment
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import kotlinx.android.synthetic.main.fragment_resume_bank_and_cnpj.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val ARG_PARAM_MFA_ACCOUNT_ENROLLMENT = "ARG_PARAM_MFA_ACCOUNT_ENROLLMENT"
const val TAG = "fragmentResumeBankAndCNPJ"

class FragmentResumeBankAndCNPJ : BaseFragment(), ResumeBankAndCNPJContract.View,
        CieloNavigationListener {

    private val presenter: ResumeBankAndCNPJPresenter by inject {
        parametersOf(this)
    }

    private var mfaAccount: MfaAccount? = null
    private var navigation: CieloNavigation? = null
    private var isEnrollment: Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_resume_bank_and_cnpj, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        getMfaAccount()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_token_title_screen))
            navigation?.setTextButton(getString(R.string.confirmar))
            navigation?.showHelpButton(true)
            navigation?.showButton(true)
            navigation?.enableButton(true)
            navigation?.setNavigationListener(this)
        }
    }

    private fun getMfaAccount() {
        arguments?.getParcelable<MfaAccount>(ARG_PARAM_MFA_ACCOUNT)?.let { mfaAccount ->
            this.mfaAccount = mfaAccount
            setupView(mfaAccount)
        }
        arguments?.getBoolean(ARG_PARAM_MFA_ACCOUNT_ENROLLMENT, false)?.let {
            isEnrollment = it
        }
    }

    private fun setupView(mfaAccount: MfaAccount) {
        text_name_bank?.text = mfaAccount.bankName
        text_numb_agency?.text = mfaAccount.agency
        text_numb_account?.text = "${mfaAccount.account}-${mfaAccount.accountDigit}"
        mfaAccount.identificationNumber?.let {
            text_numb_cnpj?.text = FormHelper.maskFormatter(it, CNPJ_MASK_COMPLETE_FORMAT).formattedText.string

        }
    }

    private fun sendAccount() {
        mfaAccount?.let {
            if (isEnrollment)
                presenter.sendEnrollment(it)
            else
                presenter.sendChallenge(it)
        }
    }

    override fun onButtonClicked(labelButton: String) {
        sendAccount()
    }

    override fun showLoading(isVisible: Boolean) {
        navigation?.showLoading(isVisible)
    }

    override fun showSuccessful() {
        showBottomSheetPending(getString(R.string.text_status_mfa_pending))
    }

    override fun showError(error: ErrorMessage) {
        navigation?.showError(error)
    }

    override fun showTemporarilyBlockError(error: ErrorMessage) {
        navigation?.showError(getString(R.string.text_view_temporarily_block_title_mfa),
                getString(R.string.text_view_temporarily_block_subtitle_mfa),
                getString(R.string.text_lgpd_saiba_mais), R.drawable.ic_07, View.OnClickListener {
            requireActivity().startActivity<CentralAjudaSubCategoriasEngineActivity>(
                    ConfigurationDef.TAG_KEY_HELP_CENTER to ConfigurationDef.TAG_HELP_CENTER_MFA,
                    ARG_PARAM_SUBCATEGORY_NAME to getString(R.string.text_token),
                    CentralAjudaSubCategoriasEngineActivity.NOT_CAME_FROM_HELP_CENTER to true)
        })
    }

    override fun showBankChallengeActive() {
        showErrorBlockedOrActive(toolbar = getString(R.string.text_bank_challenge_mfa_toolbar_success),
                image = R.drawable.ic_08,
                title = getString(R.string.text_bank_challenge_mfa_title_success),
                subtitle = getString(R.string.text_bank_challenge_mfa_subtitle_success))
    }

    override fun showBankChallengePending() {
        showBottomSheetPending(getString(R.string.text_merchant_status_mfa_pending))
    }

    override fun showBlocked() {
        showErrorBlockedOrActive(toolbar = getString(R.string.text_bank_mfa_status_blocked_toolbar),
                image = R.drawable.ic_07,
                title = getString(R.string.text_bank_mfa_status_blocked_toolbar),
                subtitle = getString(R.string.text_bank_mfa_status_blocked_title))
    }

    override fun onHelpButtonClicked() {
        findNavController()
                .navigate(
                        FragmentResumeBankAndCNPJDirections
                                .actionResumeBankAndCNPJFragmentToCentralAjudaPerguntasFragmentMFA()
                )
    }

    override fun onRetry() {
        sendAccount()
    }

    private fun showBottomSheetPending(title: String) {
        val textBtn = getString(R.string.text_close)

        OnboardingSuccessBottomSheetFragment().apply {
            this.subTitleFirst = title
            this.buttonText = textBtn
            this.listener =
                    object : OnboardingSuccessBottomSheetFragment.OnboardingSuccessListener {
                        override fun onButtonClicked() {
                            requireActivity().finish()
                        }

                        override fun onSwipeClosed() {
                            requireActivity().finish()
                        }
                    }
        }.show(
                requireActivity().supportFragmentManager,
                TAG
        )
    }

    private fun showErrorBlockedOrActive(toolbar: String, image: Int, title: String, subtitle: String) {
        val bottomSheet = BottomSheetGenericFragment
                .newInstance(
                        toolbar,
                        image,
                        title,
                        subtitle,
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
        bottomSheet.show(childFragmentManager, bottomSheet::class.java.simpleName)
    }
}