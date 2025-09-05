package br.com.mobicare.cielo.pix.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.router.MENU_ROUTER_FRAGMENT
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.debitoEmConta.analytics.DebitAccountGA4
import br.com.mobicare.cielo.debitoEmConta.analytics.DebitAccountGA4.Companion.SCREEN_NAME_OTHERS_AUTHORIZATIONS
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.pix.constants.IS_SHOW_DATA_QUERY_ARGS
import kotlinx.android.synthetic.main.item_submenu_authorization.view.*
import kotlinx.android.synthetic.main.sub_menu_authorization.*
import org.koin.android.ext.android.inject

class SubMenuAuthorizationFragment : BaseFragment() {

    private val ga4: DebitAccountGA4 by inject()
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sub_menu_authorization, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.arguments?.let {
            it.getParcelable<Menu>(MENU_ROUTER_FRAGMENT)?.let { menu ->
                rv_sub_menu.layoutManager = LinearLayoutManager(requireContext())
                rv_sub_menu?.setHasFixedSize(true)
                menu.items?.let { items ->
                    val adapter =
                            DefaultViewListAdapter(items, R.layout.item_submenu_authorization)
                    adapter.setBindViewHolderCallback(object :
                            DefaultViewListAdapter.OnBindViewHolder<Menu> {
                        override fun onBind(item: Menu, holder: DefaultViewHolderKotlin) {
                            holder.mView.text_item_submenu_label.text = item.name
                        }
                    })
                    adapter.onItemClickListener =
                            object : DefaultViewListAdapter.OnItemClickListener<Menu> {
                                override fun onItemClick(item: Menu) {
                                    Router.navigateTo(context = requireContext(), route = item,
                                            params = arguments?.apply {
                                                this.putBoolean(IS_SHOW_DATA_QUERY_ARGS, true)
                                            })
                                }
                            }

                    rv_sub_menu.adapter = adapter
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ga4.logScreenView(SCREEN_NAME_OTHERS_AUTHORIZATIONS)
    }
}