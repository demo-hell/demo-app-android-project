package br.com.mobicare.cielo.centralDeAjuda.domains.entities

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.centralDeAjuda.domains.entities.CentralAjudaDefaultObj
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PhoneSupport (
    var title: String? = null,
    var description: String? = null,
    @SerializedName("storeHours")
    var timeDescription: String? = null,
    var items: List<CentralAjudaDefaultObj> = emptyList()
): Parcelable
