package br.com.mobicare.cielo.component.requiredDataField.presentation.helper

import android.text.InputType
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.FormHelper
import br.com.mobicare.cielo.commons.utils.*

class NumberFormatFieldHelper : FormatFieldHelper() {

    override val inputType: Int
        get() = InputType.TYPE_CLASS_NUMBER

    override val errorMessage: Int
        @StringRes get() = R.string.required_data_field_validation_error_field_number

    override fun getMaskedText(value: String) = FormHelper
        .maskFormatter(value, NUMBER_MASK_FORMAT)
        .formattedText
        .string

    override fun validate(value: String) = value.trim().isNumeric()

}