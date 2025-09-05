package br.com.mobicare.cielo.pixMVVM.utils

import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidResponse
import br.com.mobicare.cielo.pixMVVM.data.mapper.toEntity
import br.com.mobicare.cielo.pixMVVM.data.model.response.OnBoardingFulfillmentResponse
import com.google.gson.Gson

object PixOnBoardingFactory {

    private val onBoardingFulfillmentJson = """
        {
          "eligible": false,
          "profileType": "FREE_MOVEMENT",
          "settlementActive": true, 
          "enabled": true,
          "status": "ACTIVE",
          "blockType": null,
          "pixAccount": {
            "pixId": "1234",
            "bank": "bank",
            "agency": "001",
            "account": "0001",
            "accountDigit": "1",
            "dockAccountId": "54321",
            "isCielo": true,
            "bankName": "bankName"
          },
          "settlementScheduled": {
            "enabled": false,
            "list": [
              "06:00", "10:00"
            ]
          },
          "document": "00.000.000/0001-00",
          "documentType": "J"
        }
    """.trimIndent()

    private val prepaidJson = """
        {
          "cards": [
            {
               proxyNumber: "1234"
            }
          ]
        }
    """.trimIndent()

    val onBoardingFulfillmentResponse: OnBoardingFulfillmentResponse =
        Gson().fromJson(onBoardingFulfillmentJson, OnBoardingFulfillmentResponse::class.java)

    val prepaidResponse: PrepaidResponse = Gson().fromJson(prepaidJson, PrepaidResponse::class.java)

    val onBoardingFulfillmentEntity = onBoardingFulfillmentResponse.toEntity()

}