package br.com.mobicare.cielo.component.requiredDataField.presentation.builder

import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import br.com.mobicare.cielo.component.requiredDataField.utils.RequiredDataFieldValueStore
import br.com.mobicare.cielo.databinding.LayoutAccordionBinding
import br.com.mobicare.cielo.extensions.collapse
import br.com.mobicare.cielo.extensions.expand
import br.com.mobicare.cielo.extensions.toRotationDown
import br.com.mobicare.cielo.extensions.toRotationUp

class RequiredDataFieldAccordionViewBuilder(
    private val layoutInflater: LayoutInflater,
    private val valueStore: RequiredDataFieldValueStore,
    private val onFieldChanged: ((String, String) -> Unit)? = null
) : RequiredDataFieldBaseViewBuilder {

    val binding = LayoutAccordionBinding.inflate(layoutInflater)

    override fun build(): View {
        setupAccordionHeaderListener()

        return binding.root
    }

    override fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    override fun setFieldDataList(list: List<RequiredDataFieldViewBuilder.FieldData>) {
        list.forEach {
            binding.accordionContent.addView(
                RequiredDataFieldViewBuilder(
                    layoutInflater, it, valueStore, onFieldChanged).build()
            )
        }
    }

    private fun setupAccordionHeaderListener() {
        binding.accordionHeader.setOnClickListener(::onAccordionHeaderClick)
    }

    private fun onAccordionHeaderClick(v: View) {
        binding.apply {
            if (accordionContent.isVisible) {
                accordionContent.collapse()
                ivArrow.toRotationUp()
            } else {
                accordionContent.expand()
                ivArrow.toRotationDown()
            }
        }
    }

}