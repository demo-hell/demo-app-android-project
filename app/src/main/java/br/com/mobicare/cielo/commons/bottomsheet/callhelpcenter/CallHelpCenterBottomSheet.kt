package br.com.mobicare.cielo.commons.bottomsheet.callhelpcenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.constants.HelpCenter.PHONE_HELP_CENTER_CAPITAL
import br.com.mobicare.cielo.commons.constants.HelpCenter.PHONE_HELP_CENTER_OTHERS
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.setupBottomSheet
import br.com.mobicare.cielo.databinding.CallHelpCenterBottomSheetBinding
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP1AnalyticsGA
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject

class CallHelpCenterBottomSheet : BottomSheetDialogFragment() {

    private var _binding: CallHelpCenterBottomSheetBinding? = null
    private val binding get() = _binding
    private val analyticsGA: IDOnboardingP1AnalyticsGA by inject()
    private var screenNameGA: String = EMPTY

    companion object {
        fun newInstance(): CallHelpCenterBottomSheet {
            return CallHelpCenterBottomSheet()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupBottomSheet(dialog = dialog,
            action = { dismiss() }
        )
        _binding = CallHelpCenterBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListeners()
        loadArguments()
        analyticsGA.logIDCallHelpCenterDisplay(screenNameGA)

    }
    private fun loadArguments() {
        arguments?.let { itBundle ->
            this.screenNameGA = itBundle.getString(
                GoogleAnalytics4Events.ScreenView.SCREEN_NAME) as String
        }
    }

    fun setupView(){
        binding?.tvPhoneNumberCapital?.text = getString(R.string.phone_number_capital)
        binding?.tvPhoneNumberOthers?.text = getString(R.string.phone_number_others)
    }

    fun setupListeners() {
        binding?.tvPhoneNumberCapital?.setOnClickListener {
            Utils.callPhone(requireActivity(), PHONE_HELP_CENTER_CAPITAL)
            analyticsGA.logIDCallHelpCenterClick(screenNameGA, PHONE_HELP_CENTER_CAPITAL)
        }
        binding?.tvPhoneNumberOthers?.setOnClickListener {
            Utils.callPhone(requireActivity(), PHONE_HELP_CENTER_OTHERS)
            analyticsGA.logIDCallHelpCenterClick(screenNameGA, PHONE_HELP_CENTER_OTHERS)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}