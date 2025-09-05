package br.com.mobicare.cielo.component.requiredDataField.presentation.helper

import android.text.InputType
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.EditTextHelper
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.commons.validators.CepValidator

class ZipCodeFormatFieldHelper : FormatFieldHelper() {

    override val inputType: Int
        get() = InputType.TYPE_CLASS_NUMBER

    override val errorMessage: Int
        @StringRes get() = R.string.required_data_field_validation_error_field_zip_code

    override fun getCleanedText(value: String) = Utils.unmask(value.trim())

    override fun getMaskedText(value: String) = EditTextHelper
        .zipCodeMaskFormatter(value)
        .formattedText
        .string

    override fun validate(value: String) = CepValidator(value).invoke()

}