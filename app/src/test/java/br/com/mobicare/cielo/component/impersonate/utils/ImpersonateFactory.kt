package br.com.mobicare.cielo.component.impersonate.utils

import br.com.mobicare.cielo.commons.constants.ONE_HUNDRED
import br.com.mobicare.cielo.component.impersonate.data.model.request.ImpersonateRequest
import br.com.mobicare.cielo.component.impersonate.data.model.response.ImpersonateResponse
import br.com.mobicare.cielo.me.MeResponse
import com.google.gson.Gson

object ImpersonateFactory {

    const val ec = "1234567890"
    private const val accessToken = "123456"
    private const val refreshToken = "123456"
    private const val tokenType = ""
    const val fingerprint = "123456"

    val impersonateResponse = ImpersonateResponse(
        accessToken,
        refreshToken,
        tokenType,
        expiresIn = ONE_HUNDRED
    )

    val impersonateRequest = ImpersonateRequest(fingerprint)

    private val meJson = """
         {
            "id":"0b0c0000000f0000ba0000fbf00fa00c0000df0fe000e0a000f",
            "advertisingId":"0b0c0000000f0000ba0000fbf00fa00c0000df0fe000e0a000",
            "username":"Massa teste",
            "login":"00000000051",
            "email":"teste@teste.com",
            "birthDate":"1990-06-11",
            "identity":{
                "cpf":"00000000051",
                "foreigner":false
            },
            "phoneNumber":"(11) 99999-9999",
            "roles":[
                "MASTER"
            ],
            "merchant":{
                "id":"0000000005",
                "name":"NAME TEST",
                "tradingName":"TEST",
                "cnpj":{
                   "rootNumber":"00000000",
                   "number":"00.000.000/0001-00"
                },
                "receivableType":"Individual",
                "hierarchyLevel":"TEST_GROUP",
                "individual":false,
                "migrated":true
            },
            "activeMerchant":{
                "id":"2200000000",
                "name":"NAME TEST",
                "tradingName":"TEST",
                "cnpj":{
                   "rootNumber":"00000000",
                   "number":"00.000.000/0001-00"
                },
                "receivableType":"Individual",
                "hierarchyLevel":"TEST_GROUP",
                "individual":false,
                "migrated":true
            },
            "impersonating":false,
            "impersonationEnabled":true,
            "lastLoginDate":"2023-02-24T00:00:00",
            "isMigrationRequired":false,
            "onboardingRequired":false,
            "digitalId":{
                "status":"P2_APPROVED",
                "deadline":"2022-06-11",
                "mandatory":false,
                "migrated":true,
                "expired":false,
                "p1Approved":false,
                "p2Approved":true,
                "notApproved":false
            }
         }
    """.trimIndent()

    val meResponse: MeResponse = Gson().fromJson(meJson, MeResponse::class.java)

}