package br.com.mobicare.cielo.changeEc.domain

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.cielo.libflue.util.EMPTY
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Impersonate(
    @SerializedName("hierarchyLevelDescription")
    val hierarchyLevelDescription: String? = EMPTY,
    val hasLoyalty: Boolean? = false,
    val hasOffer: Boolean? = false,
    @SerializedName("access_token")
    val accessToken: String? = EMPTY,
    @SerializedName("refresh_token")
    val refreshToken: String? = EMPTY,
    val expiresIn: Int?,
    val isConvivenciaUser: Boolean?
) : Parcelable