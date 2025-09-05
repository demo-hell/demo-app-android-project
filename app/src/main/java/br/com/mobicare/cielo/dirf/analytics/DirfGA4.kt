package br.com.mobicare.cielo.dirf.analytics

import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation.CONTENT_TYPE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Other.DIRF_DOWNLOAD
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Other.FILE_EXTENSION
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Other.FILE_NAME
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Other.LINK_TEXT
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Other.YEAR
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Share.SHARE_EVENT
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.extensions.toStringOrEmpty
import com.google.firebase.analytics.FirebaseAnalytics.Event.SCREEN_VIEW
import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking as ga4

class DirfGA4 {

    fun logScreenView(screenName: String) {
        ga4.trackScreenView(screenName)
    }

    fun logClickDirfDownload(
        format: String?,
        fileName: String,
        year: String,
        downloadFile: String
    ) {
        format?.let {
            ga4.trackEvent(
                eventName = DIRF_DOWNLOAD,
                eventsMap = mutableMapOf(
                    ScreenView.SCREEN_NAME to SCREEN_VIEW_DIRF,
                    FILE_EXTENSION to it,
                    FILE_NAME to fileName,
                    YEAR to year,
                    LINK_TEXT to downloadFile
                )
            )
        }

    }

    fun logScreenViewShare(format: String) {
        var URL = SCREEN_VIEW_DIRF_SUCESSO_COMPARTILHAR_FORMAT.replace("{{format}}", format)
        ga4.trackEvent(
            eventName = SCREEN_VIEW,
            eventsMap = mutableMapOf(
                ScreenView.SCREEN_NAME to URL
            )
        )
    }

    fun shareDirf(screenName: String, format: String) {
        ga4.trackEvent(
            eventName = SHARE_EVENT,
            eventsMap = mutableMapOf(
                ScreenView.SCREEN_NAME to screenName,
                CONTENT_TYPE to format
            )
        )
    }

    fun logException(screenName: String, error: ErrorMessage?) {
        val description = error?.let { it.message.ifEmpty { it.errorCode } }
            ?: EMPTY
        ga4.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to screenName,
                Exception.DESCRIPTION to description.normalizeToLowerSnakeCase(),
                Exception.STATUS_CODE to error?.httpStatus.toStringOrEmpty()
            )
        )
    }

    companion object {
        const val OUTROS = "outros"
        const val DIRF = "dirf"
        const val SUCESSO = "sucesso"
        const val SUCESSO_COMPARTILHAR = "sucesso_compartilhar"

        const val SCREEN_VIEW_DIRF = "/$OUTROS/$DIRF"
        var SCREEN_VIEW_DIRF_SUCESSO_COMPARTILHAR_FORMAT =
            "/$OUTROS/$DIRF/$SUCESSO_COMPARTILHAR/{{format}}"
        var SCREEN_VIEW_DIRF_SUCESSO_COMPARTILHAR = "/$OUTROS/$DIRF/$SUCESSO_COMPARTILHAR"
    }
}

