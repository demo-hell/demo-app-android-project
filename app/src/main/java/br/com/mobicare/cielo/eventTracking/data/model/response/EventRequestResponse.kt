package br.com.mobicare.cielo.eventTracking.data.model.response

import com.google.gson.annotations.SerializedName

data class EventRequestResponse(

    @SerializedName("merchantCode")
    var merchantCode: String? = null,
    @SerializedName("contractID")
    var contractID: String? = null,
    @SerializedName("cases")
    var cases: ArrayList<CasesResponseItem> = arrayListOf(),
    @SerializedName("events")
    var callResponse: ArrayList<CallResponseItem> = arrayListOf()

)

data class OwnerResponseItem(
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("email")
    var email: String? = null
)

data class CasesResponseItem(
    @SerializedName("caseID")
    var caseID: String? = null,
    @SerializedName("caseNumber")
    var caseNumber: String? = null,
    @SerializedName("subject")
    var subject: String? = null,
    @SerializedName("createdDate")
    var createdDate: String? = null,
    @SerializedName("priority")
    var priority: String? = null,
    @SerializedName("owner")
    var owner: OwnerResponseItem? = null
)

data class CallResponseItem(
    @SerializedName("code")
    var code: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("createdDate")
    var createdDate: String? = null,
    @SerializedName("referCode")
    var referCode: String? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("solutionDeadline")
    var solutionDeadline: Int? = null,
    @SerializedName("dependencyCode")
    var dependencyCode: String? = null
)