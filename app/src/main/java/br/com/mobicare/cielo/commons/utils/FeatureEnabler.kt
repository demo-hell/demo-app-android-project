package br.com.mobicare.cielo.commons.utils

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference

//Classe que abstrai os conceitos de feature toggle e roles
object FeatureEnabler {

    object UserPermissions {
        const val MASTER = "MASTER"
        const val EFETIVAR_CANCELAMENTO = "EFETIVAR_CANCELAMENTO"
        const val EDITAR_DADOS_CADASTRAIS = "EDITAR_DADOS_CADASTRAIS"
        const val INCLUIR_DOMICILIO_BANCARIO = "INCLUIR_DOMICILIO_BANCARIO"
    }

    fun isFeatureEnabledByFeatureToggleAndPermissions(featureName: String,
                                                      userPermissionName: String): Boolean {

        val userActionPermissions = UserPreferences.getInstance()
                .userActionPermissions

        return userActionPermissions.contains(userPermissionName) &&
                FeatureTogglePreference.instance.getFeatureTogle(featureName)
    }

    fun isFeatureToggleEnabled(featureName: String): Boolean {
        return FeatureTogglePreference.instance.getFeatureTogle(featureName)
    }

}