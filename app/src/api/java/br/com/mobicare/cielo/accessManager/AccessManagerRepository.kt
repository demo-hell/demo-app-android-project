package br.com.mobicare.cielo.accessManager

import br.com.mobicare.cielo.accessManager.addUser.model.AccessManagerSendInviteRequest
import br.com.mobicare.cielo.accessManager.addUser.model.AccessManagerValidateCpfRequest
import br.com.mobicare.cielo.accessManager.addUser.model.AccessManagerValidateEmailRequest
import br.com.mobicare.cielo.accessManager.model.AccessManagerAssignRoleRequest
import br.com.mobicare.cielo.accessManager.model.AccessManagerUnlinkUserRequest
import br.com.mobicare.cielo.accessManager.model.ForeignUserDecisionRequest
import br.com.mobicare.cielo.changeEc.domain.HierachyResponse
import br.com.mobicare.cielo.commons.constants.ONE
import io.reactivex.Observable

private const val PAGE_SIZE = 100

class AccessManagerRepository(private val api: AccessManagerApi) {
    fun getNoRoleUsers() = api.getNoRoleUsers()
    fun getUsersWithRole() = api.getUsersWithRole()
    fun getCustomUsersWithRole(customProfilesOnly: Boolean, profileId: String) =
        api.getCustomUsersWithRole(customProfilesOnly = customProfilesOnly, profileId = profileId)

    fun getProfiles(profileType: String, status: String) =
        api.getProfiles(profileType = profileType, status = status, fetchDetails = true)

    fun getProfileDetail(profileId: String) =
        api.getProfileDetail(profileId)

    fun assignRole(
            idList: List<String>,
            role: String,
            otpCode: String
    ) = api.assignRole(
            idList.map {
                AccessManagerAssignRoleRequest(it, role)
            },
            otpCode
    )

    fun sendInvite(otp: String, request: AccessManagerSendInviteRequest) =
        api.sendIvite(
            request,
            otp
        )

    fun children(
        token: String,
        pageSize: Int?,
        pageNumber: Int?,
        searchCriteria: String?
    ): Observable<HierachyResponse> {
        return api.children(token, pageSize, pageNumber, searchCriteria)
    }

    fun validateCpf(
        accessManagerValidateCpfRequest:
        AccessManagerValidateCpfRequest,
        token: String
    ) = api.validateCpf(token, accessManagerValidateCpfRequest)

    fun validateEmail(
        accessManagerValidateEmailRequest:
        AccessManagerValidateEmailRequest,
        token: String
    ) = api.validateEmail(token, accessManagerValidateEmailRequest)

    fun getCountries(
        token: String
    ) = api.getCountries(token)

    fun getCountriesDDI(
        token: String
    ) = api.getCountriesDDI(token)

    fun unlinkUser(
        userId: String,
        reason: UnlinkUserReason,
        otpCode: String
    ) = api.unlinkUser(listOf(AccessManagerUnlinkUserRequest(userId, reason.name)), otpCode)

    fun resendInvite(expiredInvites: List<String>, otpCode: String) =
        api.resendInvite(otpCode, expiredInvites)

    fun getExpiredInvites(pageSize: Int = PAGE_SIZE, pageNumber: Int = ONE) =
        api.getExpiredInvites(pageNumber = pageNumber, pageSize = pageSize, expired = true)

    fun acceptInvite(inviteId: String) = api.acceptInvite(inviteId)

    fun acceptInviteToken(inviteToken: String) = api.acceptInviteToken(inviteToken)

    fun declineInviteToken(inviteToken: String) = api.declineInviteToken(inviteToken)

    fun declineInviteLoggedToken(inviteId: String) = api.declineInviteLoggedToken(inviteId)

    fun getInviteDetails(inviteToken: String) = api.getInviteDetails(inviteToken)

    fun getPendingInvites() = api.getPendingInvites()

    fun deleteInvite(expiredInvites: List<String>, otpCode: String) = api.deleteInvite(otpCode, expiredInvites)

    fun getPendingForeignUsers(pageSize: Int = PAGE_SIZE, pageNumber: Int = ONE) =
        api.getPendingForeignUsers(pageNumber = pageNumber, pageSize = pageSize)

    fun getForeignUserDetail(userId: String) = api.getForeignUserDetail(userId)

    fun sendForeignUserDecision(otpCode: String, userId: String, decision: String) =
        api.sendForeignUserDecision(otpCode, ForeignUserDecisionRequest(userId, decision))

    fun getIdOnboardingCustomerSettings() = api.getCustomerSettings()
}