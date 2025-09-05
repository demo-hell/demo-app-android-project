package br.com.mobicare.cielo.pixMVVM.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixStatus
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Keep
@Parcelize
data class PixAuthorizationStatus(
    val status: PixStatus? = null,
    val beginTime: LocalDateTime? = null,
) : Parcelable