package br.com.mobicare.cielo.suporteTecnico.data

import br.com.mobicare.cielo.recebaMais.domain.Block
import br.com.mobicare.cielo.recebaMais.domain.OwnerAddress
import br.com.mobicare.cielo.recebaMais.domain.OwnerContact

data class UserOwnerSupportResponse(
    val companyName: String,
    val contacts: List<OwnerContact>,
    val addresses: List<OwnerAddress>,
    val blocks: List<Block> = listOf()
)
