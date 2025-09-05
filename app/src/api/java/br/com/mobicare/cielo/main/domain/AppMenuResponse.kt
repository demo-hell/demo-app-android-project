package br.com.mobicare.cielo.main.domain


import com.google.gson.annotations.SerializedName
import java.util.*

data class AppMenuResponse(
        var createdAt: Long = Calendar.getInstance().timeInMillis,
        @SerializedName("menu")
        val menu: List<Menu>
)