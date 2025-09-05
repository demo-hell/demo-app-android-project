package br.com.mobicare.cielo.main.domain


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(
        @SerializedName("code")
    val code: String,
        @SerializedName("icon")
    val icon: String,
        @SerializedName("shortIcon")
    val shortIcon: String,
        @SerializedName("name")
    val name: String,
        @SerializedName("privileges")
    val privileges: List<String>,
        @SerializedName("show")
    val show: Boolean,
        @SerializedName("showIcons")
    val showIcons: Boolean,
        @SerializedName("showItems")
    val showItems: Boolean,
        @SerializedName("target")
    val menuTarget: MenuTarget,
        @SerializedName("items")
    val items: List<Menu>
) : Parcelable