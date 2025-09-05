package br.com.mobicare.cielo.solesp.ui.selectPeriod

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Custom
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.databinding.FragmentSolespSelectPeriodBinding
import br.com.mobicare.cielo.databinding.ItemOptionRadioDateBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.solesp.constants.SOLESP_MODEL_ARGS
import br.com.mobicare.cielo.solesp.enums.SolespSelectPeriodEnum
import br.com.mobicare.cielo.solesp.enums.SolespSelectPeriodEnum.*
import br.com.mobicare.cielo.solesp.model.SolespModel
import java.util.*

class SolespSelectPeriodFragment : BaseFragment(), CieloNavigationListener {

    private val solespModel: SolespModel? by lazy {
        arguments?.getParcelable(SOLESP_MODEL_ARGS)
    }

    private var binding: FragmentSolespSelectPeriodBinding? = null
    private var navigation: CieloNavigation? = null
    private var selectOptionPeriod: SolespSelectPeriodEnum = OTHER_PERIODS
    private var selectStartDate: DataCustomNew? = null
    private var selectEndDate: DataCustomNew? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSolespSelectPeriodBinding.inflate(
            inflater,
            container,
            false
        ).also { binding = it }.root
    }

    override fun onResume() {
        super.onResume()

        setup()
        setupNavigation()
        setupListeners()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onButtonClicked(labelButton: String) {
        findNavController().navigate(
            SolespSelectPeriodFragmentDirections.actionSolespSelectPeriodFragmentToSolespInfoSendFragment(
                SolespModel(
                    solespModel?.typeSelected,
                    selectOptionPeriod,
                    selectStartDate,
                    selectEndDate
                )
            )
        )
    }

    private fun setup() {
        resetSelectDate()
        binding?.apply {
            setDataInputDate(isStartDate = true)
            setDataInputDate(isStartDate = false)
        }
    }


    private fun showCalendar(isStartDate: Boolean) {
        var selectDateAux: DataCustomNew?
        val startDate: Calendar
        val endDate: Calendar
        val todayMinus18Months = CalendarCustom.now
        todayMinus18Months.add(Calendar.MONTH,EIGHTEEN_NEGATIVE)

        if (isStartDate) {
            selectDateAux = selectStartDate
            startDate = CalendarCustom.now
            startDate.addYears(FIVE_NEGATIVE)
            endDate = selectEndDate?.toCalendar() ?: todayMinus18Months
        } else {
            selectDateAux = selectEndDate
            startDate = selectStartDate?.toCalendar() ?: CalendarCustom.now
            if (selectStartDate == null){
                startDate.addYears(FIVE_NEGATIVE)
            }
            endDate = todayMinus18Months
        }

        val cal = selectDateAux?.toCalendar() ?: endDate
        openCalendarDialog(startDate, endDate, cal, isStartDate)
    }



    private fun openCalendarDialog(
        startDate: Calendar,
        endDate: Calendar,
        cal: Calendar,
        isStartDate: Boolean
    ) {
        CalendarDialogCustom(
            null,
            null,
            ZERO,
            CalendarCustom.getDay(cal),
            CalendarCustom.getMonth(cal),
            CalendarCustom.getYear(cal),
            getString(if (isStartDate) R.string.filter_calendar_init else R.string.filter_calendar_end),
            context as Context,
            { _, year, monthOfYear, dayOfMonth ->
                val dateFilter = DataCustomNew()
                dateFilter.setDate(year, monthOfYear, dayOfMonth)
                if (isStartDate) {
                    selectStartDate = dateFilter
                    setDataInputDate(isStartDate = true)
                } else {
                    selectEndDate = dateFilter
                    setDataInputDate(isStartDate = false)
                }
                enableButtonNext()
            },
            R.style.DialogThemeMinhasVendas,
            endDate,
            startDate
        ).show()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextButton(getString(R.string.continuar))
            navigation?.showHelpButton(false)
            navigation?.setNavigationListener(this)
            enableButtonNext()
        }
    }

    private fun setupListeners() {
        binding?.apply {
            inputStartDate.root.setOnClickListener {
                showCalendar(isStartDate = true)
            }
            inputEndDate.root.setOnClickListener {
                showCalendar(isStartDate = false)
            }
        }
    }



    private fun setDataInputDate(isStartDate: Boolean) {
        val date = if (isStartDate) selectStartDate else selectEndDate
        val value = date?.formatBRDate()
        val input = if (isStartDate) binding?.inputStartDate else binding?.inputEndDate
        val hint =
            if (isStartDate) getString(R.string.filter_calendar_init) else getString(R.string.filter_calendar_end)

        input?.let {
            it.txtValue.text = if (value.isNullOrEmpty()) hint else value
            it.txtValue.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (value.isNullOrEmpty()) R.color.color_cloud_300 else R.color.color_cloud_600
                )
            )
            it.txtHint.text = hint
            it.txtHint.visible(value.isNullOrEmpty().not())
        }
    }


    private fun enableButtonNext() {
        if ((selectOptionPeriod != null && selectOptionPeriod != OTHER_PERIODS) ||
            (selectOptionPeriod == OTHER_PERIODS && selectStartDate != null && selectEndDate != null)
        ) {
            navigation?.enableButton(true)
        } else navigation?.enableButton(false)
    }

    private fun resetSelectDate() {
        if (selectOptionPeriod != OTHER_PERIODS) {
            selectStartDate = null
            selectEndDate = null
            setDataInputDate(isStartDate = true)
            setDataInputDate(isStartDate = false)
        }
    }
}