package br.com.mobicare.cielo.biometricToken.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.biometricToken.analytics.BiometricTokenGA4
import br.com.mobicare.cielo.biometricToken.analytics.BiometricTokenGA4.Companion.SCREEN_VIEW_AUTHORIZATION
import br.com.mobicare.cielo.biometricToken.analytics.BiometricTokenGA4.Companion.SCREEN_VIEW_BIOMETRIC_ERROR
import br.com.mobicare.cielo.biometricToken.analytics.BiometricTokenGA4.Companion.SCREEN_VIEW_BIOMETRIC_RERUN
import br.com.mobicare.cielo.biometricToken.analytics.BiometricTokenGA4.Companion.SCREEN_VIEW_BIOMETRIC_TIPS_SELFIE
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentBiometricTokenHomeBinding
import br.com.mobicare.cielo.extensions.moveToHome
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class BiometricTokenHomeFragment : BaseFragment(), BiometricTokenHomeContract.View,
    CieloNavigationListener {

    private var binding: FragmentBiometricTokenHomeBinding? = null
    private var navigation: CieloNavigation? = null
    private val presenter: BiometricTokenHomePresenter by inject {
        parametersOf(this)
    }
    private val analyticsGA4: BiometricTokenGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentBiometricTokenHomeBinding.inflate(
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
        presenter.getUserName()
        analyticsGA4.logScreenView(SCREEN_VIEW_AUTHORIZATION)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    override fun setupView(name: String) {
        binding?.apply {
            tvBiometricTitle.text = getString(R.string.biometric_home_title, name)

            btContinueWithoutAccess.setOnClickListener {
                analyticsGA4.logAuthorizeInstalationClick()
                activity?.moveToHome() ?: baseLogout()
            }

            btAuthorize.setOnClickListener {
                analyticsGA4.logAuthorizeInstalationSignUp()
                findNavController().navigate(BiometricTokenHomeFragmentDirections.
                actionBiometricTokenHomeFragmentToBiometricTokenSelfieFragment(
                    SCREEN_VIEW_BIOMETRIC_TIPS_SELFIE,
                    SCREEN_VIEW_BIOMETRIC_RERUN,
                    SCREEN_VIEW_BIOMETRIC_ERROR))
            }

            toolbar.icArrowLeft.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

}