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
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.main.domain.Menu
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.menu_card_grid_fragment.view.*
import kotlinx.android.synthetic.main.menu_card_grid_fragment_item.view.*

class CieloGridMenuItensWidget @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var title: String = ""
    private var item: Menu? = null
    private var onListener: OnCieloGridMenuListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.menu_card_grid_fragment, this, true)
    }

    companion object {
        const val GERAR_SUPER_LINK = "APP_ANDROID_PAYMENT_LINK"
        const val TAXAS_PLANOS = "APP_ANDROID_RATES"
        const val CONTA_DIGITAL = "APP_ANDROID_ACCOUNT"
        const val CENTRAL_DE_AJUDA = "APP_ANDROID_HELP_CENTER"
        const val MEU_CADASTRO = "APP_ANDROID_MY_ACCOUNT"
        const val MEUS_CANCELAMENTOS = "APP_ANDROID_REFUNDS"
        const val FAZER_VENDA = "fazer uma venda"
        const val CONHECA_TAMBEM = "conheca tambem"
        const val HOME = "home"
        const val SIMULADOR_VENDAS = "APP_ANDROID_SALES_SIMULATOR"
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        this.tv_title_menu_grid?.text = this.title
    }

    fun setMenu(item: Menu) {
        this.item = item
        if (item.showIcons) {
            this.tv_title_menu_grid?.visibility = View.GONE
        } else {
            this.title = item.name ?: EMPTY
            this.tv_title_menu_grid?.text = this.title
            this.tv_title_menu_grid?.visibility = View.VISIBLE
        }
        configureRecyclerView()
    }

    fun setOnCieloGridMenuListener(listener: OnCieloGridMenuListener?) {
        this.onListener = listener
    }

    private fun configureRecyclerView() {
        this.item?.items?.let {
            rv_menu_grid.layoutManager = GridLayoutManager(this.context, 3)
            rv_menu_grid.setHasFixedSize(true)
            val adapter = DefaultViewListAdapter(it, R.layout.menu_card_grid_fragment_item)
            adapter.setBindViewHolderCallback(object :
                DefaultViewListAdapter.OnBindViewHolder<Menu> {
                override fun onBind(item: Menu, holder: DefaultViewHolderKotlin) {
                    Picasso.get()
                        .load(item.icon)
                        .into(holder.itemView.imageMenuItem)
                    holder.itemView.tv_item_menu_grid.text = item.name
                }
            })
            adapter.onItemClickListener =
                object : DefaultViewListAdapter.OnItemClickListener<Menu> {
                    override fun onItemClick(item: Menu) {
                        this@CieloGridMenuItensWidget.onListener?.let {
                            gaSendMenuBottomItem(item.name ?: EMPTY, item.code)
                            it.onItemSelected(item)
                        }
                    }
                }

            rv_menu_grid.adapter = adapter
        }
        invalidate()
    }

    private fun gaSendMenuBottomItem(label: String, code: String) {
        when (code) {
            GERAR_SUPER_LINK -> {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, HOME),
                    action = listOf(FAZER_VENDA, Label.OPCOES),
                    label = listOf(Label.CARD, label)
                )
            }
            TAXAS_PLANOS, CONTA_DIGITAL, CENTRAL_DE_AJUDA, MEU_CADASTRO, MEUS_CANCELAMENTOS, SIMULADOR_VENDAS -> {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, HOME),
                    action = listOf(CONHECA_TAMBEM, Action.CLIQUE),
                    label = listOf(Label.CARD, label)
                )
            }
        }
    }

    interface OnCieloGridMenuListener {
        fun onItemSelected(item: Menu)
    }

}