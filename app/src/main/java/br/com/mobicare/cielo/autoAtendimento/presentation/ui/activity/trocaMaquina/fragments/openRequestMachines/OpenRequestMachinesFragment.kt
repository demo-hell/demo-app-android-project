package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequestMachines

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.elegibility.NotElegibilityBottomSheetFragment
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.versionMachine.OndeEncontroInformacoesMachineBottomSheetFragment
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_MACHINE_ITEM_CHANGE
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_ORDER_NUMBER
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SERIAL_NUMBER
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_VERSION_MACHINE
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.EngineNextActionListener
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.afterTextChangesEmptySubscribe
import br.com.mobicare.cielo.commons.utils.afterTextChangesNotEmptySubscribe
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosMachine
import br.com.mobicare.cielo.taxaPlanos.domain.TerminalsResponse
import kotlinx.android.synthetic.main.troca_maquina_open_request_machine_fragment.*
import kotlinx.android.synthetic.main.troca_maquina_open_request_machine_item.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class OpenRequestMachinesFragment : BaseFragment(), OpenRequestMachinesContract.View,
        EngineNextActionListener {

    private var actionJorneyListner: ActivityStepCoordinatorListener? = null
    private var actionEventListener: BaseView? = null

    val presenter: OpenRequestMachinesPresenter by inject {
        parametersOf(this)
    }

    companion object {
        fun create(bundle: Bundle?) =
                OpenRequestMachinesFragment().apply {
                    this.arguments = bundle
                }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityStepCoordinatorListener) {
            this.actionJorneyListner = context

            this.actionJorneyListner?.setButtonName("Continuar")

        }
        if (context is BaseView) {
            this.actionEventListener = context
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared()
    }

    override fun onDetach() {
        super.onDetach()
        this.actionJorneyListner = null
        this.actionEventListener = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.troca_maquina_open_request_machine_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionJorneyListner?.enableNextButton(false)

        text_view_version_machine.visibility = View.INVISIBLE
        presenter.loadMerchantSolutionsEquipments()


        text_view_version_machine.setOnClickListener {
            val fragmentBottomSheet = OndeEncontroInformacoesMachineBottomSheetFragment.create(R.layout.version_machine_show_number_fragment)
            fragmentBottomSheet.show(childFragmentManager, "BottomSheetDialogFragment")
        }

        linkWhereToFindSerieOrderNumber.setOnClickListener {
            val fragmentBottomSheet = OndeEncontroInformacoesMachineBottomSheetFragment.create(R.layout.serie_order_number_machine_fragment)
            fragmentBottomSheet.show(childFragmentManager, "BottomSheetDialogFragment")
        }

        edittext_view_version_machine.afterTextChangesNotEmptySubscribe {
            //validadeScreen()
            this.presenter.onVersionNumberChanged(it.toString())
        }

        edittext_view_version_machine.afterTextChangesEmptySubscribe {
            //validadeScreen()
            this.presenter.onVersionNumberChanged(it.toString())
        }

        this.tieSerialNumber?.afterTextChangesEmptySubscribe {
            this.presenter.onOrderAndSerialNumberChanged(this.tieOrderNumber.text.toString(), it.toString())
        }

        this.tieSerialNumber?.afterTextChangesNotEmptySubscribe {
            this.presenter.onOrderAndSerialNumberChanged(this.tieOrderNumber.text.toString(), it.toString())
        }

        this.tieOrderNumber?.afterTextChangesEmptySubscribe {
            this.presenter.onOrderAndSerialNumberChanged(it.toString(), this.tieSerialNumber.text.toString())
        }

        this.tieOrderNumber?.afterTextChangesNotEmptySubscribe {
            this.presenter.onOrderAndSerialNumberChanged(it.toString(), this.tieSerialNumber.text.toString())
        }
    }

    //region OpenRequestMachinesContract.View

    override fun renderState(state: OpenRequestMachineViewState) {
        //## Loading ##
        if (state.isLoading) {
            actionEventListener?.showLoading()
        }
        else {
            actionEventListener?.hideLoading()
        }

        this.text_view_version_machine?.visibility = if (state.isShowLinkForRentalMachine) View.VISIBLE else View.GONE
        this.linkWhereToFindSerieOrderNumber?.visibility = if (state.isShowLinkForBoughtMachine) View.VISIBLE else View.GONE
        this.text_view_title_version_machine?.visibility = if (state.isShowVersionNumber) View.VISIBLE else View.GONE
        this.tilSerialNumber?.visibility = if (state.isShowSerialNumber) View.VISIBLE else View.GONE
        this.tilOrderNumber?.visibility = if (state.isShowOrderNumber) View.VISIBLE else View.GONE
        this.actionJorneyListner?.enableNextButton(state.isNextButtonEnabled)

        state.errorMessage?.let { itError ->
            this.actionJorneyListner?.onShowError(itError)
        }

        state.terminalsResponse?.let { itResponse ->
            this.showMachines(itResponse)
        }

        if (state.isShowCannotChangeMachine) {
            showNotElegibilityBottomSheetFragment()
        }
    }

    private fun showNotElegibilityBottomSheetFragment() {
        val fragmentBottomSheet = NotElegibilityBottomSheetFragment()
        fragmentBottomSheet.show(childFragmentManager, "BottomSheetDialogFragment")
    }

    private fun validadeScreen() {
        edittext_view_version_machine.text?.let {
            if (it.isNotEmpty() && presenter.taxaPlanosMachineSeleted != null) {
                actionJorneyListner?.enableNextButton(true)
            } else {
                actionJorneyListner?.enableNextButton(false)
            }
        }
    }

    var listTaxaPlanosMachine: List<TaxaPlanosMachine>? = null

    override fun showMachines(response: TerminalsResponse) {

        listTaxaPlanosMachine = response.terminals


        if (isAttached()) {
            recycler_view.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = DefaultViewListAdapter(listTaxaPlanosMachine!!,
                    R.layout.troca_maquina_open_request_machine_item)
            adapter.setBindViewHolderCallback(object : DefaultViewListAdapter.OnBindViewHolder<TaxaPlanosMachine> {
                override fun onBind(item: TaxaPlanosMachine, holder: DefaultViewHolderKotlin) {
                    holder.mView.text_name_machine.text = item.name
                    holder.mView.text_description_login_number.text = item.logicalNumber ?: ""

                    if (item.selectedItem) {
                        holder.mView.mcv_dados_maquinas.setBackgroundResource(R.drawable.shape_check)
                    } else {
                        holder.mView.mcv_dados_maquinas.setBackgroundResource(R.drawable.shape_uncheck)
                    }
                }
            })

            adapter.onItemClickListener =
                object : DefaultViewListAdapter.OnItemClickListener<TaxaPlanosMachine> {
                    override fun onItemClick(item: TaxaPlanosMachine) {
                        listTaxaPlanosMachine?.forEach {
                            it.selectedItem = false
                        }
                        item.selectedItem = true
                        this@OpenRequestMachinesFragment.presenter.onTaxaPlanosMarchineSelected(
                            item,
                            tieOrderNumber?.text.toString(),
                            tieSerialNumber?.text.toString(),
                            edittext_view_version_machine?.text.toString()
                        )

                        adapter.notifyDataSetChanged()
                    }
                }

            recycler_view.adapter = adapter
        }
        //hideLoading()
        configureSpacer()
        //text_view_version_machine.visibility = View.VISIBLE
    }

    private fun configureSpacer() {
        this.view?.parent?.let {
            if (it is FrameLayout) {
                it.parent?.let { itLayout ->
                    if (itLayout is ScrollView) {
                        itLayout.post {
                            val mainLayoutHeight = this.constraint_main.measuredHeight  //this.spaceView?.layoutParams
                            if (mainLayoutHeight < itLayout.height) {
                                val lp = this.spacerView.layoutParams
                                lp.height = itLayout.height-mainLayoutHeight
                                spacerView.layoutParams = lp
                                spacerView.requestLayout()
                            }
                        }
                    }
                }

            }
        }
    }

    override fun retry() {
        presenter.loadMerchantSolutionsEquipments()
    }

    override fun showLoading() {
        actionEventListener?.showLoading()
    }

    override fun hideLoading() {
        actionEventListener?.hideLoading()
    }

    override fun showError(error: ErrorMessage?) {
        actionEventListener?.showError(error)
    }

    override fun logout(msg: ErrorMessage?) {
        actionEventListener?.logout(msg)
    }

    override fun onNextStep(versionMachine: String?, serialNumber: String?, orderNumber: String?, taxaPlanosMachine: TaxaPlanosMachine) {
        this.actionJorneyListner?.onNextStep(false, Bundle().apply {
            versionMachine?.let {
                putString(ARG_PARAM_VERSION_MACHINE, versionMachine)
            }
            serialNumber?.let {
                putString(ARG_PARAM_SERIAL_NUMBER, serialNumber)
            }
            orderNumber?.let {
                putString(ARG_PARAM_ORDER_NUMBER, orderNumber)
            }
            putParcelable(ARG_PARAM_MACHINE_ITEM_CHANGE, taxaPlanosMachine)
        })
    }
    //endregion

    //region EngineNextActionListener
    override fun onClicked() {
        if (isAttached()) {
            edittext_view_version_machine?.text?.let { itVersionMachineNumber ->
                tieSerialNumber?.text?.let { itSerialNumber ->
                    tieOrderNumber?.text?.let { itOrderNumber ->
                        val versionMachine: String? = if (itVersionMachineNumber.isNotBlank()) itVersionMachineNumber.toString() else null
                        val serialNumber: String? = if (itSerialNumber.isNotBlank()) itSerialNumber.toString() else null
                        val orderNumber: String? = if (itOrderNumber?.isNotBlank()) itOrderNumber.toString() else null
                        presenter.onNextButtonClicked(versionMachine, serialNumber, orderNumber)
                    }
                }
            }
        }
    }
    //endregion

}
