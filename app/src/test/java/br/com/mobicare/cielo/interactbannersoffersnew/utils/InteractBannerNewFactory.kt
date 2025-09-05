package br.com.mobicare.cielo.interactbannersoffersnew.utils

import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object InteractBannerNewFactory {

    val bannerFactoryResponse = """
            [
               {
                  "id":"000013266",
                  "treatmentCode":"2e218.33d3.ffffffffafd9933a.ffffffffe19c7512",
                  "name":"OFERTA_BLACK_FRIDAY_RR_APP",
                  "priority":99, 
                  "customerId":"0",
                  "hiringUrl":"https://minhaconta2.cielo.com.br/deeplink?fluxo=11",
                  "internal":false
               },
               {
                  "id":"000013237",
                  "treatmentCode":"2e218.33b6.26ffcfb3.ffffffffe0bd45d3",
                  "name":"ATENDIMENTO_WHATSAPP_APP",
                  "priority":0,
                  "customerId":"0",
                  "hiringUrl":"https://minhaconta2.cielo.com.br/deeplink?fluxo=4",
                  "internal":false
               },
               {
                  "id":"000011870",
                  "treatmentCode":"2e218.2e5f.13b2626f.3e12369f",
                  "name":"INTERACT_BANNER_OFFER_BCREDI",
                  "priority":1,
                  "customerId":"0",
                  "hiringUrl":"www.cielonegocie.com.br",
                  "internal":false
               },
               {
                  "id":"000011966",
                  "treatmentCode":"2e218.2ebf.6b02623d.450d7cfd",
                  "name":"ATENDIMENTO_WHATSAPP_APP",
                  "priority":2,
                  "customerId":"0",
                  "hiringUrl":"https://api.whatsapp.com/send/?phone=551130035525&text=Oi&type=phone_number&app_absent=0",
                  "internal":false
               },
               {
                  "id":"000012153",
                  "treatmentCode":"2e218.2f7c.74eb4754.3b125f5c",
                  "name":"OFERTA_BLACK_FRIDAY_ARV_APP",
                  "priority":3,
                  "customerId":"0",
                  "hiringUrl":"/minha-conta/antecipacao",
                  "internal":false
               },
               {
                  "id":"000011388",
                  "treatmentCode":"2e218.2c7d.9d42dcc.ffffffffc1b47014",
                  "name":"CRM_BONUS_ISENTOS",
                  "priority":4,
                  "customerId":"0",
                  "hiringUrl":"https://www.crmbonus.com/?partner=CIELO",
                  "internal":false
               }
            ]
        """.trimIndent()
    fun getCompleteResponse(): List<HiringOffers> {

        val listType = object : TypeToken<List<HiringOffers>>() {}.type
        return Gson().fromJson(bannerFactoryResponse, listType)
    }

    fun emptyResponse() = mutableListOf<HiringOffers>()
}