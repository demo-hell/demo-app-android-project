package br.com.mobicare.cielo.commons.utils.crashlytics

import com.google.firebase.crashlytics.FirebaseCrashlytics

fun String?.logFirebaseCrashlytics() {
    this?.let { error ->
        FirebaseCrashlytics.getInstance().log(error)
    }
}