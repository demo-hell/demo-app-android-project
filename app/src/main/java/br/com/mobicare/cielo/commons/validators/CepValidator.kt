package br.com.mobicare.cielo.commons.validators

import br.com.mobicare.cielo.commons.utils.Utils

class CepValidator(private val value: String) {

    private val cleanValue get() = Utils.unmask(value.trim())

    operator fun invoke() = cleanValue.matches(Regex("^\\d{8}$"))

}