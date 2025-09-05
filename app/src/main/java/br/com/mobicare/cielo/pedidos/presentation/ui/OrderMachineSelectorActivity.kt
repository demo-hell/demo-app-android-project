package br.com.mobicare.cielo.pedidos.presentation.ui

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_MACHINE_ITEM
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.ui.adapter.InfiniteScrollOnDefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.convertToBrDateFormat
import br.com.mobicare.cielo.pedidos.orderdetail.OrderDetailsMachinesTrackingActivity
import br.com.mobicare.cielo.pedidos.domain.MachineItem
import br.com.mobicare.cielo.pedidos.domain.OrderMachineResponse
import br.com.mobicare.cielo.pedidos.presentation.presenter.OrderMachineContract
import br.com.mobicare.cielo.pedidos.presentation.presenter.OrderMachinePresenter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_order_machine_selector.*
import kotlinx.android.synthetic.main.item_order_machine.view.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class OrderMachineSelectorActivity : BaseActivity(), OrderMachineContract.View {

    private val presenter: OrderMachinePresenter by inject {
        parametersOf(this)
    }

    private var orderedMachinesAdapter:
            InfiniteScrollOnDefaultViewListAdapter<MachineItem>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_machine_selector)
        setupToolbar(
            toolbarOrderMachineSelector as Toolbar,
            getString(R.string.text_order_machine_selector_title)
        )

        presenter.fetchOpenedOrdersFirst()
        setupScreenComponents()
    }

    private fun setupScreenComponents() {
        if (isAttached()) {
            recyclerOrderedMachineSelector.setHasFixedSize(true)
            recyclerOrderedMachineSelector.layoutManager = LinearLayoutManager(this)


            swipeRefreshOrderedMachines.setOnRefreshListener {
                presenter.fetchOpenedOrdersFirst(true)
            }


        }
    }


    override fun showEmptyOrders() {

        if (isAttached()) {
            nestedOrderedMachineSelectorContent.visibility = View.GONE
            errorHandlerOrderMachine.visibility = View.VISIBLE
            errorHandlerOrderMachine.errorHandlerCieloViewImageDrawable = R.drawable.ic_13

            errorHandlerOrderMachine.cieloErrorTitle =
                getString(R.string.text_order_machine_empty_title)
            errorHandlerOrderMachine.cieloErrorMessage =
                getString(R.string.text_order_machine_empty_error_message)

            errorHandlerOrderMachine.errorButton?.visibility = View.GONE
        }

    }

    override fun showLoading() {
        if (isAttached()) {
            linearOrderedMachinesContent.visibility = View.GONE
            frameOrderedMachinesLoading.visibility = View.VISIBLE
        }
    }

    override fun hideLoading() {
        if (isAttached()) {
            swipeRefreshOrderedMachines.isRefreshing = false

            frameOrderedMachinesLoading.visibility = View.GONE
            linearOrderedMachinesContent.visibility = View.VISIBLE
        }
    }

    override fun showError(error: ErrorMessage?) {
        if (isAttached()) {
            nestedOrderedMachineSelectorContent.visibility = View.GONE
            errorHandlerOrderMachine.visibility = View.VISIBLE

            errorHandlerOrderMachine.errorHandlerCieloViewImageDrawable =
                R.drawable.ic_generic_error_image
            errorHandlerOrderMachine.cieloErrorTitle =
                getString(R.string.text_title_generic_error)

            errorHandlerOrderMachine.cieloErrorMessage =
                getString(R.string.text_message_generic_error)

            errorHandlerOrderMachine.errorButton?.visibility = View.VISIBLE
            errorHandlerOrderMachine.errorButton?.setText(getString(R.string.text_button_try_again))

            errorHandlerOrderMachine.configureActionClickListener(View.OnClickListener {
                errorHandlerOrderMachine.visibility = View.GONE
                nestedOrderedMachineSelectorContent.visibility = View.VISIBLE
                presenter.fetchOpenedOrdersFirst()
            })

        }
    }


    override fun showOpenedMachineOrders(orderMachineResponse: OrderMachineResponse) {

        if (isAttached()) {

            orderMachineResponse.machineItems?.let { machineItemsReturned ->

                orderedMachinesAdapter =
                    InfiniteScrollOnDefaultViewListAdapter(
                        ArrayList(machineItemsReturned),
                        R.layout.item_order_machine
                    )

                recyclerOrderedMachineSelector.adapter =
                    orderedMachinesAdapter

                orderedMachinesAdapter?.setOnLoadNextPageListener(object :
                    InfiniteScrollOnDefaultViewListAdapter.OnLoadNextPageListener {

                    override fun onLoadNextPage() {
                        presenter.fetchOpenedOrdersNextPage()
                    }

                })

                orderedMachinesAdapter?.setBindViewHolderCallback(object : DefaultViewListAdapter
                .OnBindViewHolderPositon<MachineItem> {

                    override fun onBind(
                        item: MachineItem,
                        holder: DefaultViewHolderKotlin,
                        position: Int,
                        lastPositon: Int
                    ) {
                        Picasso.get().load(item.imgCardBrand)
                            .resize(49, 64)
                            .into(holder.mView.imageMachineThumbnail)

                        holder.mView.textOrderedMachineName.text = SpannableStringBuilder
                            .valueOf(item.posName)
                        holder.mView.textOrderMachineDate.text = SpannableStringBuilder
                            .valueOf(item.orderDate?.convertToBrDateFormat())
                        holder.mView.textOrderMachineId.text = SpannableStringBuilder
                            .valueOf("${item.id}")

                        holder.mView.setOnClickListener {
                            startActivity<OrderDetailsMachinesTrackingActivity>(ARG_PARAM_MACHINE_ITEM to item)
                        }
                    }

                })
            }
        }

    }

    override fun appendMachineOrders(orderMachineResponse: OrderMachineResponse) {

        orderMachineResponse.machineItems?.let { machineItemsReturned ->
            orderedMachinesAdapter?.addMoreInList(machineItemsReturned)
        }

    }

    override fun showFooterLoading() {
        if (isAttached()) {
            footerLayout.visibility = View.VISIBLE
        }
    }

    override fun hideFoorterLoading() {
        if (isAttached()) {
            footerLayout.visibility = View.GONE
        }
    }

}