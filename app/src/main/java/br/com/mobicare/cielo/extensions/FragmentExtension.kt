package br.com.mobicare.cielo.extensions

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.webkit.MimeTypeMap
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.EIGHT
import br.com.mobicare.cielo.commons.constants.Intent.DEFAULT_TYPE
import br.com.mobicare.cielo.commons.constants.Intent.DIVIDER
import br.com.mobicare.cielo.commons.constants.Intent.FILE_TYPE_JPG
import br.com.mobicare.cielo.commons.constants.Intent.KILOBYTE
import br.com.mobicare.cielo.commons.constants.Intent.POINT
import br.com.mobicare.cielo.commons.constants.Intent.REQUEST_STORE_PERMISSION_PACKAGE
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.PACKAGE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

inline fun BaseFragment?.doWhenResumed(
    crossinline action: () -> Unit,
    crossinline errorCallback: () -> Unit = {}
) {
    this?.lifecycleScope?.launchWhenResumed {
        if (isAttached()) {
            action.invoke()
        } else {
            errorCallback.invoke()
        }
    } ?: errorCallback.invoke()
}

inline fun BaseFragment?.doWhenResumed(crossinline action: () -> Unit) {
    this.doWhenResumed(
        action = action,
        errorCallback = {}
    )
}

fun BaseFragment.checkIfFragmentAttached(operation: Context.() -> Unit) {
    if (isAdded && context != null) {
        operation(requireContext())
    }
}

fun Fragment.safeRun(onAction: () -> Unit) {
    if (this.isAdded)
        this.requireActivity().runOnUiThread {
            onAction.invoke()
        }
}

fun Fragment.doWhenVisible(action: () -> Unit) {
    if (isVisible) action()
}

fun BaseFragment.showStoragePermission() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts(REQUEST_STORE_PERMISSION_PACKAGE, requireContext().packageName, tag)
    intent.data = uri
    startActivity(intent)
}

fun BaseFragment.selectFileIntent(mimeTypes: Array<String>): Intent {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)

    if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
        intent.type = if (mimeTypes.size == ONE) mimeTypes[ZERO] else DEFAULT_TYPE
        if (mimeTypes.isNotEmpty())
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
    } else {
        var mimeTypesStr = EMPTY
        for (mimeType in mimeTypes) {
            mimeTypesStr += "$mimeType$DIVIDER"
        }
        intent.type = mimeTypesStr.substring(ZERO, mimeTypesStr.length - ONE)
    }

    intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, EIGHT)
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)

    return intent
}

fun BaseFragment.convertToBase64(uri: Uri): String? {
    return try {
        requireActivity().contentResolver.openInputStream(uri)?.let {
            val fileBytes = it.available()
            val dataSize = fileBytes / (KILOBYTE * KILOBYTE)
            if (dataSize <= EIGHT)
                Base64.encodeToString(getBytes(it), Base64.NO_WRAP).replace("\\", EMPTY)
            else null
        }
    } catch (e: Exception) {
        e.message.logFirebaseCrashlytics()
        null
    }
}

private fun getBytes(inputStream: InputStream): ByteArray? {
    val byteBuffer = ByteArrayOutputStream()
    val buffer = ByteArray(KILOBYTE)
    var len: Int
    while (inputStream.read(buffer).also { len = it } != ONE_NEGATIVE) {
        byteBuffer.write(buffer, ZERO, len)
    }
    return byteBuffer.toByteArray()
}

fun BaseFragment.getMimeType(uri: Uri): String {
    val type = if (uri.scheme == ContentResolver.SCHEME_CONTENT)
        MimeTypeMap.getSingleton().getExtensionFromMimeType(context?.contentResolver?.getType(uri))
            ?: FILE_TYPE_JPG
    else
        uri.path?.let {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(it)).toString())
        } ?: FILE_TYPE_JPG
    return "$POINT$type"
}

fun BaseFragment.showApplicationConfiguration() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts(PACKAGE, requireContext().packageName, tag)
    intent.data = uri
    startActivity(intent)
}