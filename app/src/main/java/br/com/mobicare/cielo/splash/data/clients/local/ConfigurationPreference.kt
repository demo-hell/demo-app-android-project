package br.com.mobicare.cielo.splash.data.clients.local

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.COMMA
import br.com.mobicare.cielo.commons.constants.SEPARATOR
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.orhanobut.hawk.Hawk
import br.com.mobicare.cielo.splash.domain.entities.Configuration
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.StringBuilder
import java.util.ArrayList

class ConfigurationPreference {

    fun saveConfig(obj: List<Configuration>) {
        saveConfiguration(obj)
        for ((_, key, value) in obj) {
            Hawk.put(key, value)
        }
    }

    private fun saveConfiguration(configuration: List<Configuration>) {
        Hawk.put(CONFIGURATION_PREFERENCES_VALUES, Gson().toJson(configuration))
    }

    val configurationValues: List<Configuration>?
        get() {
            return try {
                val json = Hawk.get<String?>(CONFIGURATION_PREFERENCES_VALUES, null)

                if (json != null) {
                    Gson().fromJson(json, object : TypeToken<List<Configuration?>?>() {}.type)
                } else null
            } catch (e: Exception) {
                e.message.logFirebaseCrashlytics()
                saveConfig(listOf())
                null
            }
        }

    fun getConfigurationValue(
        name: String,
        defaultValue: String
    ): String {
        var localDefaultValue = defaultValue
        if (name.equals(ConfigurationDef.URL_LOGIN_CRIAR_USUARIO, ignoreCase = true)) {
            localDefaultValue = CREATE_USER_URL_DEFAULT
        } else if (name.equals(ConfigurationDef.URL_LOGIN_QUERO_SER_CLIENTE, ignoreCase = true)) {
            localDefaultValue =
                WANT_TO_BE_COSTUMER_URL_DEFAULT
        }
        return Hawk.get(name, localDefaultValue)
    }

    fun allSupportedBrandsImageUrls(context: Context): List<String> {
        val cardUrlImagesString = instance
            .getConfigurationValue(
                ConfigurationDef.ARRAY_CARD_IMAGES,
                context.getString(R.string.text_default_image_values)
            )
        val listOfImageUrls = cardUrlImagesString.split(COMMA.toRegex()).toTypedArray()
        val mappedUrls: MutableList<String> = ArrayList()
        for (currUri in listOfImageUrls) {
            val sb = StringBuilder()
            mappedUrls.add(
                sb.append(BuildConfig.RESOURCES_URL)
                    .append(currUri.replaceFirst(SEPARATOR.toRegex(), EMPTY))
                    .toString()
            )
        }
        return mappedUrls
    }

    companion object {
        private const val CONFIGURATION_PREFERENCES_VALUES = "CONFIGURATION_PREFERENCES_VALUES"
        val instance = ConfigurationPreference()
        private const val CREATE_USER_URL_DEFAULT = "https://www.cielo.com.br/seja-nosso-cliente/"
        private const val WANT_TO_BE_COSTUMER_URL_DEFAULT = "https://www.cielo.com.br/home/index.html?utm_codprod=027393&utm_campaign=app_cliente_cielo-cielo&utm_medium=display&utm_source=app-home&utm_content=rodape-roxo&utm_term=iddqFq9G"
    }
}