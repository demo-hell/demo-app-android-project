package br.com.mobicare.cielo.commons.utils

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import br.com.mobicare.cielo.commons.constants.THIRTY_THREE
import java.io.Serializable

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? =
    if (SDK_INT >= THIRTY_THREE)
        getParcelable(key, T::class.java)
    else
        @Suppress("DEPRECATION") getParcelable(key) as? T

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? =
    if (SDK_INT >= THIRTY_THREE)
        getSerializable(key, T::class.java)
    else
        @Suppress("DEPRECATION") getSerializable(key) as? T
