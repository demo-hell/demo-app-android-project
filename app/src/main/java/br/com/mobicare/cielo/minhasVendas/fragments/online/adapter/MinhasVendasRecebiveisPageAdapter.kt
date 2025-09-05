package br.com.mobicare.cielo.minhasVendas.fragments.online.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

private const val VENDAS = 0
private const val RECEBIVEIS = 1

class MinhasVendasRecebiveisPageAdapter(
    val fragment: FragmentActivity,
    private val allowedFragments: MutableSet<Pair<Fragment, String>>
) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount() = allowedFragments.size

    override fun createFragment(position: Int): Fragment {
        return allowedFragments.elementAt(position).first
    }
}