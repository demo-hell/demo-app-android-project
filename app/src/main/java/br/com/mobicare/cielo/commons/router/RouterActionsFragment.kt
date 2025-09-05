package br.com.mobicare.cielo.commons.router

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.HOME_FAZER_UMA_VENDA_VIEW
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.update.UpdateAppBottomSheet
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.component.CieloGridMenuItensWidget
import br.com.mobicare.cielo.component.CieloGridMenuItensWidget.Companion.FAZER_VENDA
import br.com.mobicare.cielo.main.domain.Menu
import kotlinx.android.synthetic.main.fragment_router_actions.*

@Keep
class RouterActionsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
        = inflater.inflate(R.layout.fragment_router_actions, container, false)

    companion object{
        const val SUPER_LINK = "Cielo Super Link"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.arguments?.let {
            it.getParcelable<Menu>(MENU_ROUTER_FRAGMENT)?.let {
                this.actionsWidget?.setMenu(it)
                this.actionsWidget?.setOnCieloGridMenuListener(object: CieloGridMenuItensWidget.OnCieloGridMenuListener {
                    override fun onItemSelected(item: Menu) {
                        gaSendGridActions(item.name ?: EMPTY)
                        Router.navigateTo(this@RouterActionsFragment.requireContext(), item, actionListener = object: Router.OnRouterActionListener {
                            override fun actionNotFound(action: Menu) {
                                UpdateAppBottomSheet
                                        .newInstance()
                                        .show(childFragmentManager, "UpdateAppBottomSheet_RouterActionsFragment")
                            }
                        })
                    }
                })
            }
        }
        Analytics.trackScreenView(
            screenName = HOME_FAZER_UMA_VENDA_VIEW,
            screenClass = this.javaClass
        )
    }

    private fun gaSendGridActions(status: String){
        if (isAttached()) {
            when(status){
                SUPER_LINK -> {
                    Analytics.trackEvent(
                        category = listOf(Category.APP_CIELO, FAZER_VENDA),
                        action = listOf(Label.OPCOES),
                        label = listOf(Label.CARD, status)
                    )
                }
            }
        }
    }

}