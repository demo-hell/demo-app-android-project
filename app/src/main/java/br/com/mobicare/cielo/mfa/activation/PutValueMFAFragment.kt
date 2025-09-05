package br.com.mobicare.cielo.mfa.activation

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGeneric
import br.com.mobicare.cielo.commons.utils.currencyToDouble
import br.com.mobicare.cielo.commons.utils.enableFlagSecure
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.mfa.api.EnrollmentBankResponse
import br.com.mobicare.cielo.mfa.router.MfaRouteHandler
import br.com.mobicare.cielo.mfa.router.MfaRouteHandler.Companion.MFA_FROM_ROUTE_HANDLER
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.dialog.BottomSheetGenericFragment
import kotlinx.android.synthetic.main.fragment_put_value_mfa.*
import kotlinx.android.synthetic.main.layout_item_conta_corrente.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PutValueMFAFragment : BaseFragment(), PutValueView, CieloNavigationListener {

    private var cieloNavigation: CieloNavigation? = null
    private lateinit var callTokenGeneratedCallback: CallTokenGeneratedCallback
    var title: String? = null
    var passOrAt: String? = MFA_ATIVO

    companion object {
        const val IS_ACTIVE_BANK_ENABLED_PARAM = "br.com.cielo.mfa.isActiveBankEnabledParam"
    }

    val isActiveBankEnabled: Boolean? by lazy {
        arguments?.getBoolean(IS_ACTIVE_BANK_ENABLED_PARAM)
    }

    private val presenter: PutValuePresenterImpl by inject {
        parametersOf(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFlagSecure(requireActivity().window)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_put_value_mfa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onCreate()
        configureNavigation()

        //ga verification
        UserPreferences.getInstance().keepStatusMfa?.let { stMfa ->
                title = if (stMfa) MFA_NOVO_TOKEN else MFA_NOVO_TOKEN_TROCA
        }
        textViewNumberOne.setOnTextViewFocusChanged(
            View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    if (!textViewNumberOne?.getText().toString().trim()
                            .isNullOrEmpty() && !textViewNumberOne?.getText().toString().trim()
                            .equals(getString(R.string.put_values))
                    ) {
                        gaSendFormInterection(getString(R.string.put_values_one))
                    }
                }
            })

        textViewNumberTwo.setOnTextViewFocusChanged(
            View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    if (!textViewNumberTwo?.getText().toString().trim()
                            .isNullOrEmpty() && !textViewNumberTwo?.getText().toString().trim()
                            .equals(getString(R.string.put_values))
                    ) {
                        gaSendFormInterection(getString(R.string.put_values_tow))
                    }
                }
            })

        isActiveBankEnabled?.let {
            if (it) {
                presenter.fetchEnrollmentActiveBank()
            }
        }

        validationShowOnboarding()
    }

    fun validationShowOnboarding() {
        val isToShowMfaOnboarding = UserPreferences.getInstance()
            .isToShowOnboarding(UserPreferences.ONBOARDING.MFA)

        passOrAt = if(isToShowMfaOnboarding) MFA_PASSIVO else MFA_ATIVO
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar(getString(R.string.put_values_toolbar_title))
            this.cieloNavigation?.setTextButton(getString(R.string.put_values_validate_button))
            this.cieloNavigation?.showButton(true)
            this.cieloNavigation?.enableButton(false)
            this.cieloNavigation?.showHelpButton(true)
            this.cieloNavigation?.setNavigationListener(this)
        }
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onResume() {
        presenter.onResume()
        super.onResume()
    }

    override fun initExplanationSpannable() {
        val text = getString(R.string.put_values_content)
        val spannableString = SpannableString(
            HtmlCompat
                .fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
        )

        textViewValueContent.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    override fun initTextChange() {
        textViewNumberOne.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
            var isChanging = false

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isChanging) {
                    isChanging = true
                    val formatValue = s.toString()
                        .currencyToDouble()
                        .toPtBrRealString()
                    textViewNumberOne.setText(formatValue)
                    textViewNumberOne.setSelection(formatValue.length)
                    isChanging = false
                }
                validateFields()
            }
        })
        textViewNumberTwo.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
            var isChanging = false

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isChanging) {
                    isChanging = true
                    val formatValue = s.toString()
                        .currencyToDouble()
                        .toPtBrRealString()
                    textViewNumberTwo.setText(formatValue)
                    textViewNumberTwo.setSelection(formatValue.length)
                    isChanging = false
                }
                validateFields()
            }
        })
    }

    private fun validateFields() {
        var enableButton = false
        val zero = 0.0
        if (!textViewNumberOne.getText().isEmpty()
            && !textViewNumberTwo.getText().isEmpty()
        ) {
            enableButton = !textViewNumberOne.getText().currencyToDouble().toString().isEmpty()
                    && !textViewNumberTwo.getText().currencyToDouble().toString().isEmpty()
                    && !(textViewNumberOne.getText().currencyToDouble() == zero
                    || textViewNumberTwo.getText().currencyToDouble() == zero)
        }
        this.cieloNavigation?.enableButton(enableButton)
        this.cieloNavigation?.setNavigationListener(this)
    }

    override fun onButtonClicked(labelButton: String) {
        presenter.activationCode(textViewNumberOne.getText().currencyToDouble().toString(),
                textViewNumberTwo.getText().currencyToDouble().toString())
    }

    override fun onHelpButtonClicked() {
        gaHelpButton()
        findNavController()
                .navigate(
                        PutValueMFAFragmentDirections
                                .actionPutValueMFAFragmentToCentralAjudaPerguntasFragmentMFA()
                )
        this.cieloNavigation?.showContent(true)

    }

    override fun onValueSuccess() {
        gaMsgCallbackSuccess()
        this.cieloNavigation?.showContent(true)
        bottomSheetGeneric(
            getString(R.string.bottom_sheet_sucess_put_value_label),
            R.drawable.ic_success_user_loan,
            getString(R.string.bottom_sheet_sucess_put_value_title),
            getString(R.string.bottom_sheet_sucess_put_value_subtitle),
            getString(R.string.ok),
            false,
            true,
            true
        ).apply {
            this.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnClose(dialog: Dialog) {
                    dialog.dismiss()
                }

                override fun onBtnOk(dialog: Dialog) {
                    //Sinalizar como final de fluxo
                    callTokenGeneratedCallback.callTokenScreen()
                    dialog.dismiss()
                    //requireActivity().startActivity<TokenGeneratorActivity>()
                }
            }
        }.show(requireActivity().supportFragmentManager, getString(R.string.bottom_sheet_generic))

        this.cieloNavigation?.showLoading(true)

        callTokenGeneratedCallback = object : CallTokenGeneratedCallback {
            override fun callTokenScreen() {
                val fromRouteHandler = activity?.intent?.extras?.get(MFA_FROM_ROUTE_HANDLER) == true
                if (fromRouteHandler) {
                    MfaRouteHandler.canMfaProceedForAction = true
                    requireActivity().setResult(Activity.RESULT_OK)
                    requireActivity().finish()
                } else {
                    findNavController().navigate(
                        PutValueMFAFragmentDirections
                            .actionPutValueMFAFragmentToOtpRegisterFragment(
                                UserPreferences
                                    .getInstance().userName
                            )
                    )
                }
            }
        }
    }

    override fun tokenLostWarning() {

        (requireActivity() as BaseActivity)
            .showOptionDialogMessage(
                dialogTitle =
                getString(R.string.text_mfa_lost_token_warning_title)
            ) {
                this.customLayout = R.layout.linear_custom_dialog_same_colors_buttons
                this.setTitle(R.string.text_mfa_lost_token_warning_title)
                this.setMessage(getString(R.string.text_mfa_lost_token_warning_message))
                this.setBtnRight(getString(R.string.text_yes_label))
                this.setBtnLeft(getString(R.string.text_no_label))
                this.setOnclickListenerLeft {
                    requireActivity().finish()
                }
            }

    }

    override fun onValueError(error: ErrorMessage) {
        gaMsgCallbackError(error)
        this.cieloNavigation?.showError(error)
    }

    override fun onInvalidRequestError(error: ErrorMessage) {
        gaMsgCallbackError(error)
        CieloAlertDialogFragment
            .Builder()
            .title(getString(R.string.text_title_error))
            .message(error.errorMessage)
            .closeTextButton(getString(R.string.ok))
            .build().let {
                it.onCloseButtonClickListener = View.OnClickListener {}
                it.showAllowingStateLoss(
                    requireActivity()
                        .supportFragmentManager, "CieloAlertDialog"
                )
            }
    }

    override fun onBusinessError(error: ErrorMessage) {
        gaMsgCallbackError(error)
        error.errorMessage = getString(R.string.business_error)
        this.cieloNavigation?.showError(error)
    }

    override fun onRetry() {
        onButtonClicked()
    }

    override fun incorrectValues() {
        this.cieloNavigation?.showContent(true)
        CieloAlertDialogFragment
            .Builder()
            .title(getString(R.string.incorrect_values_alert_title))
            .message(getString(R.string.incorrect_values_alert_message))
            .closeTextButton(getString(R.string.incorrect_values_alert_button))
            .build().let {
                it.onCloseButtonClickListener = View.OnClickListener {}
                it.showAllowingStateLoss(
                    requireActivity()
                        .supportFragmentManager, "CieloAlertDialog"
                )
            }

    }

    override fun incorrectValuesThirdAttempt() {
        this.cieloNavigation?.showContent(true)
        bottomSheetGeneric(
            getString(R.string.bottom_sheet_error_third_put_value_label),
            R.drawable.ic_generic_error_image,
            getString(R.string.bottom_sheet_error_third_put_value_title),
            getString(R.string.bottom_sheet_error_third_put_value_subtitle),
            getString(R.string.ok),
            false,
            true,
            true
        ).apply {
            this.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnClose(dialog: Dialog) {
                    dialog.dismiss()
                }

                override fun onBtnOk(dialog: Dialog) {
                    requireActivity().finish()
                }
            }
        }.show(requireActivity().supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun showLoading() {
        this.cieloNavigation?.showLoading(true)
    }

    override fun hideLoading() {
        this.cieloNavigation?.showLoading(false)
    }

    override fun hideEnrollmentActiveBank() {
        if (isAttached()) {
            linearDepositedBankInfo.visibility = View.GONE
        }
    }

    override fun configureActiveBank(it: EnrollmentBankResponse?) {
        if (isAttached()) {
            linearDepositedBankInfo.visibility = View.VISIBLE

            linearDepositedBankInfo.addView(LayoutInflater.from(requireContext())
                .inflate(R.layout.layout_item_conta_corrente, null, false))

            it?.let { bankResponse ->
                nomeBancoTextView.text = SpannableStringBuilder
                .valueOf(bankResponse.bankName ?: "")
                agenciaTextView.text = SpannableStringBuilder
                    .valueOf(bankResponse.agency ?: "")
                contaCorrenteTextView.text = SpannableStringBuilder
                    .valueOf("${bankResponse.account}-${bankResponse.accountDigit}")

                ImageUtils.loadImage(bandeiraImageView, bankResponse.imgSource,
                    R.drawable.ic_generic_brand)
            }
        }
    }

    interface CallTokenGeneratedCallback {
        fun callTokenScreen()
    }

    //ga
    private fun gaSendFormInterection(labelButton: String) {
        if (isAttached()) {
            title?.let {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, it),
                    action = listOf(Action.VALIDACAO, Action.INTERACAO, Action.CAMPO),
                    label = listOf(labelButton)
                )
            }

        }
    }

    private fun gaButtonValidation() {
        if (isAttached()) {
            title?.let {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, it),
                    action = listOf(Action.VALIDACAO, Action.CLIQUE, Action.BOTAO),
                    label = listOf(MFA_VALIDAR)
                )
            }
        }
    }

    fun gaHelpButton() {
        title?.let {t->
            passOrAt?.let {st->
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, t),
                    action = listOf(Action.ONBOARDING, st, Action.CLIQUE, Action.ICONE),
                    label = listOf(MFA_FAQ, MFA_TELA_VALIDACAO)
                )
            }
        }
    }

    fun gaMsgCallbackError(error: ErrorMessage) {
        if (isAttached()) {
            title?.let {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, it),
                    action = listOf(Action.CALLBACK, MFA_VALIDACAO_TOKEN),
                    label = listOf(ERRO, error.errorMessage, "${error.httpStatus}")
                )
            }
        }
    }

    fun gaMsgCallbackSuccess() {
        if (isAttached()) {
            title?.let {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, it),
                    action = listOf(Action.CALLBACK, MFA_VALIDACAO_TOKEN),
                    label = listOf(SUCESSO)
                )
            }
        }

    }

    //end ga

}