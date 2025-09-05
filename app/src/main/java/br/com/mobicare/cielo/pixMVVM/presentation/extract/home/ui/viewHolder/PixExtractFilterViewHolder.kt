package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.viewHolder

import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.LayoutPixExtractFilterBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PixReceiptsTab
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.AccountEntriesFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PeriodFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.StatusFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.TransactionFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.listener.PixExtractFilterListener
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.model.PixExtractFilterModel

class PixExtractFilterViewHolder(
    private val binding: LayoutPixExtractFilterBinding,
    private val pageType: PixReceiptsTab,
    private val listener: PixExtractFilterListener,
) : RecyclerView.ViewHolder(binding.root) {
    private val context = binding.root.context

    fun bind(filterData: PixExtractFilterModel) {
        binding.llFilter.visible(pageType != PixReceiptsTab.NEW_SCHEDULES)
        setupTvAccountEntries(filterData)
        setupTvPeriod(filterData)
        setupTvStatus(filterData)
        setupTvTransactions(filterData)
    }

    private fun setupTvAccountEntries(filterData: PixExtractFilterModel) {
        binding.tvAccountEntries.apply {
            visible(pageType != PixReceiptsTab.SCHEDULES)
            setOnClickListener { listener.onClickFilterAccountEntries() }
            text =
                concatTextLabelButtonFilter(
                    R.string.pix_extract_filter_item_accounting_entries,
                    filterData.accountEntriesType.label,
                )
            isSelected = filterData.accountEntriesType.isSelected
        }
    }

    private fun setupTvPeriod(filterData: PixExtractFilterModel) {
        binding.tvPeriod.apply {
            visible(pageType != PixReceiptsTab.SCHEDULES)
            setOnClickListener { listener.onClickFilterPeriod() }
            text =
                concatTextLabelButtonFilter(
                    R.string.pix_extract_filter_item_period,
                    filterData.periodType.label,
                )
            isSelected = filterData.periodType.isSelected
        }
    }

    private fun setupTvStatus(filterData: PixExtractFilterModel) {
        binding.tvStatus.apply {
            visible(pageType == PixReceiptsTab.SCHEDULES)
            setOnClickListener { listener.onClickFilterStatus() }
            text =
                concatTextLabelButtonFilter(
                    R.string.pix_extract_filter_item_status,
                    filterData.statusType.label,
                )
            isSelected = filterData.statusType.isSelected
        }
    }

    private fun setupTvTransactions(filterData: PixExtractFilterModel) {
        binding.tvTransactions.apply {
            visible(pageType == PixReceiptsTab.TRANSFER)
            setOnClickListener { listener.onClickFilterTransactions() }
            text =
                concatTextLabelButtonFilter(
                    R.string.pix_extract_filter_item_transactions,
                    filterData.transactionType.label,
                )
            isSelected = filterData.transactionType.isSelected
        }
    }

    private fun concatTextLabelButtonFilter(
        @StringRes primaryText: Int,
        @StringRes secondText: Int,
    ): String =
        when (secondText) {
            TransactionFilterTypeEnum.ALL_TRANSACTIONS.label,
            AccountEntriesFilterTypeEnum.ALL_ACCOUNT_ENTRIES.label,
            PeriodFilterTypeEnum.THIRTY_DAYS.label,
            StatusFilterTypeEnum.ALL_STATUS.label,
            -> {
                context.getString(primaryText)
            }
            else -> {
                val stringPrimaryText = context.getString(primaryText)
                val stringSecondText = context.getString(secondText)

                context.getString(
                    R.string.pix_extract_filter_concat_label_button,
                    stringPrimaryText,
                    stringSecondText,
                )
            }
        }
}
