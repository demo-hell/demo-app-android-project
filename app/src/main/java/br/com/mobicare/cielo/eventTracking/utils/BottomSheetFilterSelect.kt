package br.com.mobicare.cielo.eventTracking.utils

import androidx.fragment.app.Fragment
import br.com.cielo.libflue.bottomsheet.CieloListBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.databinding.CieloFilterItemBinding
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterChip

object BottomSheetFilterSelect {
    fun show(
        fragment: Fragment,
        cieloFilterChip: CieloFilterChip,
        bottomSheetFilterEventListener: (FilterBottomSheetEvents) -> Unit
    ) {
        var selectedPosition = cieloFilterChip.currentSelected
        var selectedControl = if (selectedPosition != ONE_NEGATIVE) {
            cieloFilterChip.filterPossibilities.getOrNull(selectedPosition)
        } else {
            cieloFilterChip.filterPossibilities.firstOrNull()
        }

        val secondaryButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
            title = CieloApplication.context.getString(R.string.clean),
            startEnabled = selectedPosition > ZERO,
            onTap = {
                if (selectedPosition > ZERO) {
                    it.dismiss()
                    bottomSheetFilterEventListener(FilterBottomSheetEvents.SecondaryButtonClick)
                }
            }
        )
        val mainButtonConfigurator =
            CieloBottomSheet.ButtonConfigurator(title = CieloApplication.context.getString(R.string.apply), onTap =  {
                it.dismiss()
                bottomSheetFilterEventListener(FilterBottomSheetEvents.MainButtonClick(if (selectedPosition == ZERO) ONE_NEGATIVE else selectedPosition))
            })

        CieloListBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = cieloFilterChip.filterBottomSheetTitle,
                onCloseTap = {
                    bottomSheetFilterEventListener(FilterBottomSheetEvents.HeaderOnCloseTap)
                }
            ),
            layoutItemRes = R.layout.cielo_filter_item,
            data = cieloFilterChip.filterPossibilities,
            onViewBound = { filterPossibility, isSelected, itemView ->
                val binding = CieloFilterItemBinding.bind(itemView)
                binding.tvItemFilterTitle.text = filterPossibility
                binding.rbSelectedItem.isChecked = isSelected
            },
            onItemClicked = { filterPossibility, position, bottomSheet ->
                selectedControl = filterPossibility
                selectedPosition = position
                bottomSheet.changeButtonStatus(true)
                bottomSheet.updateSelectedPosition(position)
                bottomSheet.removeSearchFocus()
                bottomSheet.updateSecondaryButtonConfigurator(
                    secondaryButtonConfigurator.copy(startEnabled = true, onTap = {
                        it.dismiss()
                        bottomSheetFilterEventListener(FilterBottomSheetEvents.SecondaryButtonClick)
                    })
                )
            },
            initialSelectedItem = selectedControl,
            dividerItemDecoration = dividerItemDecoration(fragment.requireContext()),
            mainButtonConfigurator = mainButtonConfigurator,
            secondaryButtonConfigurator = secondaryButtonConfigurator,
            disableExpandableMode = true
        ).show(fragment.childFragmentManager, fragment.javaClass.simpleName)
    }
}