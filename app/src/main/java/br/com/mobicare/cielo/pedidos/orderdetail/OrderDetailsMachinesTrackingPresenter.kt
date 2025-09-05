package br.com.mobicare.cielo.pedidos.orderdetail

import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.machine.MachineRepository
import br.com.mobicare.cielo.pedidos.domain.MachineItem
import br.com.mobicare.cielo.pedidos.tracking.model.OrderAffiliationDetail
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.net.HttpURLConnection

class OrderDetailsMachinesTrackingPresenter(
        private val view: OrderDetailsMachinesTrackingContract.View,
        private val repository: MachineRepository
) : OrderDetailsMachinesTrackingContract.Presenter {

    private var disposible = CompositeDisposable()
    private var machineItem: MachineItem? = null
    private var orderAffiliationDetail: OrderAffiliationDetail? = null

    private fun checkDisposiable() {
        if (this.disposible.isDisposed) {
            disposible = CompositeDisposable()
        }
    }

    override fun onStop() {
        this.disposible.dispose()
    }

    override fun load(machineItem: MachineItem) {
        checkDisposiable()
        this.view.showLoading(true)
        this.machineItem = machineItem
        this.machineItem?.let { itMachineItem ->
            itMachineItem.id?.let { itOrderId ->
                this.repository.getOrderAffiliationDetail(itOrderId)
                    .configureIoAndMainThread()
                    .subscribe({
                        this.orderAffiliationDetail = it
                        this.view.showMachineDetail(itMachineItem)
                        it.customer?.let { itCustomer ->
                            this.view.showCustomerDetail(itCustomer)
                        }
                        it.shipments?.firstOrNull()?.let { itShipment ->
                            this.view.showCarrierName(itShipment.carrier ?: "")
                            itShipment.items?.firstOrNull()?.let { itShipmentItem ->
                                it.payments?.firstOrNull()?.let { itPayment ->
                                    this.view.showPaymentDetail(itPayment, itShipmentItem.quantity?:"")
                                }
                            }
                        }
                        it.tracking?.let { itTracking ->
                            this.view.showStatus(itTracking)
                        }
                        this.view.showLoading(false)
                    }, {
                        val error = APIUtils.convertToErro(it)
                        if (error.httpStatus != HttpURLConnection.HTTP_UNAUTHORIZED) {
                            this.view.showError()
                        }
                    })
                    .addTo(disposible)
            }
        }
    }

    override fun trackingButtonClicked() {
        this.orderAffiliationDetail?.tracking?.let {
            this.view.showTracking(it)
        }
    }


}