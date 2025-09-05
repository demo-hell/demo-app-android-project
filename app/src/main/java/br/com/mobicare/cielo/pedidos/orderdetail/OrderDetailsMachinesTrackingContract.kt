package br.com.mobicare.cielo.pedidos.orderdetail

import br.com.mobicare.cielo.pedidos.domain.MachineItem
import br.com.mobicare.cielo.pedidos.tracking.model.Customer
import br.com.mobicare.cielo.pedidos.tracking.model.Payment
import br.com.mobicare.cielo.pedidos.tracking.model.Tracking

class OrderDetailsMachinesTrackingContract {
    interface View {
        fun showLoading(isShow: Boolean)
        fun showMachineDetail(machineItem: MachineItem)
        fun showCustomerDetail(customer: Customer)
        fun showPaymentDetail(payment: Payment, qtd: String)
        fun showStatus(tracking: Tracking)
        fun showCarrierName(name: String)
        fun showError()
        fun showTracking(tracking: Tracking)
    }

    interface Presenter {
        fun onStop()
        fun load(machineItem: MachineItem)
        fun trackingButtonClicked()
    }
}