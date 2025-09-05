package br.com.mobicare.cielo.pagamentoLink.orders

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.alert.CieloAskQuestionDialogFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.IntentAction.SHARE_WITH
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.dateFormatToBr
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.*
import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLink
import br.com.mobicare.cielo.pagamentoLink.orders.adapter.OrderAdapter
import br.com.mobicare.cielo.pagamentoLink.orders.model.Order
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4.Companion.SCREEN_VIEW_PAYMENT_LINK_DETAILS
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class LinkOrdersFragment : BaseFragment(), LinkOrdersView, CieloNavigationListener {

    private var binding: FragmentLinkOrdersBinding? = null
    private var linkOrderHeaderBinding: LinkOrderHeaderBinding? = null
    private var layoutListLinkOrdersBinding: LayoutListLinkOrdersBinding? = null
    private var layoutOrdersEmptyListBinding: LayoutOrdersEmptyListBinding? = null
    private var layoutErrorLinkListOrder: LayoutErrorLinkListOrderBinding? = null

    private val presenter: LinkOrdersPresenterImpl by inject {
        parametersOf(this)
    }
    private lateinit var adapter: OrderAdapter

    private var paymentLink: PaymentLink? = null
    private var cieloNavigation: CieloNavigation? = null

    private val ga4: PaymentLinkGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLinkOrdersBinding.inflate(inflater, container, false)

        binding?.root?.let {
            linkOrderHeaderBinding = LinkOrderHeaderBinding.bind(it)
            layoutListLinkOrdersBinding = LayoutListLinkOrdersBinding.bind(it)
            layoutOrdersEmptyListBinding = LayoutOrdersEmptyListBinding.bind(it)
            layoutErrorLinkListOrder = LayoutErrorLinkListOrderBinding.bind(it)
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onResume() {
        super.onResume()
        logScreenView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        linkOrderHeaderBinding = null
        layoutListLinkOrdersBinding = null
        layoutOrdersEmptyListBinding = null
        layoutErrorLinkListOrder = null
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    private fun init() {
        configureNavigation()
        initDeleteLink()
        copyLink()

        arguments?.let {
            val paymentLink = LinkOrdersFragmentArgs.fromBundle(it).paymentlinkextras
            this.paymentLink = paymentLink
            presenter.onCreate(paymentLink?.id)

            paymentLink?.name?.let { name -> this.cieloNavigation?.setTextToolbar(name) }

            linkOrderHeaderBinding?.apply {
                textViewProductName.text = paymentLink.name
                textViewProductPrice.text = paymentLink.price?.toPtBrRealString()
                linkValue.text = paymentLink.url
                textViewQuantity.text = if (paymentLink.quantity != null) {
                    paymentLink.quantity.toString()
                } else {
                    getString(R.string.link_list_detail_quantity_unlimited)
                }


                val list = ArrayList<ItemSuperLink>()

                if (paymentLink.type == ASSET || paymentLink.type == DIGITAL) {
                    paymentLink.price.let { price ->
                        list.add(
                            ItemSuperLink(
                                getString(R.string.tv_sl_valor),
                                price?.toPtBrRealString()
                            )
                        )
                    }
                    if (!paymentLink.expiration.isNullOrEmpty()) {
                        paymentLink.expiration.let { expiration ->
                            list.add(
                                ItemSuperLink(
                                    getString(R.string.tv_sl_data_expiracao),
                                    expiration?.dateFormatToBr().toString()
                                )
                            )
                        }
                    }
                    paymentLink.type.let { tp ->

                        val type: String = when (tp) {
                            ASSET -> {
                                getString(R.string.tv_sl_envio_produto)
                            }
                            else -> {
                                getString(R.string.tv_sl_cobranca_valor)
                            }
                        }
                        list.add(ItemSuperLink(getString(R.string.tv_sl_tipo_venda), type))
                    }
                    if (!paymentLink.maximumInstallment.isNullOrEmpty()) {
                        paymentLink.maximumInstallment.let { max ->
                            list.add(
                                ItemSuperLink(
                                    getString(R.string.tv_sl_num_parcelas),
                                    max + CONST_X
                                )
                            )
                        }
                    }
                    if (paymentLink.quantity != null) {
                        paymentLink.quantity.let { quantily ->
                            list.add(
                                ItemSuperLink(
                                    getString(R.string.tv_sl_qtdd_max_produtos),
                                    quantily.toString()
                                )
                            )
                        }
                    } else {
                        paymentLink.quantity.let {
                            list.add(
                                ItemSuperLink(
                                    getString(R.string.tv_sl_qtdd_max_produtos),
                                    getString(R.string.link_list_detail_quantity_unlimited)
                                )
                            )
                        }
                    }
                    if (!paymentLink.sku.isNullOrBlank()) {
                        paymentLink.sku.let { sku ->
                            list.add(
                                ItemSuperLink(
                                    getString(R.string.tv_sl_codigo_produto),
                                    sku
                                )
                            )
                        }
                    }
                    if (!paymentLink.softDescriptor.isNullOrBlank()) {
                        paymentLink.softDescriptor.let { soft ->
                            list.add(ItemSuperLink(getString(R.string.tv_sl_descricao), soft))
                        }
                    }
                } else {

                    paymentLink.price.let { price ->
                        list.add(
                            ItemSuperLink(
                                getString(R.string.tv_sl_valor),
                                price?.toPtBrRealString()
                            )
                        )
                    }
                    if (!paymentLink.expiration.isNullOrEmpty()) {
                        paymentLink.expiration.let { expiration ->
                            list.add(
                                ItemSuperLink(
                                    getString(R.string.tv_sl_data_expiracao),
                                    expiration?.dateFormatToBr().toString()
                                )
                            )
                        }
                    }
                    if (!paymentLink.finalRecurrentExpiration.isNullOrEmpty()) {
                        paymentLink.finalRecurrentExpiration.let { recurrent ->
                            list.add(
                                ItemSuperLink(
                                    getString(R.string.tv_sl_perido_cobranca),
                                    recurrent.dateFormatToBr()
                                )
                            )
                        }
                    }
                    paymentLink.type.let {
                        val type = getString(R.string.tv_sl_venda_recorrente)

                        list.add(ItemSuperLink(getString(R.string.tv_sl_tipo_venda), type))
                    }
                    if (!paymentLink.sku.isNullOrBlank()) {
                        paymentLink.sku.let { sku ->
                            list.add(
                                ItemSuperLink(
                                    getString(R.string.tv_sl_codigo_produto),
                                    sku
                                )
                            )
                        }
                    }
                    if (!paymentLink.softDescriptor.isNullOrBlank()) {
                        paymentLink.softDescriptor.let { soft ->
                            list.add(ItemSuperLink(getString(R.string.tv_sl_descricao), soft))
                        }
                    }
                }

                textViewmoreInformation.setOnClickListener {
                    logDisplayContent()

                    SuperLinkMoreInformationBottomSheet.newInstance(
                        list, textViewProductName.text.toString(), linkValue.text.toString()
                    ).show(
                        requireActivity().supportFragmentManager,
                        getString(R.string.bottom_sheet_generic)
                    )
                }


                if (paymentLink.shipping != null) {
                    when (paymentLink.shipping?.type) {
                        DeliveryType.CORREIOS.name -> {
                            imageViewDeliveryIcon.setBackgroundResource(R.drawable.ic_correios_inline)
                        }
                        DeliveryType.LOGGI.name -> {
                            imageViewDeliveryIcon.setBackgroundResource(R.drawable.ic_loggi)
                        }
                        DeliveryType.FIXED_AMOUNT.name -> {
                            imageViewDeliveryIcon.gone()
                            textViewDeliveryWayLabel.text =
                                getString(R.string.link_list_delivery_way_label_fixed_amount)
                        }
                        DeliveryType.WITHOUT_SHIPPING.name -> {
                            imageViewDeliveryIcon.gone()
                            textViewDeliveryWayLabel.gone()
                        }
                        else -> {
                            imageViewDeliveryIcon.gone()
                            textViewDeliveryWayLabel.gone()
                        }
                    }
                }
            }
        }
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar(getString(R.string.tv_sl_venda_link_ativos))
            this.cieloNavigation?.showButton(false)
            this.cieloNavigation?.showHelpButton(false)
            this.cieloNavigation?.showContent(true)
            this.cieloNavigation?.setNavigationListener(this)
        }
    }

    private fun initDeleteLink() {
        linkOrderHeaderBinding?.apply {
            imageViewTrash.setOnClickListener {
                logClickDeleteLink()

                CieloAskQuestionDialogFragment
                    .Builder()
                    .title(getString(R.string.alert_delete_link_title))
                    .message(getString(R.string.alert_delete_link_description))
                    .cancelTextButton(getString(R.string.alert_delete_link_no))
                    .positiveTextButton(getString(R.string.alert_delete_link_yes))
                    .build().let {
                        it.onCancelButtonClickListener = View.OnClickListener {

                        }
                        it.onPositiveButtonClickListener = View.OnClickListener {
                            presenter.deleteLink()
                        }
                        it.show(
                            requireActivity().supportFragmentManager,
                            CieloAskQuestionDialogFragment::class.java.simpleName
                        )
                    }
            }

            imageViewCommunication.setOnClickListener {
                shareDefaultDevice()
            }
        }
    }

    override fun onOrdersSuccess(orders: List<Order>) {
        adapter = OrderAdapter(orders) { itOrder ->
            paymentLink?.let { itPaymentLink ->
                findNavController().navigate(
                    LinkOrdersFragmentDirections
                        .actionLinkOrdersFragmentToLinkOrderDetailFragment2(itOrder, itPaymentLink)
                )
            }
        }

        layoutListLinkOrdersBinding?.apply {
            recycleViewOrders.layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false
            )
            recycleViewOrders.adapter = adapter
        }
    }

    override fun onOrderError() {
        layoutListLinkOrdersBinding?.linearLayoutOrderList?.gone()
        layoutOrdersEmptyListBinding?.linearLayoutEmptyList?.gone()

        layoutErrorLinkListOrder?.apply {
            buttonLoadAgain.setOnClickListener {
                presenter.getOrders()
                constraintLayoutError.gone()
            }
            constraintLayoutError.visible()
        }
    }

    override fun onOrderListEmpty() {
        layoutListLinkOrdersBinding?.linearLayoutOrderList?.gone()
        layoutErrorLinkListOrder?.constraintLayoutError?.gone()
        layoutOrdersEmptyListBinding?.linearLayoutEmptyList?.visible()
    }

    override fun onDeleteLinkSuccess() {
        Toast.makeText(context, getString(R.string.alert_delete_link_excluded), Toast.LENGTH_SHORT)
            .show()
        findNavController().navigateUp()
    }

    override fun onDeleteLinkError() {
        binding?.apply {
            nestedScrollView.gone()
            errorLayout.visible()
            errorLayout.configureActionClickListener {
                presenter.deleteLink()
                nestedScrollView.visible()
                errorLayout.gone()
            }
        }
    }

    override fun showLoading() {
        layoutOrdersEmptyListBinding?.linearLayoutEmptyList?.gone()
        layoutListLinkOrdersBinding?.apply {
            linearLayoutOrderList.visible()
            shimmerLayout.visible()
            shimmerLayout.startShimmer()
        }
    }

    override fun hideLoading() {
        layoutListLinkOrdersBinding?.shimmerLayout?.apply {
            stopShimmer()
            gone()
        }
    }

    override fun copyLink() {
        linkOrderHeaderBinding?.apply {
            constraintLayoutCopyLink.setOnClickListener {
                Utils.copyToClipboard(requireContext(), linkValue.text.toString())
            }
        }
    }

    private fun shareDefaultDevice() {
        logClickShareLink()

        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = TEX_PLAIN
        intent.putExtra(
            Intent.EXTRA_TEXT,
            linkOrderHeaderBinding?.linkValue?.text?.toString().orEmpty()
        )
        startActivity(Intent.createChooser(intent, SHARE_WITH))
    }

    private fun logScreenView() = ga4.logScreenView(SCREEN_VIEW_PAYMENT_LINK_DETAILS)

    private fun logClickDeleteLink() = ga4.logClickDeleteLink()

    private fun logClickShareLink() = ga4.logShareLink()

    private fun logDisplayContent() = ga4.logDisplayContentLinkDetails()

    companion object {
        fun getInstance(extras: Bundle) = LinkOrdersFragment().apply { arguments = extras }
        const val ASSET = "ASSET"
        const val DIGITAL = "DIGITAL"
        const val CONST_X = "x"
        const val TEX_PLAIN = "text/plain"
    }

}
