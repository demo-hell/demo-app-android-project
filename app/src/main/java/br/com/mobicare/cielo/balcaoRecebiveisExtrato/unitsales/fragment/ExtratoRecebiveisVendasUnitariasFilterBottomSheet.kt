package br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.concrete.canarinho.watcher.CPFCNPJTextWatcher
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.CardBrands
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Item
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.VendasUnitariasFilterBrands
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment.BalcaoRecebiveisExtratoDetailsNegotiationsFragment
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.FiltroVendasUnitariasContract
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.adapter.FiltroBrandsAdapter
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.presenter.FiltroVendasUtitariasPresenter
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.meusrecebimentosnew.fragments.DEFAULT_DATE_RANGE_DAYS
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_filter_extratp_vendas_unitarias_bottom_sheet.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class ExtratoRecebiveisVendasUnitariasFilterBottomSheet: BottomSheetDialogFragment(), FiltroVendasUnitariasContract.View {

    lateinit var negotiationItem: Item

    var brands: List<CardBrands>?=null
    var quickFilter: QuickFilter? = null
    private val initialDate = DataCustomNew()
    var onClick:OnClickButtons? = null
    var dateFilter:String? = null
    var cpfOrCnpj:String? = null
    var listBrandSales:MutableList<CardBrands>? = null
    private val presenter: FiltroVendasUtitariasPresenter by inject {
        parametersOf(this)
    }

    private val daysReceivablesLast: Int
        get() = ConfigurationPreference.instance
            .getConfigurationValue(
                ConfigurationDef
                    .DAYS_RECEIVABLES_LAST, DEFAULT_DATE_RANGE_DAYS
            ).toInt() + 1


    private val daysReceivablesFuture: Int
        get() = ConfigurationPreference.instance
            .getConfigurationValue(
                ConfigurationDef
                    .DAYS_RECEIVABLES_FUTURE, DEFAULT_DATE_RANGE_DAYS
            ).toInt() + 3

    interface OnClickButtons {
        fun onBtnCleanFilter(
            dialog: Dialog,
            quickFilter: QuickFilter?
        ) {}
        fun onBtnAddFilter(
            dialog: Dialog,
            quickFilter: QuickFilter?
        ) {}
    }

    companion object {
        fun newInstance(negotiationItem: Item, quickFilter: QuickFilter?): ExtratoRecebiveisVendasUnitariasFilterBottomSheet {
            return ExtratoRecebiveisVendasUnitariasFilterBottomSheet().apply {
                this.negotiationItem = negotiationItem
                arguments = Bundle().apply {
                    putSerializable(BalcaoRecebiveisExtratoDetailsNegotiationsFragment.QUICKFILTER, quickFilter)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  = inflater.inflate(R.layout.layout_filter_extratp_vendas_unitarias_bottom_sheet, container, false)

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
                        if (newState >= 4) {
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
            this.quickFilter = itBundle.getSerializable(
                BalcaoRecebiveisExtratoDetailsNegotiationsFragment.QUICKFILTER
            ) as QuickFilter?
        }
        presenter.initView()
    }

    private fun clickViews() {
        clearFilterButton.setOnClickListener    {
            this.quickFilter = null
            dialog?.let { it1 -> onClick?.onBtnCleanFilter(it1, this.quickFilter)}

        }

        applyFilterButton.setOnClickListener    {
            this.dateFilter = if(tv_date_filter.text.toString().isEmpty()) null else initialDate.formatDateToAPI()
            this.cpfOrCnpj = if(ev_cpf_cnpj.getText().isEmpty()) null else Utils.unmask(ev_cpf_cnpj.getText())
            this.listBrandSales = if((rv_brands.adapter as FiltroBrandsAdapter).listItemSelect.size > 0) (rv_brands.adapter as FiltroBrandsAdapter).listItemSelect else null
            dialog?.let { it1 -> onClick?.onBtnAddFilter(it1,
                QuickFilter
                    .Builder()
                    .initialDate(this.dateFilter)
                        .finalDate(this.dateFilter)
                        .identificationNumber(this.cpfOrCnpj)
                    .listBrandSales(this.listBrandSales)
                    .build()) }
        }
    }

    fun onClickDataInicio() {
        val cal = initialDate.toCalendar()
        val dia = CalendarCustom.getDay(cal)
        val mes = CalendarCustom.getMonth(cal)
        val ano = CalendarCustom.getYear(cal)

        CalendarDialogCustom(-daysReceivablesLast, daysReceivablesFuture, -1, dia, mes, ano,
            getString(R.string.extrato_filtro_data_inicio), context as Context,
            { _, year, monthOfYear, dayOfMonth ->
                initialDate.setDate(year, monthOfYear, dayOfMonth)
                tv_date_filter.text = initialDate.formatBRDate()
                applyFilterButton.isEnabled = true
            }, R.style.DialogThemeMeusRecebimentos).show()
    }

    override fun initView() {

        if(this.quickFilter != null){
            this.quickFilter?.identificationNumber?.let {
                if(it.length == 11){
                    ev_cpf_cnpj.setText(addMaskCPForCNPJ(it, getString(R.string.mask_cpf_step4)))
                }else if(it.length > 11){
                    ev_cpf_cnpj.setText(addMaskCPForCNPJ(it, getString(R.string.mask_cnpj_step4)))
                }else if(it.length < 11){
                    ev_cpf_cnpj.setText(it)
                }
            }

            this.quickFilter?.initialDate?.let {
                initialDate.setDateFromAPI(it)
                tv_date_filter.text = initialDate.formatBRDate()
                applyFilterButton.isEnabled = true
            }

        }



        tv_date_filter.setOnClickListener { onClickDataInicio() }
        ev_cpf_cnpj.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
            private val validator = CPFCNPJTextWatcher()
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

        presenter.callBrands(negotiationItem.date, negotiationItem.operationNumber ?: "")
    }

    override fun initProgress() {
        layoutError.gone()
        layoutContent.gone()
        waitingLayout.visible()
        layoutBottom.gone()
    }

    override fun finishedProgress() {
        layoutError.gone()
        layoutContent.visible()
        waitingLayout.gone()
        layoutBottom.visible()
    }

    override fun showSuccess(brands: VendasUnitariasFilterBrands) {
        this.brands = brands.cardBrands
        var adapterBrands:FiltroBrandsAdapter?= null

        if(this.quickFilter?.listBrandSales != null){
            brands.cardBrands?.let {
                 adapterBrands = FiltroBrandsAdapter(it, this.quickFilter?.listBrandSales!!)
            }
        }else{
            brands.cardBrands?.let {
                 adapterBrands = FiltroBrandsAdapter(it)
            }
        }

        adapterBrands?.onClickItem = {}
        rv_brands.adapter = adapterBrands
        rv_brands.layoutManager = GridLayoutManager(requireContext(), 4)


    }

    /**
     * methodo to show the error of the api.
     * */
    override fun serverError() {
        layoutError.visible()
        layoutContent.gone()
        waitingLayout.gone()
        layoutBottom.gone()
    }
}