package br.com.mobicare.cielo.chargeback.presentation.filters

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackListParamsStatus
import br.com.mobicare.cielo.chargeback.data.model.request.OnResultFilterListener
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilters
import br.com.mobicare.cielo.chargeback.presentation.filters.adapters.CardBrandAdapter
import br.com.mobicare.cielo.chargeback.presentation.filters.adapters.ProcessAdapter
import br.com.mobicare.cielo.chargeback.presentation.filters.adapters.TreatedTypeAdapter
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants.PENDING
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.constants.FOUR
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ONE_HUNDRED_EIGHTY_NEGATIVE
import br.com.mobicare.cielo.commons.constants.SELECTED_LESS_ONE_DAY
import br.com.mobicare.cielo.commons.constants.THIRTY_LONG
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.utils.CalendarCustom
import br.com.mobicare.cielo.commons.utils.CalendarDialogCustom
import br.com.mobicare.cielo.commons.utils.CompareDatesResults
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.commons.utils.calculateNowMinusDays
import br.com.mobicare.cielo.commons.utils.compareDates
import br.com.mobicare.cielo.databinding.BottomSheetChargebackFiltersBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.util.Calendar

class ChargebackFiltersBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: BottomSheetChargebackFiltersBinding? = null
    private val binding get() = _binding

    private val viewModel: ChargebackFiltersViewModel by viewModel()
    private lateinit var brandsAdapter: CardBrandAdapter
    private lateinit var processAdapter: ProcessAdapter
    private lateinit var treatedTypeAdapter: TreatedTypeAdapter

    private var selectStartDate = DataCustomNew()
    private var selectEndDate = DataCustomNew()

    private lateinit var applyFilterListener: OnResultFilterListener
    private var chargebackStatus = String()

    private var nsu: String? = null
    private var tid: String? = null
    private var case: String? = null
    private var fieldMandatoryToFill: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetChargebackFiltersBinding.inflate(inflater, container, false)
        viewModel.getChargebackFilters()
        return binding?.root
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
                behavior.peekHeight = ZERO
                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState >= FOUR) {
                            dismiss()
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                })
            }
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerViews()
        setupDateTextInputViewsListener()
        setupButtonsClickListener()
        configureDialogTitle()
        configureDateFieldsWhenOpenDialog()
    }

    override fun onResume() {
        super.onResume()
        handleAPIService()
        restoreIdentifiersFields()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            applyFilterListener: OnResultFilterListener,
            chargebackStatus: String
        ) = ChargebackFiltersBottomSheetDialog().apply {
            this.applyFilterListener = applyFilterListener
            this.chargebackStatus = chargebackStatus
        }
    }

    private fun handleAPIService() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> onLoading()
                is UiState.Success -> onSuccess(state.data)
                is UiState.Error, UiState.Empty -> onError()
            }
        }
    }

    private fun onSuccess(data: ChargebackFilters?) {
        treatedTypeDoneOrPending()
        data?.let {
            hideLoading()
            data.brands?.let {
                brandsAdapter.updateAdapter(it)
            }

            data.process?.let {
                processAdapter.updateAdapter(it)
            }

            data.disputeStatus?.let{ treatedType ->
                treatedTypeAdapter.updateAdapter(treatedType)
            }
        }
    }

    private fun treatedTypeDoneOrPending() {
        binding?.treatedTypeContainerLayout?.visible(chargebackStatus == ChargebackListParamsStatus.DONE.value)
    }

    private fun onLoading() {
        binding?.apply {
            treatedTypeDoneOrPending()
            this.brandsRecyclerView.gone()
            this.chargebacksTreatedTypeRecyclerView.gone()
            this.chargebacksProcessRecyclerView.gone()
            this.shimmerCardbrandsIncludedLayout.root.visible()
            this.shimmerProcessIncludedLayout.root.visible()
            this.shimmerTreatedTypeIncludedLayout.root.visible()
            this.shimmerCardbrandsIncludedLayout.shimmerCardbrands.startShimmer()
            this.shimmerTreatedTypeIncludedLayout.shimmerProcess.startShimmer()
            this.shimmerProcessIncludedLayout.shimmerProcess.startShimmer()
        }
    }

    private fun hideLoading() {
        binding?.apply {
            this.shimmerCardbrandsIncludedLayout.root.gone()
            this.shimmerTreatedTypeIncludedLayout.root.gone()
            this.shimmerProcessIncludedLayout.root.gone()
            this.brandsRecyclerView.visible()
            this.chargebacksTreatedTypeRecyclerView.visible()
            this.chargebacksProcessRecyclerView.visible()
        }
    }

    private fun onError() {
        binding?.apply {
            this.brandContainerLayout.gone()
            this.treatedTypeContainerLayout.gone()
            this.processContainerLayout.gone()
        }
    }

    private fun configureRecyclerViews() {
        brandsAdapter = CardBrandAdapter()
        treatedTypeAdapter = TreatedTypeAdapter()
        processAdapter = ProcessAdapter()
        binding?.apply {
            this.brandContainerLayout.visible()
            this.processContainerLayout.visible()
            this.brandsRecyclerView.adapter = brandsAdapter
            this.chargebacksTreatedTypeRecyclerView.adapter = treatedTypeAdapter
            this.chargebacksProcessRecyclerView.adapter = processAdapter
        }
    }

    private fun setupDateTextInputViewsListener() {
        binding?.apply {
            this.initialDateTextInput.setOnClickListener { onClickDateInit() }
            this.finalDateTextInput.setOnClickListener { onClickFinalDate() }
        }
    }

    private fun getCurrentDateParts(): Triple<Int, Int, Int> {
        val cal = Calendar.getInstance()
        val dia = CalendarCustom.getDay(cal)
        val mes = CalendarCustom.getMonth(cal) + ONE
        val ano = CalendarCustom.getYear(cal)
        return Triple(dia, mes, ano)
    }

    private fun onClickDateInit() {
        fieldMandatoryToFill = true
        val startDatePeriod: Int = ONE_HUNDRED_EIGHTY_NEGATIVE

        CalendarDialogCustom(startDatePeriod,
            ZERO,
            SELECTED_LESS_ONE_DAY,
            getCurrentDateParts().first,
            getCurrentDateParts().second,
            getCurrentDateParts().third,
            getString(R.string.extrato_filtro_data_inicio),
            context as Context,
            { _, year, monthOfYear, dayOfMonth ->
                selectStartDate.setDate(year, monthOfYear, dayOfMonth)
                binding?.initialDateTextInput?.text =
                    Editable.Factory.getInstance().newEditable(selectStartDate.formatBRDate())
                setupDateFieldsWarnings()
            },
            R.style.DialogThemeMeusRecebimentos
        ).show()
    }

    private fun onClickFinalDate() {
        fieldMandatoryToFill = true
        val finalDatePeriod: Int = ONE_HUNDRED_EIGHTY_NEGATIVE

        CalendarDialogCustom(finalDatePeriod,
            ZERO,
            SELECTED_LESS_ONE_DAY,
            getCurrentDateParts().first,
            getCurrentDateParts().second,
            getCurrentDateParts().third,
            getString(R.string.extrato_filtro_data_fim),
            context as Context,
            { _, year, monthOfYear, dayOfMonth ->
                selectEndDate.setDate(year, monthOfYear, dayOfMonth)
                binding?.finalDateTextInput?.text =
                    Editable.Factory.getInstance().newEditable(selectEndDate.formatBRDate())
                setupDateFieldsWarnings()
            },
            R.style.DialogThemeMeusRecebimentos
        ).show()
    }

    private fun checkForTreatedDates(): Boolean {
        val startDateLD = LocalDate.of(
            selectStartDate.getYear(),
            selectStartDate.getMonth(),
            selectStartDate.getDay()
        )
        val finalDateLD =
            LocalDate.of(selectEndDate.getYear(), selectEndDate.getMonth(), selectEndDate.getDay())

        return ((compareDates(
            startDateLD.toString(),
            finalDateLD.toString()
        ) == CompareDatesResults.FINAL_DATE_HIGHER_THEN_INITIAL_DATE) ||
                ((compareDates(
                    startDateLD.toString(),
                    finalDateLD.toString()
                ) == CompareDatesResults.EQUALS_DATES)))
    }

    private fun setupDateFieldsWarnings() {
        if (binding?.initialDateTextInput?.text.isNullOrBlank()) {
            binding?.apply {
                initialDateWarningTextView.visible()
                initialDateWarningTextView.text =
                    getString(R.string.chargeback_filter_date_field_mustbe_filled)
                initialDateTextInputLayout.setBackgroundResource(R.drawable.background_stroke_1dp_round_danger_400)
            }
        } else {
            binding?.apply {
                initialDateWarningTextView.gone()
                initialDateTextInputLayout.setBackgroundResource(R.drawable.background_transparent_border_cloud_200_rounded)
            }
        }

        if (binding?.finalDateTextInput?.text.isNullOrBlank()) {
            binding?.apply {
                finalDateWarningTextView.visible()
                finalDateWarningTextView.text =
                    getString(R.string.chargeback_filter_date_field_mustbe_filled)
                finalDateTextInputLayout.setBackgroundResource(R.drawable.background_stroke_1dp_round_danger_400)
            }
        } else {
            if (checkForTreatedDates() || binding?.initialDateTextInput?.text.isNullOrBlank()) {
                binding?.apply {
                    finalDateWarningTextView.gone()
                    finalDateTextInputLayout.setBackgroundResource(R.drawable.background_transparent_border_cloud_200_rounded)
                }
            } else {
                binding?.apply {
                    finalDateWarningTextView.visible()
                    finalDateWarningTextView.text =
                        getString(R.string.chargeback_filter_date_selector_hint_error)
                    finalDateTextInputLayout.setBackgroundResource(R.drawable.background_stroke_1dp_round_danger_400)
                }
            }
        }
    }

    private fun configureDateFieldsWhenOpenDialog() {
        if (chargebackStatus == ChargebackListParamsStatus.DONE.value && fieldMandatoryToFill.not()) {
            selectEndDate = DataCustomNew()
            selectEndDate.setDate(
                getCurrentDateParts().third,
                getCurrentDateParts().second,
                getCurrentDateParts().first
            )
            binding?.finalDateTextInput?.text =
                Editable.Factory.getInstance().newEditable(selectEndDate.formatBRDate())

            selectStartDate = DataCustomNew()
            val nowMinus30DaysDate = calculateNowMinusDays(THIRTY_LONG)
            selectStartDate.setDate(
                nowMinus30DaysDate.year,
                nowMinus30DaysDate.monthValue,
                nowMinus30DaysDate.dayOfMonth
            )
            binding?.initialDateTextInput?.text =
                Editable.Factory.getInstance().newEditable(selectStartDate.formatBRDate())
        }
    }

    private fun setupButtonsClickListener() {
        binding?.apply {
            this.buttonClearFilters.setOnClickListener { clearFilters() }
            this.buttonApplyFilters.setOnClickListener { checkInfosToApplyFilter() }
        }
    }

    private fun configureDialogTitle() {
        binding?.apply {
            if (chargebackStatus == PENDING)
                this.filterTitleTextView.text =
                    resources.getString(R.string.chargeback_filter_title_pending)
            else
                this.filterTitleTextView.text =
                    resources.getString(R.string.chargeback_filter_title_treated)
        }
    }

    private fun restoreIdentifiersFields() {
        binding?.apply {
            nsu?.let { nsuDocInputText.setText(it) }
            tid?.let { tidInputText.setText(it) }
            case?.let { caseInputText.setText(it) }
        }
    }

    private fun clearFilters() {
        applyFilterListener.onClearFilterListener(chargebackStatus)
        dismiss()
    }

    private fun checkForBlankDateFields(): Boolean {
        return (binding?.finalDateTextInput?.text?.isNotBlank() == true &&
                binding?.initialDateTextInput?.text?.isNotBlank() == true)
    }

    private fun checkInfosToApplyFilter() {
        if (chargebackStatus == PENDING && fieldMandatoryToFill.not()) {
            mountObjectAfterFilter(null, null, true)
        } else if (checkForTreatedDates() && checkForBlankDateFields()) {
            val startDate = selectStartDate.formatDateToAPI()
            val endDate = selectEndDate.formatDateToAPI()
            mountObjectAfterFilter(startDate, endDate, false)
        } else {
            setupDateFieldsWarnings()
        }
    }

    fun mountObjectAfterFilter(
        startDate: String?,
        endDate: String?,
        filterPendingWithoutDate: Boolean
    ) {
        binding?.apply {
            nsu = this.nsuDocInputText.getText()
            tid = this.tidInputText.getText()
            case = this.caseInputText.getText()
            val listOfSelectedBrandsIDs = brandsAdapter.getUserSelectedCardBrands()
            val listOfSelectedBrandsProcessIDs = processAdapter.getUserSelectedProcess()
            val listOfSelectedTreatedTypeIDS = treatedTypeAdapter.getUserSelectedTreatedType()
            FilterUtils.applyFilters(
                chargebackStatus = chargebackStatus,
                applyFilterListener = applyFilterListener,
                initialDate = startDate,
                finalDate = endDate,
                case = case,
                tid = tid,
                nsu = nsu,
                listOfSelectedBrands = listOfSelectedBrandsIDs,
                listOfSelectedProcess = listOfSelectedBrandsProcessIDs,
                filterPendingWithoutDate = filterPendingWithoutDate,
                disputeStatus = listOfSelectedTreatedTypeIDS
            )
        }
        dismiss()
    }
}