package br.com.mobicare.cielo.chargeback.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.presentation.home.adapter.ChargebackPageTypeDetailsAdapter
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants.IS_SHOW_TREATED_ARGS
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ONE_HUNDRED
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentChargebackHomeBinding
import com.google.android.material.tabs.TabLayoutMediator

class ChargebackHomeFragment : BaseFragment(), CieloNavigationListener {

    private var navigation: CieloNavigation? = null

    private var _binding: FragmentChargebackHomeBinding? = null
    private val binding get() = _binding

    private val isShowTreated: Boolean by lazy {
        arguments?.getBoolean(IS_SHOW_TREATED_ARGS, false) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChargebackHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupViewPager()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showHelpButton(isShow = true)
            navigation?.showContainerButton(isShow = false)
        }
    }

    private fun setupViewPager() {
        val titlesArray = resources.getStringArray(R.array.titles_page_chargeback)
        val adapter = ChargebackPageTypeDetailsAdapter(childFragmentManager, lifecycle, titlesArray)
        binding?.viewPagerChargeback?.adapter = adapter
        binding?.let {
            TabLayoutMediator(it.tabChargeback, it.viewPagerChargeback) { tab, position ->
                tab.text = titlesArray[position]
            }.attach()
        }
        checkIfNeedChangeScreen()
    }

    private fun checkIfNeedChangeScreen() {
        if (isShowTreated) {
            binding?.viewPagerChargeback?.apply {
                postDelayed({
                    val newCurrentItem = currentItem
                    currentItem = newCurrentItem + ONE
                }, ONE_HUNDRED.toLong())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
