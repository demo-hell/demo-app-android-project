package br.com.mobicare.cielo.interactbannersoffers.router

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import br.com.mobicare.cielo.commons.router.BUNDLE_TO_ROUTER
import br.com.mobicare.cielo.commons.router.FRAGMENT_TO_ROUTER
import br.com.mobicare.cielo.commons.router.RouterFragmentInActivity
import br.com.mobicare.cielo.commons.router.TITLE_ROUTER_FRAGMENT

class InteractBannerActivityRouterAction(private val clazz: Class<*>?,
                                         private var bundle: Bundle? = null,
                                         urlTarget: String? = null) : RouterAction {
    override fun execute(context: Context) {
        Intent(context, clazz).let { itIntent ->
            if (this.bundle == null) {
                this.bundle = Bundle().apply {
                    // putParcelable(APP_ANDROID_MENU, route)
                }
            }
            itIntent.putExtras(this.bundle!!)
            context.startActivity(itIntent)
        }
    }
}

class InteractBannerActivityRouterURLAction(private val urlTarget: String) : RouterAction {
    override fun execute(context: Context) {
        var url = urlTarget
        if (!urlTarget.startsWith("http://") && !urlTarget.startsWith("https://"))
            url = "http://" + url;

        Intent(Intent.ACTION_VIEW, Uri.parse(url)).let {
            context.startActivity(it)
        }
    }
}

class InteractActivityFragmentRouterAction(private var clazz: String?,
                                   private var toolbarTitle: String? = "",
                                   private var bundle: Bundle? = null) :
        RouterAction {
    override fun execute(context: Context) {
        this.clazz?.let {
            Intent(context, RouterFragmentInActivity::class.java).let {
                it.putExtra(TITLE_ROUTER_FRAGMENT, toolbarTitle)
                it.putExtra(FRAGMENT_TO_ROUTER, clazz)
                it.putExtra(BUNDLE_TO_ROUTER, bundle)
            }.let {
                context.startActivity(it)
            }
        }
    }
}