package br.com.mobicare.cielo.commons.router

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.main.domain.Menu
import kotlinx.android.synthetic.main.activity_router_fragment_activity.*

const val TITLE_ROUTER_FRAGMENT = "TITLE_ROUTER_FRAGMENT"
const val MENU_ROUTER_FRAGMENT = "MENU_ROUTER_FRAGMENT"
const val FRAGMENT_TO_ROUTER = "FRAGMENT_ROUTED"
const val BUNDLE_TO_ROUTER = "BUNDLE_TO_ROUTER"
const val ENABLE_FLAG_SECURE = "ENEBLE_FLAG_SECURE"

class RouterFragmentInActivity : BaseLoggedActivity() {

    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_router_fragment_activity)
        loadParams()
    }

    private fun loadParams() {
        intent?.extras?.let { itExtras ->
            itExtras.getString(TITLE_ROUTER_FRAGMENT)?.let {
                setupToolbar(this.toolbarRouterFragment as Toolbar, it)
            }
            itExtras.getParcelable<Menu>(MENU_ROUTER_FRAGMENT)?.let {
                this.menu = it
            }
            itExtras.getString(FRAGMENT_TO_ROUTER)?.let {
                var bundle = itExtras.getBundle(BUNDLE_TO_ROUTER)
                if (bundle == null) {
                    bundle = Bundle()
                }
                bundle.putParcelable(MENU_ROUTER_FRAGMENT, this@RouterFragmentInActivity.menu)
                createFragment(it, bundle)
            }
            enableFlagSecure(itExtras.getBoolean(ENABLE_FLAG_SECURE))
        }
    }

    private fun enableFlagSecure(enableFlagSecure: Boolean) {
        if (enableFlagSecure) {
            br.com.mobicare.cielo.commons.utils.enableFlagSecure(window)
        }
    }

    private fun createFragment(clazz: String, bundle: Bundle?) {
        val c = Class.forName(clazz)
        val fragment = c.getConstructor().newInstance() as Fragment
//        fragment.arguments = Bundle().apply {
////            putParcelable(MENU_ROUTER_FRAGMENT, this@RouterFragmentInActivity.menu)
//            putBundle()
//        }
        fragment.arguments = bundle
        this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.routerFragmentContainer, fragment)
                .commit()
    }

}