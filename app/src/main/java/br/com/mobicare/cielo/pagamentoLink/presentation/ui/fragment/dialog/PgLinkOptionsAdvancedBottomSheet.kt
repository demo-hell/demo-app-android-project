package br.com.mobicare.cielo.pagamentoLink.presentation.ui.fragment.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.utils.CalendarCustom
import br.com.mobicare.cielo.commons.utils.CalendarDialogCustom
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.databinding.LayoutLinkPaymentOptionsAdvancedBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import br.com.mobicare.cielo.pagamentoLink.domains.TypeSaleEnum
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PgLinkOptionsAdvancedBottomSheet : BottomSheetDialogFragment() {

    var onContinue: (QuickFilter) -> Unit = {}

    private var _binding: LayoutLinkPaymentOptionsAdvancedBinding? = null
    private val binding get() = _binding!!

    private var quickFilter: QuickFilter? = null
    private var paymentLinkDTO: PaymentLinkDTO? = null
    private var expirationDate = DataCustomNew()

    private val skuValue get() = binding.inputSku.getText().ifBlank { null }

    private val expirationDateValue get() =
        if (binding.tvDateExp.text.isNotBlank()) expirationDate.formatDateToAPI() else null

    private val descriptionValue get() = binding.inputDescClient.getText().ifBlank { null }

    private val maximumInstallmentValue get() =
        binding.spinnerInstallments.selectedItem.toString().run {
            if (isNotBlank()) replace(X_LETTER, EMPTY_STRING).toInt() else null
        }

    private val quantityValue get() = try {
        binding.inputPaymentLimit.getText().run { if (isNotBlank()) toInt() else null }
    } catch (e: Exception) {
        null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LayoutLinkPaymentOptionsAdvancedBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            (dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout)?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = ZERO
                behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState >= FOUR) dismiss()
                    }
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                })
            }
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomSheetBehavior(view)
        setupListeners()
        configureVisibility()
        populateViews()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupBottomSheetBehavior(view: View) {
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = ZERO
        }
    }

    private fun setupListeners() {
        binding.apply {
            btContinue.setOnClickListener(::onContinueClick)
            inputDateExp.setOnClickListener(::onExpirationDateClick)
            inputDescClient.setOnTextChangeListener(getDescriptionTextChangeListener())
        }
    }

    private fun populateViews() {
        quickFilter?.let { qf ->
            binding.apply {
                qf.sku?.let { inputSku.setText(it) }
                qf.expiredDate?.let {
                    DataCustomNew().apply {
                        setDateFromAPI(it)
                        tvDateExp.text = formatBRDate()
                    }
                }
                qf.softDescriptor?.let { inputDescClient.setText(it) }
                qf.quantity?.let { inputPaymentLimit.setText(it.toString()) }
                qf.maximumInstallment?.let { spinnerInstallments.setSelection(it) }
            }
        }
    }

    private fun onContinueClick(v: View) {
        quickFilter = QuickFilter.Builder()
            .sku(skuValue)
            .expiredDate(expirationDateValue)
            .softDescriptor(descriptionValue)
            .maximumInstallment(maximumInstallmentValue)
            .quantity(quantityValue)
            .build()
            .also { onContinue(it) }

        dismiss()
    }

    private fun onExpirationDateClick(v: View) {
        val calendar = expirationDate.toCalendar()

        CalendarDialogCustom(
            context = context as Context,
            style = R.style.DialogThemeMeusRecebimentos,
            minDay = TOMORROW,
            maxDay = MAXIMUM_1800_DAYS,
            selectedDay = SELECTED_LESS_TWO_DAY,
            day = CalendarCustom.getDay(calendar),
            month = CalendarCustom.getMonth(calendar),
            year = CalendarCustom.getYear(calendar),
            label = getString(R.string.advanced_options_expiry_date),
            onDateSetListener = { _, year, monthOfYear, dayOfMonth ->
                expirationDate.setDate(year, monthOfYear, dayOfMonth)
                binding.tvDateExp.text = expirationDate.formatBRDate()
            }
        ).show()
    }

    private fun getDescriptionTextChangeListener() = object : CieloTextInputView.TextChangeListener {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let { value ->
                val formattedValue = value.toString().replace("[^a-zA-Z0-9]".toRegex(), EMPTY_STRING)
                binding.inputDescClient.let {
                    it.setOnTextChangeListener(null)
                    it.setText(formattedValue)
                    it.setSelection(formattedValue.length)
                    it.setOnTextChangeListener(this)
                }
            }
        }
    }

    private fun configureVisibility() {
        if (paymentLinkDTO?.typeSale == TypeSaleEnum.RECURRENT_SALE) {
            binding.apply {
                textViewIntallmentsTitles.gone()
                cardViewIntallmentsTitles.gone()
                labelPaymentLimits.gone()
                labelSubPaymentLimits.gone()
                inputPaymentLimit.gone()
            }
        }
    }

    companion object {
        private const val X_LETTER = "x"

        fun newInstance(
            quickFilter: QuickFilter? = null,
            paymentLinkDTO: PaymentLinkDTO? = null
        ) = PgLinkOptionsAdvancedBottomSheet().apply {
            this.quickFilter = quickFilter
            this.paymentLinkDTO = paymentLinkDTO
        }
    }

}