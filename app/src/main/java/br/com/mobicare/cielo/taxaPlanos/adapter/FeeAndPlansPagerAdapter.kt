package br.com.mobicare.cielo.taxaPlanos.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import br.com.mobicare.cielo.taxaPlanos.TAXA_PLANOS_CONTROLE
import br.com.mobicare.cielo.taxaPlanos.TAXA_PLANOS_DO_SEU_JEITO
import br.com.mobicare.cielo.taxaPlanos.TAXA_PLANOS_LIVRE
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse
import br.com.mobicare.cielo.taxaPlanos.model.TaxAndPlansSection
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.cielolivre.PlanFreeTaxesFragment
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.cielolivre.TaxasPlanosCieloFragment
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.doSeuJeito.taxas.DoSeuJeitoTaxasPlanosFragment
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.main.TaxaPlanosMainContract
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.myPlan.TaxaPlanosPlanFragmentNew
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel.PostecipadoMeuAluguelFragment

class FeeAndPlansPagerAdapter(private val titles: MutableList<String>,
                              private val planName: String,
                              private val view: TaxaPlanosMainContract.View,
                              private val machine: TaxaPlanosSolutionResponse?,
                              fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = titles.size

    override fun createFragment(position: Int): Fragment {
        return when (planName) {
            TAXA_PLANOS_CONTROLE, TAXA_PLANOS_LIVRE -> setupPlan(position)
            TAXA_PLANOS_DO_SEU_JEITO -> setupPlanYourWay(position)
            else -> setupPlan(position)
        }
    }

    private fun setupPlan(position: Int): Fragment {
        view.visibilityHeader(true)

        return when (position) {
            TaxAndPlansSection.HIRED_PLAN.position -> {
                TaxaPlanosPlanFragmentNew.create(planName)
            }
            TaxAndPlansSection.TRACK_PLAN.position -> {
                TaxasPlanosCieloFragment.create(planName)
            }
            else -> PlanFreeTaxesFragment()
        }
    }

    private fun setupPlanYourWay(position: Int): Fragment {
        return when (position) {
            TaxAndPlansSection.TAXES.position -> {
                view.visibilityHeader(true)
                DoSeuJeitoTaxasPlanosFragment.create(planName, machine)
            }
            else -> {
                view.visibilityHeader(false)
                PostecipadoMeuAluguelFragment.create(machine)
            }
        }

    }
}