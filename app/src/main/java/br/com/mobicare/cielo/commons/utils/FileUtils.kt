package br.com.mobicare.cielo.commons.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Base64
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

open class FileUtils(private val context: Context) {

    fun convertBase64ToFile(base64String: String, parentFolder: String? = null, fileName: String, fileType: String): File {
        val file = createNewFile(parentFolder, fileName, fileType)

        convertToFile(file, Base64.decode(base64String, Base64.NO_WRAP))
        return file
    }

    private fun createNewFile(parentFolder: String?, fileName: String, type: String): File {
        val file = File(getDirectory(parentFolder), fileName + type)

        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.createNewFile()
        }
        return file
    }

    private fun convertToFile(file: File, byteArray: ByteArray) {
        try {
            val stream = FileOutputStream(file)
            stream.write(byteArray)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
        }
    }

    private fun getDirectory(parentFolder: String?): File {
        val dir = File(
            context.getExternalFilesDir(parentFolder),
            ""
        )

        if (!dir.isDirectory) {
            dir.mkdirs()
        }
        return dir
    }

    fun startShare(file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            PROVIDER, file
        )
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = file.getMimeType()
        }
        val chooser = Intent.createChooser(shareIntent, file.name)

        val resInfoList: List<ResolveInfo> = context.packageManager
            .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)

        for (resolveInfo in resInfoList) {
            val packageName: String = resolveInfo.activityInfo.packageName
            context.grantUriPermission(
                packageName,
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        context.startActivity(chooser)
    }

    companion object {
        private const val PROVIDER = "br.com.mobicare.cielo.fileprovider"
    }
}

fun File.getMimeType(fallback: String = "*/*"): String {
    return MimeTypeMap.getFileExtensionFromUrl(toString())
        ?.run { MimeTypeMap.getSingleton().getMimeTypeFromExtension(lowercase(Locale.getDefault())) }
        ?: fallback
}