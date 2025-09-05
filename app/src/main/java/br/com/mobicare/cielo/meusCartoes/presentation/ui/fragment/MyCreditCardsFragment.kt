package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.core.widget.TextViewCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.constants.CreditCard.BUTTON_CARD
import br.com.mobicare.cielo.commons.constants.CreditCard.DESCRIPTION_CARD
import br.com.mobicare.cielo.commons.constants.CreditCard.GRANULARITY_SIZE
import br.com.mobicare.cielo.commons.constants.CreditCard.IS_FINISH_CARD_ARGS
import br.com.mobicare.cielo.commons.constants.CreditCard.MAX_SIZE
import br.com.mobicare.cielo.commons.constants.CreditCard.MIN_SIZE
import br.com.mobicare.cielo.commons.constants.CreditCard.SEND_CARD_SUCCESS
import br.com.mobicare.cielo.commons.constants.CreditCard.TITLE_CARD
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.service.LocalBroadcastContract
import br.com.mobicare.cielo.commons.service.LocalBroadcastService
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.databinding.FragmentMyCreditCardsBinding
import br.com.mobicare.cielo.meusCartoes.domains.entities.Card
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidResponse
import br.com.mobicare.cielo.meusCartoes.presentation.ui.CreditCardsContract
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.BillsPaymentActivity
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.ContaDigitalCatenoActivity
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.DirectElectronicTransferActivity
import br.com.mobicare.cielo.meusCartoes.presenter.CreditCardsPresenter
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

@Keep
class MyCreditCardsFragment : BaseFragment(), CreditCardsContract.CreditCardsView, LocalBroadcastContract, CieloNavigationListener {

    private var binding: FragmentMyCreditCardsBinding? = null
    private val presenter: CreditCardsPresenter by inject {
        parametersOf(this)
    }
    private val isFinish: Boolean by lazy {
        arguments?.getBoolean(IS_FINISH_CARD_ARGS, false) ?: false
    }
    private val screenPath: String?
        get() = arguments?.getString(SCREEN_CURRENT_PATH)

    private lateinit var title: String
    private val localBroadcastService: LocalBroadcastService = LocalBroadcastService(this)
    private var isCardSuccess: Boolean = false
    private var titleCardSuccess: String? = null
    private var descriptionCardSuccess: String? = null
    private var buttonCardSuccess: String? = null
    private var returnedUserCreditCard: Card? = null
    private var navigation: CieloNavigation? = null

    companion object {
        fun create(screenPath: String): MyCreditCardsFragment {
            val creditCardsFragment = MyCreditCardsFragment()
            if (screenPath.isNotEmpty()) {
                val params = Bundle()
                params.putString(SCREEN_CURRENT_PATH, screenPath)
                creditCardsFragment.arguments = params
            }
            return creditCardsFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.myCreditCardsHeader?.txtNumberCard?.let {
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(it,
                MIN_SIZE, MAX_SIZE, GRANULARITY_SIZE, TypedValue.COMPLEX_UNIT_SP)
        }

        title = if (isFinish) getString(R.string.toolbar_pix)
        else getString(R.string.menu_meus_cartoes)
        this.configureToolbarActionListener?.changeTo(title = title)
        setupNavigation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        LocalBroadcastManager
                .getInstance(requireActivity())
                .registerReceiver(localBroadcastService, IntentFilter(SEND_CARD_SUCCESS))

        return FragmentMyCreditCardsBinding.inflate(inflater, container, false)
                .also {
                    binding = it
                }.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(localBroadcastService)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        presenter.onResume()
        getCardInformation()
        configurePrepaidCardActions()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(title)
            navigation?.showButton()
            navigation?.showHelpButton()
            navigation?.setNavigationListener(this)
        }
    }

    private fun configurePrepaidCardActions() {
        binding?.apply {
            homeActionButtonViewPrepaidTed.setOnClickListener {
                if (isAdded) {
                    if (Utils.isNetworkAvailable(requireActivity())) {
                        requireActivity().startActivity<DirectElectronicTransferActivity>(
                                DirectElectronicTransferActivity.USER_CREDIT_CARD to
                                        returnedUserCreditCard)
                    } else showErrorStartActivity()
                }
            }

            homeActionButtonBillsPayment.setOnClickListener {
                if (isAdded) {
                    if (Utils.isNetworkAvailable(requireActivity())) {
                        requireActivity().startActivity<BillsPaymentActivity>(
                                BillsPaymentActivity.USER_CREDIT_CARD to
                                        returnedUserCreditCard)
                    } else showErrorStartActivity()
                }
            }
        }

    }

    private fun showErrorStartActivity() {
        requireContext().showMessage(getString(R.string.title_error_wifi_subtitle),
                title = getString(R.string.title_error_wifi_title))
    }

    private fun getCardInformation() {
        presenter.fetchCardInformation(isCardSuccess)
        isCardSuccess = false
    }

    private fun currentScreenPath(): String = "$screenPath/${Action.MY_CREDIT_CARDS_PATH}"

    override fun showLoading() {
        binding?.apply {
            errorHandlerView.visibility = View.GONE
            linearCreditCardsContent.visibility = View.GONE
            flSuccessLayout.visibility = View.GONE
            frameProgressCreditCards.root.visibility = View.VISIBLE
        }

    }

    override fun hideLoading() {
        binding?.apply {
            frameProgressCreditCards.root.visibility = View.GONE
            flSuccessLayout.visibility = View.GONE
            linearCreditCardsContent.visibility = View.VISIBLE
        }
    }

    override fun showCardsInformation(prepaidResponse: PrepaidResponse) {
        binding?.apply {
            contaDigitalPix.constraintLayoutContentPix.gone()
            myCreditCardsHeader.constraintLayoutHeaderCards.visible()
            guideline.setGuidelineBegin(0)
            myCreditCardsHeader.constraintLayoutHeaderCards.post {
                guideline.setGuidelineBegin(myCreditCardsHeader.constraintLayoutHeaderCards.height)
            }
            frameBottomMyCardsContent.visible()

            returnedUserCreditCard = prepaidResponse.cards?.first()

            returnedUserCreditCard.run {
                myCreditCardsHeader.textCardBalanceAvaiableValue.visibility = View.VISIBLE
                myCreditCardsHeader.textCardBalanceAvaiableValue.text =
                        SpannableStringBuilder.valueOf(Utils.formatValue(this?.balance))

                myCreditCardsHeader.textCardNumber.text = SpannableStringBuilder.valueOf(this?.cardNumber
                        ?.toCharArray()?.takeLast(4)?.joinToString(""))
                myCreditCardsHeader.textStatusLabelContent.text =
                        SpannableStringBuilder.valueOf(this?.cardSituation?.situation)

                presenter.showBottomFragmentProcess(
                        prepaidResponse.status?.type, this, this?.issuer ?: ""
                )
            }
        }
    }

    override fun showError() {
        binding?.apply {
            myCreditCardsHeader.textCardBalanceAvaiableValue.visibility = View.INVISIBLE
            frameProgressCreditCards.root.visibility = View.GONE
            linearCreditCardsContent.visibility = View.GONE
            frameBottomMyCardsContent.visibility = View.GONE
            flSuccessLayout.visibility = View.GONE
            errorHandlerView.visibility = View.VISIBLE
            errorHandlerView.cieloErrorMessage = getString(R.string.text_message_generic_error)
            errorHandlerView.errorButton?.setText(getString(R.string.text_try_again_label))
            errorHandlerView.cieloErrorTitle = getString(R.string.text_title_generic_error)
            errorHandlerView.errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
            errorHandlerView.configureActionClickListener {
                this@MyCreditCardsFragment.getCardInformation()
            }
        }
    }

    override fun logout(errorMessage: ErrorMessage) {
        if (isAttached()) {
            binding?.apply {
                linearCreditCardsContent.visibility = View.VISIBLE
                frameBottomMyCardsContent.visibility = View.VISIBLE
                flSuccessLayout.visibility = View.VISIBLE
                errorHandlerView.visibility = View.GONE
                AlertDialogCustom.Builder(requireContext(), getString(R.string.home_ga_screen_name))
                        .setTitle(R.string.app_name)
                        .setMessage(getString(R.string.text_session_timeout_message_short))
                        .setBtnRight(getString(R.string.ok))
                        .setCancelable(false)
                        .setOnclickListenerRight {
                            Utils.logout(this@MyCreditCardsFragment.activity as Activity)
                        }
                        .show()

            }
        }
    }

    override fun startCardActivation(issuer: String) {
        binding?.apply {
            homeActionButtonViewPrepaidTed.visibility = View.GONE
            homeActionButtonBillsPayment.visibility = View.GONE
        }

        val activationFrag = StartCardActivationFragment.create(currentScreenPath(), issuer)
        activationFrag.addInFrame(childFragmentManager, R.id.frameBottomMyCardsContent)
    }

    override fun startCardAccountFWD() {
        binding?.apply {
            homeActionButtonViewPrepaidTed.visibility = View.GONE
            homeActionButtonBillsPayment.visibility = View.GONE
        }

        val activationFrag = AccountCardActivationFWDFragment.create(currentScreenPath())
        activationFrag.addInFrame(childFragmentManager, R.id.frameBottomMyCardsContent)

    }

    override fun startCardSentSuccess() {
        binding?.apply {
            homeActionButtonViewPrepaidTed.visibility = View.GONE
            homeActionButtonBillsPayment.visibility = View.GONE

            linearCreditCardsContent.visibility = View.GONE
            flSuccessLayout.visibility = View.VISIBLE

            val title = titleCardSuccess ?: return
            val description = descriptionCardSuccess ?: return
            val button = buttonCardSuccess ?: return

            val activationFrag = CardSuccessFragment.newInstance(
                    currentScreenPath(), title, description, button) {
                getCardInformation()
            }
            activationFrag.addInFrame(childFragmentManager, R.id.fl_success_layout)
        }
    }

    override fun startCardActivationFWD() {
        val activationFrag = CardActivationFWDFragment.create(currentScreenPath())
        activationFrag.addInFrame(childFragmentManager, R.id.frameBottomMyCardsContent)
    }

    override fun startCardProcessingFWD() {
        binding?.apply {
            homeActionButtonViewPrepaidTed.visibility = View.GONE
            homeActionButtonBillsPayment.visibility = View.GONE
        }


        val activationFrag = ProcessingCardActivationFWDFragment.create(currentScreenPath())
        activationFrag.addInFrame(childFragmentManager, R.id.frameBottomMyCardsContent)
    }

    override fun showLastTransactions(proxyCard: String, myCardTransfer: Boolean, myCardPayment: Boolean) {
        binding?.apply {
            homeActionButtonViewPrepaidTed.visibility = if (myCardTransfer) View.VISIBLE else View.GONE
            homeActionButtonBillsPayment.visibility = if (myCardPayment) View.VISIBLE else View.GONE
        }

        LastTransactionsFragment.create(currentScreenPath(), proxyCard, title).addInFrame(
                childFragmentManager, R.id.frameBottomMyCardsContent
        )
    }

    override fun startCardReadProblemFWD() {
        binding?.apply {
            homeActionButtonViewPrepaidTed.visibility = View.GONE
            homeActionButtonBillsPayment.visibility = View.GONE
        }


        val activationFrag = CardReadProblemFWDFragment.create(currentScreenPath())
        activationFrag.addInFrame(childFragmentManager, R.id.frameBottomMyCardsContent)
    }

    override fun startCardProblemFWD() {
        binding?.apply {
            homeActionButtonViewPrepaidTed.visibility = View.GONE
            homeActionButtonBillsPayment.visibility = View.GONE
        }

        val activationFrag = CardProblemFWDFragment.create(currentScreenPath())
        activationFrag.addInFrame(childFragmentManager, R.id.frameBottomMyCardsContent)
    }

    override fun action(intent: Intent) {
        isCardSuccess = true
        titleCardSuccess = intent.getStringExtra(TITLE_CARD)
        descriptionCardSuccess = intent.getStringExtra(DESCRIPTION_CARD)
        buttonCardSuccess = intent.getStringExtra(BUTTON_CARD)

        binding?.apply {
            frameProgressCreditCards.root.visibility = View.GONE
            frameProgressCreditCards.loadingCardView.visibility = View.GONE
        }
    }

    override fun showIneligible(message: String) {
        binding?.apply {
            myCreditCardsHeader.textCardBalanceAvaiableValue.visibility = View.INVISIBLE
            frameProgressCreditCards.root.visibility = View.GONE
            linearCreditCardsContent.visibility = View.GONE
            frameBottomMyCardsContent.visibility = View.GONE
            flSuccessLayout.visibility = View.GONE
            errorHandlerView.visibility = View.VISIBLE
            errorHandlerView.cieloErrorMessage = message
            errorHandlerView.errorButton?.setText(getString(R.string.ok))
            errorHandlerView.cieloErrorTitle = getString(R.string.text_title_service_unavailable)
            errorHandlerView.errorHandlerCieloViewImageDrawable = R.drawable.img_ineligible_user
            errorHandlerView.configureActionClickListener {
                this@MyCreditCardsFragment.requireActivity().finish()
            }
        }
    }

    override fun showNotOwnerError(errorMessage: ErrorMessage) {
        binding?.clHeader.gone()
        bottomSheetGenericFlui(
                nameTopBar = "",
                R.drawable.ic_07,
                getString(R.string.text_view_denny_access_title),
                getString(R.string.text_view_denny_access_subtitle),
                nameBtn1Bottom = "",
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
                btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    requireActivity().finish()
                }

                override fun onSwipeClosed() {
                    super.onSwipeClosed()
                    requireActivity().finish()
                }
            }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun showPix(prepaidResponse: PrepaidResponse) {
        val card = prepaidResponse.cards?.first()
        binding?.apply {
            myCreditCardsHeader.constraintLayoutHeaderCards.gone()
            linearButtonActions.gone()
            contaDigitalPix.constraintLayoutContentPix.visible()
            contaDigitalPix.constraintLayoutContentPix.post {
                guideline.setGuidelineBegin(contaDigitalPix.constraintLayoutContentPix.height)
            }
            contaDigitalPix.textViewBalancePix.text = card?.balance?.toPtBrRealString()
        }
        presenter.showBottomFragmentProcess(prepaidResponse.status?.type, card, card?.issuer?: "")
    }

    override fun showErrorAccessDeniedIssuer() {
        binding?.apply {
            clHeader.gone()
        }

        requireActivity().startActivity<ContaDigitalCatenoActivity>()
        requireActivity().finish()
    }

    override fun onBackButtonClicked(): Boolean {
        if (isFinish)
            requireActivity().finish()
        return super.onBackButtonClicked()
    }
}
