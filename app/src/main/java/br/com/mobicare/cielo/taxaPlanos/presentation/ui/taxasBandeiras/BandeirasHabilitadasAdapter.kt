package br.com.mobicare.cielo.taxaPlanos.presentation.ui.taxasBandeiras

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrands

/**
 * Created by benhur.souza on 26/04/2017.
 */

class BandeirasHabilitadasAdapter(fragmentManager: FragmentManager, val bandeirasList: List<CardBrands>) :
        FragmentPagerAdapter(fragmentManager) {

    // Returns total number of pages
    override fun getCount(): Int {
        return bandeirasList.size
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): Fragment {
        return BandeirasHabilitadasFragment.newInstance(bandeirasList[position])
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }


    // Returns the page title for the top indicator
    override fun getPageTitle(position: Int): CharSequence {
        return "Page " + position
    }

    override fun getPageWidth(position: Int): Float {
        return 0.655f
    }

}

