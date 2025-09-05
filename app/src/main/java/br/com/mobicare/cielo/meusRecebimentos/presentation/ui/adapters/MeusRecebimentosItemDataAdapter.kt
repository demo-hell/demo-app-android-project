package br.com.mobicare.cielo.meusRecebimentos.presentation.ui.adapters

import br.com.mobicare.cielo.meusRecebimentos.domains.entities.IncomingObj
import br.com.mobicare.cielo.meusRecebimentos.presentation.ui.fragments.MeusRecebimentosItemDataFragment
import java.util.*

/**
 * Created by benhur.souza on 26/06/2017.
 */
class MeusRecebimentosItemDataAdapter(fragmentManager: androidx.fragment.app.FragmentManager, var list:  ArrayList<IncomingObj>) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    // Returns total number of pages
    override fun getCount(): Int {
        return list.size
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return MeusRecebimentosItemDataFragment.newInstance(list[position].dayDescription)
    }
}
