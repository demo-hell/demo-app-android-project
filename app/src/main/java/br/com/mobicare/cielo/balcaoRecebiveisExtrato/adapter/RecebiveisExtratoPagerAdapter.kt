package br.com.mobicare.cielo.balcaoRecebiveisExtrato.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Negotiations
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment.BalcaoRecebiveisExtratoFragment.Companion.CIELO
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment.BalcaoRecebiveisExtratoFragment.Companion.MERCADO
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment.RecebiveisExtratoTypeFragment

class RecebiveisExtratoPagerAdapter(
    supportFragmentManager: FragmentManager,
    private val negotiation: Negotiations?,
    private val dataInit:String?,
    private val dateEnd:String?
) : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    var fragCielo: RecebiveisExtratoTypeFragment? = null
    var fragMarket: RecebiveisExtratoTypeFragment? = null
    var callBackSeerMore: (Negotiations) -> Unit = {}

    override fun getItem(position: Int): Fragment {
        return if (position == 0){
            fragCielo = RecebiveisExtratoTypeFragment.newInstance(position, dataInit, dateEnd, negotiation, callBackSeerMore)
            fragCielo as RecebiveisExtratoTypeFragment
        }else{
            fragMarket = RecebiveisExtratoTypeFragment.newInstance(position,dataInit, dateEnd, negotiation, callBackSeerMore)
            fragMarket as RecebiveisExtratoTypeFragment
        }
    }

    override fun getCount() = 2

    override fun getItemPosition(`object`: Any) = POSITION_NONE

    override fun getPageTitle(position: Int): CharSequence? {
        return if(position == 0) CIELO else MERCADO
    }


}