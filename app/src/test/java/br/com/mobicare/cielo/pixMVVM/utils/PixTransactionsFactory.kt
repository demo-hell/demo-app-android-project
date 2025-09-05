package br.com.mobicare.cielo.pixMVVM.utils

import br.com.mobicare.cielo.pixMVVM.data.mapper.toEntity
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixScheduleCancelRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixTransferBankAccountRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixTransferKeyRequest
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixEnableResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixSchedulingDetailResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixTransferBankResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixTransferDetailResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixTransferResultResponse
import com.google.gson.Gson

object PixTransactionsFactory {
    object TransferDetail {
        val json =
            """
            {
               "idAccountType": "string",
               "idAccount":"4923",
               "idEndToEnd":"E01027058202302081005sirsWeaVmgI",
               "transactionType":"TRANSFER_DEBIT",
               "transferType":0,
               "transactionStatus":"EXECUTED",
               "pixType":"TRANSFER",
               "transferOrigin":"P2M",
               "amount":10.0,
               "finalAmount":9.0,
               "tariffAmount":1.0,
               "changeAmount":3.0,
               "purchaseAmount":7.0,
               "payerAnswer":"Jogo de PS4 do Homem Aranha",
               "merchantNumber": "123456",
               "transactionDate":"2023-02-08T07:05:31.901",
               "originChannel":"App Cielo Gestão",
               "idTx":"string",
               "errorType": "string",
               "errorCode": "string",
               "errorMessage": "string",
               "idAdjustment":"185328",
               "transactionCode":"fe785da1-dd5c-43ab-8402-96441a040968",
               "transactionReversalDeadline":"2023-02-08T07:05:31.901",
               "expiredReversal":false,
               "type":"TRANSFER_DEBIT",
               "agentMode": "AGPSS",
               "agentWithdrawalIspb": "string",
               "debitParty":{
                  "ispb":"1027058",
                  "name":"MASSA DADOS AFIL. - 341-85597",
                  "bankName":"CIELO IP S.A.",
                  "bankAccountNumber":"42583000012",
                  "bankBranchNumber":"0001",
                  "bankAccountType":"CC",
                  "nationalRegistration":"66.691.597/0001-76",
                  "key": "string"
               },
               "creditParty":{
                  "ispb":"1027058",
                  "bankName":"CIELO IP S.A.",
                  "name":"Cielo",
                  "bankAccountType":"CC",
                  "nationalRegistration":"01.027.058/0001-91",
                  "bankAccountNumber":"92556400010",
                  "bankBranchNumber":"0001",
                  "key": "string"
               },
               "credit": {
                 "originChannel": "string",
                 "creditTransactionDate": "2024-01-22T18:57:35.624",
                 "creditAmount": 10.0,
                 "creditFinalAmount": 9.0,
                 "creditIdEndToEnd": "string",
                 "creditTransactionCode": "string"
               },
               "fee": {
                 "feeIdEndToEnd": "string",
                 "feeTax": 0,
                 "feePaymentDate": "2024-01-22T18:57:35.624",
                 "feeTransactionStatus": "EXECUTED",
                 "feeTransactionCode": "string",
                 "feeType": "PERCENTAGE"
               },
               "settlement": {
                 "settlementIdEndToEnd": "string",
                 "settlementDate": "2024-01-22T18:57:35.624",
                 "settlementTransactionStatus": "EXECUTED",
                 "settlementTransactionCode": "string",
                 "settlementFinalAmount": 9.0
               },
               "enable": ${Enable.json}
            }
            """.trimIndent()

        val response: PixTransferDetailResponse =
            Gson().fromJson(json, PixTransferDetailResponse::class.java)

        val entity = response.toEntity()
    }

    object TransferResult {
        private val json =
            """
            {
               "endToEndId":"E01027058202302081005sirsWeaVmgI",
               "transactionCode":"fe785da1-dd5c-43ab-8402-96441a040968",
               "transactionDate":"2024-01-22T18:57:35.624",
               "transactionStatus":"EXECUTED",
               "schedulingDate":"2024-01-22T18:57:35.624",
               "schedulingCode":"123"
            }
            """.trimIndent()

        val response: PixTransferResultResponse =
            Gson().fromJson(json, PixTransferResultResponse::class.java)

        val entity = response.toEntity()
    }

    object TransferBanks {
        private val json =
            """
            [
                {
                  "code": 1,
                  "ispb": "60701190",
                  "shortName": "Banco do Brasil",
                  "name": "Banco do Brasil"
                },
                {
                  "code": 237,
                  "ispb": "60746948",
                  "shortName": "Bradesco",
                  "name": "Bradesco"
                }
            ]
            """.trimIndent()

        val response: List<PixTransferBankResponse> =
            Gson().fromJson(json, Array<PixTransferBankResponse>::class.java).toList()

        val entity = response.toEntity()
    }

    object SchedulingDetail {
        private val json =
            """
            {
              "idAccount": 0,
              "idEndToEnd": "string",
              "payeeName": "string",
              "payeeDocumentNumber": "string",
              "payeeBankName": "string",
              "finalAmount": 0,
              "message": "string",
              "transactionType": "SCHEDULE_DEBIT",
              "schedulingCreationDate": "2024-01-30T17:47:38.451",
              "schedulingCancellationDate": "2024-01-30T17:47:38.451",
              "schedulingDate": "2024-01-30T17:47:38",
              "scheduledEndDate": "2024-01-30T17:47:38",
              "schedulingCode": "string",
              "merchantNumber": "string",
              "documentNumber": "string",
              "totalScheduled": 0,
              "totalScheduledProcessed": 0,
              "totalScheduledErrors": 0,              
              "frequencyTime": "MONTHLY",              
              "status": "EXECUTED",
              "type": "SCHEDULE_DEBIT",
              "enable": ${Enable.json}
            }
            """.trimIndent()

        val response: PixSchedulingDetailResponse =
            Gson().fromJson(json, PixSchedulingDetailResponse::class.java)

        val entity = response.toEntity()
    }

    object Enable {
        val json =
            """
            {
              "refund": false,
              "cancelSchedule": false,
              "requestAnalysis": false
            }
            """.trimIndent()

        val response: PixEnableResponse = Gson().fromJson(json, PixEnableResponse::class.java)

        val entity = response.toEntity()
    }

    object MockedParams {
        const val endToEndId = ""
        const val transactionCode = ""
        const val otpCode = ""
        const val schedulingCode = ""
        val transferKeyRequest = PixTransferKeyRequest()
        val transferAccountBankRequest = PixTransferBankAccountRequest()
        val transferScheduleCancelRequest = PixScheduleCancelRequest("")
    }
}
