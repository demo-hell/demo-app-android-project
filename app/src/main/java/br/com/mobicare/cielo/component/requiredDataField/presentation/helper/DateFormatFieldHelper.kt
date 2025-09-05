package br.com.mobicare.cielo.component.requiredDataField.presentation.helper

import android.text.InputType
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.DateTimeHelper
import br.com.mobicare.cielo.commons.helpers.EditTextHelper
import br.com.mobicare.cielo.commons.utils.SIMPLE_DATE_INTERNATIONAL
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

class DateFormatFieldHelper : FormatFieldHelper() {

    override val inputType: Int
        get() = InputType.TYPE_CLASS_NUMBER

    override val errorMessage: Int
        @StringRes get() = R.string.required_data_field_validation_error_field_date

    override fun getCleanedText(value: String) = DateTimeHelper
        .convertToDate(value.trim(), SIMPLE_DT_FORMAT_MASK, SIMPLE_DATE_INTERNATIONAL)

    override fun getMaskedText(value: String) = EditTextHelper
        .dateMaskFormatter(value)
        .formattedText
        .string

    override fun validate(value: String) = try {
        LocalDate.parse(
            value,
            DateTimeFormatter
                .ofPattern(STRICT_DT_FORMAT_MASK)
                .withResolverStyle(ResolverStyle.STRICT)
        )
        true
    } catch (e: Exception) {
        false
    }

    companion object {
        private const val STRICT_DT_FORMAT_MASK = "dd/MM/uuuu"
    }

}