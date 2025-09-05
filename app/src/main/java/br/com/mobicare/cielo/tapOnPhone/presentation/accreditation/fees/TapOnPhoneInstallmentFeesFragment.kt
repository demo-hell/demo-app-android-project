package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.fees

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.util.TWO
import br.com.cielo.libflue.util.extensions.fromHtml
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.helpers.goToHelpCenterHome
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Brand
import br.com.mobicare.cielo.databinding.FragmentTapOnPhoneInstallmentsFeesBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneAnalytics
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneGA4
import br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.fees.adapter.TapOnPhoneInstallmentFeesAdapter
import org.koin.android.ext.android.inject

class TapOnPhoneInstallmentFeesFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentTapOnPhoneInstallmentsFeesBinding? = null
    private val args: TapOnPhoneInstallmentFeesFragmentArgs by navArgs()

    private val brandRate: Array<Brand>? by lazy {
        args.brandratelistargs
    }

    private val settlementTiming: Int by lazy {
        args.settlementtimingargs
    }

    private val analytics: TapOnPhoneAnalytics by inject()
    private val ga4: TapOnPhoneGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTapOnPhoneInstallmentsFeesBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        analytics.logScreenView(TapOnPhoneAnalytics.FEES_SCREEN_PATH, javaClass)
        ga4.logScreenView(TapOnPhoneGA4.SCREEN_VIEW_FEES_BY_INSTALLMENT)
    }

    private fun setupListeners() {
        binding?.apply {
            ivRecebaRapidoFAQ.visible(settlementTiming == TWO)
            tvRecebaRapidoFAQ.apply {
                visible(settlementTiming == TWO)
                setOnClickListener {
                    analytics.logScreenActions(
                        TapOnPhoneAnalytics.FEES,
                        Action.BOTAO,
                        text.toString()
                    )
                    ga4.logClick(
                        screenName = TapOnPhoneGA4.SCREEN_VIEW_FEES_BY_INSTALLMENT,
                        contentType = GoogleAnalytics4Values.LINK,
                        contentName = text.toString()
                    )
                    requireContext().goToHelpCenterHome()
                }
            }
        }
    }

    private fun setupView() {
        setupSubtitle()
        setupRecyclerView()
        setupReceivingInfo()
        setupReceivingObservation()
    }

    private fun setupSubtitle() {
        binding?.tvSubtitle?.apply {
            visible(settlementTiming == TWO)
            fromHtml(
                resources.getQuantityString(
                    R.plurals.tap_on_phone_installment_fees_subtitle,
                    settlementTiming,
                    settlementTiming
                )
            )
        }
    }

    private fun setupRecyclerView() {
        brandRate?.let { itBandRate ->
            binding?.rvInstallmentFees?.apply {
                layoutManager =
                    LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
                adapter = TapOnPhoneInstallmentFeesAdapter(itBandRate.asList(), requireContext())
            }
        }
    }

    private fun setupReceivingInfo() {
        binding?.recebaRapidoInfoInclude?.apply {
            tvCreditAtOnceValue.fromHtml(getCustomText())
            tvCreditWithInstallmentValue.fromHtml(getCustomText())
        }
    }

    private fun setupReceivingObservation() {
        binding?.tvReceivingObservation?.apply {
            text = getString(R.string.tap_on_phone_receiving_observation, settlementTiming)
        }
    }

    private fun getCustomText() = resources.getQuantityString(
        R.plurals.tap_on_phone_rr_receivement_type_x,
        settlementTiming,
        settlementTiming
    )

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}