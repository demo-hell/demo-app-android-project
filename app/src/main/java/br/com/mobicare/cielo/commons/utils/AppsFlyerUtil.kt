package br.com.mobicare.cielo.commons.utils

import android.content.Context
import com.appsflyer.AppsFlyerLib

object AppsFlyerUtil {
    /**
     * Sends an AppsFlyer event without additional parameters.
     *
     * @param context The context from which the event is sent.
     * @param event The name of the event to send.
     */
    fun send(
        context: Context,
        event: String,
    ) {
        send(context, event, HashMap())
    }

    /**
     * Sends an AppsFlyer event with a single parameter.
     *
     * @param context The context from which the event is sent.
     * @param event The name of the event to send.
     * @param obj A pair representing the parameter's key and value.
     */
    fun send(
        context: Context,
        event: String,
        obj: Pair<String, String>,
    ) {
        val map =
            HashMap<String, Any>().apply {
                put(obj.first, obj.second)
            }
        send(context, event, map)
    }

    /**
     * Sends an AppsFlyer event with multiple parameters.
     *
     * @param context The context from which the event is sent.
     * @param event The name of the event to send.
     * @param map A map representing the parameters to send with the event.
     */
    fun send(
        context: Context,
        event: String,
        map: Map<String, Any>,
    ) {
        AppsFlyerLib.getInstance().logEvent(context, event, map)
    }
}

/**
 * Enum class for predefined AppsFlyer event names.
 */
enum class EventName(val description: String) {
    NAME("name"),
    CPF("cpf_continuar"),
    LOGIN("af_login"),
    ENTERBUTTON("entrar_app"),
}
