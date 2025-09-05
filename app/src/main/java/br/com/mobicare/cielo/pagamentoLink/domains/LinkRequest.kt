package br.com.mobicare.cielo.pagamentoLink.domains

import java.math.BigDecimal

data class LinkRequest(val title: String,
                       val value: BigDecimal,
                       val quantity: Int? = null)