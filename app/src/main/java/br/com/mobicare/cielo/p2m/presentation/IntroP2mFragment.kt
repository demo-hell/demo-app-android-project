package br.com.mobicare.cielo.p2m.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.WhatsApp
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.Utils.openBrowser
import br.com.mobicare.cielo.databinding.FragmentIntroP2mBinding
import br.com.mobicare.cielo.p2m.analytics.P2MGA4
import org.koin.android.ext.android.inject

class IntroP2mFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentIntroP2mBinding? = null
    private var navigation: CieloNavigation? = null

    private val ga4: P2MGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentIntroP2mBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupObservable()
    }

    override fun onResume() {
        super.onResume()
        ga4.logScreenView(P2MGA4.SCREEN_VIEW_P2M_INTRODUCTION)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showContainerButton(isShow = true)
            navigation?.showButton(true)
            navigation?.setTextButton(getString(R.string.title_p2m_btn2))
            navigation?.configureCollapsingToolbar(CollapsingToolbarBaseActivity.Configurator(
                show = true,
                isExpanded = false,
                disableExpandableMode = false,
                showBackButton = true
            ))
        }
    }

    override fun onButtonClicked(labelButton: String) {
        goToP2mAccreditation()
    }

    private fun goToP2mAccreditation(){
        findNavController().navigate(
            IntroP2mFragmentDirections
                .actionIntroP2mFragmentToP2mAccreditFragment()
        )
    }

    private fun setupObservable() {
        binding?.apply {
            clContainerKonwMore.apply {
                isClickable = true
                setOnClickListener {
                    openBrowser(requireActivity(), WhatsApp.LINK_TO_SALES_WHATS_APP)
                }
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}


