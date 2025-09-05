package br.com.mobicare.cielo.component.requiredDataField.presentation.helper

import android.text.InputType
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.SEVENTEEN
import br.com.mobicare.cielo.commons.helpers.EditTextHelper
import br.com.mobicare.cielo.commons.utils.INTERNATIONAL_PHONE_MASK_FORMAT

class PhoneFormatFieldHelper : FormatFieldHelper() {

    override val inputType: Int
        get() = InputType.TYPE_CLASS_NUMBER

    override val errorMessage: Int
        @StringRes get() = R.string.required_data_field_validation_error_field_phone

    override fun getMaskedText(value: String) = EditTextHelper
        .phoneMaskFormatter(value, INTERNATIONAL_PHONE_MASK_FORMAT)
        .formattedText
        .string

    override fun validate(value: String) = value.trim().length >= SEVENTEEN

}