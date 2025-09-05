package br.com.mobicare.cielo.openFinance.presentation.manager.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import br.com.mobicare.cielo.openFinance.presentation.manager.sharedData.received.OpenFinanceReceivedFragment
import br.com.mobicare.cielo.openFinance.presentation.manager.sharedData.sent.OpenFinanceSentFragment
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants

class OpenFinancePageTypeAdapter(
    val fragment: FragmentManager,
    val lifecycle: Lifecycle,
    val titles: Array<String>
) : FragmentStateAdapter(fragment, lifecycle) {

    override fun getItemCount(): Int {
        return titles.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            OpenFinanceConstants.RECEIVED_TAB -> OpenFinanceReceivedFragment()
            OpenFinanceConstants.SENT_TAB -> OpenFinanceSentFragment()
            else -> OpenFinanceReceivedFragment()
        }
    }
}