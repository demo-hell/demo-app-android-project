package br.com.mobicare.cielo.mySales.presentation.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceEditTextView
import br.com.mobicare.cielo.commons.utils.getDoubleValueForMoneyInput
import br.com.mobicare.cielo.commons.utils.moneyToBigDecimalValue
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.LayoutFilterMinhasVendasBottomSheetDialogBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.verifyNullOrBlankValue
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef
import br.com.mobicare.cielo.minhasVendas.activities.SCREENVIEW_FILTRO_DAS_VENDAS_CANCELADAS
import br.com.mobicare.cielo.minhasVendas.activities.VENDAS_CANCELADAS_CATEGORY
import br.com.mobicare.cielo.mySales.data.model.CardBrand
import br.com.mobicare.cielo.mySales.data.model.PaymentType
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.mySales.data.model.params.ItemSelectable
import br.com.mobicare.cielo.mySales.presentation.ui.adapter.salesFilters.FilterCardBrandAdapter
import br.com.mobicare.cielo.mySales.presentation.ui.adapter.salesFilters.FilterPaymentTypeAdapter
import br.com.mobicare.cielo.mySales.presentation.viewmodel.MySalesFiltersViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class MySalesFiltersBottomSheetFragment: BottomSheetDialogFragment() {

    interface OnResultListener {
        fun onResult(quickFilter: QuickFilter)
    }

    interface OnDismissListener {
        fun onDismiss()
    }

    private val PAYMENT_TYPES_GRID_SPAN = 2
    private val BRAND_CARD_GRID_SPAN = 4

    private var _binding: LayoutFilterMinhasVendasBottomSheetDialogBinding? = null
    private val binding get() = _binding

    private val viewModel: MySalesFiltersViewModel by viewModel()
    var isLoadingPaymentTypes = false
    private var listener: OnResultListener? = null
    private lateinit var brandsAdapter: FilterCardBrandAdapter
    private lateinit var paymentTypesAdapter: FilterPaymentTypeAdapter
    var onDismissListener: OnDismissListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutFilterMinhasVendasBottomSheetDialogBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.trackScreenView(
            screenName = SCREENVIEW_FILTRO_DAS_VENDAS_CANCELADAS,
            screenClass = this.javaClass
        )

        observeViewModelStates()
        configureLayoutBrands()
        configureLayoutPaymentsType()
        configureListeners()
        loadFilters()
        editTextGa()

        if (isCanceledFilters())
            configureFilterInputFields()

        if (isMoreFilters())
            configureTextViewMoreFilters()

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        setupBottomSheet(dialog)
        return dialog
    }
    
    companion object {
        private const val ARG_PARAM_QUICK_FILTER = "ARG_PARAM_QUICK_FILTER"
        private const val ARG_PARAM_IS_LOADING_PAYMENT_TYPES = "ARG_PARAM_IS_LOADING_PAYMENT_TYPES"
        private const val ARG_PARAM_IS_FILTER_BRANDS_DATA = "ARG_PARAM_IS_FILTER_BRANDS_DATA"

        fun newInstance(
            quickFilter: QuickFilter,
            listener: OnResultListener,
            isLoadingPaymentTypes: Boolean = false,
            isFilterBrandsData: Boolean = false
        ): MySalesFiltersBottomSheetFragment {
            return MySalesFiltersBottomSheetFragment().apply {
                this.listener = listener
                this.arguments = Bundle().apply {
                    this.putSerializable(ARG_PARAM_QUICK_FILTER, quickFilter)
                    this.putBoolean(ARG_PARAM_IS_LOADING_PAYMENT_TYPES, isLoadingPaymentTypes)
                    this.putBoolean(ARG_PARAM_IS_FILTER_BRANDS_DATA, isFilterBrandsData)
                }
            }
        }
    }

    private fun configureLayoutBrands() {
        binding?.rvBrands?.apply {
            this.layoutManager = GridLayoutManager(requireContext(),BRAND_CARD_GRID_SPAN)
            brandsAdapter = FilterCardBrandAdapter()
            this.adapter = brandsAdapter
        }
    }

    private fun configureLayoutPaymentsType() {
        binding?.rvPaymentTypes?.apply {
            this.layoutManager = GridLayoutManager(requireContext(),PAYMENT_TYPES_GRID_SPAN)
            paymentTypesAdapter = FilterPaymentTypeAdapter()
            this.adapter = paymentTypesAdapter
        }
    }

    private fun setupBottomSheet(dialog: Dialog){
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                R.id.design_bottom_sheet
            ) as? FrameLayout
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0
                behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState >= 4) {
                            onDismissListener?.onDismiss()
                            dismiss()
                        }
                    }
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                })
            }
        }
    }

    private fun observeViewModelStates() {
        viewModel.loadingLiveData.observe(viewLifecycleOwner) { loadingState() }
        viewModel.loadNsuLiveData.observe(viewLifecycleOwner){ loadNsuAndAuthorizationCode(it) }
        viewModel.loadMoreFiltersAndShowCancelInputsLiveData.observe(viewLifecycleOwner){
            loadMoreFilters(it)
            showCancelInputs()
        }
        viewModel.brandsLiveData.observe(viewLifecycleOwner){ showCardBrands(it) }
        viewModel.paymentTypeLiveData.observe(viewLifecycleOwner) { showPaymentTypes(it) }
        viewModel.error.observe(viewLifecycleOwner){ showError() }
        viewModel.applyFilterLiveData.observe(viewLifecycleOwner) { applyFilter(it) }
        viewModel.hideLoading.observe(viewLifecycleOwner) { hideLoading() }
    }

    private fun showPaymentTypes(paymentTypes: List<ItemSelectable<PaymentType>>?) {
        paymentTypes?.let {
            showFilterContent()
            binding?.layoutPaymentTypes.visible()
            paymentTypesAdapter.updateAdapter(it)
        }
    }

    private fun showCardBrands(cardBrands: List<ItemSelectable<CardBrand>>?) {
        cardBrands?.let {
            showFilterContent()
            brandsAdapter.updateAdapter(it)
        }
    }

    private fun configureListeners() {
        binding?.apply {
            this.layoutTop.closeButton.setOnClickListener {
                onDismissListener?.onDismiss()
                dismiss()
            }

            this.clearFilterButton.setOnClickListener {
                applyFilter(viewModel.clearFilter())
                Toast.makeText(
                    activity,
                    getString(R.string.description_action_clear_filter),
                    Toast.LENGTH_SHORT
                ).show()
            }

            this.applyFilterButton.setOnClickListener {
                if(isCanceledFilters())
                    viewModel.applyFilter(configureFilterCanceled())

                if(isMoreFilters())
                    viewModel.applyFilter(configureFilterSalesByPeriod())

                if(isLoadingPaymentTypes.not())
                    viewModel.applyFilter(configureFilterSalesByToday())

                if(isMoreFilters().not() && isCanceledFilters().not() && isLoadingPaymentTypes)
                    viewModel.applyFilter(null)

                Toast.makeText(
                    activity,
                    getString(R.string.description_action_apply_filter),
                    Toast.LENGTH_SHORT
                ).show()
            }

            binding?.retryButton?.setOnClickListener {
                loadFilters()
            }

        }
    }

    private fun loadFilters() {
        this.arguments?.let {
            isLoadingPaymentTypes = it.getBoolean(ARG_PARAM_IS_LOADING_PAYMENT_TYPES,false)

            viewModel.isCanceledFilters = isCanceledFilters()
            viewModel.isMoreFilters = isMoreFilters()
            it.getSerializable(ARG_PARAM_QUICK_FILTER)?.let {
                viewModel.getFilters(it as QuickFilter, isLoadingPaymentTypes)
            }
        }
    }


    private fun configureFilterInputFields() {
        binding?.layoutCanceledSales?.apply {
            this.editInputCanceledSaleCancelValue.addTextChangedListener(
                this.editInputCanceledSaleCancelValue.getMaskMoney(this.editInputCanceledSaleValue)
            )

            this.editInputCanceledSaleValue.addTextChangedListener(
                this.editInputCanceledSaleValue.getMaskMoney(this.editInputCanceledSaleValue)
            )
        }
    }

    private fun configureTextViewMoreFilters() {
        //layout min max
        binding?.includeLayoutAmount?.apply {
            this.textViewAmountInitial.addTextChangedListener(
                textViewAmountInitial.getMaskMoney(textViewAmountInitial)
            )
            this.textViewAmountFinal.addTextChangedListener(
                textViewAmountFinal.getMaskMoney(textViewAmountFinal)
            )
        }

        verifyErrorAmount()
        binding?.includeLayoutAmount?.apply{
            focusTypeFace(this.textViewAmountInitial)
            focusTypeFace(this.textViewAmountFinal)
        }
    }

    private fun verifyErrorAmount() {
        //min_max_layout
        binding?.includeLayoutAmount?.apply {
            this.textViewAmountInitial.addTextChangedListener(
                onTextChanged = { s, start, before, count ->
                    s?.let {
                        getDoubleValueForMoneyInput(textViewAmountFinal)?.let { final ->
                            val initial = it.toString().moneyToBigDecimalValue().toDouble()
                            viewErrorAmount(final < initial)
                        }
                    }
                }
            )
        }
    }

    private fun viewErrorAmount(isVisible: Boolean) {
        if(isVisible) {
            binding?.includeLayoutAmount?.error.visible()
            textViewStyle(
                binding?.includeLayoutAmount?.textViewAmountFinal,
                R.drawable.error_border_filter, R.color.red_DC392A)
            binding?.applyFilterButton?.isEnabled = false
        } else {
            binding?.includeLayoutAmount?.error.gone()
            binding?.includeLayoutAmount?.textViewAmountFinal?.let {
                isFocus(it.isFocused,it)
            }
            binding?.applyFilterButton?.isEnabled = true
        }
    }


    private fun focusTypeFace(typefaceEditTextView: TypefaceEditTextView){
        typefaceEditTextView.setOnFocusChangeListener { v, b ->
            isFocus(b, typefaceEditTextView)
        }
    }


    private fun isFocus(b: Boolean, typefaceEditTextView: TypefaceEditTextView){
        if(b){
            textViewStyle(
                typefaceEditTextView,
                R.drawable.focus_border_blue_filter,
                R.color.color_017CEB
            )
        }else{
            textViewStyle(typefaceEditTextView, R.drawable.date_border_4dp, R.color.color_353A40)
        }
    }

    private fun textViewStyle(
        typefaceEditTextView: TypefaceEditTextView?,
        @DrawableRes drawable: Int,
        @ColorRes color: Int) {
        typefaceEditTextView?.setBackgroundResource(drawable)
        typefaceEditTextView?.setTextColor(
            ContextCompat.getColor(
                this@MySalesFiltersBottomSheetFragment.requireContext(),
                color
            )
        )
    }



    //region - metodos de analytics
    private fun gaSendEditTextInteracao(name: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, VENDAS_CANCELADAS_CATEGORY),
            action = listOf(Action.MODAL, Action.MAIS_FILTROS),
            label = listOf(Label.INTERACAO, name)
        )
    }

    fun editTextGa() {
        binding?.apply {
            this.layoutCanceledSales.apply {
                editInputCanceledSaleValue.setOnClickListener {
                    gaSendEditTextInteracao("valor de venda")
                }

                editInputCanceledSaleValue.setOnClickListener {
                    gaSendEditTextInteracao("valor do cancelamento")
                }

                editInputCanceledSaleAuthorizationCode.setOnClickListener {
                    gaSendEditTextInteracao("codigo de autorizacao")
                }

                editInputCanceledSaleNsu.setOnClickListener {
                    gaSendEditTextInteracao("nsu")
                }

                editInputCanceledSaleTID.setOnClickListener {
                    gaSendEditTextInteracao("tid")
                }
            }
        }
    }

    //endregion


    //region - metodos de configuracao de filtros e listeners de filtros
    private fun applyFilter(quickFilter: QuickFilter){
        listener?.onResult(quickFilter)
        onDismissListener?.onDismiss()
        dismiss()
    }

    private fun isCanceledFilters(): Boolean = (arguments
        ?.getSerializable(ARG_PARAM_QUICK_FILTER) as QuickFilter).status
        ?.contains(ExtratoStatusDef.CANCELADA) ?: false

    private fun isMoreFilters(): Boolean =
        arguments?.getSerializable(ARG_PARAM_IS_FILTER_BRANDS_DATA) as Boolean


    private fun configureFilterCanceled(): QuickFilter {
        var saleGrossValue: Double? = null
        var saleCancelValue: Double? = null
        var authorizationCode: String? = null
        var nsu: String? = null
        var tid: String? = null

        binding?.layoutCanceledSales?.apply {
            saleGrossValue = getDoubleValueForMoneyInput(this.editInputCanceledSaleValue)
            saleCancelValue = getDoubleValueForMoneyInput(this.editInputCanceledSaleCancelValue)
            authorizationCode = this.editInputCanceledSaleAuthorizationCode.text.toString().trim().verifyNullOrBlankValue()
            nsu = this.editInputCanceledSaleNsu.text.toString().verifyNullOrBlankValue()
            tid = this.editInputCanceledSaleTID.text.toString().verifyNullOrBlankValue()
        }

        return QuickFilter.Builder()
            .saleGrossAmount(saleGrossValue)
            .grossAmount(saleCancelValue)
            .authorizationCode(authorizationCode)
            .nsu(nsu)
            .tid(tid)
            .build()
    }


    private fun configureFilterSalesByPeriod(): QuickFilter {
        var nsu: String? = null
        var codeAuthorization: String? = null
        var numberCard: String? = null
        var summary: String? = null
        var tid: String? = null
        var initialAmount: Double? = null
        var finalAmount: Double? = null

        binding?.includeLayoutOtherFilters?.apply {
            nsu = this.editTextSalesNsuDoc.getText().verifyNullOrBlankValue()
            codeAuthorization = this.editTextSaleAuthorizationCode.getText().verifyNullOrBlankValue()
        }
        numberCard = binding?.editTextSaleCadNumberNew?.getText().verifyNullOrBlankValue()
        summary = binding?.editTextSaleOperationSummaryNew?.getText().verifyNullOrBlankValue()
        tid = binding?.editTextSaleTidNew?.getText().verifyNullOrBlankValue()

        binding?.includeLayoutAmount?.apply {
            initialAmount = getDoubleValueForMoneyInput(textViewAmountInitial)
            finalAmount = getDoubleValueForMoneyInput(textViewAmountFinal)
        }

        return QuickFilter.Builder()
            .initialAmount(initialAmount)
            .finalAmount(finalAmount)
            .truncatedCardNumber(numberCard)
            .softDescriptor(summary)
            .authorizationCode(codeAuthorization)
            .nsu(nsu)
            .tid(tid)
            .build()
    }


    private fun configureFilterSalesByToday(): QuickFilter {
        var nsu: String? = null
        var codeAuthorization: String? = null

        binding?.includeLayoutOtherFilters?.apply {
            nsu = editTextSalesNsuDoc.getText().verifyNullOrBlankValue()
            codeAuthorization = editTextSaleAuthorizationCode.getText().verifyNullOrBlankValue()
        }

        return QuickFilter.Builder()
            .authorizationCode(codeAuthorization)
            .nsu(nsu)
            .build()
    }


    private fun loadMoreFilters(quickFilter: QuickFilter?) {
        quickFilter?.let {
            loadNsuAndAuthorizationCode(quickFilter)

            if(quickFilter.tid?.isNotEmpty() == true) {
                binding?.layoutCanceledSales?.editInputCanceledSaleTID?.text = SpannableStringBuilder.valueOf(quickFilter.tid)
                binding?.editTextSaleTidNew?.setText(quickFilter.tid)
            }

            quickFilter.saleGrossAmount?.let {
                binding?.layoutCanceledSales?.editInputCanceledSaleValue?.text =
                    SpannableStringBuilder.valueOf(it.toPtBrRealString())
            }

            quickFilter.grossAmount?.let {
                binding?.layoutCanceledSales?.editInputCanceledSaleCancelValue?.text = SpannableStringBuilder
                    .valueOf(it.toPtBrRealString())
            }

            quickFilter.initialAmount?.let {
                binding?.includeLayoutAmount?.textViewAmountInitial?.text = SpannableStringBuilder
                    .valueOf(it.toPtBrRealString())
            }

            quickFilter.finalAmount?.let {
                binding?.includeLayoutAmount?.textViewAmountFinal?.text = SpannableStringBuilder
                    .valueOf(it.toPtBrRealString())
            }


            quickFilter.truncatedCardNumber?.let {
                binding?.editTextSaleCadNumberNew?.setText(quickFilter.truncatedCardNumber.toString())
            }

            if(quickFilter.softDescriptor?.isNotEmpty() == true) {
                binding?.editTextSaleOperationSummaryNew?.setText(quickFilter.softDescriptor)
            }
        }
    }

    private fun loadNsuAndAuthorizationCode(quickFilter: QuickFilter?){
        quickFilter?.let { filter ->
            if(filter.authorizationCode?.isNotEmpty() == true) {
                binding?.layoutCanceledSales?.editInputCanceledSaleAuthorizationCode?.text =
                    SpannableStringBuilder.valueOf(filter.authorizationCode)
                binding?.includeLayoutOtherFilters?.editTextSaleAuthorizationCode?.setText(filter.authorizationCode)
            }

            if(filter.nsu?.isNotEmpty() == true) {
                binding?.layoutCanceledSales?.editInputCanceledSaleNsu?.text =  SpannableStringBuilder.valueOf(filter.nsu)
                binding?.includeLayoutOtherFilters?.editTextSalesNsuDoc?.setText(filter.nsu)
            }
        }
    }

    //endregion

    //region - metodos de atualizacao de view

    private fun loadingState() {
        binding?.layoutTop?.root?.visibility = View.VISIBLE
        visibleLayout()
        binding?.layoutContent?.visibility = View.GONE
        binding?.layoutPaymentTypes?.visibility = View.GONE
        binding?.layoutBrands?.visibility = View.GONE
        binding?.layoutError?.visibility = View.GONE
        binding?.waitingLayout?.visibility = View.VISIBLE
    }

    private fun visibleLayout() {
        if(isCanceledFilters())
            binding?.layoutCanceledSales?.root.visible()

        if(isMoreFilters())
            binding?.layoutHistorySalesContainer.visible()

        if(isLoadingPaymentTypes.not()){
            binding?.layoutCanceledSales?.root.gone()
            binding?.layoutHistorySalesContainer.visible()
            binding?.includeLayoutAmount?.root.gone()
            binding?.layoutMoreSalesNew.gone()
        }
    }

    private fun hideLoading() {
        binding?.waitingLayout?.gone()
        binding?.layoutBottom?.visible()
    }


    private fun showFilterContent() {
        binding?.layoutContent.visible()
        binding?.layoutBrands.visible()
        binding?.layoutCanceledSales?.root.gone()

        if(isLoadingPaymentTypes)
            binding?.layoutHistorySalesContainer.gone()
    }


    private fun showError() {
        binding?.apply {
            layoutContent.gone()
            layoutPaymentTypes.gone()
            layoutCanceledSales.root.gone()
            layoutBrands.gone()
            waitingLayout.gone()
            layoutBottom.gone()
            layoutHistorySalesContainer.gone()
            layoutError.visible()

        }
    }

    private fun showCancelInputs(){
        binding?.layoutContent.visible()
        visibleLayout()
    }

    //endregion
}