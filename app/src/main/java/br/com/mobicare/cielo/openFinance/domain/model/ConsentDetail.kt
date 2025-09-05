package br.com.mobicare.cielo.openFinance.domain.model

data class ConsentDetail(
    val consentSatus: String,
    val flow: String,
    val consentId: String,
    val shareId: String,
    val brand: String,
    val organizationId: String,
    val loggedUser: String,
    val userName: String,
    val merchantId: String,
    val document: String,
    val alteration: Boolean,
    val renovation: Boolean,
    val cancelation: Boolean,
    val permissions: List<Permissions>?,
    val deadLine: DeadLine?,
    val confirmationDateTime: String,
    val expirationDateTime: String,
    val lastUpdateDateTime: String,
    val authorizationServerId: String,
    val logoUri: String
)

data class Permissions(
    val permissionCode: String?,
    val displayName: String?,
    val permissionDescription: String?
)

data class DeadLine(
    val total: Int?,
    val type: String?,
    val expirationDate: String?
)