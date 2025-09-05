package br.com.mobicare.cielo.openFinance.presentation.utils

import android.app.Activity
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.Utils

object FlowReturnHolder{
    private val userPreferences: UserPreferences = UserPreferences.getInstance()

    private fun deleteHolderIntentId() {
        userPreferences.deleteHolderIntentId()
    }

    private fun deleteHolderRedirectUri() {
        userPreferences.deleteHolderRedirectUri()
    }

    fun getRedirectUriDetentora(): String? {
        return userPreferences.holderRedirectUri
    }

    fun deleteParamsHolder() {
        deleteHolderIntentId()
        deleteHolderRedirectUri()
    }

    fun flowReturnHolder(activity: Activity) {
        Utils.openBrowser(activity, getRedirectUriDetentora())
        deleteParamsHolder()
    }
}