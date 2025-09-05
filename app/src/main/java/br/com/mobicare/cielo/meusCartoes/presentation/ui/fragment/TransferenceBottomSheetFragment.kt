package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.concrete.canarinho.watcher.MascaraNumericaTextWatcher
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceEditTextView
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.afterTextChangesEmptySubscribe
import br.com.mobicare.cielo.commons.utils.afterTextChangesNotEmptySubscribe
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.meusCartoes.presenter.BankAccountConfirmTransferenceContract
import br.com.mobicare.cielo.meusCartoes.presenter.TransferenceBottomSheetContract
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.bottom_sheet_payment.*
import mehdi.sakout.fancybuttons.FancyButton

@SuppressLint("ValidFragment")
class TransferenceBottomSheetFragment :
        BottomSheetDialogFragment(), TransferenceBottomSheetContract.View {

    lateinit var step4: BankAccountConfirmTransferenceContract.View
    lateinit var textInputEditDt: TypefaceEditTextView
    lateinit var textInputEditCvv: TypefaceEditTextView
    lateinit var frameProgress: FrameLayout
    lateinit var buttonTransferConfirm: FancyButton
    lateinit var bottomSheet: FrameLayout

    companion object {
        fun newInstance(step4: BankAccountConfirmTransferenceContract.View): TransferenceBottomSheetFragment =
            TransferenceBottomSheetFragment().apply {
                this.step4 = step4
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_confirm, container, false)
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            // For AndroidX use: com.google.android.material.R.id.design_bottom_sheet
            val bottomSheet = dialog.findViewById<View>(
                    com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState >= 4) {
                        dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //proportion screen
        val r = Rect()
        view.getWindowVisibleDisplayFrame(r)

        val height = view.context.resources.displayMetrics.heightPixels
        val diff = height - r.bottom
        val heightMin = height/2
        view.minimumHeight = heightMin

        if (diff != 0) {
            if (view.paddingBottom != diff) {
                view.setPadding(0, 0, 0, diff)
            }
        } else {
            if (view.paddingBottom != 0) {
                view.setPadding(0, 0, 0, 0)
            }
        }

        textInputEditDt = view
                .findViewById(R.id.textInputEditExpirationDt)

        textInputEditCvv = view
                .findViewById(R.id.textInputEditCvv)

        frameProgress = view.findViewById(R.id.frameProgressTransactionConfirm)
        buttonTransferConfirm = view.findViewById(R.id.buttonTransferConfirm)


        textInputEditDt.addTextChangedListener(MascaraNumericaTextWatcher.Builder()
                .paraMascara("##/##").build())

        configureResetErrorSubscribers(textInputEditDt, textInputEditCvv)

        initView()
        gaEditTextFocus()


    }

    override fun onResume() {
        super.onResume()
        isCancelable = true
    }

    override fun initView() {

        buttonTransferConfirm.setOnClickListener {

            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, CONTA_DIGITAL),
                action = listOf(CONTA_DIGITAL_TRANSFERENCIA_TED_CONFIRMACAO, Action.CLIQUE),
                label = listOf(Label.BOTAO, Action.CONFIRMAR)
            )

            if (Utils.isNetworkAvailable(requireActivity())) {
                isCancelable = false
                step4.confirmTransference(textInputEditDt, textInputEditCvv)
            } else {
                requireContext().showMessage(getString(R.string.title_error_wifi_subtitle),
                        title = getString(R.string.title_error_wifi_title))
            }

        }

        //validation field
        textInputEditCvv.addTextChangedListener(textInputEditCvv.validateBottonPayment(textInputEditCvv, textInputEditDt, buttonTransferConfirm))
        textInputEditDt.addTextChangedListener(textInputEditDt.validateBottonPayment(textInputEditCvv, textInputEditDt, buttonTransferConfirm))

    }

    override fun configureResetErrorSubscribers(textInputEditDt: TypefaceEditTextView,
                                                textInputEditCvv: TypefaceEditTextView) {
        if (isAdded) {
            textInputEditCvv.run {
                afterTextChangesEmptySubscribe {
                    val textInputLayoutCvv = dialog?.findViewById<TextInputLayout>(R.id.textInputLayoutCvv)
                    textInputLayoutCvv?.error = null
                }

                afterTextChangesNotEmptySubscribe {
                    val textInputLayoutCvv = dialog?.findViewById<TextInputLayout>(R.id.textInputLayoutCvv)
                    textInputLayoutCvv?.error = null
                }

            }


            textInputEditDt.run {

                afterTextChangesEmptySubscribe {
                    val textInputLayoutDt = dialog?.findViewById<TextInputLayout>(R.id
                            .textInputLayoutExpirationDt)
                    textInputLayoutDt?.run {
                        error = null
                    }
                }

                afterTextChangesNotEmptySubscribe {
                    val textInputLayoutDt = dialog?.findViewById<TextInputLayout>(R.id
                            .textInputLayoutExpirationDt)

                    textInputLayoutDt?.run {
                        error = null
                    }
                }
            }

        }

    }

    override fun changeDialogShowLoading(progressVisibility: Int, buttonTransferVisibility: Int) {

        frameProgress.run {
            visibility = progressVisibility
        }

        buttonTransferConfirm.run {
            visibility = buttonTransferVisibility


            textInputEditCvv.run {
                isEnabled = false
            }

            textInputEditDt.run {
                isEnabled = false
            }
        }
    }

    override fun showEmptyCvv() {
        isCancelable = true

        textInputEditCvv.run {
            error = getString(R.string.text_obrigatory_field_error)
        }
    }

    override fun showWrongExpirationDate() {

        isCancelable = true

        textInputEditDt.run {
            error = getString(R.string.text_wrong_expiration_dt)
        }

    }

    fun gaEditTextFocus() {

        textInputEditCvv.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!textInputEditCvv?.getText().toString().trim().isNullOrEmpty()){
                    gaSendInteracao(getString(R.string.text_my_cards_bottom_sheet_cvv))
                }
            }
        }

        textInputEditExpirationDt.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!textInputEditExpirationDt?.getText().toString().trim().isNullOrEmpty()){
                    gaSendInteracao(getString(R.string.text_my_cards_bottom_sheet_date_01))
                }
            }
        }
    }

    private fun gaSendInteracao(nameField: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, MEUS_CARTOES),
            action = listOf(MEUS_CARTOES_CONFIMACAO_PG_TRANSFERENCIA),
            label = listOf(Label.INTERACAO, nameField)
        )
    }
}