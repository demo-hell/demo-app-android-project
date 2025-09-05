package br.com.mobicare.cielo.component.requiredDataField.presentation.helper

import android.text.InputType
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R

open class FormatFieldHelper {
    open val inputType get() = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
    open val errorMessage @StringRes get() = R.string.required_data_field_validation_error_field

    open fun getCleanedText(value: String) = value.trim()
    open fun getMaskedText(value: String) = value
    open fun validate(value: String) = true
}