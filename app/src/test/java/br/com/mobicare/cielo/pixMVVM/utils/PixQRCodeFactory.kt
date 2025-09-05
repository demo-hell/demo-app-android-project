package br.com.mobicare.cielo.pixMVVM.utils

import br.com.mobicare.cielo.pixMVVM.data.mapper.toEntity
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixDecodeQRCodeRequest
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixDecodeQRCodeResponse
import com.google.gson.Gson

object PixQRCodeFactory {
    private val pixDecodeQRCodeResponseJson =
        """
        {
          "endToEndId": "E010270582024080712199JODC2ZMdhd",
          "type": "STATIC",
          "pixType": "TRANSFER",
          "participant": 1027058,
          "participantName": "CIELO IP S.A.",
          "receiverName": "MOCK RAZAO LTDA",
          "receiverPersonType": "LEGAL_PERSON",
          "receiverDocument": "62.993.482/0001-85",
          "idTx": "CIELO20240802000000000139",
          "city": "SAO PAULO",
          "originalAmount": 0.00,
          "finalAmount": 0.00,
          "branch": "1",
          "accountType": "4",
          "accountNumber": "34728200016",
          "key": "+5583988556622",
          "keyType": "PHONE",
          "category": "",
          "modalityAlteration": "ALLOWED",
          "qrCode": "00020101021126360014br.gov.bcb.pix0114+55839885566225204000053039865802BR5915MOCK RAZAO LTDA6009SAO PAULO62290525CIELO20240802000000000139630412BF",
          "isSchedulable": true
        }
        """.trimIndent()

    val pixDecodeQRCodeResponse: PixDecodeQRCodeResponse =
        Gson().fromJson(
            pixDecodeQRCodeResponseJson,
            PixDecodeQRCodeResponse::class.java,
        )

    val pixDecodeQRCode = pixDecodeQRCodeResponse.toEntity()

    val qrCode = "00020101021126360014br.gov.bcb.pix0114+55839885566225204000053039865802BR5915MOCK RAZAO LTDA6009SAO PAULO62290525CIELO20240802000000000139630412BF"
    val pixDecodeQRCodeRequest = PixDecodeQRCodeRequest(qrCode)
}
