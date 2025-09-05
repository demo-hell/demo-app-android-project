package br.com.mobicare.cielo.minhasVendas.fragments.filter

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
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceEditTextView
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.verifyNullOrBlankValue
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef
import br.com.mobicare.cielo.minhasVendas.activities.SCREENVIEW_FILTRO_DAS_VENDAS_CANCELADAS
import br.com.mobicare.cielo.minhasVendas.activities.VENDAS_CANCELADAS_CATEGORY
import br.com.mobicare.cielo.mySales.data.model.CardBrand
import br.com.mobicare.cielo.mySales.data.model.PaymentType
import br.com.mobicare.cielo.minhasVendas.fragments.common.ItemSelectable
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_amount_min_and_max.*
import kotlinx.android.synthetic.main.layout_canceled_sale_filter.*
import kotlinx.android.synthetic.main.layout_filter_minhas_vendas_bottom_sheet_dialog.*
import kotlinx.android.synthetic.main.layout_history_sales.*
import kotlinx.android.synthetic.main.layout_item_card_brand.view.*
import kotlinx.android.synthetic.main.layout_item_payment_type.view.*
import kotlinx.android.synthetic.main.layout_other_filter_items.*
import kotlinx.android.synthetic.main.layout_top_filter.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val PAYMENT_TYPES_GRID_SPAN = 2
const val BRAND_CARD_GRID_SPAN = 4

class MinhasVendasFilterBottomSheetFragment : BottomSheetDialogFragment(),
    MinhasVendasFilterBottomSheetContract.View {

    val presenter: MinhasVendasFilterBottomSheetPresenter by inject {
        parametersOf(this)
    }

    var onDismissListener: OnDismissListener? = null

    var isLoadingPaymentTypes = false

    interface OnDismissListener {
        fun onDismiss()
    }

    companion object {
        private val ARG_PARAM_QUICK_FILTER = "ARG_PARAM_QUICK_FILTER"
        private val ARG_PARAM_IS_LOADING_PAYMENT_TYPES = "ARG_PARAM_IS_LOADING_PAYMENT_TYPES"
        private val ARG_PARAM_IS_FILTER_BRANDS_DATA = "ARG_PARAM_IS_FILTER_BRANDS_DATA"
        fun create(
            quickFilter: QuickFilter,
            listener: OnResultListener,
            isLoadingPaymentTypes: Boolean = false,
            isFilterBrandsData: Boolean = false
        ) = MinhasVendasFilterBottomSheetFragment().apply {
            this.listener = listener
            this.arguments = Bundle().apply {
                this.putSerializable(ARG_PARAM_QUICK_FILTER, quickFilter)
                this.putBoolean(ARG_PARAM_IS_LOADING_PAYMENT_TYPES, isLoadingPaymentTypes)
                this.putBoolean(ARG_PARAM_IS_FILTER_BRANDS_DATA, isFilterBrandsData)
            }
        }
    }

    private var listener: OnResultListener? = null


    private fun isCanceledFilters(): Boolean = (arguments
        ?.getSerializable(ARG_PARAM_QUICK_FILTER) as QuickFilter).status
        ?.contains(ExtratoStatusDef.CANCELADA) ?: false


    private fun isMoreFilters(): Boolean =
        arguments?.getSerializable(ARG_PARAM_IS_FILTER_BRANDS_DATA) as Boolean

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.layout_filter_minhas_vendas_bottom_sheet_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Analytics.trackScreenView(
            screenName = SCREENVIEW_FILTRO_DAS_VENDAS_CANCELADAS,
            screenClass = this.javaClass
        )
        configureLayoutBrands()
        configureLayoutPaymentsType()
        configureListeners()
        loadFilters()

        if (isCanceledFilters()) {
            configureFilterInputFields()
        }

        if (isMoreFilters())
            configureTextViewMoreFilters()

        editTextGa()
    }

    private fun editTextGa() { 
        editInputCanceledSaleValue.setOnClickListener {
            gaSendEditTextInteracao("valor de venda")
        }
        editInputCanceledSaleCancelValue.setOnClickListener {
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

    private fun configureFilterInputFields() { 
        editInputCanceledSaleCancelValue.addTextChangedListener(
            editInputCanceledSaleCancelValue
                .getMaskMoney(editInputCanceledSaleCancelValue)
        )

        editInputCanceledSaleValue.addTextChangedListener(
            editInputCanceledSaleValue
                .getMaskMoney(editInputCanceledSaleValue)
        )

    }

    private fun configureTextViewMoreFilters() { 
        textViewAmountInitial.addTextChangedListener(
            textViewAmountInitial.getMaskMoney(
                textViewAmountInitial
            )
        )
        textViewAmountFinal.addTextChangedListener(
            textViewAmountFinal.getMaskMoney(
                textViewAmountFinal
            )
        )

        verifyErrorAmount()

        focusTypeface(textViewAmountInitial)
        focusTypeface(textViewAmountFinal)
    }

    private fun verifyErrorAmount() { 
        textViewAmountInitial?.addTextChangedListener(
            onTextChanged = { s, start, before, count ->
                s?.let {
                    getDoubleValueForMoneyInput(textViewAmountFinal)?.let { final ->
                        val initial = it.toString().moneyToBigDecimalValue().toDouble()
                        viewErrorAmount(final < initial)
                    }
                }
            }
        )

        textViewAmountFinal?.addTextChangedListener(
            onTextChanged = { s, start, before, count ->
                s?.let {

                    getDoubleValueForMoneyInput(textViewAmountInitial)?.let { initial ->
                        val final = it.toString().moneyToBigDecimalValue().toDouble()
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

    private fun isFocus(b: Boolean, typefaceEditTextView: TypefaceEditTextView) { 
        if (b)
            textViewStyle(
                typefaceEditTextView,
                R.drawable.focus_border_blue_filter,
                R.color.color_017CEB
            )
        else
            textViewStyle(typefaceEditTextView, R.drawable.date_border_4dp, R.color.color_353A40)
    }

    
    private fun textViewStyle(
        typefaceEditTextView: TypefaceEditTextView?,
        @DrawableRes drawable: Int,
        @ColorRes color: Int
    ) {
        typefaceEditTextView?.setBackgroundResource(drawable)
        typefaceEditTextView?.setTextColor(
            ContextCompat.getColor(
                this@MinhasVendasFilterBottomSheetFragment.requireContext(),
                color
            )
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        setupBottomSheet(dialog)
        return dialog
    }

    override fun onPause() {
        super.onPause()
        this.presenter.onPause()
    }

    override fun onDestroy() {
        this.presenter.onDestroy()
        super.onDestroy()
    }

    /**
     * método para vericar quando o dialog muda de estado
     * @param dialog
     * */
    private fun setupBottomSheet(dialog: Dialog) {
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

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }
                })
            }
        }

    }

    private fun configureLayoutPaymentsType() {
        GridLayoutManager(requireContext(), PAYMENT_TYPES_GRID_SPAN).let {
            this.rvPaymentTypes?.layoutManager = it
        }
    }

    private fun configureLayoutBrands() {
        GridLayoutManager(requireContext(), BRAND_CARD_GRID_SPAN).let {
            this.rvBrands?.layoutManager = it
        }
    }

    private fun configureListeners() { 
        this.closeButton?.setOnClickListener {
            onDismissListener?.onDismiss()
            this.dismiss()
        }
        this.clearFilterButton?.setOnClickListener {
            this.presenter.cleanFilter()
            Toast.makeText(
                activity,
                getString(R.string.description_action_clear_filter),
                Toast.LENGTH_SHORT
            ).show()
        }
        this.applyFilterButton?.setOnClickListener {
            if (isCanceledFilters())
                this.presenter.applyFilter(configureFilterCanceled())

            if (isMoreFilters())
                this.presenter.applyFilter(configureFilterSalesByPeriod())

            if (isLoadingPaymentTypes.not())
                this.presenter.applyFilter(configureFilterSalesByToday())

            if (isMoreFilters().not() && isCanceledFilters().not() && isLoadingPaymentTypes)
                this.presenter.applyFilter()

            Toast.makeText(
                activity,
                getString(R.string.description_action_apply_filter),
                Toast.LENGTH_SHORT
            ).show()
        }
        this.retryButton?.setOnClickListener {
            this.loadFilters()
        }
    }

    private fun configureFilterCanceled(): QuickFilter { 
        var saleGrossValue = getDoubleValueForMoneyInput(editInputCanceledSaleValue)
        var saleCancelValue = getDoubleValueForMoneyInput(editInputCanceledSaleCancelValue)

        var authorizationCode =
            editInputCanceledSaleAuthorizationCode.text.toString().trim().verifyNullOrBlankValue()
        var nsu = editInputCanceledSaleNsu.text.toString().verifyNullOrBlankValue()
        var tid = editInputCanceledSaleTID.text.toString().verifyNullOrBlankValue()


        return QuickFilter.Builder()
            .saleGrossAmount(saleGrossValue)
            .grossAmount(saleCancelValue)
            .authorizationCode(authorizationCode)
            .nsu(nsu)
            .tid(tid)
            .build()
    }

    private fun configureFilterSalesByPeriod(): QuickFilter { 

        val nsu = editTextSalesNsuDoc.getText().verifyNullOrBlankValue()
        val codeAuthorization = editTextSaleAuthorizationCode.getText().verifyNullOrBlankValue()
        val numberCard = editTextSaleCadNumber.getText().verifyNullOrBlankValue()
        val summary = editTextSaleOperationSummary.getText().verifyNullOrBlankValue()
        val tid = editTextSaleTid.getText().verifyNullOrBlankValue()

        val initialAmount = getDoubleValueForMoneyInput(textViewAmountInitial)
        val finalAmount = getDoubleValueForMoneyInput(textViewAmountFinal)


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

        var nsu = editTextSalesNsuDoc.getText().verifyNullOrBlankValue()
        var codeAuthorization = editTextSaleAuthorizationCode.getText().verifyNullOrBlankValue()

        return QuickFilter.Builder()
            .authorizationCode(codeAuthorization)
            .nsu(nsu)
            .build()
    }

    private fun loadFilters() { 
        this.arguments?.let { bundle ->
            isLoadingPaymentTypes = bundle.getBoolean(ARG_PARAM_IS_LOADING_PAYMENT_TYPES, false)

            this.presenter.isCanceledFilters = isCanceledFilters()
            this.presenter.isMoreFilters = isMoreFilters()
            bundle.getSerializable(ARG_PARAM_QUICK_FILTER)?.let {
                this.presenter.load(it as QuickFilter, isLoadingPaymentTypes)
            }
        }
    }

    override fun loadingState() { 
        this.layoutTop?.visibility = View.VISIBLE

        visibleLayout()

        this.layoutContent?.visibility = View.GONE
        this.layoutPaymentTypes?.visibility = View.GONE
        this.layoutBrands?.visibility = View.GONE
        this.layoutError?.visibility = View.GONE
        this.waitingLayout?.visibility = View.VISIBLE
    }

    private fun visibleLayout() { 
        if (isCanceledFilters())
            this.layout_canceled_sales?.visible()

        if (isMoreFilters())
            this.layout_sales?.visible()

        if (isLoadingPaymentTypes.not()) {
            this.layout_canceled_sales?.gone()
            this.layout_sales?.visible()
            this.include_amount?.gone()
            this.layout_more_sales?.gone()
        }
    }

    override fun logout(msg: ErrorMessage) {

    }

    override fun hideLoading() { 
        this.waitingLayout?.visibility = View.GONE
        this.layoutBottom?.visibility = View.VISIBLE
    }


    override fun showCardBrands(cardBrands: List<ItemSelectable<CardBrand>>) {
        showFilterContent()
        val adapter = DefaultViewListAdapter(cardBrands, R.layout.layout_item_card_brand)
        adapter.setBindViewHolderCallback(object :
            DefaultViewListAdapter.OnBindViewHolder<ItemSelectable<CardBrand>> {

            private fun changeBorder(item: ItemSelectable<CardBrand>, view: View) {
                if (item.isSelected) {
                    view.borderCardBrandImage.setBackgroundResource(R.drawable.rounded_border_blue_sales)
                } else {
                    view.borderCardBrandImage.setBackgroundResource(R.drawable.rounded_border_gray)
                }
            }

            override fun onBind(item: ItemSelectable<CardBrand>, holder: DefaultViewHolderKotlin) {
                BrandCardHelper.getUrlBrandImageByCode(item.data.code)?.let { itUrl ->
                    ImageUtils.loadImage(holder.mView.cardBrandImage, itUrl)
                }
                changeBorder(item, holder.mView)
                holder.mView.contentDescription = getString(
                    if (item.isSelected) R.string.description_focused_selected_flag_card else R.string.description_focused_unselected_flag_card,
                    item.data.name
                )
                holder.mView.setOnClickListener {
                    item.isSelected = !item.isSelected
                    changeBorder(item, holder.mView)
                    holder.mView.contentDescription = getString(
                        if (item.isSelected) R.string.description_focused_selected_flag_card else R.string.description_focused_unselected_flag_card,
                        item.data.name
                    )
                    gaSendButtonCancelCheckboxBandeira(item.data.name)
                }
            }
        })
        this.rvBrands?.adapter = adapter
    }

    override fun showPaymentTypes(paymentTypes: List<ItemSelectable<PaymentType>>) {
        showFilterContent()
        this.layoutPaymentTypes?.visibility = View.VISIBLE
        val adapter =
            DefaultViewListAdapter(paymentTypes, R.layout.layout_item_payment_type_sales_filter)
        adapter.setBindViewHolderCallback(object :
            DefaultViewListAdapter.OnBindViewHolder<ItemSelectable<PaymentType>> {
            private fun changeColor(item: ItemSelectable<PaymentType>, view: View) {
                if (item.isSelected) {
                    view.setBackgroundResource(R.drawable.filled_rounded_shape_017ceb)
                    view.paymentTypeNameText?.setTextColor(
                        ContextCompat.getColor(
                            this@MinhasVendasFilterBottomSheetFragment.requireContext(),
                            android.R.color.white
                        )
                    )
                } else {
                    view.setBackgroundResource(R.drawable.rounded_border_gray)
                    view.paymentTypeNameText?.setTextColor(
                        ContextCompat.getColor(
                            this@MinhasVendasFilterBottomSheetFragment.requireContext(),
                            R.color.color_353A40
                        )
                    )
                }
            }

            override fun onBind(
                item: ItemSelectable<PaymentType>,
                holder: DefaultViewHolderKotlin
            ) {
                holder.mView.paymentTypeNameText?.text = item.data.name
                changeColor(item, holder.mView)
                holder.mView.contentDescription = getString(
                    if (item.isSelected) R.string.description_focused_selected_payment_method else R.string.description_focused_unselected_payment_method,
                    item.data.name
                )
                holder.mView.setOnClickListener {
                    item.isSelected = !item.isSelected
                    changeColor(item, holder.mView)
                    holder.mView.contentDescription = getString(
                        if (item.isSelected) R.string.description_focused_selected_payment_method else R.string.description_focused_unselected_payment_method,
                        item.data.name
                    )
                    gaSendButtonCancelCheckbox(item.data.name)
                }
            }
        })
        this.rvPaymentTypes?.adapter = adapter
    }

    private fun showFilterContent() { 
        this.layoutContent?.visible()
        this.layoutBrands?.visible()
        this.layout_canceled_sales?.gone()

        if (isLoadingPaymentTypes)
            this.layout_sales?.gone()
    }

    override fun applyFilter(quickFilter: QuickFilter) { 
        this.listener?.onResult(quickFilter)
        onDismissListener?.onDismiss()
        this.dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss()
    }

    override fun loadMoreFilters(quickFilter: QuickFilter?) { 
        quickFilter?.run {

            loadNsuAndAuthorizationCode(quickFilter)

            if (quickFilter.tid?.isNotEmpty() == true) {
                editInputCanceledSaleTID?.text = SpannableStringBuilder.valueOf(quickFilter.tid)
                editTextSaleTid?.setText(quickFilter.tid)
            }

            quickFilter.saleGrossAmount?.let {
                editInputCanceledSaleValue?.text = SpannableStringBuilder
                    .valueOf(it.toPtBrRealString())
            }

            quickFilter.grossAmount?.let {
                editInputCanceledSaleCancelValue?.text = SpannableStringBuilder
                    .valueOf(it.toPtBrRealString())
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
                editTextSaleCadNumber?.setText(quickFilter.truncatedCardNumber.toString())
            }


            if (quickFilter.softDescriptor?.isNotEmpty() == true) {
                editTextSaleOperationSummary?.setText(quickFilter.softDescriptor)
            }
        }
    }

    override fun loadNsuAndAuthorizationCode(quickFilter: QuickFilter?) { 
        quickFilter?.let { filter ->
            if (filter.authorizationCode?.isNotEmpty() == true) {
                editInputCanceledSaleAuthorizationCode?.text =
                    SpannableStringBuilder.valueOf(filter.authorizationCode)
                editTextSaleAuthorizationCode.setText(filter.authorizationCode)
            }

            if (filter.nsu?.isNotEmpty() == true) {
                editInputCanceledSaleNsu?.text = SpannableStringBuilder.valueOf(filter.nsu)
                editTextSalesNsuDoc.setText(filter.nsu)
            }
        }

    }

    override fun showError() { 
        this.layoutContent?.gone()
        this.layoutPaymentTypes?.gone()
        this.layout_canceled_sales?.gone()
        this.layout_sales?.gone()
        this.layoutBrands?.gone()
        this.waitingLayout?.gone()
        this.layoutBottom?.gone()
        this.layoutError?.visible()
    }

    interface OnResultListener { 
        fun onResult(quickFilter: QuickFilter)
    }

    override fun showCancelInputs() { 
        this.layoutContent?.visible()
        visibleLayout()
    }

    private fun gaSendButtonCancelCheckbox(name: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, VENDAS_CANCELADAS_CATEGORY),
            action = listOf(Action.MODAL, Action.MAIS_FILTROS),
            label = listOf(Label.BOTAO, "forma-de-pagamento", name)
        )
    }

    private fun gaSendButtonCancelCheckboxBandeira(name: String) { 
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, VENDAS_CANCELADAS_CATEGORY),
            action = listOf(Action.MODAL, Action.MAIS_FILTROS),
            label = listOf(Label.BOTAO, "bandeira", name)
        )
    }

    private fun gaSendEditTextInteracao(name: String) { 
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, VENDAS_CANCELADAS_CATEGORY),
            action = listOf(Action.MODAL, Action.MAIS_FILTROS),
            label = listOf(Label.INTERACAO, name)
        )
    }
}
