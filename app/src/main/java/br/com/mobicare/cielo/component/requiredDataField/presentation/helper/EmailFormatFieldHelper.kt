package br.com.mobicare.cielo.component.requiredDataField.presentation.helper

import android.text.InputType
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.ValidationUtils

class EmailFormatFieldHelper : FormatFieldHelper() {

    override val inputType: Int
        get() = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

    override val errorMessage: Int
        @StringRes get() = R.string.required_data_field_validation_error_field_email

    override fun validate(value: String) = ValidationUtils.isEmail(value)

}