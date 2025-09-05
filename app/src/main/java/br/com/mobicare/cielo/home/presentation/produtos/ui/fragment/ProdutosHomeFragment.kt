package br.com.mobicare.cielo.home.presentation.produtos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.home.presentation.produtos.ProdutosHomeContract
import br.com.mobicare.cielo.home.presentation.produtos.domain.entities.ProdutoObj
import br.com.mobicare.cielo.home.presentation.produtos.presenter.ProdutosHomePresenter
import br.com.mobicare.cielo.home.presentation.produtos.ui.adapter.ProdutosHomeAdapter
import kotlinx.android.synthetic.main.content_produtos_fidelidade_home.*

/**
 * Created by benhur.souza on 31/07/2017.
 */
class ProdutosHomeFragment : BaseFragment(), ProdutosHomeContract.View {

    var presenter: ProdutosHomePresenter? = null

    companion object{
        fun getInstance() = ProdutosHomeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_produtos_fidelidade_home, container, false)
    }

    override fun onStart() {
        super.onStart()
        presenter = ProdutosHomePresenter(this)
        presenter!!.callAPI()
        managerFeatureToggle()
    }

    override fun showProgress() {
        progress_produtos_home.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress_produtos_home.visibility = View.GONE
    }

    override fun showError(error: String) {
        if (isAttached()) {
            AlertDialogCustom.Builder(this.context, getString(R.string.home_ga_screen_name))
                    .setMessage(error)
                    .setBtnRight(getString(R.string.ok))
                    .show()
        }
    }

    override fun loadData(transactions: List<ProdutoObj>?) {
        recycler_view.layoutManager = (androidx.recyclerview.widget.LinearLayoutManager(context))
        recycler_view.adapter = (ProdutosHomeAdapter(requireContext(), transactions))
        recycler_view?.visibility = View.VISIBLE
    }

    fun managerFeatureToggle(){
        val isToShow : Boolean = FeatureTogglePreference.instance
                .getFeatureTogle(FeatureTogglePreference.PRODUTOS_SERVICOS)

        if (!isToShow){
            content_produtos_servicos.visibility = View.GONE
        } else {
            content_produtos_servicos.visibility = View.VISIBLE
        }
    }
}