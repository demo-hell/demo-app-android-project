package br.com.mobicare.cielo.pedidos.orderdetail

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_MACHINE_ITEM
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.convertTimeStampToDate
import br.com.mobicare.cielo.commons.utils.convertTimeStampToDateTime
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pedidos.domain.MachineItem
import br.com.mobicare.cielo.pedidos.tracking.TrackingOrderBottomSheetFragment
import br.com.mobicare.cielo.pedidos.tracking.model.Customer
import br.com.mobicare.cielo.pedidos.tracking.model.Payment
import br.com.mobicare.cielo.pedidos.tracking.model.Step
import br.com.mobicare.cielo.pedidos.tracking.model.Tracking
import kotlinx.android.synthetic.main.activity_order_details_machines_tracking.*
import kotlinx.android.synthetic.main.card_layout_delivery_machines_tracking.*
import kotlinx.android.synthetic.main.card_layout_order_data_machines_tracking.*
import kotlinx.android.synthetic.main.card_layout_payment_machines_tracking.*
import kotlinx.android.synthetic.main.card_layout_status_machines_tracking.*
import kotlinx.android.synthetic.main.layout_status_order_error.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class OrderDetailsMachinesTrackingActivity : BaseLoggedActivity(),
        OrderDetailsMachinesTrackingContract.View {

    val presenter: OrderDetailsMachinesTrackingPresenter by inject {
        parametersOf(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details_machines_tracking)
        setupToolbar(toolbar as Toolbar, getString(R.string.detalhes_do_pedido))
        configureListeners()
        load()
    }

    private fun load() {
        getMachineItem()?.let {
            this.presenter.load(it)
        }
    }

    private fun configureListeners() {
        this.errorView?.errorButton?.setOnClickListener {
            load()
        }
        this.trackingButton?.setOnClickListener {
            this.presenter.trackingButtonClicked()
        }
    }

    private fun getMachineItem() : MachineItem? {
        var machineItem: MachineItem? = null
        this.intent.extras?.getSerializable(ARG_PARAM_MACHINE_ITEM)?.let { itMachineItem ->
            if (itMachineItem is MachineItem) {
                machineItem = itMachineItem
            }
        }
        return machineItem
    }

    override fun showLoading(isShow: Boolean) {
        when (isShow) {
            true -> {
                this.contentLayout?.gone()
                this.errorLayout?.gone()
                this.loadingView?.visible()
            }
            false -> this.loadingView?.gone()
        }
    }

    override fun showMachineDetail(machineItem: MachineItem) {
        this.contentLayout?.visible()
        this.tvNomeMaquina?.text = machineItem.posName
        this.tvDataCompra?.text = machineItem.orderDate?.convertTimeStampToDate()
        this.tvNumeroPedido?.text = machineItem.id?.toString()
        ImageUtils.loadImage(this.ivImagemMaquina, machineItem.imgCardBrand)
    }

    override fun showCustomerDetail(customer: Customer) {
        this.contentLayout?.visible()
        this.tvNomeCliente?.text = customer.name
        this.tvEndereco?.text = customer.address?.streetAddress
        this.tvNumero?.text = customer.address?.number
        this.tvComplemento?.text = customer.address?.streetAddress2
        this.tvBairro?.text = customer.address?.neighborhood
        this.tvCidade?.text = customer.address?.city
        this.tvEstado?.text = customer.address?.state
        this.tvCep?.text = customer.address?.zipCode
    }

    override fun showPaymentDetail(payment: Payment, qtd: String) {
        this.contentLayout?.visible()
        this.tvValorTotal?.text = payment.amount?.toPtBrRealString()
        this.tvQuantidade?.text = qtd
        this.tvMeioPagamento?.text = payment.typeDescription
    }

    override fun showStatus(tracking: Tracking) {
        this.contentLayout?.visible()
        tracking.steps?.lastOrNull()?.let { itStep ->
            when(tracking.status) {
                "SUCCESS" -> showStatusOk(itStep)
                else -> showStatusFailed(tracking.description ?: "", itStep)
            }
        }
    }

    override fun showCarrierName(name: String) {
        this.contentLayout?.visible()
        this.tvTransportadora?.text = name
    }

    private fun showStatusOk(step: Step) {
        this.ivStatus?.setImageResource(R.drawable.ic_check_circle)
        this.tvStatus?.text = step.description
        this.tvStatus?.setTextColor(ContextCompat.getColor(this, R.color.success_400))
        this.tvUltimaAtualizacao?.text = "${step.lastUpdated?.convertTimeStampToDateTime()}"
        this.linearLayoutOrderStatusError?.gone()
    }

    private fun showStatusFailed(description: String, step: Step) {
        this.ivStatus?.setImageResource(R.drawable.ic_x_circle)
        this.tvStatus?.text = step.description
        this.tvStatus?.setTextColor(ContextCompat.getColor(this, R.color.red_DC392A))
        this.tvUltimaAtualizacao?.text = "${step.lastUpdated?.convertTimeStampToDateTime()}"
        if (description.isNotEmpty()) {
            this.linearLayoutOrderStatusError?.visible()
            this.textViewStatusOrderError?.text = description
        }
        else {
            this.linearLayoutOrderStatusError?.gone()
        }
    }

    override fun showError() {
        this.contentLayout?.gone()
        this.errorLayout?.visible()
    }

    override fun showTracking(tracking: Tracking) {
        TrackingOrderBottomSheetFragment.getInstance(supportFragmentManager, tracking)
    }

}