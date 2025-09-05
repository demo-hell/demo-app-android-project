package br.com.mobicare.cielo.meusCartoes.presentation.ui.adapter

import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment.RecebiveisExtratoTypeFragment
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.page.ContaDigitalCatenoPageTypeFragment01
import br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.page.ContaDigitalCatenoPageTypeFragment02
import br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.page.ContaDigitalCatenoPageTypeFragment03

class ContaDigitalCatenoPageTypeAdapter(fragmentActivity: FragmentActivity)
    : FragmentStateAdapter(fragmentActivity) {

    private val ITEM_COUNT = 3
    private val STEP_0 = 0
    private val STEP_1 = 1

    override fun getItemCount() = ITEM_COUNT

    override fun createFragment(position: Int) = when (position) {
        STEP_0 -> ContaDigitalCatenoPageTypeFragment01.newInstance()
        STEP_1 -> ContaDigitalCatenoPageTypeFragment02.newInstance()
        else -> ContaDigitalCatenoPageTypeFragment03.newInstance()
    }
}