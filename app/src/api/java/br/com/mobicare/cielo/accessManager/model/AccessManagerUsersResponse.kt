package br.com.mobicare.cielo.accessManager.model

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.login.domains.entities.UserObj
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class AccessManagerUser(
    val id: String?,
    val cpf: String?,
    val name: String?,
    val profile: Profile? = null,
    val inWhitelist: Boolean?,
    val mainRole: String?,
    val status: String?,
    val email: String?,
    val cellphone: String?,
    val statusToken: String?,
    val representative: Boolean = false
): Parcelable {

    fun statusDescription(): String {
        return when(status) {
            Status.ATIVO.name -> Status.ATIVO.description
            Status.EM_CRIACAO.name -> Status.EM_CRIACAO.description
            Status.BLOQUEADO.name -> Status.BLOQUEADO.description
            else -> ""
        }
    }

    fun mainRoleDescription(): String {
        return when(mainRole) {
            UserObj.MainRoleEnum.ADMIN.name -> UserObj.MainRoleEnum.ADMIN.description
            UserObj.MainRoleEnum.READER.name -> UserObj.MainRoleEnum.READER.description
            UserObj.MainRoleEnum.MASTER.name -> UserObj.MainRoleEnum.MASTER.description
            else -> ""
        }
    }

    enum class Status(name: String, val description: String) {
        ATIVO("ATIVO", "Ativo"),
        EM_CRIACAO("EM_CRIACAO", "Em criação"),
        BLOQUEADO("BLOQUEADO", "Bloqueado"),
    }
}

@Keep
@Parcelize
data class Profile (
    var id: String?,
    val name: String?,
    val roles: List<String>?,
    val custom: Boolean? = false,
    val p2Eligible: Boolean? = false,
    val legacy: Boolean? = false,
    val admin: Boolean? = false
): Parcelable

