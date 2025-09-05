package br.com.mobicare.cielo.component.requiredDataField.presentation.builder

import android.view.LayoutInflater
import android.view.View
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.component.requiredDataField.utils.RequiredDataFieldValueStore
import br.com.mobicare.cielo.databinding.LayoutSectionBinding

class RequiredDataFieldSectionViewBuilder(
    private val layoutInflater: LayoutInflater,
    private val valueStore: RequiredDataFieldValueStore,
    private val onFieldChanged: ((String, String) -> Unit)? = null
) : RequiredDataFieldBaseViewBuilder {

    val binding = LayoutSectionBinding.inflate(layoutInflater)

    override fun build(): View {
        return binding.root
    }

    override fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    override fun setFieldDataList(list: List<RequiredDataFieldViewBuilder.FieldData>) {
        list.forEach { item ->
            binding.sectionContent.addView(
                RequiredDataFieldViewBuilder(
                    layoutInflater = layoutInflater,
                    fieldData = item,
                    valueStore = valueStore,
                    onFieldChanged = onFieldChanged,
                    configurator = RequiredDataFieldViewBuilder.Configurator(
                        layoutSpacingTop = R.dimen.dimen_16dp,
                        labelSpacingBottom = R.dimen.dimen_8dp
                    )
                ).build()
            )
        }
    }

}