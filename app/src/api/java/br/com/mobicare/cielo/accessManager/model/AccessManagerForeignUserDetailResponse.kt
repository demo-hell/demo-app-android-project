package br.com.mobicare.cielo.accessManager.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class AccessManagerForeignUserDetailResponse(
    var id: String? = null,
    var name: String? = null,
    var email: String? = null,
    var cellphone: String? = null,
    var role: String? = null,
    var photo: String? = null,
    var sendToRevisionDateTime: String? = null,
    val profile: Profile? = null
) : Parcelable