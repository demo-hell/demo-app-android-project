package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.commons.helpers.DateTimeHelper
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.EndlessScrollListener
import br.com.mobicare.cielo.commons.utils.notNull
import br.com.mobicare.cielo.extensions.*
import br.com.mobicare.cielo.meuCadastroNovo.domain.Item
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.consultdetail.TrocaDomicilioSolicitacoesDetailActivity
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter.TrocaDomicilioSolicitacoesPresenterImpl
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.dialog.FilterTrocaDomicilioSolicitacoesBottomSheet
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import kotlinx.android.synthetic.main.activity_troca_domicilio_solicitacoes.*
import kotlinx.android.synthetic.main.item_conta_status.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val PAYMENT_ACCOUNTS_DOMICILE_ITEM = "PaymentAccountsDomicileItem"

class TrocaDomicilioSolicitacoesActivity : BaseLoggedActivity(), TrocaDomicilioSolicitacoesView {


    private val presenter: TrocaDomicilioSolicitacoesPresenterImpl by inject { parametersOf(this) }
    private var actionsMenu: Menu? = null
    private var quickFilter: QuickFilter? = null

    private var status = "ALL"
    private var protocol: String? = null
    private var showFilter = true
    private val listItem: MutableList<Item> = ArrayList()
    private lateinit var scrollListener: EndlessScrollListener
    private lateinit var adapter: DefaultViewListAdapter<Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_troca_domicilio_solicitacoes)
        this.listItem.clear()
        setupToolbar(toolbar_domicile as Toolbar,
                getString(R.string.toolbar_title_track))

        if (presenter.isToShow()) {
            initView()
            presenter.getDomicile(isLoading = true, protocol = protocol, status = status, page = PAGE, pageSize = PAGE_SIZE)
        } else {
            showFeatureToggleError()
        }
    }

    private fun initView() {
        val llm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        scrollListener = object : EndlessScrollListener(llm) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (rv_change_domicile.canScrollVertically(1))
                    presenter.getDomicile(isLoading = false, protocol = protocol, status = status, page = page + 1, pageSize = PAGE_SIZE)
            }
        }

        adapter = DefaultViewListAdapter(listItem, R.layout.item_conta_status)
        rv_change_domicile.adapter = adapter
        rv_change_domicile.layoutManager = llm
        rv_change_domicile.addOnScrollListener(scrollListener)

        adapter.setBindViewHolderCallback(object : DefaultViewListAdapter.OnBindViewHolder<Item> {
            override fun onBind(item: Item, holder: DefaultViewHolderKotlin) {
                val date = DateTimeHelper.convertToDate(item.requestDate, "yyyy-MM-dd", "dd/MM/yyyy")
                val bankName = item.bankName.formatBankName()

                holder.mView.tv_agencia_track.text = item.agency
                holder.mView.tv_conta_track.text = "${item.account}-${item.digitAccount}"
                holder.mView.tv_name_bank_and_date.text = if (bankName.isNullOrEmpty()) date else "$date - $bankName"
                holder.mView.tv_status_label_track.text = item.status?.toLowerCasePTBR()?.capitalizePTBR()
                holder.mView.tv_protocol_track.text = item.protocol

                item.code?.let { changeColorStatus(it, holder) }

                BrandCardHelper.getLoadBrandImageGeneric(item.bankCode ?: "-1")
                        .let { itUrl ->
                            ImageUtils.loadImage(holder.mView.dc_iv_brand, itUrl, R.drawable.bank_000)
                        }
                holder.mView.setOnClickListener {
                    startActivity(Intent(holder.mView.context, TrocaDomicilioSolicitacoesDetailActivity::class.java).apply {
                        putExtra(PAYMENT_ACCOUNTS_DOMICILE_ITEM, item)
                    })
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onSuccess(domiciles: List<Item>?) {
        populationRecyclerView(domiciles)
        rv_change_domicile.visible()
    }

    override fun showError(error: ErrorMessage?) {
        errorView.visible()
        errorView.showTitle = true
        errorView.configureButtonVisible(true)
        errorView.cieloErrorMessage = getString(R.string.text_message_error_mfa, error?.errorMessage ?: "")
        errorView.cieloErrorTitle = getString(R.string.text_title_generic_error)
        errorView.configureButtonLabel(getString(R.string.text_try_again_label))
        errorView.errorHandlerCieloViewImageDrawable = R.drawable.ic_07

        errorView.configureActionClickListener {
            presenter.getDomicile(isLoading = true, protocol = protocol, status = status, page = PAGE, pageSize = PAGE_SIZE)
        }
        resetList()
    }

    private fun showFeatureToggleError() {
        showFilter = false
        this.errorToggle?.visible()
        this.errorToggle?.configureActionClickListener {
            finish()
        }
    }

    override fun showLoading() {
        errorView.gone()
        progressBar.visible()
    }

    override fun hideLoading() {
        progressBar.gone()
    }

    override fun showLoadingMore() {
        rv_change_domicile.visible()
        progressBarMore.visible()

    }

    override fun hideLoadingMore() {
        progressBarMore.gone()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (showFilter) {
            actionsMenu = menu
            menuInflater.inflate(R.menu.menu_common_filter_faq, menu)

            val helpItem = menu.findItem(R.id.action_help_extrato)
            helpItem.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> {
                val bottomSheet = FilterTrocaDomicilioSolicitacoesBottomSheet.newInstance(quickFilter).apply {
                    this.onClick = object : FilterTrocaDomicilioSolicitacoesBottomSheet.OnClickButtons {
                        override fun onBtnAddFilter(dialog: Dialog?, quickFilter: QuickFilter?) {
                            dialog?.dismiss()
                            quickFilter?.statusType?.let {
                                this@TrocaDomicilioSolicitacoesActivity.status = it
                            }

                            quickFilter?.operationNumber?.let {
                                this@TrocaDomicilioSolicitacoesActivity.protocol = it
                            }

                            this@TrocaDomicilioSolicitacoesActivity.quickFilter = quickFilter

                            resetList()
                            presenter.getDomicile(isLoading = true, protocol = protocol, status = status, page = PAGE, pageSize = PAGE_SIZE)
                            filterButtonFilled()

                        }

                        override fun onBtnCleanFilter(dialog: Dialog?, quickFilter: QuickFilter?) {
                            dialog?.dismiss()
                            resetList()

                            this@TrocaDomicilioSolicitacoesActivity.status = "ALL"
                            this@TrocaDomicilioSolicitacoesActivity.protocol = null
                            this@TrocaDomicilioSolicitacoesActivity.quickFilter = null

                            presenter.getDomicile(isLoading = true, protocol = protocol, status = status, page = PAGE, pageSize = PAGE_SIZE)
                            filterButtonNotFilled()
                        }
                    }

                }
                bottomSheet.show(supportFragmentManager, FilterTrocaDomicilioSolicitacoesBottomSheet::class.java.simpleName)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun filterButtonFilled() {
        actionsMenu?.findItem(R.id.action_filter)
                ?.icon = ContextCompat.getDrawable(this,
                R.drawable.ic_filter_filled)
    }

    private fun filterButtonNotFilled() {
        actionsMenu?.findItem(R.id.action_filter)
                ?.icon = ContextCompat.getDrawable(this,
                R.drawable.ic_filter)
    }


    private fun populationRecyclerView(domiciles: List<Item>?) {
        domiciles?.toMutableList()?.let {
            listItem.addAll(it)
        }
        adapter.notifyDataSetChanged()
    }

    private fun resetList() {
        listItem.clear()
        rv_change_domicile.layoutManager?.scrollToPosition(0)
        adapter.notifyDataSetChanged()
        scrollListener.resetState()
        presenter.resetPagination()
    }

    fun changeColorStatus(statusCode: Int, holder: DefaultViewHolderKotlin) {
        when (statusCode) {
            CONCLUDED, CONCLUDED_PARTIALLY -> holder.mView.tv_status_label_track.setTextColor(ContextCompat.getColor(this, R.color.color_009e55))
            ERROR, CANCEL, REJECT -> holder.mView.tv_status_label_track.setTextColor(ContextCompat.getColor(this, R.color.red_EE2737))
            CHECKING, PENDING -> holder.mView.tv_status_label_track.setTextColor(ContextCompat.getColor(this, R.color.color_f98f25))
            else -> holder.mView.tv_status_label_track.setTextColor(ContextCompat.getColor(this, R.color.color_f98f25))
        }
    }

    override fun showEmptyList() {
        errorView.visible()
        errorView.cieloErrorMessage = getString(R.string.text_error_domicilio)
        errorView.errorHandlerCieloViewImageDrawable = R.drawable.ic_07
        errorView.showTitle = false
        errorView.configureButtonVisible(false)

        resetList()
    }

    companion object {
        const val PAGE = 1
        const val PAGE_SIZE = 25
        const val CONCLUDED = 5
        const val CONCLUDED_PARTIALLY = 6
        const val CHECKING = 4
        const val CANCEL = 8
        const val ERROR = 7
        const val PENDING = 1
        const val REJECT = 2
    }

}