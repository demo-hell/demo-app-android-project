package br.com.mobicare.cielo.login.presentation.ui.adapters

import br.com.mobicare.cielo.login.domains.entities.CieloInfoDialogContent
import br.com.mobicare.cielo.login.presentation.ui.fragments.ContentClosableModalFragment

class ClosableDialoglContentAdapter(fragmentManager: androidx.fragment.app.FragmentManager,
                                    private val dialogCieloContent: CieloInfoDialogContent) :
        androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    // Returns total number of pages
    override fun getCount(): Int {
        return dialogCieloContent.pageElements.size
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return ContentClosableModalFragment.newInstance(dialogCieloContent.pageElements[position])
    }

    // Returns the page title for the top indicator
    override fun getPageTitle(position: Int): CharSequence {
        return "Page $position"
    }
}

