package br.com.mobicare.cielo.commons.ui.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.filter.FilterContract
import br.com.mobicare.cielo.commons.presentation.filter.model.CardBrand
import br.com.mobicare.cielo.commons.presentation.filter.model.FilterReceivableResponse
import br.com.mobicare.cielo.commons.presentation.filter.model.PaymentType
import br.com.mobicare.cielo.commons.ui.adapter.CardBrandAdapter
import br.com.mobicare.cielo.commons.ui.adapter.PaymentTypeAdapter
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.addArgument
import br.com.mobicare.cielo.databinding.LayoutFilterMinhasVendasBottomSheetDialogBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.minhasVendas.fragments.common.ItemSelectable
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class FilterBottomSheetFragment : BottomSheetDialogFragment(), FilterContract.View {

    private val title: String? by lazy {
        arguments?.getString(FILTER_TITLE_SCREEN_KEY)
    }

    private val description: String? by lazy {
        arguments?.getString(FILTER_DESCRIPTION_KEY)
    }

    private val quickFilter: QuickFilter? by lazy {
        arguments?.getSerializable(SELECTED_FILTER_KEY) as QuickFilter?
    }

    var onFilterActionListener: OnFilterActionListener? = null

    val filterPresenter: FilterContract.Presenter by inject {
        parametersOf(this)
    }

    private lateinit var paymentTypeAdapter: PaymentTypeAdapter
    private lateinit var cardBrandAdapter: CardBrandAdapter
    private var binding: LayoutFilterMinhasVendasBottomSheetDialogBinding? = null
    private var allCardBrands: List<ItemSelectable<CardBrand?>>? = null
    private var allPaymentTypes: List<ItemSelectable<PaymentType?>>? = null


    interface OnFilterActionListener {
        fun onCleanFilter()
        fun onFilterSelected(quickFilter: QuickFilter)
        fun onDismiss()
    }

    companion object {

        const val FILTER_TITLE_SCREEN_KEY = "br.com.cielo.commons.filter.title"
        const val FILTER_DESCRIPTION_KEY = "br.com.cielo.commons.filter.description"
        const val SELECTED_FILTER_KEY = "br.com.cielo.commons.filter.quickFilter"

        fun create(title: String? = null,
                   description: String? = null,
                   quickFilter: QuickFilter?): FilterBottomSheetFragment {
            val currentFilterFrag = FilterBottomSheetFragment()

            title?.let {
                currentFilterFrag.addArgument(FILTER_TITLE_SCREEN_KEY, title)
            }

            quickFilter?.let {
                currentFilterFrag.addArgument(SELECTED_FILTER_KEY, quickFilter)
            }

            description?.let {
                currentFilterFrag.addArgument(FILTER_DESCRIPTION_KEY, description)
            }


            return currentFilterFrag
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title?.let { titleVal ->
            binding?.layoutTop?.tvTitle?.text = SpannableStringBuilder.valueOf(titleVal)
        }

        setupAllFilterFields()
        setupClickListeners()

        description?.let {
            binding?.textFilterDescription?.visibility = View.VISIBLE
            binding?.textFilterDescription?.text = SpannableStringBuilder.valueOf(it)
        }

        quickFilter?.let {
            filterPresenter.avaiableFilters(it)
        }
        configureRecyclerViews()
    }

    private fun setupAllFilterFields() {
        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            .let {
                binding?.rvPaymentTypes?.layoutManager = it
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): RelativeLayout? {
        binding = LayoutFilterMinhasVendasBottomSheetDialogBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        setupBottomSheet(dialog)
        return dialog
    }

    private fun setupClickListeners() {
        binding?.layoutTop?.closeButton?.setOnClickListener {
            dismiss()
        }

        binding?.applyFilterButton?.setOnClickListener {

            if (allCardBrands?.none { it.isSelected } == false ||
                allPaymentTypes?.none { it.isSelected } == false) {
                val newQuickFilter = QuickFilter.Builder()
                    .cardBrand(allCardBrands?.filter { it.isSelected }
                        ?.map { it.data?.value?.toInt()!! })
                    .paymentType(allPaymentTypes?.filter { it.isSelected }
                        ?.map { it.data?.value?.toInt()!! })
                    .initialDate(quickFilter?.initialDate!!)
                    .finalDate(quickFilter?.finalDate!!).build()

                onFilterActionListener?.onFilterSelected(newQuickFilter)
            }

            dismiss()
        }


        binding?.clearFilterButton?.setOnClickListener {
            onFilterActionListener?.onCleanFilter()
            dismiss()
        }

    }

    private fun setupBottomSheet(dialog: Dialog) {
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                R.id.design_bottom_sheet) as FrameLayout
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

    override fun displayAvaiableFilters(filterReceivableResponse: FilterReceivableResponse) {

        allCardBrands = filterReceivableResponse.cardBrands?.map {
            ItemSelectable(it)
        }
        allPaymentTypes = filterReceivableResponse.paymentTypes?.map {
            ItemSelectable(it)
        }

        allCardBrands?.let {
            showCardBrandFilterContent()
            cardBrandAdapter.updateAdapter(it)

        }
        allPaymentTypes?.let {
            showPaymentTypeFilterContent()
            paymentTypeAdapter.updateAdapter(it)
        }

        loadSelectedFilter()
    }

    private fun loadSelectedFilter() {
        quickFilter?.let {
            it.cardBrand?.let { cardBrands ->
                cardBrands.forEach { currCardBrand ->
                    allCardBrands?.find { currAllCardBrand ->
                        currAllCardBrand.data?.value?.toInt() == currCardBrand
                    }?.isSelected = true
                }
            }

            it.paymentType?.let { paymentTypes ->

                paymentTypes.forEach { currPaymentType ->
                    allPaymentTypes?.find { currAllPayment ->
                        currAllPayment.data?.value?.toInt() == currPaymentType
                    }?.isSelected = true
                }
            }
        }
    }

    override fun showError(error: ErrorMessage?) {
        binding?.apply {
            layoutContent.gone()
            layoutPaymentTypes.gone()
            layoutBrands.gone()
            waitingLayout.gone()
            layoutBottom.gone()
            layoutError.visible()
        }

    }

    override fun logout(msg: ErrorMessage?) {
        SessionExpiredHandler.userSessionExpires(requireContext(), true)
    }

    override fun showLoading() {
        if (isAdded) {
            binding?.apply {
                layoutTop.root.visible()
                waitingLayout.visible()
                layoutContent.gone()
                layoutPaymentTypes.gone()
                layoutBrands.gone()
                layoutError.gone()
            }
        }
    }

    override fun hideLoading() {
        binding?.apply {
            waitingLayout.gone()
            layoutBottom.visible()
        }

    }

    private fun configureRecyclerViews() {
        cardBrandAdapter = CardBrandAdapter()
        paymentTypeAdapter = PaymentTypeAdapter()
        binding?.apply {
            this.rvBrands.adapter = cardBrandAdapter
            this.rvPaymentTypes.adapter = paymentTypeAdapter
        }
    }

    private fun showCardBrandFilterContent() {
        binding?.apply {
            layoutContent.visible()
            layoutBrands.visible()
        }
    }


    private fun showPaymentTypeFilterContent() {
        binding?.apply {
            layoutContent.visible()
            layoutPaymentTypes.visible()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onFilterActionListener?.onDismiss()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}
