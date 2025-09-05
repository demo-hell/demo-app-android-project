package br.com.mobicare.cielo.webView.presentation

import android.os.Build
import androidx.lifecycle.ViewModel
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.webView.utils.ACCESS_TOKEN
import br.com.mobicare.cielo.webView.utils.ANDROID
import br.com.mobicare.cielo.webView.utils.APP_BUILD
import br.com.mobicare.cielo.webView.utils.APP_RELEASE_VERSION
import br.com.mobicare.cielo.webView.utils.AUTHORIZATION
import br.com.mobicare.cielo.webView.utils.BEARER
import br.com.mobicare.cielo.webView.utils.CPF
import br.com.mobicare.cielo.webView.utils.DEBUG
import br.com.mobicare.cielo.webView.utils.DEVICE_MODEL
import br.com.mobicare.cielo.webView.utils.EC_CNPJ
import br.com.mobicare.cielo.webView.utils.EC_CNPJ_ROOT
import br.com.mobicare.cielo.webView.utils.EC_HIERARCHY_LEVEL
import br.com.mobicare.cielo.webView.utils.EC_ID
import br.com.mobicare.cielo.webView.utils.EC_NAME
import br.com.mobicare.cielo.webView.utils.EC_RECEIVABLE_TYPE
import br.com.mobicare.cielo.webView.utils.EC_TRADING_NAME
import br.com.mobicare.cielo.webView.utils.EMAIL
import br.com.mobicare.cielo.webView.utils.MAIN_ROLE
import br.com.mobicare.cielo.webView.utils.OS
import br.com.mobicare.cielo.webView.utils.PRD
import br.com.mobicare.cielo.webView.utils.USERNAME

class WebViewContainerViewModel(userPreferences: UserPreferences, menuPreference: MenuPreference): ViewModel() {

    private val userToken = userPreferences.token
    private val username = userPreferences.currentUserName.orEmpty()
    private val email = userPreferences.userInformation?.email.orEmpty()
    private val cpf = menuPreference.getUserObj()?.cpf.orEmpty()
    private val mainRole = menuPreference.getUserObj()?.profile?.name?.lowercase().orEmpty()
    private val tradingName = userPreferences.userInformation?.activeMerchant?.tradingName.orEmpty()
    private val cnpj = menuPreference.getEstablishment()?.cnpj.orEmpty()
    private val cnpjRoot = userPreferences.userInformation?.activeMerchant?.cnpj?.rootNumber.orEmpty()
    private val hierarchyLevel = userPreferences.userInformation?.activeMerchant?.hierarchyLevel.orEmpty()
    private val id = userPreferences.userInformation?.activeMerchant?.id.orEmpty()
    private val name = userPreferences.userInformation?.activeMerchant?.name.orEmpty()
    private val receivableType = userPreferences.userInformation?.activeMerchant?.receivableType.orEmpty()

    fun getWebViewCacheParams(): HashMap<String, String> {
        return hashMapOf(
            ACCESS_TOKEN to userToken,
            AUTHORIZATION to "$BEARER $userToken",
            APP_BUILD to if (BuildConfig.DEBUG) DEBUG else PRD,
            OS to ANDROID,
            APP_RELEASE_VERSION to BuildConfig.VERSION_NAME,
            USERNAME to username,
            EMAIL to email,
            CPF to cpf,
            MAIN_ROLE to mainRole,
            EC_TRADING_NAME to tradingName,
            EC_CNPJ to cnpj,
            EC_CNPJ_ROOT to cnpjRoot,
            EC_HIERARCHY_LEVEL to hierarchyLevel,
            EC_ID to id,
            EC_NAME to name,
            EC_RECEIVABLE_TYPE to receivableType,
            DEVICE_MODEL to Build.MANUFACTURER + " " + Build.MODEL
        )
    }

    fun getWebViewLocalParams(): HashMap<String, String> {
        return hashMapOf()
    }
}