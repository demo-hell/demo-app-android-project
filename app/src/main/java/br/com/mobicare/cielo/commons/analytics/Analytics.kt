package br.com.mobicare.cielo.commons.analytics

import android.os.Bundle
import androidx.core.os.bundleOf
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Other.ITEMS
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.UserAndImpersonate.COMPANY_ID
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.UserAndImpersonate.COMPANY_VIEW_TYPE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.UserAndImpersonate.ESTABLISHMENT_ID
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.UserAndImpersonate.USER_PROFILE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.UserAndImpersonate.USER_TYPE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.exceptionsUpperCase
import br.com.mobicare.cielo.commons.analytics.Property.COMPANY_CODE
import br.com.mobicare.cielo.commons.analytics.Property.USER_CPF
import br.com.mobicare.cielo.commons.analytics.Property.VIEW_TYPE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.analytics.consoleLogEvent
import br.com.mobicare.cielo.commons.utils.analytics.createUserPropertiesBundle
import br.com.mobicare.cielo.commons.utils.analytics.formatScreenName
import br.com.mobicare.cielo.commons.utils.analytics.join
import br.com.mobicare.cielo.commons.utils.removeNonNumbers
import br.com.mobicare.cielo.commons.utils.security.toSha256
import br.com.mobicare.cielo.commons.utils.toLowerNoAccents
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking as GA4Tracking

/**
 * This object handles all tagging logic for event tracking and screen views using Firebase Analytics.
 * It provides methods to track events, errors, and screen views BEFORE and AFTER the GA4 update.
 */
object Analytics {
    val analytics = Firebase.analytics
    private var userPreferences = UserPreferences.getInstance()
    private var menuPreferences = MenuPreference.instance

    private val oldUserProperties: HashMap<String?, String?> = HashMap()
    private val oldEventProperties = listOf(
        COMPANY_CODE,
        Property.USER_CPF,
        Property.EC,
        Property.USER_TYPE,
        Property.VIEW_TYPE
    )

    /**
     * Tracks an event before the GA4 update.
     * @param category A list of strings representing the event category.
     * @param action A list of strings representing the event action.
     * @param label A list of strings representing the event label.
     * @param extras A variable number of extra key-value pairs to include in the event.
     */
    fun trackEvent(
        category: List<String?>? = null,
        action: List<String?>? = null,
        label: List<String?>? = null,
        vararg extras: Pair<String, String>,
        normalize: Boolean = true
    ) {
        val params = createUserPropertiesBundle(oldUserProperties, oldEventProperties.toMutableList())

        category.join(normalize)?.let { itCategory -> params.putString(EVENT_CATEGORY, itCategory) }
        action.join(normalize)?.let { itAction -> params.putString(EVENT_ACTION, itAction) }
        label.join(normalize)?.let { itLabel -> params.putString(EVENT_LABEL, itLabel) }

        if (extras.isEmpty().not()) {
            params.putAll(bundleOf(*extras))
        }

        logEvent(params, oldUserProperties)
    }

    private fun logEvent(
        params: Bundle,
        propertiesMap: HashMap<String?, String?>,
        eventName: String = EVENT
    ) {
        analytics.logEvent(eventName, params)
        consoleLogEvent(eventName, params, propertiesMap)
    }

    fun trackError(location: String = EMPTY, error: ErrorMessage) {
        listOf(
            error.httpStatus.toString(),
            error.statusText.orEmpty(),
            error.brokenServiceUrl,
            error.message
        ).join()?.let { errorString ->
            if (errorString.isNotEmpty()) {
                analytics.logEvent(ERROR_LOWERCASE) {
                    param(ERROR_LOCATION, location)
                    param(ERROR_DATA, errorString)
                }
            }
        }
    }

    fun trackScreenView(screenClass: Class<Any>) {
        trackScreenView(screenClass.simpleName, screenClass)
    }

    fun trackScreenView(screenName: String, screenClass: Class<Any>? = null) {
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass?.simpleName)
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
        consoleLogEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params, oldUserProperties)
    }

    object GoogleAnalytics4Tracking {
        val userProperties: HashMap<String?, String?> = HashMap()
        private val eventProperties = listOf(
            COMPANY_ID,
            ESTABLISHMENT_ID,
            COMPANY_VIEW_TYPE,
            USER_TYPE,
            USER_PROFILE,
            COMPANY_CODE,
            Property.USER_CPF,
            Property.EC,
            Property.USER_TYPE,
            Property.VIEW_TYPE
        )

        /**
         * Tracks an event after the GA4 update.
         * @param eventName The name of the event.
         * @param eventsMap A map of event parameters.
         * @param eventsList A list of additional event bundles.
         * @param isLoginOrImpersonateFlow Indicates if the event is part of a login or impersonation flow.
         * This flag is important because it determines whether additional parameters like PERSON_ID, USER_TYPE, and USER_PROFILE need to be passed.
         */
        fun trackEvent(
            eventName: String,
            eventsMap: Map<String, Any>,
            eventsList: ArrayList<Bundle>? = null,
            isLoginOrImpersonateFlow: Boolean = false
        ) {
            val params = createUserPropertiesBundle(
                userProperties,
                eventProperties.toMutableList(),
                isLoginOrImpersonateFlow
            )

            eventsMap.forEach { (key, value) ->
                when (value) {
                    is Double -> params.putDouble(key, value)
                    is Int -> params.putInt(key, value)
                    is Boolean -> params.putBoolean(key, value)
                    is Float -> params.putFloat(key, value)
                    is Long -> params.putLong(key, value)
                    is String -> {
                        val finalValue = if (key == ScreenView.SCREEN_NAME) {
                            value.formatScreenName()
                        } else {
                            value
                        }

                        params.putString(key, if (exceptionsUpperCase.contains(finalValue)) finalValue else finalValue.toLowerCasePTBR())
                    }
                }
            }

            eventsList?.let {
                val items = it.toTypedArray()

                if (items.first().isEmpty.not()) {
                    params.putParcelableArray(ITEMS, it.toTypedArray())
                }
            }

            analytics.logEvent(eventName, params)
            showPropertiesOnLogcat(eventName, userProperties, params)
        }

        private fun showPropertiesOnLogcat(
            eventName: String,
            userProperties: HashMap<String?, String?>,
            eventProperties: Bundle
        ) {
            fun HashMap<String?, String?>.prepareEventProperties() {
                eventProperties.putString(COMPANY_ID, get(COMPANY_ID))
                eventProperties.putString(COMPANY_CODE, get(COMPANY_CODE))
                eventProperties.putString(VIEW_TYPE, get(VIEW_TYPE))
                eventProperties.putString(COMPANY_VIEW_TYPE, get(COMPANY_VIEW_TYPE))
                eventProperties.putString(ESTABLISHMENT, get(ESTABLISHMENT))
                eventProperties.putString(ESTABLISHMENT_ID, get(ESTABLISHMENT_ID))

                eventProperties.remove(Property.USER_TYPE)
                eventProperties.remove(USER_PROFILE)
                eventProperties.remove(USER_TYPE)
            }

            fun HashMap<String?, String?>.prepareUserProperties() {
                remove(VIEW_TYPE)
                remove(COMPANY_VIEW_TYPE)
                remove(COMPANY_ID)
                remove(COMPANY_CODE)
                remove(USER_CPF)
                remove(ESTABLISHMENT_ID)
                remove(ESTABLISHMENT)
            }

            (userProperties.clone() as HashMap<String?, String?>).apply {
                prepareEventProperties()
                prepareUserProperties()

                consoleLogEvent(eventName, this, eventProperties)
            }
        }

        /**
         * Tracks a screen view event after the GA4 update.
         * @param screenName The name/path of the screen.
         * @param isLoginOrImpersonateFlow Indicates if the event is part of a login or impersonation flow.
         * This flag is important because it determines whether additional parameters like PERSON_ID, USER_TYPE, and USER_PROFILE need to be passed.
         */
        fun trackScreenView(screenName: String, isLoginOrImpersonateFlow: Boolean = false) {
            trackEvent(
                eventName = ScreenView.SCREEN_VIEW_EVENT,
                eventsMap = mapOf(
                    ScreenView.SCREEN_NAME to screenName.formatScreenName()
                ),
                isLoginOrImpersonateFlow = isLoginOrImpersonateFlow
            )
        }
    }

    object Update {
        fun updateUserProperties() {
            updateCPF(
                listOf(
                    Pair(Property.USER_CPF, oldUserProperties),
                )
            )

            updateCompanyCode(
                COMPANY_CODE,
                COMPANY_ID
            )

            updateEC(
                listOf(
                    Pair(Property.EC, oldUserProperties),
                    Pair(ESTABLISHMENT_ID, GA4Tracking.userProperties)
                )
            )

            updateViewType(
                listOf(
                    Pair(Property.VIEW_TYPE, oldUserProperties),
                    Pair(COMPANY_VIEW_TYPE, GA4Tracking.userProperties)
                )
            )

            updateUserProfile()
            updateCustomDimensions()
        }

        private fun updateCPF(documentInfo: List<Pair<String, HashMap<String?, String?>>>) {
            documentInfo.forEach { (propertyName, _) ->
                setUserProperty(
                    propertyName,
                    userPreferences.currentUserLogged?.cpf?.dropLast(TWO)
                )
            }
        }

        private fun updateCompanyCode(vararg companyInfo: String) {
            val companyCode = userPreferences.userInformation?.activeMerchant?.cnpj?.number?.removeNonNumbers()?.toSha256()

            companyInfo.forEach { info ->
                setUserProperty(info, companyCode)
            }
        }

        private fun updateEC(companyInfo: List<Pair<String, HashMap<String?, String?>>>) {
            companyInfo.forEach { (propertyName, _) ->
                setUserProperty(
                    propertyName,
                    userPreferences.ecUserLogged
                )
            }
        }

        private fun updateViewType(companyInfo: List<Pair<String, HashMap<String?, String?>>>) {
            companyInfo.forEach { (propertyName, _) ->
                setUserProperty(
                    propertyName,
                    userPreferences.userInformation?.activeMerchant?.hierarchyLevel?.toLowerCasePTBR(),
                )
            }
        }

        private fun updateUserProfile() {
            menuPreferences.getUserObj()?.mainRole?.let {
                setUserProperty(
                    USER_PROFILE,
                    it.toLowerNoAccents()
                )
            }
        }

        private fun updateCustomDimensions() {
            updateDimension5()
            updateDimension9()
        }

        private fun updateDimension5() {
            val dimension5Value = if (ValidationUtils.isCNPJ(userPreferences.userInformation?.activeMerchant?.cnpj?.number)) PJ else PF
            setUserProperty(dimension5Value, Custom.DIMENSION_5)
        }

        private fun updateDimension9() {
            setUserProperty(Custom.DIMENSION_9, userPreferences.ecUserLogged)
        }

        fun updateUserType(isInternal: Boolean) {
            val type = if (isInternal) USER_INTERNAL else USER_COMMON
            setUserProperty(Property.USER_TYPE, type)
            setUserProperty(USER_TYPE, type)
        }

        fun updateUserId() {
            val id = userPreferences.userInformation?.id

            setUserId(id)
        }

        private fun setUserId(userId: String?) {
            val cpfWithSha256 = userPreferences.currentUserLogged?.cpf?.removeNonNumbers()?.toSha256()

            analytics.setUserId(cpfWithSha256)

            oldUserProperties[USER_ID] = userId
            GA4Tracking.userProperties[USER_ID] = cpfWithSha256
        }

        fun setUserProperty(
            key: String,
            value: String?
        ) {
            analytics.setUserProperty(key, value)

            GA4Tracking.userProperties[key] = value
            oldUserProperties[key] = value
        }
    }
}
