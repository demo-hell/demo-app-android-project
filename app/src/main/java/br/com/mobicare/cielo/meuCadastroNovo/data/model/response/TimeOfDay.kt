package br.com.mobicare.cielo.meuCadastroNovo.data.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TimeOfDay(
    @SerializedName("code")
    var code: String? = null,
    @SerializedName("description")
    var description: String? = null
): Serializable