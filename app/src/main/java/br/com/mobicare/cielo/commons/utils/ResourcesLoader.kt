package br.com.mobicare.cielo.commons.utils

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.CieloApplication

class ResourcesLoader private constructor() {

    companion object {
        val instance = ResourcesLoader()
    }

    fun getString(@StringRes id: Int, vararg formatArgs: Any): String =
            CieloApplication.context!!.getString(id, formatArgs)

    fun getDrawable(@DrawableRes id: Int) : Drawable? {
        var drawable: Drawable? = null
        CieloApplication.context?.let {
            drawable = ContextCompat.getDrawable(it, id)
        }
        return drawable
    }

}