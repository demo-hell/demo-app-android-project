package br.com.mobicare.cielo.idOnboarding.updateUser.p2Policy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingP2ForeignSuccessBinding

class IDOnboardingP2ForeignSuccessFragment : BaseFragment(), CieloNavigationListener {

    private var navigation: CieloNavigation? = null
    private var binding: FragmentIdOnboardingP2ForeignSuccessBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentIdOnboardingP2ForeignSuccessBinding
        .inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupClickListeners()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupClickListeners() {
        binding?.apply {
            btnClose.setOnClickListener {
                baseLogout()
            }
            btnNext.setOnClickListener {
                baseLogout()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}