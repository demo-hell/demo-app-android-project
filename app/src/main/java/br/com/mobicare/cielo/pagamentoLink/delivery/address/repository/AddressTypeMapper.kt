package br.com.mobicare.cielo.pagamentoLink.delivery.address.repository

import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.toLowerCasePTBR

object AddressTypeMapper {

    fun mapper(string: String) = string.split(";", ignoreCase = false, limit = 0)
            .map {
                it.toLowerCasePTBR().capitalizePTBR()
            }
}