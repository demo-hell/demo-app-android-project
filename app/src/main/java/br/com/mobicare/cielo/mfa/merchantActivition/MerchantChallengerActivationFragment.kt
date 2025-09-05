package br.com.mobicare.cielo.mfa.merchantActivition

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGeneric
import br.com.mobicare.cielo.commons.utils.currencyToDouble
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.mfa.api.EnrollmentBankResponse
import br.com.mobicare.cielo.mfa.router.MfaRouteHandler
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.dialog.BottomSheetGenericFragment
import kotlinx.android.synthetic.main.fragment_put_value_mfa.*
import kotlinx.android.synthetic.main.layout_item_conta_corrente.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MerchantChallengerActivationFragment : BaseFragment(),
        MerchantChallengerActivationContract.View, CieloNavigationListener {

    private val presenter: MerchantChallengerActivationPresenter by inject {
        parametersOf(this)
    }

    private var cieloNavigation: CieloNavigation? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_put_value_mfa, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.configureTexts()
        this.configureNavigation()
        this.configureFocusView()
        this.initTextChange()
        presenter.fetchEnrollmentActiveBank()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    private fun configureTexts() {
        this.textViewValueContent?.text = getString(R.string.text_merchant_challenger_activation_message)
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

    fun initTextChange() {
        textViewNumberOne.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
            var isChanging = false

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isChanging) {
                    isChanging = true
                    var formatValue = s.toString()
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
                    var formatValue = s.toString()
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

    private fun configureFocusView() {
        textViewNumberOne.setOnTextViewFocusChanged(
                View.OnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus) {
                        if (!textViewNumberOne?.getText().toString().trim()
                                        .isNullOrEmpty() && !textViewNumberOne?.getText().toString().trim()
                                        .equals(getString(R.string.put_values))
                        ) {
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
                        }
                    }
                })
    }

    override fun onButtonClicked(labelButton: String) {
        presenter.activationCode(textViewNumberOne.getText().currencyToDouble().toString(),
                textViewNumberTwo.getText().currencyToDouble().toString())
    }

    override fun onHelpButtonClicked() {
        findNavController()
                .navigate(
                        MerchantChallengerActivationFragmentDirections
                                .actionMerchantChallengerActivationFragmentToFaqQuestionsFragment2())
        this.cieloNavigation?.showContent(true)
    }

    override fun onRetry() {
        onButtonClicked()
    }

    override fun showLoading() {
        requireActivity().hideSoftKeyboard()
        this.cieloNavigation?.showLoading(true)
    }

    override fun hideLoading() {
        this.cieloNavigation?.showLoading(false)
    }

    override fun onValueSuccess() {
        this.cieloNavigation?.showContent(true)
        bottomSheetGeneric(
                getString(R.string.bottom_sheet_sucess_put_value_label),
                R.drawable.ic_success_user_loan,
                getString(R.string.text_merchant_challange_activation_sucesso),
                getString(R.string.text_merchant_challange_activation_sucesso_message),
                getString(R.string.text_merchant_challange_activation_text_button),
                false,
                true,
                true
        ).apply {
            this.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnClose(dialog: Dialog) {
                    dialog.dismiss()
                }

                override fun onBtnOk(dialog: Dialog) {
                    val fromRouteHandler = activity?.intent?.extras?.get(MfaRouteHandler.MFA_FROM_ROUTE_HANDLER) == true
                    if (fromRouteHandler) {
                        MfaRouteHandler.canMfaProceedForAction = true
                        requireActivity().setResult(Activity.RESULT_OK)
                    }
                    requireActivity().finish()
                }
            }
        }.show(requireActivity().supportFragmentManager, getString(R.string.bottom_sheet_generic))

        this.cieloNavigation?.showLoading(true)
    }

    override fun onInvalidRequestError(error: ErrorMessage) {
        this.cieloNavigation?.showContent(true)
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
        error.errorMessage = getString(R.string.business_error)
        this.cieloNavigation?.showError(error)
    }

    override fun onValueError(error: ErrorMessage) {
        this.cieloNavigation?.showError(error)
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
                R.drawable.ic_07,
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

    override fun hideEnrollmentActiveBank() {
        if (isAttached()) {
            linearDepositedBankInfo.visibility = View.GONE
        }
    }

}