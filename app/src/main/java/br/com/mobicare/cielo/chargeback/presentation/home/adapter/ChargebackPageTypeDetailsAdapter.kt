package br.com.mobicare.cielo.chargeback.presentation.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants.PENDING_TAB
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants.TREATED_TAB
import br.com.mobicare.cielo.chargeback.presentation.home.pendingList.ChargebackPendingFragment
import br.com.mobicare.cielo.chargeback.presentation.home.treatedList.ChargebackTreatedFragment
import br.com.mobicare.cielo.commons.constants.TWO


class ChargebackPageTypeDetailsAdapter(
    val fragment: FragmentManager,
    val lifecycle: Lifecycle,
    val titles: Array<String>
) : FragmentStateAdapter(fragment, lifecycle) {

    override fun getItemCount(): Int {
        return titles.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            PENDING_TAB -> ChargebackPendingFragment()
            TREATED_TAB -> ChargebackTreatedFragment()
            else -> ChargebackPendingFragment()
        }
    }

}