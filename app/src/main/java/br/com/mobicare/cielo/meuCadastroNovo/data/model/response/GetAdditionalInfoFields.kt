package br.com.mobicare.cielo.meuCadastroNovo.data.model.response

import com.google.gson.annotations.SerializedName

data class GetAdditionalInfoFields(
    @SerializedName("timeOfDay")
    var timeOfDay: ArrayList<TimeOfDay> = arrayListOf(),
    @SerializedName("typeOfCommunication")
    var typeOfCommunication: ArrayList<TypeOfCommunication> = arrayListOf(),
    @SerializedName("contactPreference")
    var contactPreference: ArrayList<ContactPreference> = arrayListOf(),
    @SerializedName("pcdType")
    var pcdType: ArrayList<PcdType> = arrayListOf()
)