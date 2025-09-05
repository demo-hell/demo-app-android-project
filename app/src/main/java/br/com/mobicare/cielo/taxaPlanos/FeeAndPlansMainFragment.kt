package br.com.mobicare.cielo.taxaPlanos

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.MarginPageTransformer
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ONE_HUNDRED
import br.com.mobicare.cielo.commons.constants.ONE_HUNDRED_LONG
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.home.presentation.postecipado.utils.PostecipadoConstants.GO_DIRECTLY_TO_POSTECIPADO
import br.com.mobicare.cielo.taxaPlanos.adapter.FeeAndPlansPagerAdapter
import br.com.mobicare.cielo.taxaPlanos.analytics.FeeAndPlansGA4.ScreenView.SCREEN_VIEW_FEE_AND_PLANS_MY_PLAN
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosStatusPlanResponse
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.main.TaxaPlanosMainContract
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.main.TaxaPlanosMainPresenter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_fee_and_plans_main.*
import kotlinx.android.synthetic.main.fragment_fee_and_plans_main.errorView
import kotlinx.android.synthetic.main.layout_error_link_list_order.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import br.com.mobicare.cielo.taxaPlanos.analytics.FeeAndPlansGA4 as ga4

class FeeAndPlansMainFragment : BaseFragment(), TaxaPlanosMainContract.View {

    private val presenter: TaxaPlanosMainPresenter by inject {
        parametersOf(this)
    }

    private val goDirectlyToPostecipado by lazy {
        arguments?.getBoolean(GO_DIRECTLY_TO_POSTECIPADO)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fee_and_plans_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.configureToolbarActionListener?.changeTo(
            title =
            getString(R.string.txp_header)
        )
        presenter.loadData()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        logScreenView()
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    private fun logScreenView(){
        ga4.logScreenView(SCREEN_VIEW_FEE_AND_PLANS_MY_PLAN)
    }

    private fun initTab(
        titles: MutableList<String>,
        planName: String,
        machine: TaxaPlanosSolutionResponse? = null
    ) {
        viewPager?.adapter = FeeAndPlansPagerAdapter(titles, planName, this, machine, requireActivity())
        viewPager?.setPageTransformer(MarginPageTransformer(ONE_HUNDRED))
        TabLayoutMediator(tabFeeAndPlans, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        if (planName == TAXA_PLANOS_DO_SEU_JEITO && goDirectlyToPostecipado == true) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(ONE_HUNDRED_LONG)
                tabFeeAndPlans.getTabAt(ONE)?.select()
            }
        }
    }

    override fun onError(error: ErrorMessage?) {
        linearLayoutContent?.gone()
        errorView?.visible()
        errorView?.configureActionClickListener {
            presenter.loadData()
        }
    }

    override fun showResult(
        response: TaxaPlanosStatusPlanResponse,
        machine: TaxaPlanosSolutionResponse?
    ) {
        if (isAdded && isVisible) {
            requireActivity().apply {
                val tabTitles = this.resources.getStringArray(R.array.tab_titles_fee_and_plans)
                    .toMutableList()
                when (response.planName) {
                    TAXA_PLANOS_CONTROLE -> {
                        textViewPlanTitle?.text = this.getString(R.string.plan_title_cielo_controle)
                        textViewPlanSubTitle?.text =
                            this.getString(R.string.plan_sub_title_cielo_controle)
                        initTab(tabTitles.take(2).toMutableList(), TAXA_PLANOS_CONTROLE)
                    }
                    TAXA_PLANOS_LIVRE -> {
                        textViewPlanTitle?.text = this.getString(R.string.txp_livre_title)
                        textViewPlanSubTitle?.text = this.getString(R.string.txp_livre_description)
                        initTab(tabTitles, TAXA_PLANOS_LIVRE)
                    }
                    TAXA_PLANOS_DO_SEU_JEITO -> {
                        textViewPlanTitle?.text = this.getString(R.string.txp_seu_jeito_title)
                        textViewPlanSubTitle?.text =
                            this.getString(R.string.txp_seu_jeito_description)
                        val tabYourWay =
                            this.resources.getStringArray(R.array.tab_titles_fee_and_plans_your_way)
                                .toMutableList()
                        initTab(tabYourWay, response.planName, machine)
                    }
                }
            }
        }
    }

    override fun showLoading() {
        constraintLayoutError.gone()
        linearLayoutContent.gone()
        progress_view.visible()
    }

    override fun hideLoading() {
        linearLayoutContent?.visible()
        progress_view?.gone()
    }

    override fun visibilityHeader(isVisible: Boolean) {
        textViewPlanTitle?.visible(isVisible)
        textViewPlanSubTitle?.visible(isVisible)
    }

    override fun isAttached() = false
    override fun onLogout() = Unit
    override fun statusPlan() = ""
}
