package br.com.mobicare.cielo.component.requiredDataField.presentation.builder

import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.core.view.updatePadding
import br.com.cielo.libflue.inputtext.CieloInputText
import br.com.mobicare.cielo.component.requiredDataField.presentation.factory.FormatFieldHelperFactory
import br.com.mobicare.cielo.component.requiredDataField.utils.RequiredDataFieldValueStore
import br.com.mobicare.cielo.databinding.LayoutFormGroupBinding
import br.com.mobicare.cielo.extensions.visible

class RequiredDataFieldViewBuilder(
    private val layoutInflater: LayoutInflater,
    private val fieldData: FieldData,
    private val valueStore: RequiredDataFieldValueStore,
    private val onFieldChanged: ((String, String) -> Unit)? = null,
    private val configurator: Configurator? = null,
) : CieloInputText.TextChangeListener, OnFocusChangeListener {

    private val binding = LayoutFormGroupBinding.inflate(layoutInflater)
    private val formatFieldHelper = FormatFieldHelperFactory.create(fieldData.format)

    private var isEditing = false

    init {
        valueStore.updateError(fieldData.id, true)
    }

    private val isValidValue get() = binding.etField.getText().let {
        it.isNotBlank() && formatFieldHelper.validate(it)
    }

    fun build() = binding.apply {
        with(fieldData) {
            tvLabel.text = labelText
            etField.apply {
                setHint(hintText.orEmpty())
                setInputType(formatFieldHelper.inputType)
                setOnTextChangeListener(this@RequiredDataFieldViewBuilder)
                setOnFocusChanged(this@RequiredDataFieldViewBuilder)
                setupConfigurator()
            }
        }
    }.root

    private fun setupConfigurator() {
        configurator?.let { configurator ->
            configurator.layoutSpacingTop?.let {
                binding.root.updatePadding(top = getDimensionValue(it))
            }
            configurator.labelSpacingBottom?.let {
                binding.tvLabel.updatePadding(bottom = getDimensionValue(it))
            }
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus.not()) {
            isValidValue.let { valid ->
                valueStore.updateError(fieldData.id, valid.not())
                binding.apply {
                    etField.showError(valid.not())
                    tvError.text = if (valid.not()) getString(formatFieldHelper.errorMessage) else null
                    tvError.visible(valid.not())
                }
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (isEditing.not()) {
            isEditing = true
            val text = s?.toString().orEmpty()
            updateField(formatFieldHelper.getMaskedText(text))
            isEditing = false
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable?) {}

    private fun updateField(text: String) {
        binding.apply {
            etField.setText(text)
            etField.setSelection(text.length)
        }
        valueStore.run {
            update(fieldData.id, formatFieldHelper.getCleanedText(text))
            updateError(fieldData.id, isValidValue.not())
        }
        onFieldChanged?.invoke(fieldData.id, text)
    }

    private fun getString(@StringRes resId: Int) =
        layoutInflater.context.getString(resId)

    private fun getDimensionValue(@DimenRes dimenId: Int) =
        layoutInflater.context.resources.getDimensionPixelOffset(dimenId)

    data class FieldData(
        val id: String,
        val labelText: String,
        val hintText: String? = null,
        val format: String? = null
    ) {
        companion object {
            const val FORMAT_PHONE = "PHONE"
            const val FORMAT_DATE = "DATE"
            const val FORMAT_DOCUMENT = "DOCUMENT"
            const val FORMAT_EMAIL = "EMAIL"
            const val FORMAT_NUMBER = "NUMBER"
            const val FORMAT_STATE = "STATE"
            const val FORMAT_ZIP_CODE = "ZIP_CODE"
        }
    }

    data class Configurator(
        @DimenRes val layoutSpacingTop: Int? = null,
        @DimenRes val labelSpacingBottom: Int? = null
    )

}