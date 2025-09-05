package br.com.mobicare.cielo.deeplink.model

import android.net.Uri
import android.os.Parcelable
import br.com.mobicare.cielo.commons.constants.Deeplink
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeepLinkModel(
    val id: String,
    val params: Map<String, String>
) : Parcelable {

    companion object {

        fun generateDeepLinkModel(uri: Uri): DeepLinkModel? {
            var id: String? = null
            val params = mutableMapOf<String, String>()

            uri.queryParameterNames.forEach { parameter ->
                val value = uri.getQueryParameter(parameter)

                when (parameter) {
                    Deeplink.DEEPLINK_PARAMETER -> id = value
                    else -> value?.let { params[parameter] = value }
                }
            }

            return id?.let { DeepLinkModel(it, params) }
        }

    }

}