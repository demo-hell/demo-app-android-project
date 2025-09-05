package br.com.mobicare.cielo.home.presentation.main.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.main.domain.Item
import br.com.mobicare.cielo.main.domain.Menu
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.menu_card_grid_fragment.*
import kotlinx.android.synthetic.main.menu_card_grid_fragment_item.view.*

class MenuCardGridFragment : BaseFragment() {

    private lateinit var adapter: DefaultViewListAdapter<Menu>
    private lateinit var name: String
    private lateinit var genericMenuGrid: Item

    companion object {
        //const val TITLE_MENU = "TITLE_MENU"
        const val GENERIC_MENU_GRID = "GENERIC_MENU_GRID"
        fun newInstance(genericMenuGrid: Item) = MenuCardGridFragment().apply {
            arguments = Bundle().apply {
                //putString(TITLE_MENU, name)
                putParcelable(GENERIC_MENU_GRID, genericMenuGrid)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.menu_card_grid_fragment, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            it.getParcelable<Item>(GENERIC_MENU_GRID)?.let {
                this.genericMenuGrid = it
                configureTitle()
                configureRecyclerView()
            }
        }
    }


    private fun configureTitle() {
        this.tv_title_menu_grid?.text = this.genericMenuGrid.name
    }

    /**
     * método para popular o adapter
     * */
    private fun configureRecyclerView() {
        this.genericMenuGrid.items?.let {
            rv_menu_grid.layoutManager = GridLayoutManager(requireActivity(), 3)
            adapter = DefaultViewListAdapter(it, R.layout.menu_card_grid_fragment_item)
            adapter.setBindViewHolderCallback(object : DefaultViewListAdapter.OnBindViewHolder<Menu> {
                override fun onBind(item: Menu, holder: DefaultViewHolderKotlin) {
                    Picasso.get()
                            .load(item.icon)
                            .into(holder.itemView.imageMenuItem)
                    holder.itemView.tv_item_menu_grid.text = item.name
                }
            })
            adapter.onItemClickListener = object : DefaultViewListAdapter.OnItemClickListener<Menu> {
                override fun onItemClick(item: Menu) {

                    Router.navigateTo(requireContext(), item)

                }
            }

            rv_menu_grid.adapter = adapter
        }
    }

}