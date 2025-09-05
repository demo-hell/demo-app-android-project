package br.com.mobicare.cielo.pagamentoLink.presentation.ui.listAtivos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.extrato.presentation.ui.adapters.EndlessRecyclerViewScrollListener
import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLink
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.adapter.PagamentoLinkListItemAtivosAdapter
import kotlinx.android.synthetic.main.pagamento_list_fragment_ativos.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PagamentoLinkListAtivosFragment : BaseFragment(), PagamentoLinkListAtivosContract.View, CieloNavigationListener {

    val presenter: PagamentoLinkListAtivosPresenter by inject {
        parametersOf(this)
    }

    private var cieloNavigation: CieloNavigation? = null

    private lateinit var linearLayoutManager: LinearLayoutManager

    private val listAtivosAdapter: PagamentoLinkListItemAtivosAdapter by lazy {
        PagamentoLinkListItemAtivosAdapter {
            findNavController()
                    .navigate(
                            PagamentoLinkListAtivosFragmentDirections
                                    .actionPagamentoLinkListAtivosFragment2ToLinkOrdersFragment(it))
        }
    }

    companion object {
        const val SCREEN_NAME = "SCREEN_NAME"

        fun create(): PagamentoLinkListAtivosFragment {
            val fragment = PagamentoLinkListAtivosFragment()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.pagamento_list_fragment_ativos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        configureNavigation()

        showLoading()
        loadRecyclerView()

        presenter.setView(this)

        listAtivosAdapter.clearItems()
        presenter.loadListAtivosResumo()
    }

    private val refreshListener = OnRefreshListener {
        presenter.onSwipeRefresh()

    }

    override fun setupSwiperToRefresh() {
        swipeRefreshLayout.isRefreshing = true
        listAtivosAdapter.clearItems()
        presenter.loadListAtivosResumo(false)
        addAdapterInRecyclerview()
    }

    private fun loadRecyclerView() {
        addAdapterInRecyclerview()
        swipeRefreshLayout.setOnRefreshListener(refreshListener)
    }

    private fun addAdapterInRecyclerview() {
        linearLayoutManager = LinearLayoutManager(activity?.applicationContext)
        addSrollEvent(linearLayoutManager)
        recycler_view_itens_ativos.layoutManager = linearLayoutManager
        recycler_view_itens_ativos.adapter = listAtivosAdapter
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar("Links Ativos")
            this.cieloNavigation?.showButton(false)
            this.cieloNavigation?.showHelpButton(false)
            this.cieloNavigation?.setNavigationListener(this)
        }
    }

    override fun onResume() {
        presenter.setView(this)
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onCleared()
    }

    override fun showListAtivos(itens: List<PaymentLink>) {
        if (isAttached()) {
            this.cieloNavigation?.showContent(true)
            this.cieloNavigation?.showLoading(false)
            listAtivosAdapter.appendData(itens)
        }
    }

    override fun showLastLinks(items: List<PaymentLink>) {
        if (isAttached()) {
            this.cieloNavigation?.showContent(true)
            this.cieloNavigation?.showLoading(false)
            listAtivosAdapter.initialData(items)
        }
    }

    override fun showEmptyLinks() {
    }

    private fun addSrollEvent(linearLayoutManager: LinearLayoutManager) {
        recycler_view_itens_ativos.addOnScrollListener(
                object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
                    override fun onLoadMore() {
                        presenter.loadMore()
                    }
                }
        )
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
            if (isAttached()) {
                hideLoading()
                AlertDialogCustom.Builder(requireActivity(), it.title)
                        .setMessage(it.message)
                        .setBtnRight(getString(android.R.string.ok))
                        .setOnclickListenerRight {
                            presenter.resubmit()
                        }
                        .show()
            }
        }
    }

    override fun showSubmit(error: ErrorMessage) {
        if (isAttached()) {
//            include_error.visibility = View.VISIBLE
//            progress.visibility = View.GONE
            this.cieloNavigation?.showError(error)
        }
    }

    override fun showLoading() {
        //super.showLoading()
//        include_error.visibility = View.GONE
//        progress.visibility = View.VISIBLE
//
//        progress.bringToFront()
//        constraint_main.requestLayout()
//        constraint_main.invalidate()
        this.cieloNavigation?.showLoading(true)
    }

    override fun hideLoading() {
//        super.hideLoading()
//        include_error.visibility = View.GONE
//        progress.visibility = View.GONE
        this.cieloNavigation?.showLoading(false)
    }

    override fun hideLoadingSwipeToRefresh() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onRetry() {
        this.presenter.resubmit()
    }
}
