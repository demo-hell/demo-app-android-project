package br.com.mobicare.cielo.login.domains.entities

import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.login.domain.MultichannelLoginType
import br.com.mobicare.cielo.login.domains.entities.UserObj.RolePrefix.DIG
import br.com.mobicare.cielo.me.DigitalId
import br.com.mobicare.cielo.me.Profile
import com.google.gson.annotations.Expose

class UserObj {

    /**
     * "user": {
     * "nameLogin": "LUCAS",
     * "birthdayDate": 346302000000,
     * "cpf": "28275593824",
     * "rg": "21349159",
     * "name": "Lucas de Castro Santos",
     * "email": "vania.prieto@gmail.com",
     * "registerDate": 1408417200000,
     * "inactive": 1,
     * "lastAccessDate": 1494534437000,
     * "QuantityAttemptReset": 0,
     * "entity": 3613584,
     * "ec": "\u0000"
     * },
     */

    var nameLogin: String? = null
    var birthdayDate: String? = null
    var cpf: String? = null
    var rg: String? = null
    var name: String? = null
    var email: String? = null
    var registerDate: Double = 0.0
    var inactive: Int = 0
    var lastAccessDate: Double = 0.0
    var quantityAttemptReset: Int = 0
    var entity: Double = 0.0
    var ec: String? = null
    var impersonationEnabled: Boolean = false
    var activeMerchant: ActiveMerchantObj? = null
    var mainRole: String? = null
    var onboardingRequired: Boolean = false
    var digitalId: DigitalId? = null
    var roles: List<String> = emptyList()
    var profile: Profile? = null

    val isCNPJ: Boolean
        get() = ValidationUtils.isCNPJ(cpf)

    @Expose(serialize = false, deserialize = false)
    var multichannelLoginType: MultichannelLoginType = MultichannelLoginType.EC_NUMBER

    val isLockedProfile: Boolean
        get() = mainRole == TECHNICAL

    val isCustomRole: Boolean
        get() = mainRole == CUSTOM

    val customRoleName: String
        get() =
            if (isCustomRole)
                profile?.name?.ifEmpty { MainRoleEnum.CUSTOM.description }
                    ?: MainRoleEnum.CUSTOM.description
            else MainRoleEnum.CUSTOM.description


    fun isRoleAvailable(
        prefix: String = DIG,
        roleName: String,
        roleControlPermission: RoleControlPermission = RoleControlPermission.READ
    ): Boolean {
        if (isCustomRole.not()) {
            return true
        }

        return roles.any {
            it.lowercase().contains(prefix.lowercase())
                    && it.lowercase().contains(roleName.lowercase())
                    && it.lowercase().contains(roleControlPermission.name.lowercase())
        }
    }

    fun doWhenRoleAvailable(
        prefix: String = DIG,
        roleName: RoleControlResources,
        roleControlPermission: RoleControlPermission = RoleControlPermission.READ,
        doWhenAvailable: () -> Unit
    ) {
        if (isRoleAvailable(prefix, roleName.roleName, roleControlPermission)) {
            doWhenAvailable.invoke()
        }
    }

    object RolePrefix {
        const val DIG = "dig"
    }

    object Role {
        const val SALES = "vendas"
        const val RECEIVABLES = "recebiveis"
    }

    enum class RoleControlResources(val roleName: String) {
        SALES(Role.SALES),
        RECEIVABLES(Role.RECEIVABLES),
    }

    enum class RoleControlPermission {
        READ,
        WRITE
    }

    enum class MainRoleEnum(name: String, val description: String) {
        ADMIN(MainRole.ADMIN, "Administrador"),
        READER(MainRole.READER, "Leitor"),
        MASTER(MainRole.MASTER, "Proprietário"),
        ANALYST(MainRole.ANALYST, "Analista"),
        TECHNICAL(MainRole.TECHNICAL, "Técnico"),
        CUSTOM(MainRole.CUSTOM, "Personalizado")
    }

    companion object MainRole {
        const val ADMIN = "ADMIN"
        const val READER = "READER"
        const val MASTER = "master"
        const val ANALYST = "ANALYST"
        const val TECHNICAL = "TECHNICAL"
        const val CUSTOM = "CUSTOM"
    }
}