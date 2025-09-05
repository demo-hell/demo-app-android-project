package br.com.mobicare.cielo.turboRegistration.presentation.registration

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.databinding.FragmentErrorBinding
import br.com.mobicare.cielo.turboRegistration.analytics.TurboRegistrationAnalytics
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationStepError

class ErrorFragment : BaseFragment(), CieloNavigationListener {

    private val binding: FragmentErrorBinding by viewBinding()
    private lateinit var navigation: CieloNavigation

    private val args: ErrorFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupScreen()
        setListener()
    }

    private fun setupScreen() {
        binding.tvMessage.text = args.apiError
        when (UserPreferences.getInstance().turboRegistrationErrorStep) {
            RegistrationStepError.ADDRESS.ordinal -> binding.tvTitleMessage.text = getString(R.string.error_address_title)
            RegistrationStepError.MONTHLY_INCOME.ordinal -> binding.tvTitleMessage.text =
                if (UserPreferences.getInstance().isLegalEntity) {
                    getString(R.string.error_monthly_invoice_title)
                } else {
                    getString(R.string.error_monthly_income_title)
                }

            RegistrationStepError.BUSINESS_SECTOR.ordinal -> binding.tvTitleMessage.text = getString(R.string.error_business_sector_title)
            RegistrationStepError.BANK.ordinal -> binding.tvTitleMessage.text = getString(R.string.error_bank_title)
            else -> {}
        }
    }

    private fun setListener() {
        val destination = when (UserPreferences.getInstance().turboRegistrationErrorStep) {
            RegistrationStepError.MONTHLY_INCOME.ordinal -> R.id.nav_monthly_income
            RegistrationStepError.BUSINESS_SECTOR.ordinal -> R.id.nav_line_business
            RegistrationStepError.BANK.ordinal -> R.id.nav_bank_data
            else -> R.id.nav_address
        }

        binding.closeButton.setOnClickListener {
            val resultIntent = android.content.Intent()
            requireActivity().setResult(Activity.RESULT_CANCELED, resultIntent)
            requireActivity().finish()
        }

        binding.fillAgainBtn.setOnClickListener {
            TurboRegistrationAnalytics.clickEventTypeAgainError(args.apiError)
            findNavController().popBackStack(destination, false)
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation.setNavigationListener(this)
            navigation.onStepChanged(RegistrationStepError.UNDEFINED.ordinal)
            navigation.setupToolbar(
                title = "",
                isCollapsed = true,
                subtitle = ""
            )
            navigation.showBackButton(isShow = false)
        }
    }
}