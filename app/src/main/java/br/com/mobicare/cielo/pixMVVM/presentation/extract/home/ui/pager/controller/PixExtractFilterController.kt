package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.pager.controller

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.CieloListBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.ItemPixExtractFilterRadioButtonBinding
import br.com.mobicare.cielo.databinding.LayoutPixExtractFilterBottomSheetPeriodBinding
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.AccountEntriesFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PeriodFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.StatusFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.TransactionFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.listener.PixExtractFilterListener
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.pager.viewModel.PixExtractPageViewModel

class PixExtractFilterController(
    private val viewModel: PixExtractPageViewModel,
    private val context: Context,
    private val supportFragmentManager: FragmentManager
) : PixExtractFilterListener {

    private var filterPeriodValue: PeriodFilterTypeEnum? = null

    override fun onClickClearFilter() {
        viewModel.clearFilter()
    }

    override fun onClickFilterTransactions() {
        showBottomSheetFilter(
            R.string.pix_extract_filter_item_transactions_title_bottom_sheet,
            TransactionFilterTypeEnum.values().toList(),
            viewModel.filterData.value?.transactionType
        )
    }

    override fun onClickFilterAccountEntries() {
        showBottomSheetFilter(
            R.string.pix_extract_filter_item_accounting_entries_title_bottom_sheet,
            AccountEntriesFilterTypeEnum.values().toList(),
            viewModel.filterData.value?.accountEntriesType
        )
    }

    override fun onClickFilterPeriod() {
        filterPeriodValue = null
        showBottomSheetFilterPeriod()
    }

    override fun onClickFilterStatus() {
        showBottomSheetFilter(
            R.string.pix_extract_filter_item_accounting_entries_title_bottom_sheet,
            StatusFilterTypeEnum.values().toList(),
            viewModel.filterData.value?.statusType
        )
    }

    private fun showBottomSheetFilterPeriod() {
        CieloContentBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = context.getString(R.string.pix_extract_filter_item_period_title_bottom_sheet)
            ),
            contentLayoutRes = R.layout.layout_pix_extract_filter_bottom_sheet_period,
            onContentViewCreated = ::onContentViewCreatedBottomSheetFilterPeriod,
            mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                title = context.getString(R.string.pix_extract_filter_label_button_bs),
                onTap = ::onTapApplyFilterPeriod
            )
        ).show(supportFragmentManager, EMPTY)
    }

    private fun onContentViewCreatedBottomSheetFilterPeriod(
        view: View,
        bottomSheet: CieloBottomSheet
    ) {
        LayoutPixExtractFilterBottomSheetPeriodBinding.bind(view).apply {
            tvSevenDays.isSelected =
                viewModel.filterData.value?.periodType == PeriodFilterTypeEnum.SEVEN_DAYS
            tvSevenDays.setOnClickListener {
                onClickButtonFilterPeriod(
                    PeriodFilterTypeEnum.SEVEN_DAYS,
                    this
                )
            }

            tvFifteenDays.isSelected =
                viewModel.filterData.value?.periodType == PeriodFilterTypeEnum.FIFTEEN_DAYS
            tvFifteenDays.setOnClickListener {
                onClickButtonFilterPeriod(
                    PeriodFilterTypeEnum.FIFTEEN_DAYS,
                    this
                )
            }

            tvThirtyDays.isSelected =
                viewModel.filterData.value?.periodType == PeriodFilterTypeEnum.THIRTY_DAYS
            tvThirtyDays.setOnClickListener {
                onClickButtonFilterPeriod(
                    PeriodFilterTypeEnum.THIRTY_DAYS,
                    this
                )
            }

        }
    }

    private fun onClickButtonFilterPeriod(
        value: PeriodFilterTypeEnum,
        binding: LayoutPixExtractFilterBottomSheetPeriodBinding
    ) {
        filterPeriodValue = value

        binding.apply {
            tvSevenDays.isSelected = value == PeriodFilterTypeEnum.SEVEN_DAYS
            tvFifteenDays.isSelected = value == PeriodFilterTypeEnum.FIFTEEN_DAYS
            tvThirtyDays.isSelected = value == PeriodFilterTypeEnum.THIRTY_DAYS
        }
    }

    private fun onTapApplyFilterPeriod(bottomSheet: CieloBottomSheet) {
        filterPeriodValue?.let {
            if (viewModel.filterData.value?.periodType != it) viewModel.setFilter(it)
        }

        bottomSheet.dismiss()
    }

    private fun <T> showBottomSheetFilter(
        @StringRes title: Int,
        data: List<T>,
        initialSelectedItem: T?
    ) {
        var itemSelected: T? = null

        CieloListBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = context.getString(title)
            ),
            layoutItemRes = R.layout.item_pix_extract_filter_radio_button,
            data = data,
            initialSelectedItem = initialSelectedItem,
            onViewBound = ::setupOnViewBoundBottomSheetFilter,
            onItemClicked = { item, position, bottomSheet ->
                itemSelected = item
                bottomSheet.updateSelectedPosition(position)
            },
            mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                title = context.getString(R.string.pix_extract_filter_label_button_bs),
                onTap = {
                    itemSelected?.let { item -> viewModel.setFilter(item) }
                    it.dismiss()
                }
            )
        ).show(supportFragmentManager, EMPTY)
    }

    private fun <T> setupOnViewBoundBottomSheetFilter(item: T, isSelected: Boolean, view: View) {
        ItemPixExtractFilterRadioButtonBinding.bind(view).apply {
            tvLabel.text = getLabelButton(item)
            ivRadioButton.isSelected = isSelected
        }
    }

    private fun <T> getLabelButton(item: T): String {
        return when (item) {
            is TransactionFilterTypeEnum -> context.getString(item.label)
            is AccountEntriesFilterTypeEnum -> context.getString(item.label)
            is PeriodFilterTypeEnum -> context.getString(item.label)
            is StatusFilterTypeEnum -> context.getString(item.label)
            else -> EMPTY
        }
    }

}