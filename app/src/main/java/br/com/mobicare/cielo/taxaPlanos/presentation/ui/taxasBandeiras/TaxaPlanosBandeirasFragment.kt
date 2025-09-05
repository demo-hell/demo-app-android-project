package br.com.mobicare.cielo.taxaPlanos.presentation.ui.taxasBandeiras

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.LogoutListener
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrandFees
import br.com.mobicare.cielo.taxaPlanos.TAXA_PLANOS_CONTROLE
import br.com.mobicare.cielo.taxaPlanos.TAXA_PLANOS_DO_SEU_JEITO
import br.com.mobicare.cielo.taxaPlanos.TAXA_PLANOS_LIVRE
import br.com.mobicare.cielo.taxaPlanos.TAXA_PLANOS_PLAN
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosMachine
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.api.PlanListener
import kotlinx.android.synthetic.main.fragment_receba_mais_failure.*
import kotlinx.android.synthetic.main.taxa_planos_bandeira_fragment.*
import kotlinx.android.synthetic.main.taxa_planos_machine_itens_fragment.view.*

class TaxaPlanosBandeirasFragment : BaseFragment(),
        TaxaPlanosBandeirasContract.View {

    private var logoutListener: LogoutListener? = null
    private lateinit var mPlan : String

    companion object {
        fun newInstance(logoutListener: LogoutListener, planListener: PlanListener): TaxaPlanosBandeirasFragment {
            val fragment = TaxaPlanosBandeirasFragment()
            fragment.logoutListener = logoutListener
            val bundle = Bundle().apply {
                putString(TAXA_PLANOS_PLAN, planListener.statusPlan())
            }
            fragment.arguments =  bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.taxa_planos_bandeira_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPlan = this.requireArguments().getString(TAXA_PLANOS_PLAN, "")

        callFlow()
        buttonUpdate.setOnClickListener {
            callFlow()
        }
    }

    private fun callFlow() {
        when (mPlan) {
            TAXA_PLANOS_DO_SEU_JEITO -> {
                ms_tv_title_bandeiras_taxas.text = getString(R.string.txp_seu_jeito_title)
                ms_tv_plano_description.text = getString(R.string.txp_seu_jeito_description)
            }
            TAXA_PLANOS_CONTROLE -> {
                ms_tv_title_bandeiras_taxas.text = getString(R.string.txp_controle_title)
                ms_tv_plano_description.text = getString(R.string.txp_controle_description)
                constraint_view_machine.gone()
                text_view_maquinas_adicionais.gone()
            }
            TAXA_PLANOS_LIVRE -> {
                ms_tv_title_bandeiras_taxas.text = getString(R.string.txp_livre_title)
                ms_tv_plano_description.text = getString(R.string.txp_livre_description)
                constraint_view_machine.gone()
                text_view_maquinas_adicionais.gone()
            }
        }
    }


    override fun onDestroy() {
        logoutListener = null
        super.onDestroy()
    }

    override fun showLoading() {
        if (isAttached()) {
            content_bandeiras.gone()
            constraint_view_machine.gone()
            frameProgress_dc.visible()
        }
    }

    override fun hideLoading() {
        if (isAttached()) {
            frameProgress_dc.gone()
        }
    }

    override fun showError(error: ErrorMessage?) {
        if (isAttached()) {
            content_bandeiras.gone()
            constraint_view_machine.gone()
            dc_error.visible()
            frameProgress_dc.gone()
        }
    }

    override fun logout(msg: ErrorMessage?) {
        logoutListener?.onLogout()
    }

    override fun showBrandsFee(fees: CardBrandFees) {
        if (isAttached()) {


            val bandeiras = BandeirasHabilitadasAdapter(childFragmentManager, fees.cardBrands)
            vp_bandeiras_habilitadas.adapter = bandeiras
            indicator_band_hab.setViewPager(vp_bandeiras_habilitadas)
            vp_bandeiras_habilitadas.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                var lastPosition = 0
                override fun onPageSelected(position: Int) {
                    lastPosition = position
                }
            })
        }
    }

    override fun showMachine(response: TaxaPlanosSolutionResponse) {
        if (isAttached()) {


            recycler_view_machines.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = DefaultViewListAdapter(response.pos, R.layout.taxa_planos_machine_itens_fragment)
            adapter.setBindViewHolderCallback(object : DefaultViewListAdapter.OnBindViewHolder<TaxaPlanosMachine> {
                override fun onBind(item: TaxaPlanosMachine, holder: DefaultViewHolderKotlin) {
                    holder.mView.text_view_name.text = item.name
                    holder.mView.text_view_right_01.text = item.rentalAmount?.toPtBrRealString()
                    holder.mView.text_view_right_02.text = item.logicalNumber ?: ""
                }
            })

            recycler_view_machines.adapter = adapter
        }
    }

    override fun showRgister() {
        content_bandeiras.visible()
        linearBrandsAndTaxes.visible()
        constraint_view_machine.visible()
    }

}