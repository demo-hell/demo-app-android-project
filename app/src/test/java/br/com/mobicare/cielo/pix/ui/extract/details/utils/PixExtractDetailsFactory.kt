package br.com.mobicare.cielo.pix.ui.extract.details.utils

import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse
import com.google.gson.Gson

object PixExtractDetailsFactory {

    private val transferDetailsJson = """
        {
            "idAccount": "322242",
            "transactionType": "TRANSFER_CREDIT",
            "idEndToEnd": "E0102705820230718200901668558387",
            "transferType": 3,
            "transactionStatus": "EXECUTED",
            "debitParty": {
                "ispb": "1027058",
                "name": "DigitechLTDA",
                "bankName": "CIELO IP S.A.",
                "bankAccountNumber": "24580800010",
                "bankBranchNumber": "0001",
                "bankAccountType": "CC",
                "nationalRegistration": "34.035.256/0001-56"
            },
            "amount": 7.44,
            "creditParty": {
                "ispb": "1027058",
                "bankName": "CIELO IP S.A.",
                "name": "MOCK RAZAO LTDA",
                "bankAccountType": "PA",
                "nationalRegistration": "71.664.429/0001-21",
                "bankAccountNumber": "37740000018",
                "bankBranchNumber": "0001",
                "key": "9048dfec-00f1-4abc-946c-c5ec6be06e71"
            },
            "finalAmount": 7.37,
            "tariffAmount": 0.07,
            "idAdjustment": "321455",
            "idTx": "CIELO202307180000000000000000000071",
            "payerAnswer": "Teste 01022022 PIX4",
            "transactionCode": "588d8792-8f75-4bc1-a3d4-ea5c2be41e5f",
            "transactionDate": "2023-07-18T17:09:07.513",
            "pixType": "TRANSFER",
            "transactionReversalDeadline": "2023-10-16T17:09:07.513",
            "expiredReversal": false,
            "merchantNumber": "2312092250",
            "transferOrigin": "P2P",
            "originChannel": "App Cielo Gestão",
            "fee": {
                "feeIdEndToEnd": "E01027058202307182010hxSrj4lR07y",
                "feeTax": 0.92,
                "feePaymentDate": "2023-07-18T17:10:59.859",
                "feeTransactionStatus": "EXECUTED",
                "feeTransactionCode": "a65fc13a-39f9-4560-b9eb-9d8190e248f2",
                "feeType": "PERCENTAGE"
            },
            "settlement": {
                "settlementIdEndToEnd": "E01027058202307182010GuB6Xp2xTHG",
                "settlementDate": "2023-07-18T20:10:57.897",
                "settlementTransactionStatus": "EXECUTED",
                "settlementTransactionCode": "a66ecf38-7e69-4937-a9cd-8f138bf36455",
                "settlementFinalAmount": 7.37
            }
        }
    """.trimIndent()

    val transferDetailsResponse: TransferDetailsResponse =
        Gson().fromJson(transferDetailsJson, TransferDetailsResponse::class.java)

}