package br.com.mobicare.cielo.pedidos.presentation.presenter

import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.presentation.CommonPresenter
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.pedidos.domain.OrderMachinePagination
import br.com.mobicare.cielo.pedidos.managers.OrderMachineRepository
import io.reactivex.Scheduler

class OrderMachinePresenter(
    private val orderMachineView: OrderMachineContract.View,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val orderMachineRepository: OrderMachineRepository
) : CommonPresenter {

    private val compositeDisposableHandler = CompositeDisposableHandler()

    private var currentPaginnation: OrderMachinePagination? = null

    override fun onResume() {
        compositeDisposableHandler.start()
    }

    override fun onDestroy() {
        compositeDisposableHandler.destroy()
    }


    fun fetchOpenedOrdersFirst(isBySwipeRefresh: Boolean = false) {

        compositeDisposableHandler.compositeDisposable.add(orderMachineRepository
            .fetchMachineOpenedOrders(1)
            .subscribeOn(ioScheduler)
            .observeOn(uiScheduler)
            .doOnSubscribe {
                if (!isBySwipeRefresh) {
                    orderMachineView.showLoading()
                }
            }
            .subscribe({ orderMachineResponse ->

                //Atualiza qual a pagina corrente
                orderMachineResponse.pagination?.let {
                    currentPaginnation = it
                }

                orderMachineView.hideLoading()

                orderMachineResponse.machineItems?.let {
                    orderMachineView.showOpenedMachineOrders(orderMachineResponse)
                } ?: run {
                    orderMachineView.showEmptyOrders()
                }

            }, { orderMachineError ->
                orderMachineView.hideLoading()

                val error = APIUtils.convertToErro(orderMachineError)
                orderMachineView.showError(error)
            })
        )
    }


    fun fetchOpenedOrdersNextPage() {

        currentPaginnation?.pageNumber?.let { currentPageId ->

            if (currentPaginnation?.lastPage != true) {
                compositeDisposableHandler.compositeDisposable.add(orderMachineRepository
                    .fetchMachineOpenedOrders(currentPageId + 1)
                    .subscribeOn(ioScheduler)
                    .observeOn(uiScheduler)
                    .doOnSubscribe {
                        orderMachineView.showFooterLoading()
                    }
                    .subscribe({ orderMachineResponse ->

                        orderMachineResponse.pagination?.let { orderMachPag ->
                            currentPaginnation = orderMachPag
                        }

                        orderMachineView.hideFoorterLoading()

                        orderMachineResponse.machineItems?.let {
                            orderMachineView.appendMachineOrders(orderMachineResponse)
                        }

                    }, { orderMachineError ->
                        orderMachineView.hideFoorterLoading()

                        val error = APIUtils.convertToErro(orderMachineError)
                        orderMachineView.showError(error)
                    })
                )

            }

        }


    }

}