package br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.cielolivre

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.addInFrame
import br.com.mobicare.cielo.component.CieloCardBrandsView
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference
import br.com.mobicare.cielo.taxaPlanos.componentes.taxas.BandeiraModelView
import br.com.mobicare.cielo.taxaPlanos.componentes.taxas.BandeirasFragment
import br.com.mobicare.cielo.taxaPlanos.componentes.taxas.CieloAllBrandsBottomSheet
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.presenter.PlanFreeTaxesPresenter
import kotlinx.android.synthetic.main.fragment_plan_free_taxes.*
import kotlinx.android.synthetic.main.layout_error_link_list_order.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PlanFreeTaxesFragment : BaseFragment(), PlanFreeTaxesPresenterContract.View {

    private val presenter: PlanFreeTaxesPresenter by inject {
        parametersOf(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_plan_free_taxes
                , container,
            false
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardAllUserEnabledBrands.setListener(object : CieloCardBrandsView.OnButtonClickListener {
            override fun onButtonClicked() {
                CieloAllBrandsBottomSheet.create(
                    ArrayList(
                        ConfigurationPreference.instance
                        .allSupportedBrandsImageUrls(requireContext()))
                ).show(childFragmentManager,
                    TaxasPlanosCieloFragment::class.java.simpleName)
            }
        })

        presenter.fetchAllSupportedBrands()
    }

    override fun onResume() {
        presenter.onResume()
        super.onResume()
    }

    override fun onPause() {
        presenter.onDestroy()
        super.onPause()
    }

    private fun hideAllContent() {
        cardAllUserEnabledBrands.visibility = View.GONE
        fragmentSearchBrandTax.visibility = View.GONE
    }

    private fun showAllContent() {
        cardAllUserEnabledBrands.visibility = View.VISIBLE
        fragmentSearchBrandTax.visibility = View.VISIBLE
    }

    override fun showLoading() {
        hideAllContent()
        linearShimmerLoading.visibility = View.VISIBLE
        shimmerLoading.startShimmer()
    }

    override fun hideLoading() {
        shimmerLoading.stopShimmer()
        linearShimmerLoading.visibility = View.GONE
        showAllContent()
    }

    override fun loadBrands(cardBrands: ArrayList<BandeiraModelView>) {
        BandeirasFragment.newInstance(cardBrands).addInFrame(childFragmentManager,
            R.id.fragmentSearchBrandTax)
    }

    override fun showError(error: ErrorMessage?) {
        fragmentSearchBrandTax.gone()
        constraintLayoutError.visible()
        buttonLoadAgain.setOnClickListener {
            presenter.fetchAllSupportedBrands()
        }
    }
}