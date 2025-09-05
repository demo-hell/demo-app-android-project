package br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.addAccount.transferAccount

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.ERRO
import br.com.mobicare.cielo.commons.analytics.MEUS_CADASTRO
import br.com.mobicare.cielo.commons.analytics.MEUS_CADASTRO_CONTAS_ADICIONAR
import br.com.mobicare.cielo.commons.constants.ELEGIBILITY
import br.com.mobicare.cielo.commons.constants.FOUR
import br.com.mobicare.cielo.commons.constants.OTP_CODE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.meuCadastroDomicilio.MEU_CADASTRO_DOMICILIO_BANKS
import br.com.mobicare.cielo.meuCadastroDomicilio.MEU_CADASTRO_DOMICILIO_DESTINATION
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.AccountTransferRequest
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.BankAccount
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.CardBrand
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.Destination
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.DomicilioBankVo
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.DomicilioFlagVo
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.Origin
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.FlagTransferActionListener
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.FlagTransferQuantityListener
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.activity.AddAccountEngineActivity
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroAnalytics
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroGA4.ScreenView.SCREEN_VIEW_MY_PROFILE_ACCOUNT_ADD_ACCOUNT_SUCCESS
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroGA4.logScreenView
import br.com.mobicare.cielo.meuCadastroNovo.domain.Bank
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fl_fragment_add_account_02.errorLayout
import kotlinx.android.synthetic.main.fl_fragment_add_account_02.errorLayoutDefault
import kotlinx.android.synthetic.main.fl_fragment_add_account_02.materialCardView
import kotlinx.android.synthetic.main.fl_fragment_add_account_02.recycler_view_banks
import kotlinx.android.synthetic.main.ft_fragment_02_item_bank.view.line_view_down
import kotlinx.android.synthetic.main.ft_fragment_02_item_bank.view.recycler_view_flags
import kotlinx.android.synthetic.main.ft_fragment_02_item_bank.view.text_view_ag
import kotlinx.android.synthetic.main.ft_fragment_02_item_bank.view.text_view_bank_name
import kotlinx.android.synthetic.main.ft_fragment_02_item_bank.view.text_view_cc
import kotlinx.android.synthetic.main.ft_fragment_02_item_flag.view.content_flag
import kotlinx.android.synthetic.main.ft_fragment_02_item_flag.view.image_view_flag
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

private const val SEED = "seed"
private const val CODE_995 = "995"

/**
 * create by Claudio Stevanato
 * */
class AddAccount02Fragment : BaseFragment(),
    FlagTransferActionListener, AddAccountContract.View {

    private var actionListener: ActivityStepCoordinatorListener? = null
    private var quantityListener: FlagTransferQuantityListener? = null
    private var listenerProgress: BaseView? = null

    private lateinit var adapter: DefaultViewListAdapter<DomicilioBankVo>
    private val bankVo = ArrayList<DomicilioBankVo>()
    private var bankDestination: Destination? = null
    val analytics: MeuCadastroAnalytics by inject()

    var elegibility: Boolean? = false

    private val isUserTokenOnWhitelist by lazy {
        arguments?.getBoolean(AddAccountEngineActivity.IS_USER_MFA_WHITELIST)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    val presenter: AddAccountPresenter by inject {
        parametersOf(this)
    }

    companion object {
        fun newInstance(
            actionListner: ActivityStepCoordinatorListener,
            quantityListener: FlagTransferQuantityListener, bundle: Bundle,
            listenerProgress: BaseView
        ): AddAccount02Fragment {
            val fragment = AddAccount02Fragment()
            fragment.arguments = bundle
            fragment.actionListener = actionListner
            fragment.quantityListener = quantityListener
            fragment.listenerProgress = listenerProgress
            return fragment

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fl_fragment_add_account_02, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analytics.addFlagsScreenView()
        initView()

        errorLayout.configureActionClickListener {
            if (elegibility == true) {
                registerOtpSuccess()
            } else
                proceedToAddAccount(OTP_CODE)
        }

        errorLayoutDefault?.configureActionClickListener {
            if (elegibility == true) {
                registerOtpSuccess()
            } else
                proceedToAddAccount(OTP_CODE)
        }
    }

    private fun sendErrorEvent(error: ErrorMessage) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, MEUS_CADASTRO),
            action = listOf(MEUS_CADASTRO_CONTAS_ADICIONAR, Action.CALLBACK),
            label = listOf(ERRO, error.errorMessage, error.errorCode)
        )
    }

    fun initView() {
        quantityListener?.showBottomBar()
        quantityListener?.quantitychosen(ZERO)
        listenerProgress?.lockScreen()
        loadArguments()
        loadBanks()

    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    private fun loadBanks() {
        recycler_view_banks?.layoutManager = LinearLayoutManager(requireActivity())

        val filteredBanks = bankVo.filter { it.code != CODE_995 }

        if (filteredBanks.isNotEmpty()) {
            adapter = DefaultViewListAdapter(filteredBanks, R.layout.ft_fragment_02_item_bank)

            adapter.setBindViewHolderCallback(object :
                DefaultViewListAdapter.OnBindViewHolderPositon<DomicilioBankVo> {
                override fun onBind(
                    item: DomicilioBankVo,
                    holder: DefaultViewHolderKotlin,
                    position: Int,
                    lastPositon: Int
                ) {
                    loadFlags(holder, item)
                    holder.itemView.text_view_bank_name.text = item.name
                    if (item.agencyDigit == null) {
                        holder.itemView.text_view_ag.text = "Ag: ${item.agency}"
                    } else {
                        holder.itemView.text_view_ag.text = "Ag: ${item.agency}-${item.agencyDigit}"
                    }
                    holder.itemView.text_view_cc.text =
                        "Cc: ${item.accountNumber}-${item.accountDigit}"

                    if (position == lastPositon) holder.itemView.line_view_down.gone()
                }
            })
            recycler_view_banks?.adapter = adapter
        }
    }

    private fun loadFlags(holder: DefaultViewHolderKotlin, item: DomicilioBankVo) {
        holder.itemView.recycler_view_flags?.layoutManager = GridLayoutManager(
            requireActivity(),
            FOUR
        )
        item.brands?.let { brands ->
            val adapterFlag =
                DefaultViewListAdapter(brands, R.layout.ft_fragment_02_item_flag)
            adapterFlag.setBindViewHolderCallback(object :
                DefaultViewListAdapter.OnBindViewHolder<DomicilioFlagVo> {
                override fun onBind(item: DomicilioFlagVo, holder: DefaultViewHolderKotlin) {
                    holder.itemView.setBackgroundResource(getCheckedBackground(item.checked))

                    Picasso.get()
                        .load(item.imgSource)
                        .into(holder.itemView.image_view_flag)

                    holder.itemView.content_flag.setOnClickListener {
                        item.checked = item.checked.not()
                        holder.itemView.setBackgroundResource(getCheckedBackground(item.checked))

                        checkDateScreen()
                    }
                }
            })
            holder.itemView.recycler_view_flags.adapter = adapterFlag
        }
    }

    private fun getCheckedBackground(checked: Boolean) =
        if (checked)
            R.drawable.ft_frag_02_item_selector_check
        else
            R.drawable.ft_frag_02_item_selector_uncheck

    private fun loadArguments() {
        arguments?.let { itBundle ->

            if (itBundle.containsKey(MEU_CADASTRO_DOMICILIO_DESTINATION))
                bankDestination = itBundle.getParcelable(MEU_CADASTRO_DOMICILIO_DESTINATION)

            val banks = itBundle.getParcelableArrayList<Bank>(MEU_CADASTRO_DOMICILIO_BANKS)
            elegibility = itBundle.getBoolean(ELEGIBILITY)
            banks?.forEach { bank ->
                val flagVo = ArrayList<DomicilioFlagVo>()
                val newBankVo = DomicilioBankVo(
                    bank.accountDigit ?: EMPTY,
                    bank.accountNumber ?: EMPTY,
                    bank.agency ?: EMPTY,
                    bank.agencyDigit ?: EMPTY,
                    flagVo,
                    bank.code,
                    bank.imgSource,
                    bank.name,
                    bank.savingsAccount, false
                )

                bank.brands?.forEach { brand ->
                    flagVo.add(
                        DomicilioFlagVo(
                            brand.code,
                            brand.imgSource,
                            brand.name,
                            false,
                            newBankVo
                        )
                    )
                }

                bankVo.add(newBankVo)

            }
        }
    }

    private fun checkDateScreen() {
        var isFlags = false
        var qtd = ZERO
        bankVo.forEach { bank ->
            val i = bank.brands?.filter { it.checked }?.size
            i?.let {
                qtd += it
                if (it > ZERO) {
                    isFlags = true
                }
            }
        }
        quantityListener?.quantitychosen(qtd)
        if (isFlags)
            listenerProgress?.unlockScreen()
        else
            listenerProgress?.lockScreen()
    }

    override fun validade(bundle: Bundle?) {
        if (elegibility == true) {
            registerOtpSuccess()
        } else
            proceedToAddAccount(OTP_CODE)
    }

    override fun validateWithProcedure(
        bundle: Bundle?,
        transferAccountLambda: (origins: ArrayList<Origin>) -> Unit
    ) {
        val origins = ArrayList<Origin>()
        populateBanksVo(origins)
        if (origins.isNotEmpty())
            transferAccountLambda(origins)
    }


    override fun transferSuccessWithToken() {
        if (elegibility == true)
            validationTokenWrapper.playAnimationSuccess(
                object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                    override fun callbackTokenSuccess() {
                        showSuccessBottomSheet()
                    }
                })
        else
            showSuccessBottomSheet()
    }

    private fun showSuccessBottomSheet() {
        logScreenView(SCREEN_VIEW_MY_PROFILE_ACCOUNT_ADD_ACCOUNT_SUCCESS)

        bottomSheetGenericFlui(
            EMPTY,
            R.drawable.ic_08,
            getString(R.string.rm_ss_sub_title),
            getString(R.string.message_success_new_deadline),
            EMPTY,
            getString(R.string.text_close),
            statusNameTopBar = false,
            statusTitle = true,
            statusSubTitle = true,
            statusImage = true,
            statusBtnClose = false,
            statusBtnFirst = false,
            statusBtnSecond = true,
            statusView1Line = true,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    dialog.dismiss()
                    actionListener?.onNextStep(true, null)

                }
            }

        }.show(requireActivity().supportFragmentManager, getString(R.string.bottom_sheet_generic))

    }

    override fun transferSuccess() {
        showSuccessBottomSheet()
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
        quantityListener?.showBottomBar()
        listenerProgress?.hideLoading()
        error?.let {
            requireActivity().showMessage(it.message, "Adicionar Conta")
        }
    }

    override fun resubmit() {
        quantityListener?.showBottomBar()
        listenerProgress?.hideLoading()
        listenerProgress?.showError(ErrorMessage())
    }

    override fun registerOtpSuccess() {
        val origins = ArrayList<Origin>()
        populateBanksVo(origins)

        if (origins.isNotEmpty()) {
            bankDestination?.let {
                validationTokenWrapper.generateOtp(showAnimation = elegibility == true) { otpCode ->
                    presenter.transferAccount(
                        AccountTransferRequest(it, origins),
                        otpGenerated = otpCode,
                        isUserOnMfaWhitelist = isUserTokenOnWhitelist ?: false
                    )
                }
            }
        }
        listenerProgress?.showLoading()
        quantityListener?.hideBottomBar()
    }

    override fun proceedToAddAccount(otpGenerated: String?) {
        validateWithProcedure(transferAccountLambda = { origins ->
            isUserTokenOnWhitelist?.let { fieldValue ->
                bankDestination?.let { destination ->
                    validationTokenWrapper.generateOtp(showAnimation = fieldValue) {
                        presenter.transferAccount(
                            AccountTransferRequest(destination, origins),
                            otpGenerated,
                            isUserOnMfaWhitelist = isUserTokenOnWhitelist ?: false
                        )
                    }
                }
            }
        })
    }

    private fun populateBanksVo(origins: ArrayList<Origin>) {
        bankVo.forEach { bank ->

            val cardBrand = ArrayList<CardBrand>()
            var bankAccount: BankAccount? = null

            bank.brands?.filter { it.checked }?.forEach { flag ->

                bankAccount = BankAccount(
                    bank.accountNumber,
                    bank.accountDigit,
                    bank.agency,
                    bank.accountDigit,
                    bank.code,
                    bank.savingsAccount
                )
                cardBrand.add(CardBrand(flag.code))

            }
            bankAccount?.let {
                if (cardBrand.isNotEmpty()) {
                    origins.add(Origin(it, cardBrand))
                }
            }
        }
    }

    override fun errorOnOtpGeneration(error: ErrorMessage) {
        if (elegibility == true) {
            validationTokenWrapper.playAnimationError(error,
                object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                    override fun callbackTokenError() {
                        quantityListener?.hideBottomBar()
                        listenerProgress?.hideLoading()
                        materialCardView?.gone()
                        errorLayoutDefault?.errorHandlerCieloViewImageDrawable =
                            R.drawable.ic_token_invalido
                        errorLayoutDefault?.cieloErrorTitle =
                            getString(R.string.text_title_generic_error_token)
                        errorLayoutDefault?.cieloErrorMessage =
                            getString(R.string.text_subtitle_generic_error_token)
                        errorLayoutDefault?.visible()
                        errorLayout?.gone()
                        errorLayoutDefault?.configureActionClickListener {
                            retryTransfer()
                        }
                    }
                })
        } else {
            quantityListener?.hideBottomBar()
            listenerProgress?.hideLoading()
            materialCardView?.gone()
            errorLayoutDefault?.errorHandlerCieloViewImageDrawable =
                R.drawable.ic_token_invalido
            errorLayoutDefault?.cieloErrorTitle =
                getString(R.string.text_title_generic_error_token)
            errorLayoutDefault?.cieloErrorMessage =
                getString(R.string.text_subtitle_generic_error_token)
            errorLayoutDefault?.visible()
            errorLayout?.gone()
            errorLayoutDefault?.configureActionClickListener {
                retryTransfer()
            }
        }

    }

    override fun showAddAccountErrorType(error: ErrorMessage) {
        validationTokenWrapper.playAnimationError(error,
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenError() {
                    quantityListener?.hideBottomBar()
                    listenerProgress?.hideLoading()
                    errorLayoutDefault?.errorButton?.setText(getString(R.string.ok))
                    materialCardView?.gone()
                    errorLayoutDefault?.cieloErrorMessage = error.errorMessage
                    errorLayoutDefault?.visible()
                    errorLayout?.gone()
                    errorLayoutDefault?.configureActionClickListener {
                        requireActivity().onBackPressed()
                    }
                }
            })
    }

    override fun genericBlockError(error: ErrorMessage) {
        validationTokenWrapper.playAnimationError(error,
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenError() {
                    quantityListener?.hideBottomBar()
                    listenerProgress?.hideLoading()
                    errorLayoutDefault?.errorButton?.setText(getString(R.string.ok))
                    materialCardView?.gone()
                    errorLayoutDefault?.cieloErrorMessage = error.errorMessage
                    errorLayoutDefault?.visible()
                    errorLayout?.gone()
                    errorLayoutDefault?.configureActionClickListener {
                        requireActivity().finish()
                    }
                }
            })
    }

    private fun retryTransfer() {
        validateWithProcedure { origins ->
            isUserTokenOnWhitelist?.let { fieldValue ->
                bankDestination?.let { destination ->
                    validationTokenWrapper.generateOtp(showAnimation = fieldValue) {
                        presenter.transferAccount(
                            AccountTransferRequest(
                                destination,
                                origins
                            ),
                            isUserOnMfaWhitelist = isUserTokenOnWhitelist ?: false
                        )
                    }
                }
            }
        }
    }

    override fun errorOnOtpTemporaryBlocked(error: ErrorMessage) {
        if (elegibility == true) {
            validationTokenWrapper.playAnimationError(error,
                object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                    override fun callbackTokenError() {
                        quantityListener?.hideBottomBar()
                        listenerProgress?.hideLoading()
                        materialCardView?.gone()
                        errorLayoutDefault?.configureButtonLabel(getString(R.string.ok))
                        errorLayoutDefault?.errorHandlerCieloViewImageDrawable =
                            R.drawable.ic_image_error_38
                        errorLayoutDefault?.cieloErrorTitle =
                            getString(R.string.multiple_attempt_error_opt_transfer_account_title)
                        errorLayoutDefault?.cieloErrorMessage =
                            getString(R.string.multiple_attempt_error_opt_transfer_account)
                        errorLayoutDefault?.visible()
                        errorLayout?.gone()
                        errorLayoutDefault?.configureActionClickListener {
                            requireActivity().finish()
                        }
                    }
                })
        } else {
            quantityListener?.hideBottomBar()
            listenerProgress?.hideLoading()
            materialCardView?.gone()
            errorLayoutDefault.configureButtonLabel(getString(R.string.ok))
            errorLayoutDefault.errorHandlerCieloViewImageDrawable =
                R.drawable.ic_image_error_38
            errorLayoutDefault.cieloErrorTitle =
                getString(R.string.multiple_attempt_error_opt_transfer_account_title)
            errorLayoutDefault.cieloErrorMessage =
                getString(R.string.multiple_attempt_error_opt_transfer_account)
            errorLayoutDefault?.visible()
            errorLayout?.gone()
            errorLayoutDefault?.configureActionClickListener {
                requireActivity().finish()
            }
        }

    }

    override fun genericErrorOnOtpGeneration(error: ErrorMessage) {
        if (elegibility == true) {
            validationTokenWrapper.playAnimationError(error,
                object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                    override fun callbackTokenError() {
                        quantityListener?.hideBottomBar()
                        listenerProgress?.hideLoading()
                        materialCardView?.gone()
                        errorLayoutDefault?.errorHandlerCieloViewImageDrawable =
                            R.drawable.ic_generic_error_image
                        errorLayoutDefault?.cieloErrorTitle =
                            getString(R.string.text_title_generic_error)
                        errorLayoutDefault?.cieloErrorMessage =
                            getString(R.string.text_message_generic_error)
                        errorLayoutDefault?.errorButton?.setText(getString(R.string.text_try_again_label))
                        errorLayoutDefault?.visible()
                        errorLayout?.gone()
                        errorLayoutDefault?.configureActionClickListener {
                            retryTransfer()
                        }
                    }
                })
        } else {
            quantityListener?.hideBottomBar()
            listenerProgress?.hideLoading()
            materialCardView?.gone()
            errorLayoutDefault?.errorHandlerCieloViewImageDrawable =
                R.drawable.ic_generic_error_image
            errorLayoutDefault?.cieloErrorTitle =
                getString(R.string.text_title_generic_error)
            errorLayoutDefault?.cieloErrorMessage =
                getString(R.string.text_message_generic_error)
            errorLayoutDefault?.errorButton?.setText(getString(R.string.text_try_again_label))
            errorLayoutDefault?.visible()
            errorLayout?.gone()
            errorLayoutDefault?.configureActionClickListener {
                retryTransfer()
            }
        }

    }

    override fun logout(msg: ErrorMessage?) {
        listenerProgress?.logout(msg)
    }

    override fun transferInProcess() {
    }
}