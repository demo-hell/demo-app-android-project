package br.com.mobicare.cielo.me

import androidx.annotation.Keep

@Keep
data class MeResponse(
    val id: String?,
    val activeMerchant: ActiveMerchant,
    val alternateEmail: String,
    val birthDate: String,
    val email: String,
    val identity: Identity?,
    val impersonating: Boolean,
    val impersonationEnabled: Boolean,
    val lastLoginDate: String,
    val lastModificationDate: String,
    val login: String,
    val merchant: Merchant?,
    val nationality: String,
    val phoneNumber: String,
    val roles: List<String>,
    val successLoginAttempts: String,
    val username: String,
    val onboardingRequired: Boolean?,
    val mainRole: String?,
    val digitalId: DigitalId?,
    val status: String?,
    val profile: Profile,
    val representative: Boolean = false
)

@Keep
data class Merchant(
    val cnpj: CnpjX?,
    val hierarchyLevel: String,
    val id: String,
    val individual: Boolean,
    val name: String?,
    val receivableType: String,
    val tradingName: String?
)

@Keep
data class Identity(
    val cpf: String?,
    val foreigner: Boolean,
    val foreignerId: String,
    val rg: String
)

@Keep
data class CnpjX(
    val branchNumber: String,
    val digit: String,
    val number: String?,
    val rootNumber: String
)

@Keep
data class Cnpj(
    val branchNumber: String,
    val digit: String,
    val number: String?,
    val rootNumber: String
)

@Keep
data class ActiveMerchant(
    val cnpj: Cnpj?,
    val hierarchyLevel: String?,
    val id: String,
    val individual: Boolean,
    val name: String,
    val receivableType: String,
    val tradingName: String?,
    val migrated: Boolean? = false
)

@Keep
data class DigitalId(
    val deadline: String?,
    val expired: Boolean?,
    val mandatory: Boolean?,
    val migrated: Boolean?,
    val notApproved: Boolean?,
    val p1Approved: Boolean?,
    val p2Approved: Boolean?,
    val status: String?
)

@Keep
data class Profile (
    var id: String?,
    val name: String?,
    val custom: Boolean?,
    val p2Eligible: Boolean?
)