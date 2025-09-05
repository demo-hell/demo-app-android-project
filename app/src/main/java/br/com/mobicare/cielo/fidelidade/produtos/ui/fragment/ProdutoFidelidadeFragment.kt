package br.com.mobicare.cielo.fidelidade.produtos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.fidelidade.data.managers.FidelidadeRepository
import br.com.mobicare.cielo.fidelidade.domains.ProdutoFidelidadeObj
import br.com.mobicare.cielo.fidelidade.produtos.presenter.ProdutosFidelidadePresenter
import br.com.mobicare.cielo.fidelidade.produtos.ui.ProdutosFidelidadeContract
import br.com.mobicare.cielo.fidelidade.produtos.ui.ProdutosFidelidadeItemAdapter
import kotlinx.android.synthetic.main.content_produtos_fidelidade.*

/**
 * Created by silvia.miranda on 16/08/2017.
 */
class ProdutoFidelidadeFragment : BaseFragment(), ProdutosFidelidadeContract.View {

    var presenter: ProdutosFidelidadePresenter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.content_produtos_fidelidade, container, false)
    }

    override fun onStart() {
        super.onStart()
        presenter = ProdutosFidelidadePresenter(this, FidelidadeRepository(requireActivity()))
        presenter?.callAPI()
    }

    override fun hideProgress() {
        progress_bar_produtos_fidelidade.visibility = View.GONE
    }

    override fun showProgress() {
        progress_bar_produtos_fidelidade.visibility = View.VISIBLE
    }

    override fun loadItensCard(produtos: ArrayList<ProdutoFidelidadeObj>?) {
        recycle_view_item_produtos_fidelidade.setLayoutManager(androidx.recyclerview.widget.LinearLayoutManager(this.getActivity(), androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        recycle_view_item_produtos_fidelidade.adapter = (produtos?.let { ProdutosFidelidadeItemAdapter(it, requireActivity()) })
    }
}