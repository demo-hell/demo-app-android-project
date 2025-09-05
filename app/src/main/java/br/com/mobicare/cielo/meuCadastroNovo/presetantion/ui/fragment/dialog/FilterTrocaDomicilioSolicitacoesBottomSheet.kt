package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.cielo.libflue.selectandmultiselect.model.ComponentFilterSelectAndMultiselectModel
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment.BalcaoRecebiveisExtratoDetailsNegotiationsFragment.Companion.QUICKFILTER
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.contato.TrocaDomicilioStatus
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_filter_details_negociations_bottom_sheet.applyFilterButton
import kotlinx.android.synthetic.main.layout_filter_details_negociations_bottom_sheet.clearFilterButton
import kotlinx.android.synthetic.main.layout_filter_details_negociations_bottom_sheet.closeButton
import kotlinx.android.synthetic.main.layout_filter_troca_domicilio_solicitacoes_bottom_sheet.*

const val STATUS_NAME = ""

class FilterTrocaDomicilioSolicitacoesBottomSheet() : BottomSheetDialogFragment() {


    var onClick: OnClickButtons? = null
    var quickFilter: QuickFilter? = null


    var operationNumber: String? = null
    var hiringStatus: String? = null

    interface OnClickButtons {
        fun onBtnCleanFilter(
                dialog: Dialog?,
                quickFilter: QuickFilter?
        )

        fun onBtnAddFilter(
                dialog: Dialog?,
                quickFilter: QuickFilter?
        )
    }

    companion object {
        const val VALUE = 4
        fun newInstance(quickFilter: QuickFilter?): FilterTrocaDomicilioSolicitacoesBottomSheet {
            return FilterTrocaDomicilioSolicitacoesBottomSheet().apply {
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

        return inflater.inflate(
                R.layout.layout_filter_troca_domicilio_solicitacoes_bottom_sheet,
                container,
                false
        )
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
        arguments?.let { itBundle ->
            this.quickFilter = itBundle.getSerializable(QUICKFILTER) as QuickFilter?
        }
        loadArguments()
        initView()
        configurationListener()
        populateItem()
    }

    private fun loadArguments() {
        arguments?.let { itBundle ->
            this.quickFilter = itBundle.getSerializable(QUICKFILTER) as QuickFilter?
        }
    }


    /**
     * method to init view
     * */
    private fun initView() {
        this.quickFilter?.operationNumber?.let {
            evNumberOperation.setText(it)
        }
    }

    /**
     * method to load filter in the screen
     * */
    private fun configurationListener() {

        closeButton.setOnClickListener {
            dismiss()
        }

        clearFilterButton.setOnClickListener {
            this.quickFilter = null
            dialog.let { dClear -> onClick?.onBtnCleanFilter(dClear, this.quickFilter) }

        }

        applyFilterButton.setOnClickListener {
            this.operationNumber = if (evNumberOperation.getText().trim().isEmpty()) null else evNumberOperation.getText().trim()
            componentSelect.getSelectedItems().map {
                this.hiringStatus = if (it.label.isEmpty()) null else getStatus(it.label)
            }


            var quickFilter: QuickFilter? = null

            if (this.operationNumber != null || this.hiringStatus != null) {
                quickFilter = QuickFilter
                        .Builder()
                        .operationNumber(this.operationNumber)
                        .statusType(this.hiringStatus)
                        .build()
            }
            dialog?.let { it1 -> onClick?.onBtnAddFilter(it1, quickFilter) }
        }
    }

    private fun populateItem() {
        val list = TrocaDomicilioStatus.values().map {
            ComponentFilterSelectAndMultiselectModel(it.status).apply {
                this@FilterTrocaDomicilioSolicitacoesBottomSheet.quickFilter?.statusType?.let {status ->
                    if (this.label == getNameFromStatus(status)) {
                        this.isChecked = true
                    }
                }
            }
        }

        componentSelect.setContent(list.toMutableList())
    }

    private fun getStatus(label: String): String = when (label) {

        TrocaDomicilioStatus.PENDING.status -> TrocaDomicilioStatus.PENDING.name
        TrocaDomicilioStatus.REJECT.status -> TrocaDomicilioStatus.REJECT.name
        TrocaDomicilioStatus.AWAITING.status -> TrocaDomicilioStatus.AWAITING.name
        TrocaDomicilioStatus.CHECKING.status -> TrocaDomicilioStatus.CHECKING.name
        TrocaDomicilioStatus.CONCLUDED.status -> TrocaDomicilioStatus.CONCLUDED.name
        TrocaDomicilioStatus.ERROR.status -> TrocaDomicilioStatus.ERROR.name
        TrocaDomicilioStatus.CANCEL.status -> TrocaDomicilioStatus.CANCEL.name
        TrocaDomicilioStatus.ALL.status -> TrocaDomicilioStatus.ALL.name
        else -> TrocaDomicilioStatus.ALL.name

    }

    private fun getNameFromStatus(label: String): String = when (label) {

        TrocaDomicilioStatus.PENDING.name -> TrocaDomicilioStatus.PENDING.status
        TrocaDomicilioStatus.REJECT.name -> TrocaDomicilioStatus.REJECT.status
        TrocaDomicilioStatus.AWAITING.name -> TrocaDomicilioStatus.AWAITING.status
        TrocaDomicilioStatus.CHECKING.name -> TrocaDomicilioStatus.CHECKING.status
        TrocaDomicilioStatus.CONCLUDED.name -> TrocaDomicilioStatus.CONCLUDED.status
        TrocaDomicilioStatus.ERROR.name -> TrocaDomicilioStatus.ERROR.status
        TrocaDomicilioStatus.CANCEL.name -> TrocaDomicilioStatus.CANCEL.status
        TrocaDomicilioStatus.ALL.name -> TrocaDomicilioStatus.ALL.status
        else -> STATUS_NAME
    }
}
