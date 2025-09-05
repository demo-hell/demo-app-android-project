package br.com.mobicare.cielo.pixMVVM.utils

import br.com.mobicare.cielo.pixMVVM.data.model.request.PixProfileRequest
import com.google.gson.Gson

object PixProfileFactory {

    const val otpCode = "1234"

    private val pixProfileRequestJson = """
            {
              "settlementActive": true
            }
        """.trimIndent()

    val pixProfileRequest: PixProfileRequest =
        Gson().fromJson(pixProfileRequestJson, PixProfileRequest::class.java)

}