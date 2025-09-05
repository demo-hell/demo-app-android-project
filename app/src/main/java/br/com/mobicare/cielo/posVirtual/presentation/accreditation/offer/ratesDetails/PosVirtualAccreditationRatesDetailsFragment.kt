package br.com.mobicare.cielo.posVirtual.presentation.accreditation.offer.ratesDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Brand
import br.com.mobicare.cielo.databinding.FragmentPosVirtualAccreditationOfferRatesDetailsBinding
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.minhasVendas.fragments.common.ScrollControlledLinearManager
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics.Companion.CONTENT_COMPONENT_RATES_DETAILS_OPEN_FAQ
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics.Companion.SCREEN_VIEW_ACCREDITATION_RATES
import br.com.mobicare.cielo.posVirtual.presentation.accreditation.offer.ratesDetails.adapter.PosVirtualAccreditationRatesDetailsBrandAdapter
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualProductTypeEnum
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.android.ext.android.inject

class PosVirtualAccreditationRatesDetailsFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentPosVirtualAccreditationOfferRatesDetailsBinding? = null
    private var navigation: CieloNavigation? = null
    private var scrollControlledLinearManager: ScrollControlledLinearManager? = null
    private var brandAdapter = PosVirtualAccreditationRatesDetailsBrandAdapter()

    private val args: PosVirtualAccreditationRatesDetailsFragmentArgs by navArgs()

    private val ga4: PosVirtualAnalytics by inject()
    private val screenPath get() = SCREEN_VIEW_ACCREDITATION_RATES.format(typeProduct.labelGa4)

    private val isAcceptAutomaticReceipt by lazy {
        args.posvirtualisacceptautomaticreceiptargs
    }

    private val typeProduct: PosVirtualProductTypeEnum by lazy {
        args.posvirtualtypeproductargs
    }

    private val brands: Array<Brand> by lazy {
        args.posvirtualbrandsargs
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPosVirtualAccreditationOfferRatesDetailsBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupListeners()
        setupView()
        setupCardRR()
        setupRecyclerView()

        logScreenView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.apply {
                configureCollapsingToolbar(setupConfiguratorToolbar())
                showButton(false)
            }
        }
    }

    private fun setupConfiguratorToolbar(): CollapsingToolbarBaseActivity.Configurator {
        return CollapsingToolbarBaseActivity.Configurator(
            toolbarTitle = getString(R.string.pos_virtual_rates_details_title, typeProduct.label),
            toolbarTitleAppearance = CollapsingToolbarBaseActivity.ToolbarTitleAppearance(
                collapsed = R.style.CollapsingToolbar_Collapsed_BlackBold,
                expanded = R.style.CollapsingToolbar_Expanded_BlackBold,
            )
        )
    }

    private fun setupListeners() {
        binding?.tvBtnOpenFAQ?.apply {
            setOnClickListener {
                logOpenFaq(text.toString())

                requireActivity().openFaq(
                    ConfigurationDef.TAG_HELP_CENTER_RECEBA_RAPIDO,
                    getString(R.string.pos_virtual_rates_details_title_faq_automatic_receipt)
                )
            }
        }
    }

    private fun setupView() {
        binding?.apply {
            tvPeriodDebit.fromHtml(R.string.pos_virtual_rates_details_period_debit)
            tvPeriodCashCredit.fromHtml(
                if (isAcceptAutomaticReceipt) {
                    R.string.pos_virtual_rates_details_period_cash_credit_rr_enabled
                } else {
                    R.string.pos_virtual_rates_details_period_cash_credit_rr_disabled
                }
            )
            tvPeriodInstallmentCredit.fromHtml(
                if (isAcceptAutomaticReceipt) {
                    R.string.pos_virtual_rates_details_period_installment_cash_rr_enabled
                } else {
                    R.string.pos_virtual_rates_details_period_installment_cash_rr_disabled
                }
            )
        }
    }

    private fun setupCardRR() {
        binding?.apply {
            ivIconCardRR.visible(isAcceptAutomaticReceipt.not())

            if (isAcceptAutomaticReceipt) {
                tvTitleCardRR.text =
                    getString(R.string.pos_virtual_rates_details_title_card_rr_enabled)
                tvDescriptionCardRR.text = getString(
                    if (typeProduct == PosVirtualProductTypeEnum.PAYMENT_LINK) {
                        R.string.pos_virtual_rates_details_description_card_rr_enabled_payment_link
                    } else {
                        R.string.pos_virtual_rates_details_description_card_rr_enabled_cielo_tap
                    }
                )
                clCardRR.setCustomDrawable {
                    radius = R.dimen.dimen_8dp
                    solidColor = R.color.cloud_100
                }
            } else {
                tvTitleCardRR.text =
                    getString(R.string.pos_virtual_rates_details_title_card_rr_disabled)
                tvDescriptionCardRR.text =
                    getString(R.string.pos_virtual_rates_details_description_card_rr_disabled)
                clCardRR.setCustomDrawable {
                    radius = R.dimen.dimen_8dp
                    solidColor = R.color.purple_100
                }
            }
        }
    }

    private fun setupRecyclerView() {
        scrollControlledLinearManager = ScrollControlledLinearManager(requireContext())
        brandAdapter.setBrands(brands.toList())

        binding?.apply {
            rvBrands.adapter = brandAdapter
            rvBrands.layoutManager = scrollControlledLinearManager
        }
    }

    private fun logScreenView() = ga4.logScreenView(screenPath)

    private fun logOpenFaq(labelButton: String) {
        ga4.logClick(
            screenPath,
            String.format(CONTENT_COMPONENT_RATES_DETAILS_OPEN_FAQ, typeProduct.labelGa4),
            GoogleAnalytics4Values.BUTTON,
            labelButton
        )
    }

}