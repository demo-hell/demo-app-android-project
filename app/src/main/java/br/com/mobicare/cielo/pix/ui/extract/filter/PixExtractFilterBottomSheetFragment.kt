package br.com.mobicare.cielo.pix.ui.extract.filter

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import br.com.cielo.libflue.util.extensions.visible
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.FORMAT_DATE_AMERICAN
import br.com.mobicare.cielo.commons.constants.FORMAT_DATE_PORTUGUESE
import br.com.mobicare.cielo.commons.constants.NINETY
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.utils.CalendarCustom
import br.com.mobicare.cielo.commons.utils.CalendarDialogCustom
import br.com.mobicare.cielo.commons.utils.DataCustom
import br.com.mobicare.cielo.commons.utils.dateFormatToBr
import br.com.mobicare.cielo.commons.utils.parseToLocalDate
import br.com.mobicare.cielo.commons.utils.setupBottomSheet
import br.com.mobicare.cielo.commons.utils.showCustomToast
import br.com.mobicare.cielo.databinding.FragmentPixExtractFilterBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.domain.FilterExtract
import br.com.mobicare.cielo.pix.enums.PixExtractFilterEnum.ALL_RELEASES
import br.com.mobicare.cielo.pix.enums.PixExtractFilterEnum.ALL_TRANSACTIONS
import br.com.mobicare.cielo.pix.enums.PixExtractFilterEnum.CREDIT
import br.com.mobicare.cielo.pix.enums.PixExtractFilterEnum.DEBIT
import br.com.mobicare.cielo.pix.enums.PixExtractFilterEnum.OTHERS_PERIODS
import br.com.mobicare.cielo.pix.enums.PixExtractFilterEnum.QRCODE
import br.com.mobicare.cielo.pix.enums.PixExtractFilterEnum.RECENTS
import br.com.mobicare.cielo.pix.enums.PixExtractFilterEnum.RETURNS
import br.com.mobicare.cielo.pix.enums.PixExtractFilterEnum.TRANSFER
import br.com.mobicare.cielo.pix.enums.PixExtractPeriodEnum.FIFTEEN_DAYS
import br.com.mobicare.cielo.pix.enums.PixExtractPeriodEnum.SEVEN_DAYS
import br.com.mobicare.cielo.pix.enums.PixExtractPeriodEnum.THIRTY_DAYS
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.Calendar

class PixExtractFilterBottomSheetFragment : BottomSheetDialogFragment(),
    CieloNavigationListener, PixExtractFilterBottomSheetContract.View {

    private var _binding: FragmentPixExtractFilterBinding? = null
    private val binding get() = _binding
    private var navigation: CieloNavigation? = null
    private var listener: OnResultListener? = null

    private var chosenCashFlowType: String? = ALL_RELEASES.name
    private var chosenTransferType: String? = ALL_TRANSACTIONS.name
    private var chosenFilterPeriod: String? = RECENTS.name

    private val presenter: PixExtractFilterBottomSheetPresenter by inject {
        parametersOf(this)
    }

    private val sdf = SimpleDateFormat(FORMAT_DATE_AMERICAN)
    private val sdfBR = SimpleDateFormat(FORMAT_DATE_PORTUGUESE)

    private var startDate: String? = null
    private var endDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupBottomSheet(
            dialog = dialog,
            action = { dismiss() },
            isFullScreen = true
        )
        _binding = FragmentPixExtractFilterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogResize
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configViews()
        configureListeners()
        loadingSavedsFilters()
    }

    override fun onResume() {
        super.onResume()
        setupNavigation()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_pix_extract_filter_title))
            navigation?.showContainerButton()
            navigation?.showContent()
            navigation?.setNavigationListener(this)
        }
    }

    private fun configViews() {
        binding?.apply {
            tvTransactionsFiltered.text = getTitle()
            btnClearFilter.isClickable = false
            btnClearFilter.isEnabled = false
            btnClearFilter.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray_light
                )
            )
        }

        configureListeners()
    }

    private fun configureFilters(
        chosenTransferTypeFilter: String?,
        chosenCashFlowTypeFilter: String?,
        chosenPeriodFilter: String?,
        startDate: String?,
        endDate: String?
    ): FilterExtract {
        var qtdFilters = ZERO

        if (chosenTransferTypeFilter != ALL_TRANSACTIONS.name
            && chosenTransferTypeFilter.isNullOrBlank().not()
        )
            qtdFilters = ONE

        if (chosenCashFlowTypeFilter != ALL_RELEASES.name
            && chosenCashFlowTypeFilter.isNullOrBlank().not()
            || chosenPeriodFilter != RECENTS.name && chosenPeriodFilter.isNullOrBlank().not()
        )
            qtdFilters += ONE

        return FilterExtract.Builder()
            .initialDate(startDate)
            .finalDate(endDate)
            .transferType(chosenTransferTypeFilter)
            .cashFlowType(chosenCashFlowTypeFilter)
            .period(chosenPeriodFilter)
            .qtdFilters(qtdFilters)
            .build()
    }

    private fun configButton(color: Int, isEnable: Boolean) {
        binding?.apply {
            btnClearFilter.isEnabled = isEnable
            btnClearFilter.isClickable = isEnable
            btnClearFilter.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    color
                )
            )
        }
    }

    private fun showButtonClearFilter(isDefaultFilter: Boolean) {
        val color = if (isDefaultFilter) R.color.gray_light else R.color.brand_400
        configButton(color, isDefaultFilter.not())
    }

    private fun showCalendarPeriodFilter(chooseOptionPeriod: String?) {
        binding?.includePeriodsFilter?.apply {
            val isShow = chooseOptionPeriod == OTHERS_PERIODS.name
            containerDate.visible(isShow)
            forceScrollDown(isShow)
        }
    }

    private fun forceScrollDown(isForce: Boolean) {
        if (isForce)
            binding?.apply {
                scrollViewFilter.post {
                    scrollViewFilter.fullScroll(View.FOCUS_DOWN)
                }
            }
    }

    private fun filterExtractSaved(): FilterExtract? =
        (arguments?.getSerializable(ARG_PARAM_EXTRACT_FILTER) as FilterExtract?)

    private fun getTitle(): String =
        (arguments?.getString(ARG_PARAM_EXTRACT_FILTER)
            ?: getString(R.string.text_pix_extract_filter_title))

    private fun checkChosenCashFlowType(choseRadioButton: RadioButton) {
        when (choseRadioButton.id) {
            R.id.rbAllEntries -> chosenCashFlowType = ALL_RELEASES.name
            R.id.rbEntries -> chosenCashFlowType = CREDIT.name
            R.id.rbExits -> chosenCashFlowType = DEBIT.name
        }

        showButtonClearFilter(chosenCashFlowType == ALL_RELEASES.name)
    }

    private fun checkChoseTransferType(choseRadioButton: RadioButton) {
        when (choseRadioButton.id) {
            R.id.rbAllTransactions -> chosenTransferType = ALL_TRANSACTIONS.name
            R.id.rbTransfers -> chosenTransferType = TRANSFER.name
            R.id.rbQrCode -> chosenTransferType = QRCODE.name
            R.id.rbReturns -> chosenTransferType = RETURNS.name

        }

        showButtonClearFilter(chosenTransferType == ALL_TRANSACTIONS.name)
    }

    private fun checkChoseFilterPeriod(choseRadioButton: RadioButton) {

        when (choseRadioButton.id) {
            R.id.rbRecents -> chosenFilterPeriod = RECENTS.name
            R.id.rbLast7days -> chosenFilterPeriod = SEVEN_DAYS.name
            R.id.rbLast15Days -> chosenFilterPeriod = FIFTEEN_DAYS.name
            R.id.rbLast30Days -> chosenFilterPeriod = THIRTY_DAYS.name
            R.id.rbOthersPeriods -> chosenFilterPeriod = OTHERS_PERIODS.name
        }

        showButtonClearFilter(chosenFilterPeriod == RECENTS.name)
        showCalendarPeriodFilter(chosenFilterPeriod)
    }

    private fun applyFilter() {
        if (binding?.includePeriodsFilter?.containerDate?.visibility == View.VISIBLE)
            checkFieldsDate()
        else
            this.presenter.applyFilter(
                configureFilters(
                    chosenTransferType,
                    chosenCashFlowType,
                    chosenFilterPeriod,
                    startDate ?: filterExtractSaved()?.startDate,
                    endDate ?: filterExtractSaved()?.endDate
                )
            )
    }

    private fun configureListeners() {
        binding?.apply {
            includeReleasesFilter.radioGroupTypeEntries.setOnCheckedChangeListener { group, checkedId ->
                checkChosenCashFlowType(group.findViewById<AppCompatRadioButton>(checkedId))
            }

            includeTransactionsFilter.radioGroupTransactionType.setOnCheckedChangeListener { group, checkedId ->
                checkChoseTransferType(group.findViewById<AppCompatRadioButton>(checkedId))
            }

            includePeriodsFilter.radioGroupPeriodType.setOnCheckedChangeListener { group, checkedId ->
                checkChoseFilterPeriod(group.findViewById<AppCompatRadioButton>(checkedId))

            }

            includePeriodsFilter.btPeriodInitial.setOnClickListener {
                presenter.onStartPeriodoClicked()
            }
            includePeriodsFilter.btPeriodEnd.setOnClickListener {
                presenter.onEndPeriodClicked()
            }
            btnClearFilter.setOnClickListener {
                resetFilterInputs()
            }
            btnApplyFilter.setOnClickListener {
                applyFilter()
            }
        }

    }

    interface OnDismissListener {
        fun onDismiss()
    }

    var onDismissListener: OnDismissListener? = null

    override fun applyFilter(filter: FilterExtract?) {
        this.listener?.onResult(filter)
        onDismissListener?.onDismiss()
        this.dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss()
    }

    override fun showCalendar(
        cal: Calendar,
        idTitleRes: Int,
        type: Int,
        startDate: Calendar,
        endDate: Calendar
    ) {
        CalendarDialogCustom(
            null,
            null,
            ZERO,
            CalendarCustom.getDay(cal),
            CalendarCustom.getMonth(cal),
            CalendarCustom.getYear(cal),
            getString(idTitleRes),
            context as Context,
            { _, year, monthOfYear, dayOfMonth ->
                val dataFilter = DataCustom(year, monthOfYear, dayOfMonth)
                this.presenter.onChangePeriod(type, dataFilter)
            },
            R.style.DialogThemeMinhasVendas,
            endDate,
            startDate
        ).show()
    }

    override fun fillStartPeriod(data: DataCustom?) {
        binding?.includePeriodsFilter?.apply {
            data?.let { itData ->
                startDate = sdf.format(itData.toDate())

                btPeriodInitial.text = getString(
                    R.string.text_pix_extract_filter_date_start_complete,
                    sdfBR.format(itData.toDate())
                )
                containerStartDate.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)
                txtMsgEmptyStartDate.gone()

            }
        }
    }

    override fun fillEndPeriod(data: DataCustom?) {
        binding?.includePeriodsFilter?.apply {
            data?.let { itData ->
                btPeriodEnd.text = getString(
                    R.string.text_pix_extract_filter_date_end_complete,
                    sdfBR.format(itData.toDate())
                )
                endDate = sdf.format(itData.toDate())
                containerEndDate.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)
                txtMsgEmptyEndDate.gone()

            } ?: run {
                btPeriodEnd.text = EMPTY
            }
        }
    }

    private fun loadingSavedsFilters() {
        binding?.apply {
            when (filterExtractSaved()?.cashFlowType) {
                ALL_RELEASES.name -> includeReleasesFilter.rbAllEntries.isChecked = true
                CREDIT.name -> includeReleasesFilter.rbEntries.isChecked = true
                DEBIT.name -> includeReleasesFilter.rbExits.isChecked = true

            }

            when (filterExtractSaved()?.transferType) {
                ALL_TRANSACTIONS.name -> binding?.includeTransactionsFilter?.rbAllTransactions?.isChecked =
                    true

                TRANSFER.name -> includeTransactionsFilter.rbTransfers.isChecked = true
                QRCODE.name -> includeTransactionsFilter.rbQrCode.isChecked = true
                RETURNS.name -> includeTransactionsFilter.rbReturns.isChecked = true
            }

            when (filterExtractSaved()?.period) {
                RECENTS.name -> includePeriodsFilter.rbRecents.isChecked = true
                SEVEN_DAYS.name -> includePeriodsFilter.rbLast7days.isChecked = true
                FIFTEEN_DAYS.name -> includePeriodsFilter.rbLast15Days.isChecked = true
                THIRTY_DAYS.name -> includePeriodsFilter.rbLast30Days.isChecked = true
                OTHERS_PERIODS.name -> includePeriodsFilter.rbOthersPeriods.isChecked =
                    true
            }


            if (filterExtractSaved()?.startDate != null)
                includePeriodsFilter.btPeriodInitial.text = getString(
                    R.string.text_pix_extract_filter_date_start_complete,
                    filterExtractSaved()?.startDate?.dateFormatToBr()
                )

            if (filterExtractSaved()?.endDate != null)
                includePeriodsFilter.btPeriodEnd.text = getString(
                    R.string.text_pix_extract_filter_date_end_complete,
                    filterExtractSaved()?.endDate?.dateFormatToBr()
                )
        }
    }

    private fun resetFilterInputs() {
        binding?.apply {
            includeReleasesFilter.rbAllEntries.isChecked = true
            includeTransactionsFilter.rbAllTransactions.isChecked = true
            includePeriodsFilter.rbRecents.isChecked = true
            resetDate()

            if (arguments?.getSerializable(ARG_PARAM_EXTRACT_FILTER) != null)
                arguments?.remove(ARG_PARAM_EXTRACT_FILTER)
        }

        Toast(requireContext()).showCustomToast(
            message = getString(R.string.text_pix_extract_filter_toast_msg_clear),
            activity = requireActivity(),
            trailingIcon = R.drawable.ic_check_toast
        )
    }

    private fun resetDate() {
        startDate = null
        endDate = null

        binding?.includePeriodsFilter?.apply {
            btPeriodInitial.text = EMPTY
            btPeriodEnd.text = EMPTY

            btPeriodInitial.hint = getString(R.string.text_pix_extract_filter_date_start)
            btPeriodEnd.hint = getString(R.string.text_pix_extract_filter_date_end)

            containerStartDate.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)
            containerEndDate.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)

            txtMsgEmptyStartDate.gone()
            txtMsgEmptyEndDate.gone()
        }
    }

    private fun checkFieldsDate() {
        binding?.includePeriodsFilter?.apply {
            val initial =
                startDate?.parseToLocalDate() ?: filterExtractSaved()?.startDate?.parseToLocalDate()
            val final =
                endDate?.parseToLocalDate() ?: filterExtractSaved()?.endDate?.parseToLocalDate()

            val period = ChronoUnit.DAYS.between(initial, final)
            val isValid = period in ZERO..NINETY

            if (isValid) {
                checkFieldsDateAfterRanger()
            } else {
                moreThanNinetyDays()
            }
        }
    }

    private fun moreThanNinetyDays() {
        binding?.includePeriodsFilter?.apply {
            containerStartDate.setBackgroundResource(R.drawable.shape_solid_white_border_color_red)
            containerEndDate.setBackgroundResource(R.drawable.shape_solid_white_border_color_red)
            txtMsgEmptyEndDate.apply {
                val isDateEmpty =
                    btPeriodInitial.text.isNullOrBlank() || btPeriodEnd.text.isNullOrBlank()
                visible()
                text = getString(
                    if (isDateEmpty) {
                        R.string.text_pix_extract_filter_choose_other_period
                    } else {
                        R.string.txt_pix_filter_date_invalid
                    }
                )
            }
        }
    }

    private fun checkFieldsDateAfterRanger() {
        binding?.includePeriodsFilter?.apply {
            val isInvalid = btPeriodInitial.text.isNullOrBlank() || btPeriodEnd.text.isNullOrBlank()
            if (isInvalid) {
                txtMsgEmptyEndDate.apply {
                    gone()
                    text = getString(R.string.text_pix_extract_filter_choose_other_period)
                }

                if (btPeriodInitial.text.isNullOrBlank()) {
                    containerStartDate.setBackgroundResource(R.drawable.shape_solid_white_border_color_red)
                    txtMsgEmptyStartDate.visible()
                }

                containerStartDate.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus && btPeriodInitial.text.isNullOrBlank().not()) {
                        containerStartDate.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)
                        txtMsgEmptyStartDate.gone()
                    }
                }

                if (btPeriodEnd.text.isNullOrBlank()) {
                    containerEndDate.setBackgroundResource(R.drawable.shape_solid_white_border_color_red)
                    txtMsgEmptyEndDate.visible()
                }

                containerEndDate.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus && btPeriodEnd.text.isNullOrBlank().not()) {
                        containerEndDate.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)
                        txtMsgEmptyEndDate.gone()
                    }
                }
            } else {
                presenter.applyFilter(
                    configureFilters(
                        chosenTransferType,
                        chosenCashFlowType,
                        chosenFilterPeriod,
                        startDate ?: filterExtractSaved()?.startDate,
                        endDate ?: filterExtractSaved()?.endDate
                    )
                )
            }
        }
    }

    interface OnResultListener {
        fun onResult(myFilter: FilterExtract?)
    }

    companion object {
        private const val ARG_PARAM_EXTRACT_FILTER = "ARG_PARAM_EXTRACT_FILTER"
        private const val ARG_PARAM_EXTRACT_FILTER_TITLE = "ARG_PARAM_EXTRACT_FILTER_TITLE"
        fun create(
            filter: FilterExtract?,
            title: String,
            listener: OnResultListener,
        ) = PixExtractFilterBottomSheetFragment().apply {
            this.listener = listener
            this.arguments = Bundle().apply {
                this.putSerializable(ARG_PARAM_EXTRACT_FILTER, filter)
                this.putString(ARG_PARAM_EXTRACT_FILTER_TITLE, title)
            }
        }
    }

}