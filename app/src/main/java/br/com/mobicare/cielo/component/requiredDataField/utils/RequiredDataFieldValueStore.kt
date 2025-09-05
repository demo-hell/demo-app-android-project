package br.com.mobicare.cielo.component.requiredDataField.utils

import br.com.mobicare.cielo.component.requiredDataField.data.model.request.Field

class RequiredDataFieldValueStore {

    private val values = mutableMapOf<String, String>()
    private val errors = mutableMapOf<String, Boolean>()

    val asFieldRequestList get() = values.map { Field(id = it.key, value = it.value) }

    val isValid get() = errors.none { it.value }

    fun update(id: String, value: String) {
        values[id] = value
    }

    fun get(id: String): String? = values[id]

    fun updateError(id: String, value: Boolean) {
        errors[id] = value
    }

    override fun toString() = values.toString()

}