package br.com.mobicare.cielo.cieloFarol.utils

import br.com.mobicare.cielo.cieloFarol.data.model.response.CieloFarolResponse

object CieloFarolFactory {
    val farolCompleted = CieloFarolResponse(
        bestDayOfWeek = "Sexta-Feira",
        bestTime = "0912",
        averageTicketAmount = "500.50",
        insightText = "Seus clientes gastaram em média 18% a mais que os clientes dos concorrentes."
    )

    val farolRequestMerchantId = "5421245521"
    val farolRequestAuthorization = "saioai2494454b5a587xx5a8"
}