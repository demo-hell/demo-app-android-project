package br.com.mobicare.cielo.openFinance.data.model.response

import com.google.errorprone.annotations.Keep

@Keep
data class SharedDataConsentsResponse(
    val summary: Summary,
    val pagination: Pagination,
    val items: List<Items>
)

data class Summary(
    val totalQuantity: Int,
    val totalAmount: Int,
    val lastPage: String,
    val nextPage: String,
    val currentPage: String,
    val previousPage: String
)

data class Pagination(
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Int,
    val firstPage: Boolean,
    val lastPage: Boolean,
    val numPages: Int
)

data class Items(
    val consentId: String,
    val shareId: String,
    val organizationName: String,
    val organisationId: String,
    val authorizationServerId: String,
    val creationDateTime: String,
    val revocationDateTime: String,
    val expirationDateTime: String,
    val flowType: String,
    val consentStatus: String,
    val logo: String
)
