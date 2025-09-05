package br.com.mobicare.cielo.commons.data.clients.local

import timber.log.Timber
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class UserPreferencesDelegate<T>(
    private val key: String,
    private val defaultValue: T,
    private val isProtected: Boolean
) : ReadWriteProperty<Any?, T> {

    private val userPreferences by lazy {
        UserPreferences.getInstance()
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val value =  when (defaultValue) {
            is Boolean -> userPreferences.get(key, defaultValue, isProtected) as T
            is Int -> userPreferences.get(key, defaultValue, isProtected) as T
            is Set<*> -> userPreferences.get(key, defaultValue as Set<String>, isProtected) as T
            is ArrayList<*> -> userPreferences.get(
                key,
                defaultValue as ArrayList<String>,
                isProtected
            ) as T

            is String -> userPreferences.get(key, defaultValue, isProtected) as T
            null -> userPreferences.get(key, defaultValue, isProtected) as T

            else -> throw Exception()
        }
        Timber.d("Getting value from preferences: $key - $value")
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        Timber.d("Setting value to preferences: $key - $value")
        when (value) {
            is Boolean -> userPreferences.put(key, value, isProtected)
            is Int -> userPreferences.put(key, value, isProtected)
            is Set<*> -> userPreferences.put(key, value as Set<String>, isProtected)
            is ArrayList<*> -> userPreferences.put(key, value as ArrayList<String>, isProtected)
            is String -> userPreferences.put(key, value, isProtected)
            null -> userPreferences.delete(key)
            else -> throw Exception()
        }
    }
}

fun <T> userPreferences(key: String, defaultValue: T, isProtected: Boolean? = null) =
    UserPreferencesDelegate(key, defaultValue, isProtected ?: false)