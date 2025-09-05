package br.com.mobicare.cielo.component.requiredDataField.presentation.factory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.*
import br.com.mobicare.cielo.component.requiredDataField.presentation.builder.RequiredDataFieldViewBuilder
import br.com.mobicare.cielo.component.requiredDataField.presentation.builder.RequiredDataFieldAccordionViewBuilder
import br.com.mobicare.cielo.component.requiredDataField.presentation.builder.RequiredDataFieldSectionViewBuilder
import br.com.mobicare.cielo.component.requiredDataField.utils.RequiredDataFieldValueStore
import br.com.mobicare.cielo.extensions.orZero

class RequiredDataFieldViewFactory(
    private val layoutInflater: LayoutInflater,
    private val requiredResponse: Required,
    private val valueStore: RequiredDataFieldValueStore,
    private val onFieldChanged: ((String, String) -> Unit)? = null
) {

    private val hasFewFields get() = requiredResponse.run {
        val fieldsCount = individualFields?.size.orZero +
                addressFields?.size.orZero +
                companyFields?.size.orZero +
                phoneFields?.let { getFilteredPhones(it).size }.orZero

        fieldsCount <= THREE
    }

    private val viewBuilderInstance get() = if (hasFewFields) {
        RequiredDataFieldSectionViewBuilder(layoutInflater, valueStore, onFieldChanged)
    } else {
        RequiredDataFieldAccordionViewBuilder(layoutInflater, valueStore, onFieldChanged)
    }

    fun create() = with(requiredResponse) {
        listOfNotNull(
            createIndividualFields(individualFields),
            createAddressFields(addressFields),
            createCompanyFields(companyFields),
            createPhoneFields(phoneFields)
        ).removeLastDivider()
    }

    private fun createAddressFields(addressFields: List<Field>?) =
        addressFields?.let { fields ->
            if (fields.isEmpty()) return null
            viewBuilderInstance.run {
                setTitle(getString(R.string.required_data_field_section_title_address))
                setFieldDataList(
                    fields.map {
                        RequiredDataFieldViewBuilder.FieldData(
                            id = it.id.orEmpty(),
                            labelText = it.label.orEmpty(),
                            hintText = it.placeholder.orEmpty(),
                            format = it.format
                        )
                    }
                )
                build()
            }
        }

    private fun createCompanyFields(companyFields: List<Field>?) =
        companyFields?.let { fields ->
            if (fields.isEmpty()) return null
            viewBuilderInstance.run {
                setTitle(getString(R.string.required_data_field_section_title_company_info))
                setFieldDataList(
                    fields.map {
                        RequiredDataFieldViewBuilder.FieldData(
                            id = it.id.orEmpty(),
                            labelText = it.label.orEmpty(),
                            hintText = it.placeholder.orEmpty(),
                            format = it.format
                        )
                    }
                )
                build()
            }
        }

    private fun createIndividualFields(individualsFields: List<Field>?) =
        individualsFields?.let { fields ->
            if (fields.isEmpty()) return null
            viewBuilderInstance.run {
                setTitle(getString(R.string.required_data_field_section_title_individual_info))
                setFieldDataList(
                    fields.map {
                        RequiredDataFieldViewBuilder.FieldData(
                            id = it.id.orEmpty(),
                            labelText = it.label.orEmpty(),
                            hintText = it.placeholder.orEmpty(),
                            format = it.format
                        )
                    }
                )
                build()
            }
        }

    private fun createPhoneFields(phoneFields: List<Field>?) =
        phoneFields?.let { fields ->
            if (getFilteredPhones(fields).isEmpty()) return null
            viewBuilderInstance.run {
                setTitle(getString(R.string.required_data_field_section_title_contact_info))
                setFieldDataList(
                    getFilteredPhones(fields).map {
                        RequiredDataFieldViewBuilder.FieldData(
                            id = it.id.orEmpty(),
                            labelText = getPhoneLabel(it.id.orEmpty()),
                            hintText = getString(R.string.required_data_field_hint_phone),
                            format = RequiredDataFieldViewBuilder.FieldData.FORMAT_PHONE
                        )
                    }
                )
                build()
            }
        }

    private fun getFilteredPhones(fields: List<Field>) =
        fields.filter { it.id == PHONE_NUMBER_ID || it.id == MOBILE_PHONE_NUMBER_ID }

    private fun getPhoneLabel(id: String) =
        getString(
            if (id == MOBILE_PHONE_NUMBER_ID) R.string.required_data_field_label_field_cellphone
            else R.string.required_data_field_label_field_phone
        )

    private fun getString(@StringRes value: Int) = layoutInflater.context.getString(value)

    private fun List<View>.removeLastDivider() = try {
        (lastOrNull()?.rootView as? ViewGroup)?.apply {
            removeView(findViewById(R.id.divider))
        }
        this
    } catch (_: Exception) {
        this
    }

    companion object {
        const val PHONE_NUMBER_ID = "phones[].number"
        const val MOBILE_PHONE_NUMBER_ID = "phones[].mobilePhone"
    }

}