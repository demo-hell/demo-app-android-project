package br.com.mobicare.cielo.pixMVVM.presentation.transfer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.datePicker.CieloDatePicker
import br.com.cielo.libflue.datePicker.CieloDateValidator
import br.com.cielo.libflue.datePicker.CieloDateValidatorDayOfWeek
import br.com.cielo.libflue.field.CieloSelectField
import br.com.cielo.libflue.util.dateUtils.getDayOfMonth
import br.com.cielo.libflue.util.dateUtils.getDayOfWeek
import br.com.cielo.libflue.util.dateUtils.plusMonths
import br.com.cielo.libflue.util.dateUtils.plusWeeks
import br.com.cielo.libflue.util.dateUtils.plusYears
import br.com.cielo.libflue.util.dateUtils.toString
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import br.com.mobicare.cielo.databinding.FragmentPixTransferRecurrenceBinding
import br.com.mobicare.cielo.databinding.LayoutPixFooterRoundedButtonBinding
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.enums.PixPeriodRecurrence
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.models.PixRecurrenceData
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.ui.dialog.PixTransferRecurrenceSelectPeriodBottomSheet
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.viewmodel.PixTransferViewModel
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.Calendar

class PixTransferRecurrenceFragment : BaseFragment() {
    private val viewModel: PixTransferViewModel by sharedViewModel()

    private var binding: FragmentPixTransferRecurrenceBinding? = null
    private var bindingFooter: LayoutPixFooterRoundedButtonBinding? = null
    private var navigation: CieloNavigation? = null

    private var recurrenceData = PixRecurrenceData()

    private val isValidData: Boolean get() = recurrenceData.period != null

    private val startDatePickerEndDate
        get(): Calendar {
            return when (recurrenceData.period) {
                PixPeriodRecurrence.WEEKLY -> recurrenceData.startDate.plusWeeks(ONE)
//            PixPeriodRecurrence.BIWEEKLY -> recurrenceData.startDate.plusWeeks(TWO)
                else -> recurrenceData.startDate.plusMonths(ONE)
            }
        }

    private val validatorDatePicker
        get(): CieloDateValidator {
            return when (recurrenceData.period) {
                PixPeriodRecurrence.WEEKLY ->
                    CieloDateValidator.dayOfWeekValidator(
                        CieloDateValidatorDayOfWeek(recurrenceData.startDate.getDayOfWeek(), ONE),
                    )
                /*PixPeriodRecurrence.BIWEEKLY ->
                    CieloDateValidator.dayOfWeekValidator(
                        CieloDateValidatorDayOfWeek(recurrenceData.startDate.getDayOfWeek(), ONE),
                    )*/
                else -> CieloDateValidator.daysOfMonthValidator(listOf(recurrenceData.startDate.getDayOfMonth()))
            }
        }

    private val selectPeriodBottomSheet by lazy {
        PixTransferRecurrenceSelectPeriodBottomSheet(requireContext()) { periodSelected ->
            onSelectPeriod(periodSelected)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentPixTransferRecurrenceBinding
        .inflate(inflater, container, false)
        .also {
            bindingFooter = LayoutPixFooterRoundedButtonBinding.inflate(inflater, container, false)
            binding = it
        }.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setRecurrenceData()
        setupNavigation()
        setupView()
        setupListeners()
    }

    override fun onDestroyView() {
        bindingFooter = null
        binding = null
        super.onDestroyView()
    }

    private fun setRecurrenceData() {
        recurrenceData = viewModel.store.recurrenceData.copy()
    }

    private fun setupNavigation() {
        navigation =
            (requireActivity() as? CieloNavigation)?.also {
                it.configureCollapsingToolbar(
                    CieloCollapsingToolbarLayout.Configurator(
                        toolbar =
                            CieloCollapsingToolbarLayout.Toolbar(
                                title = getString(R.string.pix_transfer_recurrence_title_toolbar),
                                menu =
                                    CieloCollapsingToolbarLayout.ToolbarMenu(
                                        menuRes = R.menu.menu_help,
                                        onOptionsItemSelected = ::onMenuOptionSelected,
                                    ),
                            ),
                        footerView = bindingFooter?.root,
                    ),
                )
            }
    }

    private fun setupView() {
        binding?.apply {
            recurrenceData.also {
                setSelectedOption(csfSelectStartDate, it.startDate.toString(SIMPLE_DT_FORMAT_MASK))
                setSelectedOption(csfSelectPeriod, it.period?.label?.let { label -> getString(label) })
                setSelectedOption(csfSelectEndDate, it.endDate?.toString(SIMPLE_DT_FORMAT_MASK))
            }
        }
    }

    private fun setupListeners() {
        bindingFooter?.button?.setOnClickListener(::onButtonNextClicked)

        binding?.apply {
            csfSelectStartDate.setOnSelectListener { _, _ ->
                CieloDatePicker.show(
                    title = R.string.pix_transfer_recurrence_date_picker_start_date_title,
                    selectedDate = recurrenceData.startDate,
                    fragmentManager = childFragmentManager,
                    tag = this@PixTransferRecurrenceFragment.javaClass.simpleName,
                    onDateSelected = ::onDateSelectedStartDate,
                )
            }

            csfSelectPeriod.setOnSelectListener { _, _ ->
                selectPeriodBottomSheet.show(
                    recurrenceData.period,
                    childFragmentManager,
                    this@PixTransferRecurrenceFragment.javaClass.simpleName,
                )
            }

            csfSelectEndDate.setOnSelectListener { _, _ ->
                if (validateForm()) {
                    CieloDatePicker.show(
                        title = R.string.pix_transfer_recurrence_date_picker_end_date_title,
                        selectedDate = recurrenceData.endDate ?: startDatePickerEndDate,
                        startDate = startDatePickerEndDate,
                        endDate = recurrenceData.startDate.plusYears(TWO),
                        validator = validatorDatePicker,
                        fragmentManager = childFragmentManager,
                        tag = this@PixTransferRecurrenceFragment.javaClass.simpleName,
                        onDateSelected = ::onDateSelectedEndDate,
                    )
                }
            }
        }
    }

    private fun onDateSelectedStartDate(date: Calendar) {
        recurrenceData.also {
            it.startDate = date
            it.endDate = null
        }
        binding?.apply {
            setSelectedOption(csfSelectStartDate, date.toString(SIMPLE_DT_FORMAT_MASK))
            csfSelectEndDate.setSelectedOption(null)
        }
    }

    private fun onDateSelectedEndDate(date: Calendar) {
        recurrenceData.endDate = date
        binding?.csfSelectEndDate?.let { setSelectedOption(it, date.toString(SIMPLE_DT_FORMAT_MASK)) }
    }

    private fun setSelectedOption(
        select: CieloSelectField,
        value: String?,
    ) {
        select.setSelectedOption(
            value?.let {
                CieloSelectField.Option(
                    id = null,
                    value = it,
                )
            },
        )
    }

    private fun onSelectPeriod(periodSelected: PixPeriodRecurrence) {
        recurrenceData.also {
            it.period = periodSelected
            it.endDate = null
        }
        binding?.apply {
            setSelectedOption(csfSelectPeriod, getString(periodSelected.label))
            validateForm()
            setSelectedOption(csfSelectEndDate, null)
        }
    }

    private fun validateForm(): Boolean {
        validateSelectPeriod(isValidData)
        return isValidData
    }

    private fun validateSelectPeriod(isValid: Boolean) {
        binding?.csfSelectPeriod?.apply {
            if (isValid) {
                unsetError()
                helperText = null
            } else {
                setError()
                helperText = getString(R.string.pix_transfer_recurrence_hint_select_period)
            }
        }
    }

    private fun onButtonNextClicked(view: View) {
        if (validateForm()) {
            viewModel.also {
                it.saveRecurrenceData(recurrenceData)
                it.selectPixRecurrence(true)
            }
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun onMenuOptionSelected(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.menuActionHelp) {
            requireActivity().openFaq(
                tag = ConfigurationDef.TAG_HELP_CENTER_PIX,
                subCategoryName = getString(R.string.cielo_facilita_central_de_ajuda_pix),
            )
        }
    }
}
