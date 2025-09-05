package br.com.mobicare.cielo.component.requiredDataField.presentation.helper

import android.text.InputType
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.helpers.FormHelper
import br.com.mobicare.cielo.commons.utils.STATE_MASK_FORMAT

class StateFormatFieldHelper : FormatFieldHelper() {

    override val inputType: Int
        get() = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS

    override val errorMessage: Int
        @StringRes get() = R.string.required_data_field_validation_error_field_state

    override fun getCleanedText(value: String) = value.trim().uppercase()

    override fun getMaskedText(value: String) = FormHelper
        .maskFormatter(value.uppercase(), STATE_MASK_FORMAT)
        .formattedText
        .string

    override fun validate(value: String) = value.trim().length == TWO

}