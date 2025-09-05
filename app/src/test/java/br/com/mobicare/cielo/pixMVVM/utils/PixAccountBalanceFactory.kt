package br.com.mobicare.cielo.pixMVVM.utils

import br.com.mobicare.cielo.pixMVVM.data.mapper.toEntity
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixAccountBalanceResponse
import com.google.gson.Gson

object PixAccountBalanceFactory {

    private val pixAccountBalanceJson = """
            {
              "idStatusAccount": 0,
              "statusAccount": "string",
              "dataStatusAccount": "2023-11-03T19:39:02.966Z",
              "balanceAvailableGlobal": 0,
              "balanceAvailableWithdrawal": 0,
              "finalCurrentBalance": 0,
              "previousExtractBalance": 0,
              "timeOfRequest": "2023-12-05T10:21:42.395-03:00"
            }
        """.trimIndent()

    val pixAccountBalanceResponse: PixAccountBalanceResponse =
        Gson().fromJson(pixAccountBalanceJson, PixAccountBalanceResponse::class.java)

    val pixAccountBalanceEntity = pixAccountBalanceResponse.toEntity()

}