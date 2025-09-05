package br.com.mobicare.cielo.p2m.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.spannable.htmlTextFormat
import br.com.mobicare.cielo.commons.utils.spannable.htmlTextFormatWithValue
import br.com.mobicare.cielo.databinding.FragmentP2mAccreditBinding
import br.com.mobicare.cielo.p2m.analytics.P2MAnalytics
import br.com.mobicare.cielo.p2m.analytics.P2MAnalytics.Companion.P2M_ANALITYCS_BACK
import br.com.mobicare.cielo.p2m.analytics.P2MAnalytics.Companion.P2M_ANALITYCS_HELP
import br.com.mobicare.cielo.p2m.analytics.P2MAnalytics.Companion.P2M_ANALITYCS_NEXT
import br.com.mobicare.cielo.p2m.analytics.P2MAnalytics.Companion.P2M_ANALITYCS_RECEIPT_DEADLINE
import br.com.mobicare.cielo.p2m.analytics.P2MAnalytics.Companion.P2M_ANALITYCS_RECEIPT_DEADLINE_THIRTY_DAYS
import br.com.mobicare.cielo.p2m.analytics.P2MAnalytics.Companion.P2M_ANALITYCS_RECEIPT_DEADLINE_TWO_DAYS
import br.com.mobicare.cielo.p2m.analytics.P2MAnalytics.Companion.SCREENVIEW_P2M_CHOOSE_FORM_OF_RECEIPT
import br.com.mobicare.cielo.p2m.analytics.P2MGA4
import br.com.mobicare.cielo.p2m.domain.model.TaxModel
import br.com.mobicare.cielo.p2m.presentation.viewmodel.P2mAcreditationViewModel
import br.com.mobicare.cielo.p2m.utils.UiTaxTextState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class P2mAccreditFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentP2mAccreditBinding? = null
    private val viewModel: P2mAcreditationViewModel by viewModel()
    private val analytics: P2MAnalytics by inject()
    private val ga4: P2MGA4 by inject()
    private var isSelected = true
    private var isThirtyDaysSelected = false
    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentP2mAccreditBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupObservable()
        initSetup()
        observeTaxTextState()
    }

    override fun onResume() {
        super.onResume()
        analytics.logScreenView(
            name = SCREENVIEW_P2M_CHOOSE_FORM_OF_RECEIPT,
            className = this.javaClass
        )
        analytics.logScreenActions(
            P2M_ANALITYCS_RECEIPT_DEADLINE,
            EMPTY_STRING,
            Label.BOTAO,
            P2M_ANALITYCS_RECEIPT_DEADLINE_TWO_DAYS
        )
        ga4.logScreenView(P2MGA4.SCREEN_VIEW_P2M_CHOOSE)
    }

    private fun initSetup() {
        viewModel.getFeatureToggle()
        selectTextCardBehavior(isSelected)
    }

    private fun observeTaxTextState() {
        viewModel.featureToggle.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiTaxTextState.Success -> {
                    state.data?.let { onSuccess(it) }
                }
            }
        }
    }

    private fun onSuccess(taxModel: TaxModel) {
        setupTextCardOne(taxModel)
        setupTextCardTwo(taxModel)
        setupTextCardInfoTax(taxModel)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showButton(true)
            navigation?.setTextButton(getString(R.string.p2m_btn_next))
            navigation?.enableButton(true)
            navigation?.configureCollapsingToolbar(
                CollapsingToolbarBaseActivity.Configurator(
                    show = true,
                    isExpanded = true,
                    disableExpandableMode = false,
                    toolbarMenu = CollapsingToolbarBaseActivity.ToolbarMenu(
                        menuRes = R.menu.menu_common_only_faq_blue,
                        onOptionsItemSelected = {
                            if ((it.itemId == R.id.action_help)) {
                                showFaqOnBrowser()
                            } else {
                                analytics.logScreenActions(
                                    P2M_ANALITYCS_RECEIPT_DEADLINE,
                                    EMPTY_STRING,
                                    Label.BOTAO,
                                    P2M_ANALITYCS_BACK
                                )
                            }
                        }
                    ),
                    showBackButton = true,
                    toolbarTitle = getString(R.string.p2m_choose_period_time_title)
                )
            )
        }
    }

    override fun onButtonClicked(labelButton: String) {
        analytics.logScreenActions(
            P2M_ANALITYCS_RECEIPT_DEADLINE,
            EMPTY_STRING,
            Label.BOTAO,
            P2M_ANALITYCS_NEXT
        )
        goToP2mTermFragment()
    }

    private fun showFaqOnBrowser() {
        analytics.logScreenActions(
            P2M_ANALITYCS_RECEIPT_DEADLINE,
            EMPTY_STRING,
            Label.BOTAO,
            P2M_ANALITYCS_HELP
        )
        Utils.openBrowser(requireActivity(), WhatsApp.LINK_TO_SALES_WHATS_APP)
    }

    private fun goToP2mTermFragment() {
        findNavController().navigate(
            P2mAccreditFragmentDirections
                .actionP2mAccreditFragmentToP2mTermFragment(isThirtyDaysSelected)
        )
    }

    private fun setupObservable() {
        binding?.apply {
            taxTwoDaysP2m.apply {
                containerButtonTax.setOnClickListener {
                    analytics.logScreenActions(
                        P2M_ANALITYCS_RECEIPT_DEADLINE,
                        EMPTY_STRING,
                        Label.BOTAO,
                        P2M_ANALITYCS_RECEIPT_DEADLINE_TWO_DAYS
                    )
                    selectTextCardBehavior(isSelected = true)

                }
                ivRadioButton.setOnClickListener {
                    selectTextCardBehavior(isSelected = true)
                }
            }
            taxThirtyDaysP2m.apply {
                containerButtonTax.setOnClickListener {
                    selectTextCardBehavior(isSelected = false)
                    analytics.logScreenActions(
                        P2M_ANALITYCS_RECEIPT_DEADLINE,
                        EMPTY_STRING,
                        Label.BOTAO,
                        P2M_ANALITYCS_RECEIPT_DEADLINE_THIRTY_DAYS
                    )

                }
                ivRadioButton.setOnClickListener {
                    selectTextCardBehavior(isSelected = false)
                }
            }
        }
    }

    private fun setupTextCardOne(taxModel: TaxModel) {
        taxModel.taxes.forEach { taxes ->
            binding?.apply {
                if (taxes.id == ONE) {
                    taxes.values.forEach { tax ->
                        when (tax.deadline) {
                            ONE -> taxTwoDaysP2m.apply {
                                tvTitleSelectTax.text =
                                    getString(R.string.p2m_choose_period_time_two_days).htmlTextFormat()
                                tvDeadlineDebitP2m.text =
                                    getString(R.string.p2m_receipt_deadline_one_day)
                                tvDebitP2m.text = context?.let { context ->
                                    R.string.p2m_tax_period_debit.htmlTextFormatWithValue(
                                        context,
                                        tax.value.toString().replace(".", ",")
                                    )
                                }
                            }
                            TWO -> taxTwoDaysP2m.apply {
                                tvCreditP2m.text = context?.let { context ->
                                    R.string.p2m_tax_period_credit_spots_rr.htmlTextFormatWithValue(
                                        context,
                                        tax.value.toString().replace(".", ",")
                                    )
                                }
                                tvDeadlineCreditP2m.text =
                                    getString(R.string.p2m_receipt_deadline_two_days)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupTextCardTwo(taxModel: TaxModel) {
        taxModel.taxes.forEach { taxes ->
            binding?.apply {
                if (taxes.id == TWO) {
                    taxes.values.forEach { tax ->
                        when (tax.deadline) {
                            ONE -> taxThirtyDaysP2m.apply {
                                tvTitleSelectTax.text =
                                    getString(R.string.p2m_choose_period_time_thirty_days).htmlTextFormat()
                                tvDebitP2m.text = context?.let { context ->
                                    R.string.p2m_tax_period_debit.htmlTextFormatWithValue(
                                        context,
                                        tax.value.toString().replace(".", ",")
                                    )
                                }
                                tvDeadlineDebitP2m.text =
                                    getString(R.string.p2m_receipt_deadline_one_day)
                            }
                            THIRTY -> taxThirtyDaysP2m.apply {
                                tvCreditP2m.text = context?.let { context ->
                                    R.string.p2m_tax_period_credit_spots.htmlTextFormatWithValue(
                                        context,
                                        tax.value.toString().replace(".", ",")
                                    )
                                }
                                tvDeadlineCreditP2m.text =
                                    getString(R.string.p2m_receipt_deadline_thirty_days)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupTextCardInfoTax(taxModel: TaxModel) {
        taxModel.taxes.forEach { taxes ->
            binding?.apply {
                if (taxes.id == THREE) {
                    taxInfoBoxP2m.tvTaxInfo.text = context?.let { context ->
                        R.string.p2m_info_tax_months.htmlTextFormatWithValue(
                            context,
                            taxes.period
                        )
                    }
                    taxes.values.forEach { tax ->
                        when (tax.deadline) {
                            ONE -> taxInfoBoxP2m.apply {
                                tvTaxDebit.text = context?.let { context ->
                                    R.string.p2m_info_tax_debit.htmlTextFormatWithValue(
                                        context,
                                        tax.value.toString().replace(".", ",")
                                    )
                                }
                            }
                            TWO -> taxInfoBoxP2m.apply {
                                tvTaxCreditTwoDays.text = context?.let { context ->
                                    R.string.p2m_info_tax_credit_two_days.htmlTextFormatWithValue(
                                        context,
                                        tax.value.toString().replace(".", ",")
                                    )
                                }
                            }
                            THIRTY -> taxInfoBoxP2m.apply {
                                tvTaxCreditThirtyDays.text = context?.let { context ->
                                    R.string.p2m_info_tax_credit_thirty_days.htmlTextFormatWithValue(
                                        context,
                                        tax.value.toString().replace(".", ",")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun selectTextCardBehavior(isSelected: Boolean) {
        if (isSelected) {
            binding?.apply {
                taxTwoDaysP2m.apply {
                    containerButtonTax.setBackgroundResource(R.drawable.background_brand_400_stroke_2_dp_radius_8_dp)
                    ivRadioButton.gone()
                    ivRadioButtonSelect.apply {
                        visible()
                        contentDescription =
                            getString(R.string.btn_checked_two_days_accessibility)
                    }
                }
                taxThirtyDaysP2m.apply {
                    containerButtonTax.setBackgroundResource(R.drawable.background_cloud_200_stroke_2_dp_radius_8_dp)
                    ivRadioButton.apply {
                        visible()
                        contentDescription =
                            getString(R.string.btn_not_checked_two_days_accessibility)
                    }
                    ivRadioButtonSelect.gone()
                    isThirtyDaysSelected = false
                }
            }
        } else {
            binding?.apply {
                taxThirtyDaysP2m.apply {
                    containerButtonTax.setBackgroundResource(R.drawable.background_brand_400_stroke_2_dp_radius_8_dp)
                    ivRadioButton.gone()
                    ivRadioButtonSelect.apply {
                        visible()
                        contentDescription =
                            getString(R.string.btn_checked_thirty_days_accessibility)
                    }
                    isThirtyDaysSelected = true
                }
                taxTwoDaysP2m.apply {
                    containerButtonTax.setBackgroundResource(R.drawable.background_cloud_200_stroke_2_dp_radius_8_dp)
                    ivRadioButton.apply {
                        visible()
                        contentDescription =
                            getString(R.string.btn_not_checked_thirty_days_accessibility)
                    }
                    ivRadioButtonSelect.gone()
                }
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}