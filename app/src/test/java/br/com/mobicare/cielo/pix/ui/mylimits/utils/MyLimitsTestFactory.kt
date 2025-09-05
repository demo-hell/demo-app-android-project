package br.com.mobicare.cielo.pix.ui.mylimits.utils

import br.com.mobicare.cielo.pix.domain.LimitsRequest

object MyLimitsTestFactory {

    const val OTP = "0000"

    val listWithOneLimitRequest = mutableListOf(
        LimitsRequest(
            type = "DAYTIME_TRANSACTION_LIMIT",
            value = 10000.01
        )
    )

    val myLimitsEmptyJson = """
        {
           "merchantNumber":"0000786952",
           "limits":[]
        }
    """.trimIndent()

    val myLimitsWithdrawAndChargeJson = """
        {
           "merchantNumber":"0000786952",
           "limits":[
              {
                 "type":"NIGHTTIME_TRANSACTION_LIMIT",
                 "transactionLimit":999,
                 "serviceGroup":"CHARGE_WITHDRAW",
                 "defaultLimit":1000,
                 "accountLimit":1000
              },
              {
                 "type":"DAYTIME_TRANSACTION_LIMIT",
                 "transactionLimit":999,
                 "serviceGroup":"CHARGE_WITHDRAW",
                 "defaultLimit":5000000,
                 "accountLimit":10000
              },
              {
                 "type":"TOTAL_DAYTIME_TRANSACTION_LIMIT",
                 "transactionLimit":999,
                 "serviceGroup":"CHARGE_WITHDRAW",
                 "defaultLimit":5000000,
                 "accountLimit":10000
              },
              {
                 "type":"TOTAL_MONTH_TRANSACTION_LIMIT",
                 "transactionLimit":999,
                 "serviceGroup":"CHARGE_WITHDRAW",
                 "defaultLimit":5000000,
                 "accountLimit":50000
              },
              {
                 "type":"TOTAL_NIGHTTIME_TRANSACTION_LIMIT",
                 "transactionLimit":999,
                 "serviceGroup":"CHARGE_WITHDRAW",
                 "defaultLimit":1000,
                 "accountLimit":1000
              }
           ]
        }
    """.trimIndent()

    val myLimitsForNaturalPersonJson = """
        {
           "merchantNumber":"0000786952",
           "limits":[
              {
                 "type":"NIGHTTIME_TRANSACTION_LIMIT",
                 "beneficiaryType":"FISICA",
                 "transactionLimit":999,
                 "serviceGroup":"PIX",
                 "defaultLimit":1000,
                 "accountLimit":1000
              },
              {
                 "type":"DAYTIME_TRANSACTION_LIMIT",
                 "beneficiaryType":"FISICA",
                 "transactionLimit":999,
                 "serviceGroup":"PIX",
                 "defaultLimit":5000000,
                 "accountLimit":10000
              },
              {
                 "type":"TOTAL_DAYTIME_TRANSACTION_LIMIT",
                 "beneficiaryType":"FISICA",
                 "transactionLimit":999,
                 "serviceGroup":"PIX",
                 "defaultLimit":5000000,
                 "accountLimit":10000
              },
              {
                 "type":"TOTAL_MONTH_TRANSACTION_LIMIT",
                 "beneficiaryType":"FISICA",
                 "transactionLimit":999,
                 "serviceGroup":"PIX",
                 "defaultLimit":5000000,
                 "accountLimit":50000
              },
              {
                 "type":"TOTAL_NIGHTTIME_TRANSACTION_LIMIT",
                 "beneficiaryType":"FISICA",
                 "transactionLimit":999,
                 "serviceGroup":"PIX",
                 "defaultLimit":1000,
                 "accountLimit":1000
              }
           ]
        }
    """.trimIndent()

    const val timeManagementResponse = "{\n" +
            "    \"merchantNumber\":\"2014784161\",\n" +
            "    \"nighttimeStart\":\"20:00:00\",\n" +
            "    \"actualDayTimeDescription\":\"Período Diurno: 06h a 20h.\",\n" +
            "    \"actualNightTimeDescription\":\"Período Noturno: 20h a 06h.\",\n" +
            "    \"lastRequest\":{\n" +
            "        \"nighttimeStart\":\"22:00:00\",\n" +
            "        \"status\":\"PENDING\",\n" +
            "        \"requestDate\":\"2022-07-20T15:55:08Z\"\n" +
            "    }\n" +
            "}"

}