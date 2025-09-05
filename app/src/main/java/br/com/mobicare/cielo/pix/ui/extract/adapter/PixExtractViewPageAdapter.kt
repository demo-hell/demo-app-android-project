package br.com.mobicare.cielo.pix.ui.extract.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import br.com.mobicare.cielo.pix.ui.extract.home.tabs.PixExtractTabsContract
import br.com.mobicare.cielo.pix.ui.extract.home.tabs.pageAll.PixExtractPageAllTransactionFragment
import br.com.mobicare.cielo.pix.ui.extract.home.tabs.pageRefunds.PixExtractPageRefundsTransactionFragment
import br.com.mobicare.cielo.pix.ui.extract.home.tabs.pageScheduled.PixExtractPageScheduledTransactionFragment

private const val ALL = 0
private const val RETURNS = 1
private const val SCHEDULED = 2

class PixExtractViewPageAdapter(
    val fragment: FragmentActivity,
    private val titles: List<String>
) :
    FragmentStateAdapter(fragment) {

    private var selectedFragment: PixExtractTabsContract.View? = null

    override fun getItemCount(): Int {
        return titles.size
    }

    override fun createFragment(position: Int): Fragment {
        selectedFragment = when (position) {
            ALL -> PixExtractPageAllTransactionFragment()
            RETURNS -> PixExtractPageRefundsTransactionFragment()
            SCHEDULED -> PixExtractPageScheduledTransactionFragment()
            else -> PixExtractPageRefundsTransactionFragment()
        }
        return selectedFragment as Fragment
    }

    fun setSelectedFragmentBalance(balance: String) {
        selectedFragment?.balance = balance
    }
}