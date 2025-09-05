package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import br.com.cielo.libflue.util.THREE
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.DisposableDefault
import br.com.mobicare.cielo.meuCadastroNovo.domain.Bank
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.MeuCadastroAdapterPositions.DADOS_CONTA_FRAGMENT
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.MeuCadastroAdapterPositions.DADOS_ESTABLISHMENT_FRAGMENT
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.MeuCadastroAdapterPositions.DADOS_USUARIO_FRAGMENT
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.DadosContaFragment
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.DadosUsuarioFragment
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.ListenerCadastroScreen
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment.DadosEstablishmentFragment

class MeuCadastroNovoAdapter(
    fm: FragmentManager,
    var activity: FragmentActivity,
    val listener: ListenerCadastroScreen
) : FragmentPagerAdapter(fm), DisposableDefault,
    DadosContaFragment.OnTransferBrandListener {

    private var disposableDefault: DisposableDefault? = null

    var onTransferBrandListener: DadosContaFragmentBrandTransferListener? = null

    interface DadosContaFragmentBrandTransferListener {
        fun transferBrandListener(listBanks: List<Bank>, currentBank: Bank)
    }

    interface DataUpdateListener {
        fun onDataUpdated()
    }

    private val dataUpdateListener: DataUpdateListener = object : DataUpdateListener {
        override fun onDataUpdated() {
            notifyDataSetChanged()
        }
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            DADOS_ESTABLISHMENT_FRAGMENT -> DadosEstablishmentFragment.newInstance(listener, dataUpdateListener)
            DADOS_USUARIO_FRAGMENT -> DadosUsuarioFragment.newInstance()
            else -> DadosContaFragment.newInstance(listener, this)
        }
    }

    override fun getCount(): Int {
        return THREE
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            DADOS_ESTABLISHMENT_FRAGMENT -> activity.getString(R.string.title_minha_empresa)
            DADOS_USUARIO_FRAGMENT -> activity.getString(R.string.title_dados_bancarios)
            else -> activity.getString(R.string.title_minhas_solucoes)
        }
    }

    override fun getItemPosition(item: Any): Int {
        return when (item) {
            is DadosEstablishmentFragment -> DADOS_ESTABLISHMENT_FRAGMENT
            is DadosUsuarioFragment -> DADOS_USUARIO_FRAGMENT
            else -> DADOS_CONTA_FRAGMENT
        }
    }

    fun getTabView(position: Int): View {
        val tab = LayoutInflater.from(activity).inflate(R.layout.tab_layout_item, null)
        val tv = tab.findViewById<TextView>(R.id.custom_text)
        tv.text = getPageTitle(position)
        return tab
    }

    override fun disposable() {
        disposableDefault?.disposable()
        disposableDefault = null
    }

    override fun transferBrand(listBanks: List<Bank>, currentBank: Bank) {
        onTransferBrandListener?.transferBrandListener(listBanks, currentBank)
    }
}