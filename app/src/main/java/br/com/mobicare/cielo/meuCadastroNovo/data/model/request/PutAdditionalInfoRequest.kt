package br.com.mobicare.cielo.meuCadastroNovo.data.model.request

data class PutAdditionalInfoRequest(
    var timeOfDay: String? = null,
    var typeOfCommunication: ArrayList<String> = arrayListOf(),
    var contactPreference: String? = null,
    var pcdType: String? = null,
)
