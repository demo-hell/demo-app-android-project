package br.com.mobicare.cielo.openFinance.data.model.response

import br.com.mobicare.cielo.openFinance.domain.model.DeadLine


data class CreateShareResponse(
        val authorizationServer: AuthorizationServerResponse,
        val deadLines: List<DeadLine>,
        val shareId: String,
        val resourceGroups: List<ResourceGroupResponse>,
        val userInformation: UserInformationResponse
)

data class AuthorizationServerResponse(
        val organizationId: String,
        val logoUri: String,
        val authorizationServerId: String,
        val customerFriendlyName: String
)

data class PermissionResponse(
        val displayName: String,
        val detail: String,
        val required: Boolean,
        val permissionCode: String
)

data class ResourceGroupResponse(
        val displayName: String,
        val type: String,
        val permission: List<PermissionResponse>
)

data class UserInformationResponse(
        val document: String,
        val typeDocument: String
)