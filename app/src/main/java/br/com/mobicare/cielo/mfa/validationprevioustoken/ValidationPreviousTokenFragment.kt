package br.com.mobicare.cielo.mfa.validationprevioustoken

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGeneric
import br.com.mobicare.cielo.mfa.router.MfaRouteHandler
import br.com.mobicare.cielo.mfa.router.MfaRouteHandler.Companion.MFA_FROM_ROUTE_HANDLER
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.dialog.BottomSheetGenericFragment
import kotlinx.android.synthetic.main.fragment_validation_previous_token.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class ValidationPreviousTokenFragment : BaseFragment(), CieloNavigationListener,
    ValidationPreviousTokenContract.View {

    var title: String? = null
    val presenter: ValidationPreviousTokenPresenter by inject {
        parametersOf(this)
    }

    private var cieloNavigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(
        R.layout.fragment_validation_previous_token,
        container, false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureNavigation()
        configureViews()
        configureListeners()
        //ga verification
        UserPreferences.getInstance().keepStatusMfa?.let { stMfa ->
                title = if (stMfa) MFA_NOVO_TOKEN else MFA_NOVO_TOKEN_TROCA
        }
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar(getString(R.string.text_token))
            this.cieloNavigation?.showButton(false)
            this.cieloNavigation?.showHelpButton(true)
            this.cieloNavigation?.setNavigationListener(this)
        }
    }

    private fun configureViews() {
        val text = getString(R.string.text_message_validation_previous_token)
        val spannableString =
            SpannableString(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
        this.tvMessage?.text = spannableString
        this.sendButton?.isEnabled = false
        this.ctivToken?.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                this@ValidationPreviousTokenFragment.sendButton?.isEnabled = s?.length == 6
            }
        })

        this.ctivToken.setOnTextViewFocusChanged(
            View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    if (!this.ctivToken?.getText().toString().trim()
                            .isNullOrEmpty() && !this.ctivToken?.getText().toString().trim()
                            .equals(getString(R.string.number_token))
                    ) {
                        gaSendFormInterection(getString(R.string.number_token_ga))
                    }
                }
            })
    }

    private fun configureListeners() {
        this.sendButton?.setOnClickListener {
            this.ctivToken?.let {
                this.presenter.putCode(it.getText())
            }
            gaButtonValidation()
        }
    }

    override fun showIncorrectValues() {
        CieloAlertDialogFragment.Builder()
            .title(getString(R.string.incorrect_values_alert_title))
            .message(getString(R.string.text_message_incorrect_values))
            .closeTextButton(getString(R.string.text_close))
            .onCloseButtonClickListener {
                this.cieloNavigation?.showContent(true)
            }
            .build()
            .show(childFragmentManager, "CieloAlertDialogFragment")
    }

    override fun showSuccess() {
        val bottomSheet = BottomSheetGenericFragment
            .newInstance(
                getString(R.string.text_shipping_confirmation),
                R.drawable.ic_08,
                getString(R.string.bottom_sheet_sucess_put_value_title),
                getString(R.string.bottom_sheet_sucess_put_value_subtitle),
                getString(R.string.ok),
                statusBtnClose = false,
                statusBtnOk = true,
                statusViewLine = true,
            )
        bottomSheet.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
            override fun onBtnOk(dialog: Dialog) {
                val fromRouteHandler = activity?.intent?.extras?.get(MFA_FROM_ROUTE_HANDLER) == true
                if (fromRouteHandler) {
                    MfaRouteHandler.canMfaProceedForAction = true
                    requireActivity().setResult(Activity.RESULT_OK)
                }
                requireActivity().finish()
            }
        }
        bottomSheet.show(this.childFragmentManager, "BottomSheetGenericFragment")
    }

    override fun showLoading(isShow: Boolean) {
        this.cieloNavigation?.showLoading(isShow)
    }

    override fun showUserBlocked() {
        this.cieloNavigation?.showContent(true)
        bottomSheetGeneric(
            getString(R.string.bottom_sheet_error_third_put_value_label),
            R.drawable.ic_generic_error_image,
            getString(R.string.bottom_sheet_error_third_put_value_title),
            getString(R.string.bottom_sheet_error_third_put_value_subtitle),
            getString(R.string.ok),
            statusBtnClose = false,
            statusBtnOk = true,
            statusViewLine = true
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

    override fun onInvalidRequestError(error: ErrorMessage) {
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

    override fun onHelpButtonClicked() {
        findNavController().navigate(ValidationPreviousTokenFragmentDirections.actionValidationPreviousTokenFragmentToFaqQuestionsFragment())
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
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, MFA_NOVO_TOKEN_TROCA),
                action = listOf(Action.VALIDACAO, Action.CLIQUE, Action.BOTAO),
                label = listOf(Label.ENVIAR)
            )
        }
    }

    //end ga

}