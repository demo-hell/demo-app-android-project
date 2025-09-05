package br.com.mobicare.cielo.meusrecebimentosnew.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceEditTextView
import br.com.mobicare.cielo.commons.utils.getDoubleValueForMoneyInput
import br.com.mobicare.cielo.commons.utils.moneyToBigDecimalValue
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.verifyNullOrBlankValue
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_amount_min_and_max.*
import kotlinx.android.synthetic.main.layout_filter_visao_detalhada_meus_recebiveis.*
import kotlinx.android.synthetic.main.layout_other_filter_items.*
import kotlinx.android.synthetic.main.layout_top_filter.*

class FilterVisaoDetalhadaMeusRecebiveisBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var listener: FilterVisaoDetalhadaContact
    private var filter: QuickFilter? = null

    companion object {
        private const val ARG_PARAM_FILTER_RECEIVE = "ARG_PARAM_FILTER_RECEIVE"

        fun create(quickFilter: QuickFilter?,
                   listener: FilterVisaoDetalhadaContact) = FilterVisaoDetalhadaMeusRecebiveisBottomSheetFragment().apply {
            this.listener = listener
            this.arguments = Bundle().apply {
                this.putSerializable(ARG_PARAM_FILTER_RECEIVE, quickFilter)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.layout_filter_visao_detalhada_meus_recebiveis, container, false)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        setupDialog(dialog)

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getFilter()
        setupEditTextFilters()
        setupListeners()
    }

    private fun getFilter() {
        filter = arguments?.getSerializable(ARG_PARAM_FILTER_RECEIVE) as? QuickFilter
        loadMoreFilters(filter)
    }

    private fun setupDialog(dialog: Dialog) {
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                    R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.let {
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
        }

    }

    private fun setupEditTextFilters() {
        textViewAmountInitial?.addTextChangedListener(textViewAmountInitial.getMaskMoney(textViewAmountInitial))
        textViewAmountFinal?.addTextChangedListener(textViewAmountFinal.getMaskMoney(textViewAmountFinal))

        verifyErrorAmount()

        focusTypeface(textViewAmountInitial)
        focusTypeface(textViewAmountFinal)

    }

    private fun setupListeners() {
        closeButton?.setOnClickListener {
            dismiss()
        }
        clearFilterButton?.setOnClickListener {
            listener.onCleanFilter(clearFilter())
            dismiss()
        }

        applyFilterButton?.setOnClickListener {
            listener.onFilterSelected(setupFilter())
            dismiss()

        }
    }

    private fun loadMoreFilters(filter: QuickFilter?) {
        filter?.let { quickFilter ->

            if (quickFilter.authorizationCode?.isNotEmpty() == true) {
                editTextSaleAuthorizationCode.setText(quickFilter.authorizationCode)
            }

            if (quickFilter.nsu?.isNotEmpty() == true) {
                editTextSalesNsuDoc.setText(quickFilter.nsu)
            }

            if (quickFilter.softDescriptor?.isNotEmpty() == true) {
                editTextOperationSummary?.setText(quickFilter.softDescriptor)
            }

            quickFilter.initialAmount?.let {
                textViewAmountInitial?.text = SpannableStringBuilder
                        .valueOf(it.toPtBrRealString())
            }

            quickFilter.finalAmount?.let {
                textViewAmountFinal?.text = SpannableStringBuilder
                        .valueOf(it.toPtBrRealString())
            }

            quickFilter.truncatedCardNumber?.let {
                editTextCadNumber?.setText(it)
            }

            quickFilter.operationNumber?.let {
                editTextOperationNumber?.setText(it)
            }

            quickFilter.saleCode?.let {
                editTextCodeSales?.setText(it)
            }

            quickFilter.terminal?.let {
                editTextMachineNumber?.setText(it[0])
            }
        }
    }

    private fun verifyErrorAmount() {
        textViewAmountInitial?.addTextChangedListener(
                onTextChanged = { s, start, before, count ->
                    s?.let { initialValue ->
                        getDoubleValueForMoneyInput(textViewAmountFinal)?.let { final ->
                            val initial = initialValue.toString().moneyToBigDecimalValue().toDouble()
                            viewErrorAmount(final < initial)
                        }
                    }
                }
        )

        textViewAmountFinal?.addTextChangedListener(
                onTextChanged = { s, start, before, count ->
                    s?.let { finalValue ->
                        getDoubleValueForMoneyInput(textViewAmountInitial)?.let { initial ->
                            val final = finalValue.toString().moneyToBigDecimalValue().toDouble()
                            viewErrorAmount(final < initial)
                        }
                    }
                })
    }

    private fun focusTypeface(typefaceEditTextView: TypefaceEditTextView) {
        typefaceEditTextView.setOnFocusChangeListener { _, b ->
            isFocus(b, typefaceEditTextView)
        }
    }

    private fun viewErrorAmount(isVisible: Boolean) {
        if (isVisible) {
            error?.visible()
            textViewStyle(textViewAmountFinal, R.drawable.error_border_filter, R.color.red_DC392A)
            applyFilterButton?.isEnabled = false
        } else {
            error?.gone()
            textViewAmountFinal?.let {
                isFocus(it.isFocused, it)
            }
            applyFilterButton?.isEnabled = true

        }
    }

    private fun isFocus(isFocus: Boolean, typefaceEditTextView: TypefaceEditTextView) {
        if (isFocus)
            textViewStyle(typefaceEditTextView, R.drawable.focus_border_blue_filter, R.color.color_017CEB)
        else
            textViewStyle(typefaceEditTextView, R.drawable.date_border_4dp, R.color.color_353A40)
    }

    private fun textViewStyle(typefaceEditTextView: TypefaceEditTextView?, drawable: Int, color: Int) {
        typefaceEditTextView?.setBackgroundResource(drawable)
        typefaceEditTextView?.setTextColor(ContextCompat.getColor(this@FilterVisaoDetalhadaMeusRecebiveisBottomSheetFragment.requireContext(), color))
    }

    private fun setupFilter(): QuickFilter {

        val nsu = editTextSalesNsuDoc.getText().verifyNullOrBlankValue()
        val codeAuthorization = editTextSaleAuthorizationCode.getText().verifyNullOrBlankValue()
        val numberCard = editTextCadNumber.getText().verifyNullOrBlankValue()
        val summary = editTextOperationSummary.getText().verifyNullOrBlankValue()
        val operationNumber = editTextOperationNumber.getText().verifyNullOrBlankValue()
        val saleCode = editTextCodeSales.getText().verifyNullOrBlankValue()

        val machineNumber = editTextMachineNumber.getText().verifyNullOrBlankValue()?.let { listOf(it) }
                ?: run { null }

        val initialAmount = getDoubleValueForMoneyInput(textViewAmountInitial)
        val finalAmount = getDoubleValueForMoneyInput(textViewAmountFinal)

        return QuickFilter.Builder()
                .initialDate(filter?.initialDate)
                .finalDate(filter?.finalDate)
                .cardBrand(filter?.cardBrand)
                .paymentType(filter?.paymentType)
                .transactionTypeCode(filter?.transactionTypeCode)
                .initialAmount(initialAmount)
                .finalAmount(finalAmount)
                .truncatedCardNumber(numberCard)
                .roNumber(summary)
                .authorizationCode(codeAuthorization)
                .nsu(nsu)
                .operationNumber(operationNumber)
                .saleCode(saleCode)
                .terminal(machineNumber)
                .merchantId(filter?.merchantId)
                .build()
    }

    private fun clearFilter(): QuickFilter {
        return QuickFilter.Builder()
                .initialDate(filter?.initialDate)
                .finalDate(filter?.finalDate)
                .cardBrand(filter?.cardBrand)
                .paymentType(filter?.paymentType)
                .transactionTypeCode(filter?.transactionTypeCode)
                .initialAmount(null)
                .finalAmount(null)
                .truncatedCardNumber(null)
                .roNumber(null)
                .authorizationCode(null)
                .nsu(null)
                .operationNumber(null)
                .saleCode(null)
                .merchantId(filter?.merchantId)
                .build()
    }
}