package br.com.mobicare.cielo.pedidos.presentation.presenter

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.presentation.CommonPresenter
import br.com.mobicare.cielo.pedidos.domain.MachineItem
import br.com.mobicare.cielo.pedidos.domain.OrderMachinePagination
import br.com.mobicare.cielo.pedidos.domain.OrderMachineResponse

interface OrderMachineContract {

    interface Presenter : CommonPresenter {}

    interface View : BaseView {

        fun showOpenedMachineOrders(orderMachineResponse: OrderMachineResponse)
        fun appendMachineOrders(orderMachineResponse: OrderMachineResponse)

        fun showFooterLoading()
        fun hideFoorterLoading()
        fun showEmptyOrders()
    }

}