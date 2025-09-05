package br.com.mobicare.cielo.centralDeAjuda.data.clients.domains

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class OmbudsmanResponse(
        val codigo: String?,
        val date: String?,
        val descricao: String?,
        val requestId: String?
) : Parcelable