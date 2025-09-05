package br.com.mobicare.cielo.pixMVVM.utils

import br.com.mobicare.cielo.pixMVVM.data.mapper.toEntity
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixExtractFilterRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixReceiptsScheduledRequest
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixExtractResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixReceiptsScheduledResponse
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PeriodFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.model.PixExtractFilterModel
import com.google.gson.Gson

object PixExtractFactory {
    private val pixExtractReceiptsJson =
        """
        {
          "totalItemsPage": 25,
          "items": [
            {
              "title": "Janeiro 2024",
              "yearMonth": "2024-01",
              "receipts": [
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E0102705820240122111737434125359",
                  "transactionDate": "2024-01-22T08:17:40.968",
                  "transactionType": "TRANSFER_CREDIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "QR_CODE_DINAMICO",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 0.1,
                  "finalAmount": 0.1,
                  "idAdjustment": 449594,
                  "transactionCode": "85ae5181-359b-49be-b75d-7d4bdb6f391a",
                  "payeeName": "MOCK RAZAO LTDA",
                  "idTx": "faabdb748ae9439097314166f044d277398",
                  "date": "2024-01-22T08:17:40.968",
                  "type": "QRCODE_CREDIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E01027058202401201000QB6ZAYY2HCS",
                  "transactionDate": "2024-01-20T07:03:33.048",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 0.02,
                  "finalAmount": 0.02,
                  "idAdjustment": 449582,
                  "transactionCode": "46d249b7-b75d-484b-9740-c86447e827ee",
                  "payerName": "social name",
                  "date": "2024-01-20T07:03:33.048",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E0102705820240119153449444423366",
                  "transactionDate": "2024-01-19T12:34:49.878",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 8.25,
                  "finalAmount": 8.25,
                  "idAdjustment": 449329,
                  "transactionCode": "556cdc74-fc2b-485a-ac6b-ecbeb14dee74",
                  "payerName": "WANTUIRTOM FERREIRA DE QUEIROZ",
                  "date": "2024-01-19T12:34:49.878",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E0102705820240119150525613962664",
                  "transactionDate": "2024-01-19T12:05:26.132",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 8.25,
                  "finalAmount": 8.25,
                  "idAdjustment": 448695,
                  "transactionCode": "1a4977d6-b484-4f2c-a4e3-30652a377478",
                  "payerName": "WANTUIRTOM FERREIRA DE QUEIROZ",
                  "date": "2024-01-19T12:05:26.132",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E0102705820240119150342472912278",
                  "transactionDate": "2024-01-19T12:03:42.922",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 8.25,
                  "finalAmount": 8.25,
                  "idAdjustment": 448681,
                  "transactionCode": "676ca1d0-511b-4bb3-846c-cef889c18722",
                  "payerName": "WANTUIRTOM FERREIRA DE QUEIROZ",
                  "date": "2024-01-19T12:03:42.922",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E0102705820240119145927912540757",
                  "transactionDate": "2024-01-19T11:59:28.272",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 8.25,
                  "finalAmount": 8.25,
                  "idAdjustment": 448671,
                  "transactionCode": "8e64561f-d2fb-48d8-aac3-2ad721dc0f22",
                  "payerName": "WANTUIRTOM FERREIRA DE QUEIROZ",
                  "date": "2024-01-19T11:59:28.272",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E0102705820240119145106635195146",
                  "transactionDate": "2024-01-19T11:51:07.164",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 8.25,
                  "finalAmount": 8.25,
                  "idAdjustment": 448660,
                  "transactionCode": "a77ecefb-cf81-4004-86d1-a64f64ba979e",
                  "payerName": "WANTUIRTOM FERREIRA DE QUEIROZ",
                  "date": "2024-01-19T11:51:07.164",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E0102705820240119143806254138093",
                  "transactionDate": "2024-01-19T11:38:07.318",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 8.25,
                  "finalAmount": 8.25,
                  "idAdjustment": 448653,
                  "transactionCode": "61500398-18ef-4af5-826e-e046533309be",
                  "payerName": "WANTUIRTOM FERREIRA DE QUEIROZ",
                  "date": "2024-01-19T11:38:07.318",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E0102705820240119143622421882012",
                  "transactionDate": "2024-01-19T11:36:22.857",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "NOT_EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 8.25,
                  "finalAmount": 8.25,
                  "idAdjustment": 448651,
                  "transactionCode": "cb800217-43d1-4df7-a270-5d5c8a41aafc",
                  "payerName": "Endrick Jose",
                  "date": "2024-01-19T11:36:22.857",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E0102705820240119142925888805818",
                  "transactionDate": "2024-01-19T11:29:26.348",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 8.25,
                  "finalAmount": 8.25,
                  "idAdjustment": 448642,
                  "transactionCode": "0c131b18-f831-4590-b81c-344a879ffc8d",
                  "payerName": "WANTUIRTOM FERREIRA DE QUEIROZ",
                  "date": "2024-01-19T11:29:26.348",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E00000000202401191349QxxAJq1XPMS",
                  "transactionDate": "2024-01-19T10:49:53.492",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 1.05,
                  "finalAmount": 1.05,
                  "idAdjustment": 448605,
                  "transactionCode": "7fd5a755-2c6a-4159-aa61-62c57e8ccd62",
                  "payerName": "Joao Silva",
                  "date": "2024-01-19T10:49:53.492",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E0102705820240119134134203969059",
                  "transactionDate": "2024-01-19T10:41:37.51",
                  "transactionType": "TRANSFER_CREDIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "QR_CODE_ESTATICO",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 0.1,
                  "finalAmount": 0.1,
                  "idAdjustment": 448598,
                  "transactionCode": "d7d7f79c-0570-45af-ba83-a77210d35cec",
                  "payeeName": "MOCK RAZAO LTDA",
                  "idTx": "0000000000000adeb96649863",
                  "date": "2024-01-19T10:41:37.51",
                  "type": "QRCODE_CREDIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E010270582024011910403a3cb11149a",
                  "transactionDate": "2024-01-19T10:40:34.382",
                  "transactionType": "TRANSFER_CREDIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "QR_CODE_ESTATICO",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 0.1,
                  "finalAmount": 0.1,
                  "idAdjustment": 448588,
                  "transactionCode": "5d725280-44bf-4af9-bcba-dce0ae107ecc",
                  "payeeName": "MOCK RAZAO LTDA",
                  "idTx": "CIELO20240119000000000049",
                  "date": "2024-01-19T10:40:34.382",
                  "type": "QRCODE_CREDIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E0102705820240119133318353194898",
                  "transactionDate": "2024-01-19T10:33:18.68",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 0.1,
                  "finalAmount": 0.1,
                  "idAdjustment": 448583,
                  "transactionCode": "0dffa625-d10e-416d-bd99-3d6005ce3c06",
                  "payerName": "DigitechLTDA",
                  "date": "2024-01-19T10:33:18.68",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E0102705820240119133309721267237",
                  "transactionDate": "2024-01-19T10:33:10.093",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 0.1,
                  "finalAmount": 0.1,
                  "idAdjustment": 448581,
                  "transactionCode": "42f62387-0d27-45b4-8ab8-b9ec393abc3d",
                  "payerName": "social name",
                  "date": "2024-01-19T10:33:10.093",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E0102705820240119133300508340971",
                  "transactionDate": "2024-01-19T10:33:00.835",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 0.1,
                  "finalAmount": 0.1,
                  "idAdjustment": 448579,
                  "transactionCode": "f4d4b143-11ca-457f-8fb4-bdb09cc5530e",
                  "payerName": "Fernando Mafaciolli",
                  "date": "2024-01-19T10:33:00.835",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E0102705820240119133251242680149",
                  "transactionDate": "2024-01-19T10:32:51.569",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 0.1,
                  "finalAmount": 0.1,
                  "idAdjustment": 448577,
                  "transactionCode": "a1d91a67-770c-4563-8b8b-5cbd3348542c",
                  "payerName": "E2E PIX FULL",
                  "date": "2024-01-19T10:32:51.569",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E0102705820240119133239918875170",
                  "transactionDate": "2024-01-19T10:32:43.022",
                  "transactionType": "TRANSFER_CREDIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 0.1,
                  "finalAmount": 0.1,
                  "idAdjustment": 448576,
                  "transactionCode": "84712554-ecee-44f2-bffa-256d31047b92",
                  "payeeName": "DigitechLTDA",
                  "date": "2024-01-19T10:32:43.022",
                  "type": "TRANSFER_CREDIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E0102705820240119133239918875170",
                  "transactionDate": "2024-01-19T10:32:40.29",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 0.1,
                  "finalAmount": 0.1,
                  "idAdjustment": 448575,
                  "transactionCode": "83ba2255-6852-4ff6-b7bb-03fe8759a363",
                  "payerName": "MOCK RAZAO LTDA",
                  "date": "2024-01-19T10:32:40.29",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E01027058202401191031d6cca76e940",
                  "transactionDate": "2024-01-19T10:31:52.856",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "MANUAL",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 0.1,
                  "finalAmount": 0.1,
                  "idAdjustment": 448571,
                  "transactionCode": "0ea1a788-086e-45e1-867e-aabed45af743",
                  "payerName": "social name",
                  "date": "2024-01-19T10:31:52.856",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E010270582024011910310eb0da5a89d",
                  "transactionDate": "2024-01-19T10:31:33.091",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "MANUAL",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 0.1,
                  "finalAmount": 0.1,
                  "idAdjustment": 448567,
                  "transactionCode": "254cafeb-092d-49db-939c-379870c46bac",
                  "payerName": "social name",
                  "date": "2024-01-19T10:31:33.091",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E01027058202401191021514e9f4b3c6",
                  "transactionDate": "2024-01-19T10:21:17.392",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "TRANSFER_DEBIT",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 0.1,
                  "finalAmount": 0.1,
                  "idAdjustment": 448562,
                  "transactionCode": "82254014-72ec-4675-a44b-ce9ff2a7d832",
                  "payerName": "DigitechLTDA",
                  "idTx": "Cielo202401191021",
                  "date": "2024-01-19T10:21:17.392",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E01027058202401191021c1ab2d00a20",
                  "transactionDate": "2024-01-19T10:21:12.747",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 0.1,
                  "finalAmount": 0.1,
                  "idAdjustment": 448560,
                  "transactionCode": "060c9a54-acd6-4c51-a431-db4364d96aa4",
                  "payerName": "DigitechLTDA",
                  "date": "2024-01-19T10:21:12.747",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E010270582024011910219a92e8b615a",
                  "transactionDate": "2024-01-19T10:21:08.329",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "TRANSFER_DEBIT",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 0.1,
                  "finalAmount": 0.1,
                  "idAdjustment": 448557,
                  "transactionCode": "070042e9-b9c4-4829-828c-2ddea04bfc1b",
                  "payerName": "social name",
                  "idTx": "Cielo202401191021",
                  "date": "2024-01-19T10:21:08.329",
                  "type": "TRANSFER_DEBIT"
                },
                {
                  "idAccount": 51501,
                  "idEndToEnd": "E01027058202401191020c2250a69603",
                  "transactionDate": "2024-01-19T10:21:03.525",
                  "transactionType": "TRANSFER_DEBIT",
                  "transactionStatus": "EXECUTED",
                  "transferType": "CHAVE",
                  "pixType": "TRANSFER",
                  "tariffAmount": 0,
                  "amount": 0.1,
                  "finalAmount": 0.1,
                  "idAdjustment": 448554,
                  "transactionCode": "05a53057-1851-42bc-8363-236fb38be3ba",
                  "payerName": "social name",
                  "date": "2024-01-19T10:21:03.525",
                  "type": "TRANSFER_DEBIT"
                }
              ]
            }
          ]
        }
        """.trimIndent()

    private val jsonReceiptsScheduled =
        """
        {
            "last": true,
            "totalItemsPage": 0,
            "lastSchedulingIdentifierCode": "string",
            "lastNextDateTimeScheduled": "2024-08-30",
            "items": [
                {
                    "title": "string",
                    "yearMonth": "2024-08",
                    "receipts": [
                        {
                            "transactionStatus": "EXECUTED",
                            "finalAmount": 230.0,
                            "payeeName": "João da Silva",
                            "payeeDocumentNumber": "string",
                            "payeeBankName": "string",
                            "schedulingCode": "123456789",
                            "schedulingDate": "2024-06-20T18:14:38.454",
                            "type": "SCHEDULE_DEBIT"
                        },
                        {
                            "transactionStatus": "EXECUTED",
                            "finalAmount": 380.0,
                            "payeeName": "Maria de Lourdes",
                            "payeeDocumentNumber": "string",
                            "payeeBankName": "string",
                            "schedulingCode": "123456788",
                            "schedulingDate": "2024-06-30T18:14:38.454",
                            "type": "SCHEDULE_RECURRENCE_DEBIT"
                        }
                    ]
                }
            ]
        }
        """.trimIndent()

    private val pixExtractReceiptsResponse = Gson().fromJson(pixExtractReceiptsJson, PixExtractResponse::class.java)
    val pixExtract = pixExtractReceiptsResponse.toEntity()

    val pixExtractEmpty = PixExtractResponse().toEntity()

    private val pixExtractReceiptsScheduledResponse = Gson().fromJson(jsonReceiptsScheduled, PixReceiptsScheduledResponse::class.java)
    val pixExtractScheduling = pixExtractReceiptsScheduledResponse.toEntity()

    val pixExtractSchedulingEmpty = PixReceiptsScheduledResponse().toEntity()

    val pixExtractFilterRequest = PixExtractFilterRequest()

    val pixReceiptsScheduledRequest = PixReceiptsScheduledRequest()

    val filterActive = PixExtractFilterModel(periodType = PeriodFilterTypeEnum.SEVEN_DAYS)
}
