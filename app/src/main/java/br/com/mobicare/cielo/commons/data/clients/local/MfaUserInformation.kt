package br.com.mobicare.cielo.commons.data.clients.local

import br.com.mobicare.cielo.mfa.activation.repository.PutValueResponse
import br.com.mobicare.cielo.mfa.model.MfaUser
import br.com.mobicare.cielo.mfa.token.CieloMfaTokenGenerator
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.gson.GsonBuilder

private const val MFA_CURRENT_USER_KEY = "br.com.cielo.mfa.keys"

class MfaUserInformation(private val userPreferences: UserPreferences) {

    fun getMfaUser(key: String?): MfaUser? {
        key?.let {
            val mfaUser: MfaUser? = GsonBuilder().create().fromJson(
                userPreferences.get(
                    key = key,
                    defaultValue = EMPTY,
                    isProtected = true
                ),
                MfaUser::class.java
            )

            if (mfaUser?.mfaSeed != null && CieloMfaTokenGenerator.seedHasCorrectPattern(mfaUser.mfaSeed).not()) {
                cleanMfaRegisters()
            }

            return mfaUser
        }

        return null
    }

    fun saveMfaUserInformation(putValueResponse: PutValueResponse) {
        userPreferences.apply {
            userInformation?.let { userInformation ->
                saveMfaUserInformation(
                    MfaUser(
                        userInformation.id,
                        userName,
                        userInformation.email,
                        userInformation.identity?.cpf,
                        numeroEC,
                        putValueResponse.card
                    )
                )
            }
        }
    }

    private fun saveMfaUserInformation(mfaUserInformation: MfaUser) {
        val mfaUserJson = GsonBuilder().create().toJson(
            mfaUserInformation,
            MfaUser::class.java
        )

        userPreferences.put(key = MFA_CURRENT_USER_KEY, value = mfaUserInformation.id, isProtected = true)

        mfaUserInformation.usernameInLogin?.let { username ->
            saveAllMfaUserInformation(username, mfaUserJson)
        }
        mfaUserInformation.ec?.let { ec ->
            saveAllMfaUserInformation(ec, mfaUserJson)
        }
        mfaUserInformation.cpf?.let { cpf ->
            saveAllMfaUserInformation(cpf, mfaUserJson)
        }
        mfaUserInformation.email?.let { email ->
            saveAllMfaUserInformation(email, mfaUserJson)
        }
        mfaUserInformation.id?.let { id ->
            saveAllMfaUserInformation(id, mfaUserJson)
        }
    }

    fun hasActiveMfaUser(): Boolean {
        userPreferences.userInformation?.id?.let {
            return isMfaUserActive(it)
        }

        return false
    }

    private fun saveAllMfaUserInformation(data: String, json: String) {
        userPreferences.put(key = data, value = json, isProtected = true)
    }

    private fun isMfaUserActive(mfaUserId: String): Boolean {
        return lastMfaUserId().isNotEmpty() && mfaUserId != lastMfaUserId()
    }

    private fun lastMfaUserId(): String {
        return userPreferences.get(key = MFA_CURRENT_USER_KEY, defaultValue = EMPTY, isProtected = true) ?: EMPTY
    }

    private fun deleteMfaUserInformation(mfaUser: MfaUser?) {
        userPreferences.apply {
            delete(key = mfaUser?.cpf)
            delete(key = mfaUser?.email)
            delete(key = mfaUser?.ec)
            delete(key = mfaUser?.usernameInLogin)
            delete(key = mfaUser?.id)
        }
    }

    fun getCurrentMfaUser(): MfaUser? {
        val user = userPreferences.userInformation
        val key = user?.identity?.cpf ?: user?.id ?: user?.email

        return getMfaUser(key)
    }

    fun cleanMfaRegisters() {
        val currentUserKey =  userPreferences.get(MFA_CURRENT_USER_KEY, null, isProtected = true)

        currentUserKey?.let {
            val currentMfaUserJson = userPreferences.get(it, null, isProtected = true)
            val currentMfaUser = GsonBuilder().create().fromJson(currentMfaUserJson, MfaUser::class.java)

            deleteMfaUserInformation(currentMfaUser)
        }
    }
}