package br.com.mobicare.cielo.main.presentation.data.clients.local

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.login.domains.entities.EstabelecimentoObj
import br.com.mobicare.cielo.login.domains.entities.LoginObj
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.gson.Gson

class MenuPreference {

    private val gson = Gson()
    private var loginObj: LoginObj? = null

    private object Holder {
        internal val INSTANCE = MenuPreference()
    }

    /**
     * Salvar response do login

     * @param context
     */
    fun saveLoginObj(obj: LoginObj) {
        saveEstablishment(obj.establishment)

        updateMenu(obj)

        UserPreferences.getInstance().apply {
            put(key = LOGIN_JSON, value = gson.toJson(obj), isProtected = true)
            saveToken(obj.token)
            saveConvivenciaStatus(obj.isConvivenciaUser)
        }
    }

    private fun saveEstablishment(establishmentObj: EstabelecimentoObj?) {
        UserPreferences.getInstance().put(ESTABLISHMENT, value = gson.toJson(establishmentObj), isProtected = true)
    }

    fun getEC(): String? {
        if (getEstablishment() == null) {
            return null
        }
        return getEstablishment()?.ec
    }


    /**
     * Retorna o LoginObj

     * @param context
     * *
     * @return
     */
    fun getLoginObj(): LoginObj? {
        val json = UserPreferences.getInstance().get(key = LOGIN_JSON, defaultValue = EMPTY, isProtected = true)
        return gson.fromJson(json, LoginObj::class.java)
    }

    /**
     * Retorna o usuário do Login

     * @param context
     * *
     * @return
     */
    fun getUserObj(): UserObj? {
        return getLoginObj()?.user
    }

    private fun updateMenu(obj: LoginObj) {
        saveOfferStatus(obj.hasOffer)
    }

    /**
     * Retorna o estabelecimento do Login

     * @param context
     * *
     * @return
     */
    fun getEstablishment(): EstabelecimentoObj? {
        val establishment = UserPreferences.getInstance().get(key = ESTABLISHMENT, defaultValue = EMPTY, isProtected = true)
        return gson.fromJson(establishment,
                EstabelecimentoObj::class.java)
    }

    fun isMerchants(): Boolean {
        val loginObj = getLoginObj() ?: return false

        loginObj.user?.let {
            return it.impersonationEnabled
        } ?: run {
            return false
        }
    }

    /**
     * Verifica se o menu Produtos e Ofertas é para ser exibido

     * @param context
     * *
     * @return
     */
    fun showOffer(): Boolean {
        return false
    }

    private fun saveOfferStatus(status: Boolean) {
        UserPreferences.getInstance().put(OFFER, status)
    }

    /**
     * Apagar dados quando logout
     */
    fun logout() {
        UserPreferences.getInstance().put(key = LOGIN_JSON, value = EMPTY, isProtected = true)
        saveEstablishment(null)
        loginObj = null
    }

    companion object {
        private const val LOGIN_JSON = "login_json"
        private const val ESTABLISHMENT = "user_establishment"
        private const val SCHEDULE_ARV = "schedule_antecipation"
        private const val LOOSE_ARV = "loose_antecipation"
        private const val OFFER = "menu_offer"

        val instance: MenuPreference
            get() = Holder.INSTANCE
    }
}
