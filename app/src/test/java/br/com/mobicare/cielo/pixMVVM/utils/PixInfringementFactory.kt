package br.com.mobicare.cielo.pixMVVM.utils

import br.com.mobicare.cielo.pixMVVM.data.model.request.PixCreateNotifyInfringementRequest
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixCreateNotifyInfringementResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixEligibilityInfringementResponse
import br.com.mobicare.cielo.pixMVVM.domain.model.PixCreateNotifyInfringement
import com.google.gson.Gson

object PixInfringementFactory {

    private val pixGetInfringementResponseJson = """
        {
          "isEligible": true,
          "merchantId": "2040607891",
          "amount": 999.99,
          "idEndToEnd": "E0102705820240208135741406938291",
          "reasonType": "REQUEST_DEVOLUTION",
          "transactionDate": "2024-01-09T16:38:01.357",
          "payee": {
            "name": "MASSA DADOS AFIL. - 341-85597",
            "document": "***.999.999-**",
            "key": "f84d3b7c-951d-4a04-b6e7-c4ebb3f19960",
            "bank": {
              "ispb": "1027058",
              "name": "CIELO IP S.A.",
              "accountType": "CC",
              "accountNumber": "42583000012",
              "branchNumber": "0001"
            }
          },
          "situations": [
            {
              "description": "Fui vítima de golpe",
              "type": "COUP"
            },
            {
              "description": "Não reconheço essa transação",
              "type": "UNAUTHORIZED_TRANSACTION"
            },
            {
              "description": "Fiz o Pix sob ameaça, coação ou sequestro",
              "type": "COERCION"
            },
            {
              "description": "Fui vitima de uma fraude",
              "type": "FRAUD_ACCESS_OR_AUTHORIZATION"
            },
            {
              "description": "Outros",
              "type": "OTHERS"
            }
          ]
        }
    """.trimIndent()

    val pixCreateInfringementResponseJson = """
        {
          "id": "123456789",
          "creationDate": "2024-01-24T13:27:24.717Z"
        }
    """.trimIndent()

    val pixGetInfringementResponse: PixEligibilityInfringementResponse = Gson().fromJson(
        pixGetInfringementResponseJson,
        PixEligibilityInfringementResponse::class.java
    )

    val pixCreateInfringementResponse: PixCreateNotifyInfringementResponse = Gson().fromJson(
        pixCreateInfringementResponseJson,
        PixCreateNotifyInfringementResponse::class.java
    )

    const val idEndToEnd = "E0102705820240208135741406938291"

    val pixCreateInfringementRequest = PixCreateNotifyInfringementRequest(
        idEndToEnd = "123456789",
        message = "Motivo",
        situationType = "",
        reasonType = "",
        amount = 10.0,
        merchantId = "123456",
    )

    const val reason = "Razão"

    const val reasonDetails = "Descrição da razão"

    val pixGetInfringementResponseWithIneligible = PixEligibilityInfringementResponse(
        isEligible = false,
        details = "Detalhamento do motivo da ineligibilidade",
        merchantId = "123456"
    )

    val pixCreateNotifyInfringement = PixCreateNotifyInfringement(
        idEndToEnd = "123456789",
        message = "Motivo",
        situationType = "COUP",
        reasonType = "REQUEST_DEVOLUTION",
        amount = 10.0,
        merchantId = "123456"
    )

}