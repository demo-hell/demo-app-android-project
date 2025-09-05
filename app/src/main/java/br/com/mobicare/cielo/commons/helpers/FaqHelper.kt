package br.com.mobicare.cielo.commons.helpers

import android.app.Activity
import android.content.Context
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUBCATEGORY_NAME
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.domain.MenuTarget
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.jetbrains.anko.startActivity

fun Activity?.openFaq(tag: String, subCategoryName: String, notCameFromHelpCenter: Boolean = true) {
    this?.startActivity<CentralAjudaSubCategoriasEngineActivity>(
        ConfigurationDef.TAG_KEY_HELP_CENTER to tag,
        ARG_PARAM_SUBCATEGORY_NAME to subCategoryName,
        CentralAjudaSubCategoriasEngineActivity.NOT_CAME_FROM_HELP_CENTER to notCameFromHelpCenter
    )
}

fun Context.goToHelpCenterHome() {
    Router.navigateTo(this, Menu(
        Router.APP_ANDROID_HELP_CENTER, EMPTY, listOf(),
        getString(R.string.text_body_dirf_02), false, EMPTY,
        listOf(), show = false, showItems = false, menuTarget = MenuTarget(
            false,
            type = EMPTY, mail = EMPTY, url = EMPTY
        )
    ), object : Router.OnRouterActionListener {
        override fun actionNotFound(action: Menu) {
            FirebaseCrashlytics
                .getInstance()
                .recordException(Throwable(HomeAnalytics.ERROR_ON_OPEN_HELP_CENTER_TAG))
        }
    })
}