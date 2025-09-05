package br.com.mobicare.cielo.commons.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.extensions.activity

object QRCodePixUtils {

    fun qrCodeBase64ToBitmap(qrCodeBase64: String?): Bitmap? {
        try {
            if (qrCodeBase64.isNullOrEmpty()) return null

            val decodedBytes = Base64.decode(qrCodeBase64, Base64.NO_WRAP)
            return BitmapFactory.decodeByteArray(decodedBytes, ZERO, decodedBytes.size)
        } catch (e: Exception) {
            return null
        }
    }

    fun copyQRCodePix(context: Context, code: String) {
        Utils.copyToClipboard(context, code, showMessage = false)
        context.activity()?.let {
            Toast(context).showCustomToast(
                message = context.getString(R.string.text_pix_generated_qr_code_copy),
                activity = it,
                trailingIcon = R.drawable.ic_symbol_check_round_white_20_dp
            )
        }
    }

}