package br.com.mobicare.cielo.pagamentoLink.orders.orderdetail

import android.app.Dialog
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.EMPTY_STRING
import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.error.ErrorCallBackApi
import br.com.mobicare.cielo.commons.utils.addMaskCPForCNPJ
import br.com.mobicare.cielo.commons.utils.bottomSheetGeneric
import br.com.mobicare.cielo.commons.utils.convertIsoDateToBr
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.FragmentLinkOrderDetailBinding
import br.com.mobicare.cielo.databinding.LinkOrderDetailCustomerDataBinding
import br.com.mobicare.cielo.databinding.LinkOrderDetailSaleFirstBinding
import br.com.mobicare.cielo.databinding.LinkOrderDetailSaleSecondBinding
import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLink
import br.com.mobicare.cielo.pagamentoLink.orders.model.Order
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.ResponseMotoboy
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.dialog.BottomSheetGenericFragment
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4.Companion.SCREEN_VIEW_PAYMENT_SALE_DETAILS
import org.jetbrains.anko.browse
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class LinkOrderDetailFragment : BaseFragment(), LinkOrderDetailView, CieloNavigationListener {

    private var binding: FragmentLinkOrderDetailBinding? = null
    private var linkOrderDetailCustomerDataBinding: LinkOrderDetailCustomerDataBinding? = null
    private var linkOrderDetailSaleFirstBinding: LinkOrderDetailSaleFirstBinding? = null
    private var linkOrderDetailSaleSecondBinding: LinkOrderDetailSaleSecondBinding? = null

    private lateinit var order: Order
    private lateinit var paymentLink: PaymentLink

    private val presenter: LinkOrderDetailPresenterImpl by inject {
        parametersOf(this)
    }

    private var cieloNavigation: CieloNavigation? = null

    private val ga4: PaymentLinkGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLinkOrderDetailBinding.inflate(inflater, container, false)

        binding?.root?.let {
            linkOrderDetailCustomerDataBinding = LinkOrderDetailCustomerDataBinding.bind(it)
            linkOrderDetailSaleFirstBinding = LinkOrderDetailSaleFirstBinding.bind(it)
            linkOrderDetailSaleSecondBinding = LinkOrderDetailSaleSecondBinding.bind(it)
        }

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding = null
        linkOrderDetailCustomerDataBinding = null
        linkOrderDetailSaleFirstBinding = null
        linkOrderDetailSaleSecondBinding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onResume() {
        super.onResume()
        logScreenView()
    }

    private fun init() {
        configureNavigation()
        arguments?.let {
            LinkOrderDetailFragmentArgs.fromBundle(it).let { itSafeArgs ->
                this.order = itSafeArgs.orderdetailextra
                this.paymentLink = itSafeArgs.paymentlinkextras
            }
            this.paymentLink.name?.let { name -> this.cieloNavigation?.setTextToolbar(name) }
            presenter.onCreate(order)
        }
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            presenter.onCreate(order)
        }
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar(getString(R.string.payment_link_toolbar_order_detail))
            this.cieloNavigation?.showButton(false)
            this.cieloNavigation?.showHelpButton(false)
            this.cieloNavigation?.setNavigationListener(this)
            this.cieloNavigation?.showContent(true)
        }
    }

    override fun initCustomer(order: Order) {
        this.cieloNavigation?.showContent(true)
        this.order = order

        linkOrderDetailCustomerDataBinding?.apply {
            textViewCustomerName.text = order.customer?.name
            textViewPhoneData.text =
                addMaskCPForCNPJ(order.customer?.phone, getString(R.string.mask_cellphone_step5))
            textViewEmailData.text = order.customer?.email

            textViewAddressData.text =
                if (order.shipping?.address?.streetAddress.isNullOrEmpty().not()) {
                    StringBuilder()
                        .append("${order.shipping?.address?.streetAddress.orEmpty()}, ")
                        .append("${order.shipping?.address?.number.orEmpty()} - ")
                        .append("${order.shipping?.address?.neighborhood.orEmpty()}, ")
                        .append("${order.shipping?.address?.zipCode?.toString().orEmpty()}\n")
                        .append("${order.shipping?.address?.city.orEmpty()} - ")
                        .append(order.shipping?.address?.state.orEmpty())
                } else {
                    EMPTY_STRING
                }

            if (order.shipping?.address?.streetAddress.isNullOrEmpty().not()) {
                textViewAddressData.visible()
                textViewAddressLabel.visible()
            } else {
                textViewAddressData.gone()
                textViewAddressLabel.gone()
            }
        }
    }

    override fun initSale() {
        linkOrderDetailSaleFirstBinding?.apply {
            textViewDate.text = if (order.date.isNullOrEmpty().not()) {
                    order.date?.convertIsoDateToBr()
                } else {
                    SIMPLE_LINE
                }
            textViewOrderData.text = order.merchantOrderCode
            textViewPaymentWayData.text = order.payment?.typeDescription
            textViewDeliveryData.text = order.shipping?.typeDescription
            textViewTotalData.text = order.payment?.price?.toPtBrRealString()
        }
    }

    override fun initSaleStatus() {
        linkOrderDetailSaleSecondBinding?.apply {
            textViewAFData.text = order.payment?.antifraud?.description.orEmpty()
            textViewStatusPayData.text = order.payment?.statusDescription
            textViewDatePayData.text = if (order.payment?.date.isNullOrEmpty().not()) {
                    order.payment?.date?.convertIsoDateToBr()
                } else {
                    SIMPLE_LINE
                }
            textViewStatusDeliveryData.text = order.shipping?.statusDescription

            if (order.shipping?.statusDescription.isNullOrEmpty().not()) {
                textViewStatusDeliveryData.visible()
                textViewStatusDeliveryLabel.visible()
            } else {
                textViewStatusDeliveryData.gone()
                textViewStatusDeliveryLabel.gone()
            }
        }
    }

    override fun showLoggiButton() {
        binding?.apply {
            buttonCallLoggi.setOnClickListener {
                order.id?.let {
                    findNavController().navigate(
                        LinkOrderDetailFragmentDirections
                            .actionLinkOrderDetailFragment2ToSolicitationMotoboyStep01Impl(it)
                    )
                }
            }
            buttonCallLoggi.visible()
            textViewInfoLoginTime.visible()
        }
    }

    override fun showTrackLoggiButton() {
        binding?.apply {
            buttonCallLoggi.setText(getString(R.string.track_loggi_button))
            buttonCallLoggi.setOnClickListener {
                presenter.getStatusLoggi()
            }
            buttonCallLoggi.visible()
            textViewInfoLoginTime.visible()
        }
    }

    override fun setStatusColor(color: Int) {
        (linkOrderDetailSaleSecondBinding?.textViewStatusPayData?.background as GradientDrawable)
            .setColor(ContextCompat.getColor(requireContext(), color))
    }

    override fun getStatusLoggiSuccess(responseMotoboy: ResponseMotoboy) {
        this.cieloNavigation?.showContent(true)
        requireActivity().browse(responseMotoboy.trackingUrl)
    }

    override fun showLoading() {
        this.cieloNavigation?.showLoading(true)
    }

    override fun hideLoading() {
        binding?.swipeRefreshLayout?.isRefreshing = false
        this.cieloNavigation?.showLoading(false)
    }

    override fun serverError(error: ErrorMessage) {
        this.cieloNavigation?.showError(error)
    }

    override fun onRetry() {
        presenter.onCreate(order)
    }

    override fun enhance() {
        if (isAttached()) {
            ErrorCallBackApi(this)
                .code(HTTP_ENHANCE)
                .build() {
                    it.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
                        override fun onBtnClose(dialog: Dialog) {
                            dialog.dismiss()
                        }
                    }
                }
        }
    }

    override fun notFound() {
        if (isAttached()) {
            bottomSheetGeneric(
                    getString(R.string.txt_topbar_name_error_404),
                    R.drawable.ic_generic_error_image,
                    getString(R.string.txt_title_error_404),
                    getString(R.string.txt_subtitle_error_404),
                    getString(R.string.btn_name_motoboy),
                    true,
                    false
            ).apply {
                this.onClick = object : BottomSheetGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnClose(dialog: Dialog) {
                        dialog.dismiss()
                    }
                }
            }.show(
                requireActivity().supportFragmentManager,
                getString(R.string.bottom_sheet_generic)
            )
        }
    }

    private fun logScreenView() = ga4.logScreenView(SCREEN_VIEW_PAYMENT_SALE_DETAILS)

    companion object {

        fun getInstance(extras: Bundle) = LinkOrderDetailFragment().apply { arguments = extras }

    }

}