package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.address

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.cielo.libflue.util.EMPTY
import br.com.concrete.canarinho.formatador.Formatador
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.analytics.AutoAtendimentoAnalytics
import br.com.mobicare.cielo.autoAtendimento.domain.MachineInstallAddressObj
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.engine.getNameMachine
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.address.newAddress.InstalacaoMaquinaChooseAddressBottomSheetFragment
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.address.newAddress.MachineInstallAddressListener
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.analytics.Action.CONTINUAR
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_ADDRESS_CHOSEN
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_NAME_MACHINE
import br.com.mobicare.cielo.commons.constants.EIGHT
import br.com.mobicare.cielo.commons.constants.ONE_HUNDRED
import br.com.mobicare.cielo.commons.constants.Text.PICKER_BS
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.EngineNextActionListener
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.containsOnlyNumbers
import br.com.mobicare.cielo.databinding.InstalacaoMaquinaChooseAddressFragmentBinding
import br.com.mobicare.cielo.meuCadastroNovo.domain.Address
import br.com.mobicare.cielo.meuCadastroNovo.domain.MCMerchantResponse
import br.com.mobicare.cielo.recebaMais.presentation.ui.component.PickerBottomSheetFragment
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import org.jetbrains.anko.startActivityForResult
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit

class InstalacaoMaquinaChooseAddressFragment : BaseFragment(),
    InstalacaoMaquinaChooseAddressContract.View,
    EngineNextActionListener, MachineInstallAddressListener {

    private var binding: InstalacaoMaquinaChooseAddressFragmentBinding? = null
    private var actionJourneyListener: ActivityStepCoordinatorListener? = null
    private var actionEventListener: BaseView? = null

    val presenter: InstalacaoMaquinaChooseAddressPresenter by inject {
        parametersOf(this)
    }

    private val analytics: AutoAtendimentoAnalytics by inject()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityStepCoordinatorListener) {
            actionJourneyListener = context
        }
        if (context is BaseView) {
            actionEventListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = InstalacaoMaquinaChooseAddressFragmentBinding.inflate(
        inflater,
        container,
        false
    ).also { binding = it }.root

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        presenter.onCleared()
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
        actionJourneyListener = null
        actionEventListener = null
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logScreenView()

        presenter.loadMerchant()

        binding?.textViewAddMore?.let {
            RxView.clicks(it)
                .throttleFirst(DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    activity?.startActivityForResult<InstalacaoMaquinaChooseAddressBottomSheetFragment>(
                        ONE_HUNDRED
                    )
                    activity?.overridePendingTransition(
                        R.anim.slide_from_bottom_to_up,
                        R.anim.slide_nothing
                    )
                }, {})
        }
    }

    private fun logScreenView() {
        analytics.logScreenView(AutoAtendimentoAnalytics.SCREEN_VIEW_REQUEST_MACHINE_ADDRESS)
    }

    override fun goToNextScreen(addressChosen: MachineInstallAddressObj) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, SOLICITAR_MAQUININHA),
                action = listOf(Action.SOLICITAR_MAQUININHA, getNameMachine() ?: EMPTY),
                label = listOf(Label.BOTAO, String.format(PASSO_FORMAT, 2), CONTINUAR)
            )

            actionJourneyListener?.onNextStep(false, Bundle().apply {
                putParcelable(ARG_PARAM_ADDRESS_CHOSEN, addressChosen)
                putString(ARG_PARAM_NAME_MACHINE, getNameMachine() ?: EMPTY)
            })
        }
    }

    override fun showAddress(address: Address, addressType: String) {
        if (isAttached()) {
            binding?.apply {
                textViewAddressChoose.text = addressType
                textViewAddressStreet.text = getString(
                    R.string.txt_install_machine_address_street,
                    address.streetAddress,
                    address.number
                )
                textViewReferencePoint.text = address.complementAddress
                textViewNeighborhood.text = address.neighborhood
                textViewCity.text = address.city
                textViewState.text = address.state
                address.zipCode?.let {
                    textViewZipCode.text =
                        if (it.length == EIGHT && it.containsOnlyNumbers())
                            Formatador.CEP.formata(it)
                        else it
                } ?: run {
                    textViewZipCode.text = EMPTY
                }
            }
            actionEventListener?.hideLoading()
        }
    }

    override fun showLoading() {
        actionEventListener?.showLoading()
    }

    override fun showError(error: ErrorMessage?) {
        analytics.logException(
            screenName = AutoAtendimentoAnalytics.SCREEN_VIEW_REQUEST_MACHINE_ADDRESS,
            errorCode = error?.httpStatus?.toString().orEmpty(),
            errorMessage = error?.errorMessage.orEmpty()
        )
        actionEventListener?.showError(error)
    }

    override fun logout(msg: ErrorMessage?) {
        actionEventListener?.logout(msg)
    }

    private fun callComboBottomSheet(title: String, list: List<String>, type: Int) {
        val pickerBS = PickerBottomSheetFragment.newInstance(title, list).apply {
            onItemSelectedListener = object :
                PickerBottomSheetFragment.OnItemSelectedListener {
                override fun onSelected(selectedItem: Int) {
                    val selectedInstallment = list.get(selectedItem)
                    selectedInstallment.run {
                        presenter.addressChosen(this)
                    }
                }
            }
        }
        pickerBS.show(childFragmentManager, PICKER_BS)
    }

    override fun onClicked() {
        presenter.onNextButtonClicked()
    }

    override fun retry() {
        presenter.loadMerchant()
    }

    override fun onAddressChosen(address: MachineInstallAddressObj?) {
        if (isAttached()) {
            binding?.apply {
                textViewAddressChoose.text = getString(R.string.other_address)
                textViewAddressStreet.text = getString(
                    R.string.txt_install_machine_address_street,
                    address?.streetAddress.orEmpty(),
                    address?.numberAddress.orEmpty()
                )
                textViewReferencePoint.text = address?.referencePoint.orEmpty()
                textViewNeighborhood.text = address?.neighborhood.orEmpty()
                textViewCity.text = address?.city.orEmpty()
                textViewState.text = address?.state.orEmpty()
                address?.zipcode?.let {
                    textViewZipCode.text =
                        if (it.length == EIGHT && it.containsOnlyNumbers())
                            Formatador.CEP.formata(it)
                        else it
                } ?: run {
                    textViewZipCode.text = EMPTY
                }
            }

            address?.let {
                presenter.addressChosen(address)
            }
        }
    }

    override fun merchantResponse(mStablishment: MCMerchantResponse) {
        val listAddressType = presenter.getAddressTypes()
        if (listAddressType.isNotEmpty()) {
            binding?.apply {
                imageViewArrow.visible()
                imageViewArrow.setOnClickListener {
                    callComboBottomSheet(getString(R.string.adress_type), listAddressType, ZERO)
                }
            }
        }
    }

    companion object {
        private const val DELAY = 2500L
        fun create(bundle: Bundle?) =
            InstalacaoMaquinaChooseAddressFragment().apply {
                arguments = bundle
            }
    }

}
