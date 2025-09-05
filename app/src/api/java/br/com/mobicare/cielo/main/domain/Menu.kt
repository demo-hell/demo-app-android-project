package br.com.mobicare.cielo.main.domain

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Menu(
    @SerializedName("code")
    var code: String,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("items")
    var items: List<Menu>?,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("showIcons")
    val showIcons: Boolean,
    @SerializedName("shortIcon")
    var shortIcon: String?,
    @SerializedName("privileges")
    val privileges: List<String>,
    @SerializedName("show")
    val show: Boolean,
    @SerializedName("showItems")
    val showItems: Boolean,
    @SerializedName("target")
    val menuTarget: MenuTarget
) : Parcelable