package br.com.mobicare.cielo.pixMVVM.utils

import br.com.mobicare.cielo.pixMVVM.data.mapper.toEntity
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixRefundCreateRequest
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixRefundCreatedResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixRefundDetailFullResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixRefundDetailResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixRefundReceiptsResponse
import com.google.gson.Gson

object PixRefundsFactory {
    object RefundDetail {
        val json =
            """
            {
                "idAccount": 0,
                "idEndToEndOriginal": "E01027058202302081005sirsWeaVmgI",
                "idEndToEndReturn": "E01027058202302081005sirsWeaVmgR",
                "transactionDate": "2024-01-18T21:06:23.744",
                "transactionType": "REVERSAL_CREDIT",
                "errorType": 0,
                "transactionStatus": "EXECUTED",
                "creditParty": {
                  "ispb": 0,
                  "bankName": "string",
                  "nationalRegistration": "string",
                  "name": "Receiver",
                  "bankBranchNumber": "string",
                  "bankAccountType": "CC: Conta corrente",
                  "bankAccountNumber": "string"
                },
                "debitParty": {
                  "ispb": 0,
                  "bankName": "string",
                  "nationalRegistration": "string",
                  "name": "Sender",
                  "bankBranchNumber": "string",
                  "bankAccountType": "CC: Conta corrente",
                  "bankAccountNumber": "string"
                },
                "amount": 10.0,
                "tariffAmount": 0,
                "finalAmount": 0,
                "reversalCode": 0,
                "reversalReason": "string",
                "idAdjustment": 0,
                "transactionCode": "string",
                "payerAnswer": "Me enviou valor por engano.",
                "transactionCodeOriginal": "string",
                "type": "REVERSAL_DEBIT",
                "enable": ${PixTransactionsFactory.Enable.json}
            }
            """.trimIndent()

        val response: PixRefundDetailResponse =
            Gson().fromJson(json, PixRefundDetailResponse::class.java)

        val entity = response.toEntity()
    }

    object RefundReceipts {
        private val json =
            """
            {
              "currentPage": 0,
              "last": true,
              "totalPages": 0,
              "totalAmountPossibleReversal": 10.0,
              "totalItemsPage": 1,
              "items": [
                {
                  "title": "string",
                  "yearMonth": {
                    "year": 0,
                    "month": "JANUARY",
                    "monthValue": 0,
                    "leapYear": true
                  },
                  "receipts": [
                    {
                      "idAccount": 0,
                      "idEndToEnd": "string",
                      "idEndToEndOriginal": "string",
                      "transactionDate": "2024-01-23T21:08:41.644",
                      "transactionType": "TRANSFER_DEBIT",
                      "transactionStatus": "EXECUTED",
                      "reversalCode": 0,
                      "reversalReason": "string",
                      "tariffAmount": 0,
                      "amount": 5.0,
                      "finalAmount": 5.0,
                      "idAdjustment": 0,
                      "transactionCode": "string"
                    }
                  ]
                }
              ]
            }
            """.trimIndent()

        val response: PixRefundReceiptsResponse =
            Gson().fromJson(json, PixRefundReceiptsResponse::class.java)

        val entity = response.toEntity()
    }

    object RefundDetailFull {
        private val json =
            """
            {
              "refundDetail": ${RefundDetail.json},
              "transferDetail": ${PixTransactionsFactory.TransferDetail.json},
              "enable": ${PixTransactionsFactory.Enable.json}
            }
            """.trimIndent()

        val response: PixRefundDetailFullResponse =
            Gson().fromJson(json, PixRefundDetailFullResponse::class.java)

        val entity = response.toEntity()
    }

    object RefundCreated {
        private val json =
            """
            {
              "idEndToEndReturn": "string",
              "idEndToEndOriginal": "string",
              "transactionDate": "2023-05-09T07:05:31.901",
              "idAdjustment": "string",
              "transactionCode": "string",
              "transactionStatus": "NOT_EXECUTED",
              "idTx": "string"
            }
            """.trimIndent()

        val response: PixRefundCreatedResponse =
            Gson().fromJson(json, PixRefundCreatedResponse::class.java)

        val entity = response.toEntity()
    }

    object MockedParams {
        const val idEndToEndOriginal = ""
        const val transactionCode = ""
        const val otpCode = ""
        val refundCreateRequest = PixRefundCreateRequest()
    }
}
