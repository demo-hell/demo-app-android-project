package br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.cardActivateCateno

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
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.afterTextChangesEmptySubscribe
import br.com.mobicare.cielo.commons.utils.afterTextChangesNotEmptySubscribe
import br.com.mobicare.cielo.commons.utils.showMessage
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.bottom_sheet_new_password.*

class CardNewPasswordBottomSheetFragment :  BottomSheetDialogFragment() {

    lateinit var cardNewPasswordListener: CardNewPasswordListener

    companion object {
        fun newInstance(cardNewPasswordListener: CardNewPasswordListener): CardNewPasswordBottomSheetFragment =
            CardNewPasswordBottomSheetFragment().apply {
                this.cardNewPasswordListener = cardNewPasswordListener
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_new_password, container, false)
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
        requireView().getWindowVisibleDisplayFrame(r)

        val height = requireView().context.resources.displayMetrics.heightPixels
        val diff = height - r.bottom
        val heightMin = height/2
        view.minimumHeight = heightMin

        if (diff != 0) {
            if (view.getPaddingBottom() != diff) {
                view.setPadding(0, 0, 0, diff)
            }
        } else {
            if (view.getPaddingBottom() != 0) {
                view.setPadding(0, 0, 0, 0)
            }
        }

        textInputEditCvv.addTextChangedListener(textInputEditCvv
                .validateBottonPayment(textInputEditCvv, textInputEditExpirationDt, buttonTransferConfirm))

        textInputEditExpirationDt
                .addTextChangedListener(textInputEditExpirationDt
                        .validateBottonPayment(textInputEditCvv, textInputEditExpirationDt, buttonTransferConfirm))

        textInputEditExpirationDt.addTextChangedListener(MascaraNumericaTextWatcher.Builder()
                .paraMascara("##/##").build())


        buttonTransferConfirm.setOnClickListener {

            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, CONTA_DIGITAL),
                action = listOf(CONTA_DIGITAL_DESBLOQUEIO_CARTAO_CONFIRMACAO, Action.CLIQUE),
                label = listOf(Label.BOTAO, Action.CONFIRMAR)
            )

            if (Utils.isNetworkAvailable(requireActivity())) {
                cardNewPasswordListener.activateCar(textInputEditCvv.text.toString(), textInputEditExpirationDt.text.toString())
            } else {
                requireContext().showMessage(getString(R.string.title_error_wifi_subtitle),
                        title = getString(R.string.title_error_wifi_title))
            }
        }

        configureResetErrorSubscribers()
        gaEditTextFocus()

    }


     fun configureResetErrorSubscribers() {

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

            textInputEditExpirationDt.run {

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

    fun changeDialogShowLoading(progressVisibility: Int, buttonTransferVisibility: Int) {

        frameProgressTransactionConfirm?.run {
            visibility = progressVisibility
        }

        buttonTransferConfirm?.run {
            visibility = buttonTransferVisibility


            textInputEditCvv.run {
                isEnabled = false
            }

            textInputEditExpirationDt.run {
                isEnabled = false
            }
        }
    }


    fun hideDialog() {
        this.dismiss()
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
                    gaSendInteracao(getString(R.string.text_my_cards_bottom_sheet_date))
                }
            }
        }
    }

    private fun gaSendInteracao(nameField: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, MEUS_CARTOES),
            action = listOf(MEUS_CARTOES_MODAL),
            label = listOf(Label.INTERACAO, nameField)
        )
    }

}