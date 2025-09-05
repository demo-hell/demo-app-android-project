package br.com.mobicare.cielo.login.domains.entities

import android.text.SpannableStringBuilder
import androidx.annotation.DrawableRes
import br.com.mobicare.cielo.commons.ui.widget.ClosableFullscrenDialog
import java.io.Serializable

open class CieloInfoDialogContent : Serializable {

    var windowTitle: String = ""
    var pageElements: MutableList<PageContent> = mutableListOf()


    open class PageContent : Serializable {
        var title : String  = ""
        @Transient var subTitle : SpannableStringBuilder = SpannableStringBuilder()

        var buttonLabel: String = ""

        @DrawableRes var imageDrawable: Int = -1

        @Transient var listener: ClosableFullscrenDialog.OnActionButtonClick? = null
    }

}