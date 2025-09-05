package br.com.mobicare.cielo.pixMVVM.utils

import br.com.mobicare.cielo.pixMVVM.data.model.request.PixScheduledSettlementRequest
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixScheduledSettlementResponse
import com.google.gson.Gson

object PixScheduledSettlementFactory {

    const val otpCode = "1234"

    private val requestJson = """
            {
              "listScheduled": [
                "06:00", "12:00", "17:30", "22:00"
              ],
              "settlementScheduled": true
            }
        """.trimIndent()

    val pixScheduledSettlementRequest: PixScheduledSettlementRequest =
        Gson().fromJson(requestJson, PixScheduledSettlementRequest::class.java)

    private val responseJson = """
            {
              "listScheduled": [
                "06:00", "12:00", "17:30", "22:00"
              ],
              "document": "string"
            }
        """.trimIndent()

    val pixScheduledSettlementResponse: PixScheduledSettlementResponse =
        Gson().fromJson(responseJson, PixScheduledSettlementResponse::class.java)

    val scheduleList = listOf(
        "06:00", "12:00", "17:30", "22:00"
    )

}