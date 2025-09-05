package br.com.mobicare.cielo.meuCadastroNovo.data.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetUserAdditionalInfo(
    @SerializedName("timeOfDay")
    var timeOfDay: TimeOfDay? = TimeOfDay(),
    @SerializedName("typeOfCommunication")
    var typeOfCommunication: ArrayList<TypeOfCommunication> = arrayListOf(),
    @SerializedName("contactPreference")
    var contactPreference: ContactPreference? = ContactPreference(),
    @SerializedName("pcdType")
    var pcdType: PcdType? = PcdType()
): Serializable