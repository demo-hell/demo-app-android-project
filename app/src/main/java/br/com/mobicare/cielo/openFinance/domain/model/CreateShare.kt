package br.com.mobicare.cielo.openFinance.domain.model

data class CreateShare(
        val authorizationServer: AuthorizationServer,
        val deadLines: List<DeadLine>,
        val shareId: String,
        val resourceGroups: List<ResourceGroup>,
        val userInformation: UserInformation
)

data class AuthorizationServer(
        val organizationId: String,
        val logoUri: String,
        val authorizationServerId: String,
        val customerFriendlyName: String
)

data class Permission(
        val displayName: String,
        val detail: String,
        val required: Boolean,
        val permissionCode: String
)

data class ResourceGroup(
        val displayName: String,
        val type: String,
        val permission: List<Permission>
)

data class UserInformation(
        val document: String,
        val typeDocument: String
)
