package br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.tipoVenda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_PAYMENT_LINK_DTO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.help.HelpMainActivity
import br.com.mobicare.cielo.databinding.FragmentTipoVendaPagamentoPorLinkBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.PAGAMENTO_POR_LINK
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import br.com.mobicare.cielo.pagamentoLink.domains.TypeSaleEnum
import br.com.mobicare.cielo.pagamentoLink.presentation.PPL_HELP_ID
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4.Companion.SCREEN_VIEW_PAYMENT_LINK_SELECT_SALE_TYPE
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class TipoVendaPagamentoPorLinkFragment :
    BaseFragment(), TipoVendaPagamentoPorLinkContract.View, CieloNavigationListener {

    private var _binding: FragmentTipoVendaPagamentoPorLinkBinding? = null
    private val binding get() = _binding!!

    private val presenter: TipoVendaPagamentoPorLinkPresenter by inject {
        parametersOf(this)
    }

    private var navigation: CieloNavigation? = null

    private val analytics: SuperLinkAnalytics by inject()
    private val ga4: PaymentLinkGA4 by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentTipoVendaPagamentoPorLinkBinding
            .inflate(inflater, container, false)
            .also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureNavigation()
        configureListeners()
        verifyProductDeliveryOption()
    }

    override fun onResume() {
        super.onResume()
        if (isAttached()) logScreenView()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = (requireActivity() as CieloNavigation).also {
                it.setTextToolbar(getString(R.string.text_link_type))
                it.showButton(false)
                it.setNavigationListener(this)
                it.showContent(true)
            }
        }
    }

    private fun configureListeners() {
        binding.apply {
            deliveryProductButtonView.setOnClickListener(::onDeliveryProductClick)
            chargeAmountButtonView.setOnClickListener(::onChargeAmountClick)
            recurrentSalesButtonView.setOnClickListener(::onRecurrentSaleClick)
        }
    }

    private fun verifyProductDeliveryOption() {
        presenter.verifyProductDeliveryFeature()
    }

    override fun onHelpButtonClicked() {
        if (isAttached())
            analytics.gaSendButtonTooltip(SuperLinkAnalytics.HELP_CENTER, PAGAMENTO_POR_LINK)

        HelpMainActivity.create(requireActivity(), getString(R.string.text_pg_lk_help_title), PPL_HELP_ID)
    }

    override fun onShowProductDeliveryOption(show: Boolean) {
        binding.deliveryProductButtonView.visible(show)
    }

    private fun onDeliveryProductClick(view: View?) {
        logClickButton(getString(R.string.text_view_finality_superlink_send_product))
        navigateToCreateLinkPayment(TypeSaleEnum.SEND_PRODUCT)
    }

    private fun onChargeAmountClick(view: View?) {
        logClickButton(getString(R.string.text_view_finality_superlink_charge_value))
        navigateToCreateLinkPayment(TypeSaleEnum.CHARGE_AMOUNT)
    }

    private fun onRecurrentSaleClick(view: View?) {
        logClickButton(getString(R.string.text_view_finality_superlink_recurrent_sale))
        navigateToCreateLinkPayment(TypeSaleEnum.RECURRENT_SALE)
    }

    private fun navigateToCreateLinkPayment(typeSale: TypeSaleEnum) {
        Bundle().let {
            it.putParcelable(ARG_PARAM_PAYMENT_LINK_DTO, PaymentLinkDTO(typeSale = typeSale))
            navigation?.saveData(it)
        }
        findNavController()
            .navigate(TipoVendaPagamentoPorLinkFragmentDirections
                .actionTipoVendaPagamentoPorLinkFragmentToCreateLinkPaymentFragment())
    }

    private fun logScreenView(){
        analytics.sendGaScreenView(SuperLinkAnalytics.LINK_TYPE_SCREEN)
        ga4.logScreenView(SCREEN_VIEW_PAYMENT_LINK_SELECT_SALE_TYPE)
    }

    private fun logClickButton(label: String){
        analytics.sendGaLinkTypeButton(label)
        ga4.logSelectContentSelectSaleType(label)
    }

}