package br.com.mobicare.cielo.meuCadastro.presetantion.ui.adapters

import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroDomicilioBancario
import br.com.mobicare.cielo.meuCadastro.presetantion.ui.fragments.DomicilioBancarioFragment
import java.util.*

class DomicilioBancarioAdapter(fragmentManager: androidx.fragment.app.FragmentManager, vararg list: MeuCadastroDomicilioBancario) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {
    private val bancoList: List<MeuCadastroDomicilioBancario>

    init {
        this.bancoList = Arrays.asList(*list)
    }

    // Returns total number of pages
    override fun getCount(): Int {
        return bancoList.size
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return DomicilioBancarioFragment.newInstance(bancoList[position])
    }

    // Returns the page title for the top indicator
    override fun getPageTitle(position: Int): CharSequence {
        return "Page " + position
    }

    override fun getPageWidth(position: Int): Float {
        return 0.485f
    }
}
