package br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.cielo.libflue.util.EMPTY
import br.com.knowledge.capitulo8_fragment.util.FragmentManagerUtil
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.DomicilioFlagVo
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.FlagTransferBankAccount
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.FlagTransferCode
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.FlagTransferRequest
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.FlagTransferActionListener
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.activity.FlagTransferEngineActivity
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.FlagTransfer04Fragment.Companion.BRANDSELECTED
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.FtScreenSucessBottomSheet.Companion.TYPETRANFER
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.addAccount.transferAccount.AddAccountContract
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.addAccount.transferAccount.AddAccountPresenter
import br.com.mobicare.cielo.meuCadastroNovo.domain.Bank
import kotlinx.android.synthetic.main.fragment_receba_mais_failure.*
import kotlinx.android.synthetic.main.ft_fragment_03.*
import kotlinx.android.synthetic.main.ft_fragment_03.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


/**
 * create by Enzo Teles
 * */
class FlagTransfer03Fragment : BaseFragment(), FlagTransferActionListener, AddAccountContract.View {

    private var bankSelected: Bank? = null
    private var listBanks: ArrayList<Bank>? = null
    private var listBrands: ArrayList<DomicilioFlagVo>? = null
    var listener03: FlagTransferEngineActivity? = null
    private var actionListner: ActivityStepCoordinatorListener? = null
    private var listenerProgress: BaseView? = null

    val presenter: AddAccountPresenter by inject {
        parametersOf(this)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    companion object {
        const val LISTBANK = "listBank"
        const val BANK = "bank"
        fun newInstance(listBanks: Bundle) = FlagTransfer03Fragment().apply {
            arguments = listBanks
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val flagTransferEngineActivity = activity as? FlagTransferEngineActivity
        actionListner = flagTransferEngineActivity
        listenerProgress = flagTransferEngineActivity
        return inflater.inflate(R.layout.ft_fragment_03, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener03?.showTopBar()
        arguments?.let {
            listBanks = it.getParcelableArrayList(LISTBANK)
            bankSelected = it.getParcelable(BANK)
            listBrands = it.getParcelableArrayList(BRANDSELECTED)
            initFragment()
        }

        buttonUpdate.setOnClickListener {
            listener03?.showTopBar()
            validade(null)
        }
    }

    /**
     * setando o fragmento 01 e fragmento 02
     * */
    private fun initFragment() {

        val listBanks034: ArrayList<Bank> = ArrayList()
        listBanks?.let { itBank ->
            listBanks034.addAll(itBank.filter { !it.accountNumber.equals(bankSelected?.accountNumber) })
        }

        // populando o fragment 02
        FragmentManagerUtil.apply {

            val bundle = Bundle().apply {
                putParcelableArrayList(LISTBANK, listBanks)
            }

            val frag = FlagTransfer01Fragment.newInstance(bundle, actionListner) {
                bankSelected = it
            }.apply {
                this.listener = listener03
                this.textTitleBandeiras = "Selecione a conta de destino das bandeiras"
                this.listBanks03 = listBanks034
            }

            replaceFragment(
                R.id.layout_ft_two,
                frag,
                "layout_ft_two",
                false,
                childFragmentManager
            )
        }

        // populando o fragment 01
        FragmentManagerUtil.apply {

            val bundle = Bundle().apply {
                putParcelableArrayList(BRANDSELECTED, listBrands)
                putParcelable(BANK, bankSelected)
            }

            val frag = FlagTransfer04Fragment.newInstance(bundle, actionListner).apply {
                this.listener = listener03
            }

            replaceFragment(
                R.id.layout_ft_one,
                frag,
                "layout_ft_one",
                false,
                childFragmentManager
            )
        }
    }

    /**
     * método que envia as bandeiras para o seu banco de destino
     * @param bundle
     * */
    override fun validade(bundle: Bundle?) {
        if (isAttached()) {
            if (Utils.isNetworkAvailable(requireActivity())) {
                showProgress()

                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, MEUS_CADASTRO),
                    action = listOf(MEUS_CADASTRO_CONTAS_TRANSFERIR_DESTINO, Action.CLIQUE),
                    label = listOf(Label.BOTAO, MEUS_CADASTRO_CONTAS_TRANSFERIR_BANDEIRA)
                )

                val flagtransferbankaccount =
                    FlagTransferBankAccount(
                        bankSelected?.code?.toInt() ?: ZERO,
                        bankSelected?.agency ?: EMPTY,
                        bankSelected?.agencyDigit,
                        bankSelected?.accountNumber ?: EMPTY,
                        bankSelected?.accountDigit ?: EMPTY,
                        bankSelected?.savingsAccount ?: false
                    )

                val listFlagTransferCode = ArrayList<FlagTransferCode>()

                listBrands?.forEach {
                    if (it.checked) {
                        val flagTransferCode = FlagTransferCode(it.code)
                        listFlagTransferCode.add(flagTransferCode)
                    }
                }
                val transferFlag = FlagTransferRequest(flagtransferbankaccount, listFlagTransferCode)

                validationTokenWrapper.generateOtp(
                    onResult = { otpCode ->
                        presenter.transferOfBrands(transferFlag, otpCode)
                    }
                )
            } else {
                requireActivity().showMessage(
                    getString(R.string.title_error_wifi_subtitle),
                    title = getString(R.string.title_error_wifi_title)
                )
            }
        }
    }

    fun showProgress() {

        if (isAttached()) {
            layout_ft_one.invisible()
            layout_ft_two?.gone()
            frameProgress_tb?.visible()
            tb_error?.gone()

            listener03?.onLayoutOptionHide()
        }

    }

    fun hideProgress(viewDB: View?) {

        if (isAttached()) {
            viewDB?.let {
                it.layout_ft_one?.visible()
                it.layout_ft_two?.visible()
                it.frameProgress_tb?.gone()
                it.tb_error?.gone()
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared()
    }

    override fun showLoading() {
        listenerProgress?.showLoading()
    }

    override fun hideLoading() {
        listenerProgress?.hideLoading()
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, MEUS_CADASTRO),
                action = listOf(MEUS_CADASTRO_CONTAS_TRANSFERIR_DESTINO, Action.CALLBACK),
                label = listOf(ERRO, it.errorMessage, it.errorCode)
            )

            if (isAttached()) {
                layout_ft_one?.gone()
                layout_ft_two?.gone()
                frameProgress_tb?.gone()
                tb_error?.visible()
            }
        }

        //listener03.hideTopBar()
    }

    /**
     * método para verificar se a sessão do usuário está expirada.
     * */
    override fun logout(msg: ErrorMessage?) {
        listenerProgress?.logout(msg)
    }

    /**
     * método para mostrar a msg de sucesso para o usuário.
     * */
    override fun transferSuccessWithToken() {
        hideLoading()

        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, MEUS_CADASTRO),
            action = listOf(MEUS_CADASTRO_CONTAS_TRANSFERIR_DESTINO, Action.CALLBACK),
            label = listOf(SUCESSO, getString(R.string.meu_cadastro_transacao_efetuada_token))
        )

        if (isAttached()) {
            bankSelected?.let {
                FtScreenSucessBottomSheet
                    .newInstanceTransferBrands(it, TYPETRANFER, actionListner)
                    .show(
                        childFragmentManager,
                        tag
                    )
            }

        }
    }

    /**
     * método para mostrar a msg que a transação está em processo para o usuário.
     * */
    override fun transferInProcess() {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, MEUS_CADASTRO),
                action = listOf(MEUS_CADASTRO_CONTAS_TRANSFERIR_DESTINO, Action.CALLBACK),
                label = listOf(SUCESSO, getString(R.string.meu_cadastro_transacao_processo_usuario))
            )
            FtScreenProgressingBottomSheet
                .newInstance(actionListner)
                .show(
                    childFragmentManager,
                    "FtScreenProgressingBottomSheet"
                )
        }
    }

    override fun resubmit() {
    }
}
