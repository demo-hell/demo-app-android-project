package br.com.mobicare.cielo.component.requiredDataField.presentation.helper

import android.text.InputType
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ELEVEN
import br.com.mobicare.cielo.commons.helpers.EditTextHelper
import br.com.mobicare.cielo.commons.utils.*

class DocumentFormatFieldHelper : FormatFieldHelper() {

    override val inputType: Int
        get() = InputType.TYPE_CLASS_NUMBER

    override val errorMessage: Int
        @StringRes get() = R.string.required_data_field_validation_error_field_document

    override fun getCleanedText(value: String) = Utils.unmask(value.trim())

    override fun getMaskedText(value: String) = getCleanedText(value).let { cleanString ->
        if (cleanString.length <= ELEVEN) {
            EditTextHelper.cpfMaskFormatter(cleanString).formattedText.string
        } else {
            EditTextHelper.cnpjMaskFormatter(cleanString).formattedText.string
        }
    }

    override fun validate(value: String) =
        if (getCleanedText(value).length == ELEVEN) {
            ValidationUtils.isCPF(value)
        } else {
            ValidationUtils.isCNPJ(value)
        }

}