package br.com.mobicare.cielo.component.requiredDataField.presentation.factory

import br.com.mobicare.cielo.component.requiredDataField.presentation.builder.RequiredDataFieldViewBuilder.FieldData
import br.com.mobicare.cielo.component.requiredDataField.presentation.helper.*

object FormatFieldHelperFactory {

    fun create(fieldFormat: String?) = when (fieldFormat) {
        FieldData.FORMAT_NUMBER -> NumberFormatFieldHelper()
        FieldData.FORMAT_DATE -> DateFormatFieldHelper()
        FieldData.FORMAT_EMAIL -> EmailFormatFieldHelper()
        FieldData.FORMAT_DOCUMENT -> DocumentFormatFieldHelper()
        FieldData.FORMAT_PHONE -> PhoneFormatFieldHelper()
        FieldData.FORMAT_STATE -> StateFormatFieldHelper()
        FieldData.FORMAT_ZIP_CODE -> ZipCodeFormatFieldHelper()
        else -> FormatFieldHelper()
    }

}