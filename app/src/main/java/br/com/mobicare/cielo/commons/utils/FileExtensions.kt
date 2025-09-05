package br.com.mobicare.cielo.commons.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File


fun File.convertBase64(quality: Int = 100): String? {

    if (this.exists()) {
        //encode image to base64 string
        val baos = ByteArrayOutputStream()
        val bitmap = BitmapFactory.decodeFile(this.absolutePath)
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }
    return null
}

//fun File.compressImage(context: Context,
//                       maxWidth: Int,
//                       maxHeight: Int,
//                       quality: Int = 100): File {
//
//    val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CieloAppC")
//    if (!mediaStorageDir.exists()) {
//        mediaStorageDir.mkdir()
//        if (!mediaStorageDir.exists()) {
//           throw Exception("Não foi possível criar um novo diretorio.")
//
//        }
//    }
//
//    return Compressor(context).setMaxWidth(maxWidth)
//            .setMaxHeight(maxHeight)
//            .setQuality(quality)
//            .setCompressFormat(Bitmap.CompressFormat.WEBP)
//            .setDestinationDirectoryPath(mediaStorageDir.absolutePath)
//            .compressToFile(this)
//
//}