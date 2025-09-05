package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.pager.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.listener.PixHomeExtractListener
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.pager.pages.PixExtractNewPageSchedulingFragment
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.pager.pages.PixExtractPageAllFragment
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.pager.pages.PixExtractPageRefundsFragment
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.pager.pages.PixExtractPageSchedulingFragment

class PixExtractPagerAdapter(
    val fragmentManager: FragmentManager,
    val lifecycle: Lifecycle,
    val pixHomeExtractListener: PixHomeExtractListener,
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    private val pages =
        arrayListOf(
            PixExtractPageAllFragment.create(pixHomeExtractListener),
            PixExtractPageRefundsFragment.create(pixHomeExtractListener),
            PixExtractPageSchedulingFragment.create(pixHomeExtractListener),
        )

    override fun createFragment(position: Int): Fragment = pages[position]

    override fun getItemCount() = pages.size

    fun reloadExtractPage(page: Int) {
        pages[page].reloadExtract()
    }

    // TODO: REMOVER APÓS ESTABELECER O NOVO EXTRATO PARA AGENDADOS, POIS SERÁ ADICIONADO DIRETAMENTE NA LISTA O NOVO FRAGMENT
    fun changeSchedulingPageToNewScheduling() {
        pages[TWO] = PixExtractNewPageSchedulingFragment.create(pixHomeExtractListener)
    }
}
