package br.com.mobicare.cielo.postecipado.utils

import br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel.PlanInformationResponse
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel.PostecipadoRentInformationResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PostecipadoFactory {
    fun getCompleteResponse(): PlanInformationResponse {
        val response = """
           [
              {
                "currentDate":"2022-08-23T20:13:22",
                "dateUpdate":"2023-08-01T08:19:23",
                "expirationDate":"2023-11-01",
                "referenceMonth":"2022-12-01",
                "valueContract":47500,
                "valueDiscountPartial":129.9,
                "valueDiscountNegotiated":0,
                "percentageReached":"100",
                "percentageMissing":"0",
                "billingPerformed":47500,
                "missingValue":0,
                "isWaitingPeriod":false,
                "isExempted":false,
                "terminals":[],
                "daysToEndTheMonth":4
              }
           ]
    """.trimIndent()

        val listType = object : TypeToken<PlanInformationResponse>() {}.type
        return  Gson().fromJson(response, listType)
    }

    fun getEmptyListResponse(): PlanInformationResponse {
        return PlanInformationResponse()
    }
}