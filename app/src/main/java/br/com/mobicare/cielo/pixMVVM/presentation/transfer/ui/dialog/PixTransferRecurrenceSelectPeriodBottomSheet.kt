package br.com.mobicare.cielo.pixMVVM.presentation.transfer.ui.dialog

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentManager
import br.com.cielo.libflue.bottomsheet.CieloListBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.LayoutPixTransferRecurrenceItemSelectPeriodBinding
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.enums.PixPeriodRecurrence

class PixTransferRecurrenceSelectPeriodBottomSheet(
    private val context: Context,
    private val onSelect: (PixPeriodRecurrence) -> Unit,
) {
    fun show(
        periodSelected: PixPeriodRecurrence?,
        fragmentManager: FragmentManager,
        tag: String,
    ) {
        CieloListBottomSheet.create(
            headerConfigurator =
                CieloBottomSheet.HeaderConfigurator(
                    title = getString(R.string.pix_transfer_recurrence_bs_select_period_title),
                ),
            data = PixPeriodRecurrence.values().toList(),
            layoutItemRes = R.layout.layout_pix_transfer_recurrence_item_select_period,
            initialSelectedItem = periodSelected,
            onViewBound = ::onViewBound,
            onItemClicked = ::onItemClicked,
        ).show(fragmentManager, tag)
    }

    private fun onItemClicked(
        item: PixPeriodRecurrence,
        index: Int,
        bottomSheet: CieloListBottomSheet<PixPeriodRecurrence>,
    ) {
        onSelect(item)
        bottomSheet.also {
            it.updateSelectedPosition(index)
            it.dismiss()
        }
    }

    private fun onViewBound(
        item: PixPeriodRecurrence,
        isChecked: Boolean,
        view: View,
    ) {
        val binding = LayoutPixTransferRecurrenceItemSelectPeriodBinding.bind(view)

        binding.rbOption.apply {
            this.isChecked = isChecked
            setTextValue(getString(item.label))
        }
    }

    private fun getString(resId: Int) = context.getString(resId)
}
