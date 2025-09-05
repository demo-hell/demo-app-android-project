package br.com.mobicare.cielo.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.main.domain.Menu
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_home_shortcut.view.*
import kotlinx.android.synthetic.main.menu_card_grid_fragment.view.*

class CieloActionServiceGridWidget @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var title: String = ""
    private var item: Menu? = null
    private var listener: OnItemListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.menu_card_grid_fragment, this, true)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        this.tv_title_menu_grid?.text = this.title
    }

    fun setMenu(item: Menu) {
        this.item = item
        if (item.showIcons) {
            this.tv_title_menu_grid?.visibility = View.GONE
        }
        else {
            this.title = item.name ?: EMPTY
            this.tv_title_menu_grid?.text = this.title
            this.tv_title_menu_grid?.visibility = View.VISIBLE
        }
        configureRecyclerView()
    }

    fun setOnItemListener(listener: OnItemListener?) {
        this.listener = listener
    }

    private fun configureRecyclerView() {
        this.item?.items?.let {
            rv_menu_grid.layoutManager = GridLayoutManager(this.context, 3)
            rv_menu_grid?.setHasFixedSize(true)
            val adapter = DefaultViewListAdapter(it, R.layout.item_home_shortcut)
            adapter.setBindViewHolderCallback(object: DefaultViewListAdapter.OnBindViewHolder<Menu> {
                override fun onBind(item: Menu, holder: DefaultViewHolderKotlin) {
                    Picasso.get()
                            .load(item.icon)
                            .into(holder.itemView.imageHeaderButton)
                    holder.itemView.textHeaderLabel.text = item.name
                }
            })
            adapter.onItemClickListener = object : DefaultViewListAdapter.OnItemClickListener<Menu> {
                override fun onItemClick(item: Menu) {
                    gaSendMenuService(item.name ?: EMPTY)
                    this@CieloActionServiceGridWidget.listener?.onItemSelected(item)
                }
            }

            rv_menu_grid.adapter = adapter
        }
    }

    interface OnItemListener {
        fun onItemSelected(item: Menu)
    }

    private fun gaSendMenuService(status: String){
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Label.SERVICOS),
                action = listOf(Action.CLIQUE),
                label = listOf(Label.CARD, status.toLowerCasePTBR())
            )
    }

}