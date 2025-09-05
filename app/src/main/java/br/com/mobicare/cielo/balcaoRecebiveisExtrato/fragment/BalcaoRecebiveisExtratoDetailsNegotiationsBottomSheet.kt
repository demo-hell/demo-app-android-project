package br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.concrete.canarinho.watcher.CPFCNPJTextWatcher
import br.com.concrete.canarinho.watcher.ValorMonetarioWatcher
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment.BalcaoRecebiveisExtratoDetailsNegotiationsFragment.Companion.QUICKFILTER
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.addMaskCPForCNPJ
import br.com.mobicare.cielo.commons.utils.currencyToDouble
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_filter_details_negociations_bottom_sheet.*

class BalcaoRecebiveisExtratoDetailsNegotiationsBottomSheet() : BottomSheetDialogFragment() {


    var onClick: OnClickButtons? = null
    var quickFilter: QuickFilter? = null

    var valorBrutoInit:Double?= null
    var valorBrutoFinal:Double?= null
    var validationValorInitial:Double?= null
    var validationValorFinal:Double?= null
    var cpfOrCpnj:String?= null
    var operationNumber:String?= null



    interface OnClickButtons {
        fun onBtnCleanFilter(
            dialog: Dialog?,
            quickFilter: QuickFilter?
        ) {}
        fun onBtnAddFilter(
            dialog: Dialog?,
            quickFilter: QuickFilter?
        ) {}
    }

    companion object {
        const val VALUE = 4
        fun newInstance(quickFilter: QuickFilter?): BalcaoRecebiveisExtratoDetailsNegotiationsBottomSheet {
            return BalcaoRecebiveisExtratoDetailsNegotiationsBottomSheet().apply {
                arguments = Bundle().apply {
                    putSerializable(QUICKFILTER, quickFilter)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =
            inflater.inflate(R.layout.layout_filter_details_negociations_bottom_sheet, container, false)
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            ) as? FrameLayout
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0
                behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState >= VALUE) {
                            dismiss()
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }
                })
            }
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
        }
        clickViews()
        closeButton.setOnClickListener {
            dismiss()
        }

        arguments?.let { itBundle ->
            this.quickFilter = itBundle.getSerializable(QUICKFILTER) as QuickFilter?
        }

        initView()
    }

    /**
     * method to init view
     * */
    private fun initView() {
        if(this.quickFilter != null){
            if (this.quickFilter?.initialAmount != 0.0){
                this.quickFilter?.initialAmount?.let {
                    ev_valor_bruto_init.setText(it.toPtBrRealString())
                }
            }

            if (this.quickFilter?.finalAmount != 0.0){
                this.quickFilter?.finalAmount?.let {
                    ev_valor_bruto_final.setText(it.toPtBrRealString())
                }
            }


            this.quickFilter?.merchantId?.let {
                if(it.length!! == 11){
                    ev_cpf_cnpj.setText(addMaskCPForCNPJ(it, getString(R.string.mask_cpf_step4)))
                }else if(it.length!! > 11){
                    ev_cpf_cnpj.setText(addMaskCPForCNPJ(it, getString(R.string.mask_cnpj_step4)))
                }else if(it.length!! < 11){
                    ev_cpf_cnpj.setText(it)
                }
            }

            this.quickFilter?.operationNumber?.let {
                ev_number_operation.setText(it)
            }
        }

        ev_valor_bruto_init.setOnTextChangeListener(object : CieloTextInputView
        .TextChangeListener {

            private val validator: ValorMonetarioWatcher = ValorMonetarioWatcher.Builder()
                .comSimboloReal().build()

            override fun afterTextChanged(s: Editable?) {
                validator.afterTextChanged(s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                validator.beforeTextChanged(s, start, count, after)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validator.onTextChanged(s, start, before, count)
            }
        })

        ev_valor_bruto_final.setOnTextChangeListener(object : CieloTextInputView
        .TextChangeListener {

            private val validator: ValorMonetarioWatcher = ValorMonetarioWatcher.Builder()
                .comSimboloReal().build()

            override fun afterTextChanged(s: Editable?) {
                validator.afterTextChanged(s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                validator.beforeTextChanged(s, start, count, after)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validator.onTextChanged(s, start, before, count)
            }
        })


        ev_cpf_cnpj.setOnTextChangeListener(object : CieloTextInputView
        .TextChangeListener {

            private val validator: CPFCNPJTextWatcher = CPFCNPJTextWatcher()

            override fun afterTextChanged(s: Editable?) {
                if(s?.length!! > 10){
                    validator.afterTextChanged(s)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if(s?.length!! > 10){
                    validator.beforeTextChanged(s, start, count, after)
                }


            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length!! > 10){
                    validator.onTextChanged(s, start, before, count)
                }
            }
        })
    }

    /**
     * method to load filter in the screen
     * */
    private fun clickViews() {
        clearFilterButton.setOnClickListener    {
            this.quickFilter = null
            dialog.let { it -> onClick?.onBtnCleanFilter(it, this.quickFilter)}

        }

        applyFilterButton.setOnClickListener    {
            this.operationNumber        = if(ev_number_operation.getText().isNullOrEmpty()) null else ev_number_operation.getText()
            this.valorBrutoInit         = if(ev_valor_bruto_init.getText().isNullOrEmpty()) null else ev_valor_bruto_init.getText().trim().currencyToDouble()
            this.valorBrutoFinal        = if(ev_valor_bruto_final.getText().isNullOrEmpty()) null else ev_valor_bruto_final.getText().trim().currencyToDouble()
            this.cpfOrCpnj              = if(ev_cpf_cnpj.getText().isNullOrEmpty()) null else Utils.unmask(ev_cpf_cnpj.getText())

            this.validationValorInitial = if(this.valorBrutoInit != null && this.valorBrutoInit != 0.0) this.valorBrutoInit else null
            this.validationValorFinal = if(this.valorBrutoFinal != null && this.valorBrutoFinal != 0.0) this.valorBrutoFinal else null

            dialog?.let { it1 -> onClick?.onBtnAddFilter(it1, QuickFilter
                .Builder()
                .initialAmount(this.validationValorInitial)
                .finalAmount(this.validationValorFinal)
                .operationNumber(this.operationNumber)
                .merchantId(this.cpfOrCpnj)
                .build()) }

        }
    }
}
