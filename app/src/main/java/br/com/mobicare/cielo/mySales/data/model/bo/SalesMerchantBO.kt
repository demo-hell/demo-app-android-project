package br.com.mobicare.cielo.mySales.data.model.bo

import br.com.mobicare.cielo.recebaMais.domain.OwnerAddress

data class SalesMerchantBO(
        val address: OwnerAddress,
        val companyName: String,
        val cnpj: String
)