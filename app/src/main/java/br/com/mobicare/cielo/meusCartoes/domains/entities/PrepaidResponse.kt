package br.com.mobicare.cielo.meusCartoes.domains.entities

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.login.domain.UserStatusPrepago
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PrepaidResponse(
    @SerializedName("status") val status: UserStatusPrepago? = null,
    @SerializedName("cards") val cards: List<Card>? = null
) : Parcelable

