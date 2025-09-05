package br.com.mobicare.cielo.openFinance.data.mapper

import br.com.mobicare.cielo.openFinance.data.model.response.AuthorizationServerResponse
import br.com.mobicare.cielo.openFinance.data.model.response.BrandResponse
import br.com.mobicare.cielo.openFinance.data.model.response.ChangeOrRenewShareResponse
import br.com.mobicare.cielo.openFinance.data.model.response.ConfirmShareResponse
import br.com.mobicare.cielo.openFinance.data.model.response.ConsentDetailResponse
import br.com.mobicare.cielo.openFinance.data.model.response.CreateShareResponse
import br.com.mobicare.cielo.openFinance.data.model.response.DeadLineResponse
import br.com.mobicare.cielo.openFinance.data.model.response.GivenUpShareResponse
import br.com.mobicare.cielo.openFinance.data.model.response.InstitutionResponse
import br.com.mobicare.cielo.openFinance.data.model.response.OpenFinancePixAccountResponse
import br.com.mobicare.cielo.openFinance.data.model.response.PermissionResponse
import br.com.mobicare.cielo.openFinance.data.model.response.PermissionsResponse
import br.com.mobicare.cielo.openFinance.data.model.response.PixMerchantOpenFinanceResponse
import br.com.mobicare.cielo.openFinance.data.model.response.ResourceGroupResponse
import br.com.mobicare.cielo.openFinance.data.model.response.UpdateShareResponse
import br.com.mobicare.cielo.openFinance.data.model.response.UserInformationResponse
import br.com.mobicare.cielo.openFinance.domain.model.AuthorizationServer
import br.com.mobicare.cielo.openFinance.domain.model.Brand
import br.com.mobicare.cielo.openFinance.domain.model.ChangeOrRenewShare
import br.com.mobicare.cielo.openFinance.domain.model.ConfirmShare
import br.com.mobicare.cielo.openFinance.domain.model.ConsentDetail
import br.com.mobicare.cielo.openFinance.domain.model.CreateShare
import br.com.mobicare.cielo.openFinance.domain.model.DeadLine
import br.com.mobicare.cielo.openFinance.domain.model.GivenUpShare
import br.com.mobicare.cielo.openFinance.domain.model.Institution
import br.com.mobicare.cielo.openFinance.domain.model.OpenFinancePixAccount
import br.com.mobicare.cielo.openFinance.domain.model.Permission
import br.com.mobicare.cielo.openFinance.domain.model.Permissions
import br.com.mobicare.cielo.openFinance.domain.model.PixMerchantListResponse
import br.com.mobicare.cielo.openFinance.domain.model.ResourceGroup
import br.com.mobicare.cielo.openFinance.domain.model.TermsOfUse
import br.com.mobicare.cielo.openFinance.domain.model.UpdateShare
import br.com.mobicare.cielo.openFinance.domain.model.UserInformation

object HolderAPIMapper {
    fun mapToListOfPixMerchantBO(response: List<PixMerchantOpenFinanceResponse>?): List<PixMerchantListResponse>? {
        response?.let {
            val pixMerchantList: MutableList<PixMerchantListResponse> = mutableListOf()
            it.forEach { merchantResponse ->
                val pixMerchantOpenFinanceBO = PixMerchantListResponse(
                    id = merchantResponse.id,
                    name = merchantResponse.name,
                    merchantNumber = merchantResponse.merchantNumber,
                    documentType = merchantResponse.documentType,
                    documentNumber = merchantResponse.documentNumber,
                    pixAccount = mapToOpenFinancePixAccount(merchantResponse.pixAccountInfo)
                )
                pixMerchantList.add(pixMerchantOpenFinanceBO)
            }
            return pixMerchantList
        }
        return null
    }

    private fun mapToOpenFinancePixAccount(response: OpenFinancePixAccountResponse?): OpenFinancePixAccount? {
        response?.let {
            return OpenFinancePixAccount(
                dockAccountId = response.dockAccountId,
                isCielo = response.isCielo
            )
        }
        return null
    }

    fun mapToConsentDetail(response: ConsentDetailResponse?): ConsentDetail? {
        response?.let {
            return ConsentDetail(
                consentSatus = response.consentSatus,
                flow = response.flow,
                consentId = response.consentId,
                shareId = response.shareId,
                brand = response.brand,
                organizationId = response.organizationId,
                loggedUser = response.loggedUser,
                userName = response.userName,
                merchantId = response.merchantId,
                document = response.document,
                alteration = response.alteration,
                renovation = response.renovation,
                cancelation = response.cancelation,
                permissions = mapToPermissions(response.permissions),
                deadLine = mapToDeadLine(response.deadLine),
                confirmationDateTime = response.confirmationDateTime,
                expirationDateTime = response.expirationDateTime,
                lastUpdateDateTime = response.lastUpdateDateTime,
                authorizationServerId = response.authorizationServerId,
                logoUri = response.logoUri
            )
        }
        return null
    }

    private fun mapToPermissions(response: List<PermissionsResponse>?): List<Permissions>? {
        val permissions = mutableListOf<Permissions>()
        response.let {
            it?.forEach { permission ->
                val permissionBO = Permissions(
                    permissionCode = permission.permissionCode,
                    displayName = permission.displayName,
                    permissionDescription = permission.permissionDescription
                )
                permissions.add(permissionBO)
            }
            return permissions
        }
        return null
    }

    private fun mapToDeadLine(response: DeadLineResponse?): DeadLine? {
        response?.let {
            return DeadLine(
                total = response.total,
                type = response.type,
                expirationDate = response.expirationDate
            )
        }
        return null
    }

    fun mapToBank(response: List<BrandResponse>?): List<Brand>? {
        return response?.map { bankResponse ->
            Brand(
                brand = bankResponse.brand,
                institutions = mapToInstitutions(bankResponse.institutions)
            )
        }
    }

    private fun mapToInstitutions(response: List<InstitutionResponse>?): List<Institution>? {
        return response?.map { permission ->
            Institution(
                organizationId = permission.organizationId,
                organizationName = permission.organizationName,
                logoUri = permission.logoUri,
                authorizationServerId = permission.authorizationServerId,
                brandDescription = permission.brandDescription
            )
        }
    }

    fun mapToCreateShare(response: CreateShareResponse?): CreateShare? {
        response?.let {
            return CreateShare(
                authorizationServer = mapToAuthorizationServer(response.authorizationServer),
                deadLines = response.deadLines,
                shareId = response.shareId,
                resourceGroups = mapToResourceGroup(response.resourceGroups),
                userInformation = mapToUserInformation(response.userInformation)
            )
        }
        return null
    }

    private fun mapToAuthorizationServer(authorizationServer: AuthorizationServerResponse): AuthorizationServer {
        return AuthorizationServer(
                organizationId = authorizationServer.organizationId,
                logoUri = authorizationServer.logoUri,
                authorizationServerId = authorizationServer.authorizationServerId,
                customerFriendlyName = authorizationServer.customerFriendlyName
        )
    }

    private fun mapToPermission(permission: List<PermissionResponse>): List<Permission> {
        return permission.map { permissionResponse ->
            Permission(
                    displayName = permissionResponse.displayName,
                    detail = permissionResponse.detail,
                    required = permissionResponse.required,
                    permissionCode = permissionResponse.permissionCode
            )
        }
    }

    private fun mapToResourceGroup(resourceGroups: List<ResourceGroupResponse>): List<ResourceGroup> {
        return resourceGroups.map { resourceGroupResponse ->
            ResourceGroup(
                    displayName = resourceGroupResponse.displayName,
                    type = resourceGroupResponse.type,
                    permission = mapToPermission(resourceGroupResponse.permission)
            )
        }
    }

    private fun mapToUserInformation(userInformation: UserInformationResponse): UserInformation {
        return UserInformation(
            document = userInformation.document,
            typeDocument = userInformation.typeDocument
        )
    }

    fun mapToUpdateShare(response: UpdateShareResponse?): UpdateShare? {
        response?.let {
            return UpdateShare(response.redirectUri)
        }
        return null
    }

    fun mapToTermsOfUse(response: String?): TermsOfUse? {
        return response?.let { TermsOfUse(it) }
    }

    fun mapToConfirmShare(response: ConfirmShareResponse?): ConfirmShare? {
        return response?.let {
            ConfirmShare(
                response.consentId,
                response.customerFrindlyName,
                response.expirationDateTime,
                response.shareType
            )
        }
    }

    fun mapToGivenUpShare(response: GivenUpShareResponse?): GivenUpShare? {
        return response?.let {
            GivenUpShare(
                response.result,
                response.codeConsent,
                response.shareId
            )
        }
    }

    fun mapToChangeOrRenewShare(response: ChangeOrRenewShareResponse?): ChangeOrRenewShare? {
        return response?.let {
            ChangeOrRenewShare(
                response.consentId,
                response.customerFrindlyName,
                response.expirationDateTime,
                response.shareType
            )
        }
    }
}