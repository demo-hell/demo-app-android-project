package br.com.mobicare.cielo.pixMVVM.utils

import br.com.mobicare.cielo.pixMVVM.data.mapper.toEntity
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixKeysResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixValidateKeyResponse
import com.google.gson.Gson

object PixKeysFactory {

    private val pixAllKeysJson = """
            {
              "keys": {
                "date": "string",
                "count": 0,
                "keys": [
                  {
                    "key": "string",
                    "keyType": "CPF",
                    "claimType": "PORTABILITY",
                    "main": false,
                    "claimDetail": {
                      "claimType": "PORTABILITY",
                      "participationType": "CLAIMANT",
                      "keyType": "CPF",
                      "key": "string",
                      "claimId": "string",
                      "claimStatus": "OPENED",
                      "confirmationReason": "string",
                      "cancellationReason": "string",
                      "canceledBy": "string",
                      "resolutionLimitDate": "string",
                      "completionLimitDate": "string",
                      "lastModifiedDate": "string",
                      "keyOwningRevalidationRequired": true,
                      "claimantIspb": "string",
                      "claimantIspbName": "string",
                      "donorIspb": "string",
                      "donorIspbName": "string"
                    }                  
                  },
                  {
                    "key": "string",
                    "keyType": "CPF",
                    "claimType": "PORTABILITY",
                    "main": false,
                    "claimDetail": {
                      "claimType": "PORTABILITY",
                      "participationType": "CLAIMANT",
                      "keyType": "CPF",
                      "key": "string",
                      "claimId": "string",
                      "claimStatus": "OPENED",
                      "confirmationReason": "string",
                      "cancellationReason": "string",
                      "canceledBy": "string",
                      "resolutionLimitDate": "string",
                      "completionLimitDate": "string",
                      "lastModifiedDate": "string",
                      "keyOwningRevalidationRequired": true,
                      "claimantIspb": "string",
                      "claimantIspbName": "string",
                      "donorIspb": "string",
                      "donorIspbName": "string"
                    }                  
                  }
                ]
              },
              "claims": {
                "date": "string",
                "count": 0,
                "keys": [
                  {
                    "key": "string",
                    "keyType": "CPF",
                    "claimType": "PORTABILITY",
                    "main": true,
                    "claimDetail": {
                      "claimType": "PORTABILITY",
                      "participationType": "CLAIMANT",
                      "keyType": "CPF",
                      "key": "string",
                      "claimId": "string",
                      "claimStatus": "OPENED",
                      "confirmationReason": "string",
                      "cancellationReason": "string",
                      "canceledBy": "string",
                      "resolutionLimitDate": "string",
                      "completionLimitDate": "string",
                      "lastModifiedDate": "string",
                      "keyOwningRevalidationRequired": true,
                      "claimantIspb": "string",
                      "claimantIspbName": "string",
                      "donorIspb": "string",
                      "donorIspbName": "string"
                    }
                  }                
                ]
              }
            }
        """.trimIndent()

    private val pixValidateKeyJson = """
        {
           "key":"78719216653",
           "keyType":"CPF",
           "participant":17184037,
           "participantName":"BCO MERCANTIL DO BRASIL S.A.",
           "branch":"1",
           "accountType":"****",
           "accountNumber":"****",
           "ownerType":"NATURAL_PERSON",
           "ownerName":"WANTUIRTOM FERREIRA DE QUEIROZ",
           "ownerDocument":"***.192.166-**",
           "creationDate":"2021-06-22T18:21:00.638Z",
           "ownershipDate":"2020-12-21T14:01:54.500Z",
           "claimType":"POSSESSION_CLAIM",
           "endToEndId":"E0102705820240313184312289942399"
        }
    """.trimIndent()

    val pixAllKeysResponse: PixKeysResponse =
        Gson().fromJson(pixAllKeysJson, PixKeysResponse::class.java)

    private val pixValidateKeyResponse: PixValidateKeyResponse = Gson().fromJson(pixValidateKeyJson, PixValidateKeyResponse::class.java)

    val pixValidateKey = pixValidateKeyResponse.toEntity()

    object WithMasterKey {
        val keyItems = pixAllKeysResponse.keys?.keys!!.mapIndexed {
                index, keyItem -> keyItem.copy(main = index == 0)
        }

        val masterKey = keyItems.first()
    }

    object WithoutMasterKey {
        val keyItems = pixAllKeysResponse.keys?.keys!!.map { keyItem -> keyItem.copy(main = false) }
    }

    const val key = "78719216653"
    const val keyType = "CPF"

}