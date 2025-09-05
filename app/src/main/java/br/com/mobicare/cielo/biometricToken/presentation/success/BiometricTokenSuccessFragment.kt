package br.com.mobicare.cielo.biometricToken.presentation.success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.biometricToken.analytics.BiometricTokenGA4
import br.com.mobicare.cielo.biometricToken.analytics.BiometricTokenGA4.Companion.SCREEN_VIEW_SAFE_DEVICE
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentBiometricTokenSuccessBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.moveToHome
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import org.koin.android.ext.android.inject

class BiometricTokenSuccessFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentBiometricTokenSuccessBinding? = null
    private var navigation: CieloNavigation? = null

    private val analyticsGA4: BiometricTokenGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentBiometricTokenSuccessBinding.inflate(
        inflater,
        container,
        false
    ).also {
        binding = it
    }.root

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupView()
        analyticsGA4.logScreenView(SCREEN_VIEW_SAFE_DEVICE)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupView() {
        binding?.apply {
            tvInfoText.fromHtml(R.string.biometric_success_alert_message)

            btFinish.setOnClickListener {
                requireActivity().moveToHome()
            }
        }
    }

}