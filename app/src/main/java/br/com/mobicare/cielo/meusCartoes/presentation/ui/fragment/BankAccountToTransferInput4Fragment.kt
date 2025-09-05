package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceEditTextView
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.Authorization
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BankTransferRequest
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.TransferAuthorization
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.TransferResponse
import br.com.mobicare.cielo.meusCartoes.domains.entities.BankAccountType
import br.com.mobicare.cielo.meusCartoes.domains.entities.Card
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.DirectElectronicTransferActivity
import br.com.mobicare.cielo.meusCartoes.presenter.BankAccountConfirmTransferenceContract
import br.com.mobicare.cielo.meusCartoes.presenter.BankAccountToTransferInputContract
import br.com.mobicare.cielo.meusCartoes.presenter.BankAccountToTransferInputPresenter
import kotlinx.android.synthetic.main.fragment_input_transfer_to_account_bank_4.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class BankAccountToTransferInput4Fragment : BaseFragment(), BankAccountToTransferInputContract.View, BankAccountConfirmTransferenceContract.View {

    var textInputEditDt: TypefaceEditTextView? = null
    var textInputEditCvv: TypefaceEditTextView? = null
    var tranferenceBottomSheetFragment: TransferenceBottomSheetFragment? = null

    val presenter: BankAccountToTransferInputPresenter by inject {
        parametersOf(this)
    }

    private var bankTransferRequest: BankTransferRequest? = null
        get() = arguments?.getParcelable(DirectElectronicTransferActivity.TRANFER_DIRECT_KEY)

    private var transferResponse: TransferResponse? = null
        get() = arguments?.getParcelable(DirectElectronicTransferActivity.TRANSFER_RESPONSE_KEY)

    private var userCreditCard: Card? = null
        get() = arguments?.getParcelable(DirectElectronicTransferActivity.USER_CREDIT_CARD)

    companion object {
        fun create(): BankAccountToTransferInput4Fragment {
            return BankAccountToTransferInput4Fragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_input_transfer_to_account_bank_4,
                container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonBankNext.setOnClickListener {
            tranferenceBottomSheetFragment = TransferenceBottomSheetFragment.newInstance(this)
            tranferenceBottomSheetFragment!!.show(requireActivity().supportFragmentManager,
                    "transference_bottom_sheet")
        }

        configureTextPerfomed()
        initView()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun initView() {

        bankTransferRequest?.run {

            textDetailTitleSecValue.text = this.amount.toPtBrRealString()
            textDetailUser.text = this.accountHolderName
            //addInFrame mask
            val cpfOrCnpj = this.accountHolderDocument
            textDetailCpforCnpj.text = if(cpfOrCnpj.length > 11) addMaskCpforCnpj(this.accountHolderDocument, getString(R.string.mask_cnpj_step4))
            else addMaskCpforCnpj(this.accountHolderDocument, getString(R.string.mask_cpf_step4))

            typefaceTextView3.text = if(cpfOrCnpj.length > 11) getString(R.string.mask_cnpj_step4_text) else getString(R.string.mask_cpf_step4_text)

            textDetailAg.text = this.bankBranch
            textDetailACount.text = "${this.bankAccount}-${this.bankAccountDigit}"
            textDetailBank.text = "${this.bankCode} - ${this.bankName}"

            val typeAccount = this.accountType

            textDetailTypeAcount.text = if(typeAccount.equals(BankAccountType.CHECKING.typeName)) getString(R.string.text_accout_checking_label)
                                        else getString(R.string.text_account_savings_label)

            textDetailDate.text = this.transferDate

            transferResponse?.run {
                text_boss_one_title.text = getString(R.string.text_my_cards_detail_box_title,
                        this.fee)
            }

        }

    }

    private fun configureTextPerfomed() {
        val text = SpannableStringBuilder()

        text.append(getString(R.string.text_my_cards_detail_box2_title_1)
                .addSpannable(TextAppearanceSpan(requireContext(), R.style.TextBlack11spNormal)))

        text.append(" ")

        text.append(getString(R.string.text_my_cards_detail_box2_title_2)
                .addSpannable(TextAppearanceSpan(requireContext(), R.style.TextBlackAcountBoss)))

        text.append(" ")

        text.append(getString(R.string.text_my_cards_detail_box2_title_3).addSpannable(TextAppearanceSpan(requireContext(), R.style.TextBlack11spNormal)))

        text.append(" ")

        text.append(getString(R.string.text_my_cards_detail_box2_title_4)
                .addSpannable(TextAppearanceSpan(requireContext(), R.style.TextBlack11spNormal)))

        text.append(" ")

        text.append(getString(R.string.text_my_cards_detail_box2_title_5_ted)
                .addSpannable(TextAppearanceSpan(requireContext(), R.style.TextBlackAcountBoss)))

        text.append(" ")

        text.append(getString(R.string.text_my_cards_detail_box2_title_6).addSpannable(TextAppearanceSpan(requireContext(), R.style.TextBlack11spNormal)))

        text.append(" ")

        text.append(getString(R.string.text_my_cards_detail_box2_title_7)
                .addSpannable(TextAppearanceSpan(requireContext(), R.style.TextBlack11spNormal)))

        text_my_cards_detail_box2_title.text = text
    }

    override fun initData() {
    }

    override fun nextStep(transferResponse: TransferResponse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun errorFields() {
    }

    override fun updateButtonNextState(enabled: Boolean) {

    }

    override fun showProgress() {
        if (isAdded) {
            changeDialogShowLoading(View.VISIBLE, View.GONE)
            tranferenceBottomSheetFragment!!.isCancelable = false
            this.textInputEditDt!!.isEnabled = false
            this.textInputEditCvv!!.isEnabled = false
        }
    }

    override fun hideProgress() {
        if (isAdded) {
            changeDialogShowLoading(View.GONE, View.VISIBLE)
            Handler().postDelayed({
                tranferenceBottomSheetFragment!!.isCancelable = true
                this.textInputEditDt!!.isEnabled = true
                this.textInputEditCvv!!.isEnabled = true
            }, 2000)
        }
    }


    private fun changeDialogShowLoading(progressVisibility: Int, buttonTransferVisibility: Int) {
        tranferenceBottomSheetFragment!!.changeDialogShowLoading(progressVisibility, buttonTransferVisibility)
    }

    @SuppressLint("StringFormatInvalid")
    override fun transferSuccess(successMessage: String) {

        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, CONTA_DIGITAL),
            action = listOf(CONTA_DIGITAL_TRANSFERENCIA_TED, Action.CALLBACK),
            label = listOf(SUCESSO, getString(R.string.text_card_document_sent_success_title_botton_sheet))
        )

        val intent = Intent("card_sent_success")
        intent.putExtra("titleCard", getString(R.string.text_card_document_sent_success_title_botton_sheet))
        intent.putExtra("descriptionCard", successMessage)
        intent.putExtra("buttonCard", getString(R.string.txt_button_msg_sucess_payment))
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(requireActivity().baseContext).sendBroadcast(intent)

        val screen = requireActivity()
        screen.finish()

        tranferenceBottomSheetFragment?.dismiss()
    }

    override fun wrongTransfer(message: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, CONTA_DIGITAL),
            action = listOf(CONTA_DIGITAL_TRANSFERENCIA_TED, Action.CALLBACK),
            label = listOf(ERRO, message)
        )
    }

    override fun showError() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, CONTA_DIGITAL),
            action = listOf(CONTA_DIGITAL_TRANSFERENCIA_TED, Action.CALLBACK),
            label = listOf(ERRO, getString(R.string.text_unavaiable_transfer_bank))
        )
        if (isAttached()) {
            requireActivity().showMessage(getString(R.string.text_unavaiable_transfer_bank))
        }
    }


    override fun unavaiableAmount() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun showWrongInputDataError() {
        tranferenceBottomSheetFragment?.dismiss()
        requireActivity().showMessage(getString(R.string.text_wrong_input_data_message),
                getString(R.string.text_transfer_dialog_title))
    }

    override fun showUnavaibleServer() {
        tranferenceBottomSheetFragment?.dismiss()
        requireActivity().showMessage(getString(R.string.text_unavaiable_server_message))
    }

    override fun showWrongExpirationDate() {
        tranferenceBottomSheetFragment?.showWrongExpirationDate()
    }

    override fun showEmptyCvv() {
        tranferenceBottomSheetFragment!!.showEmptyCvv()
    }


    override fun showUnauthorizedAndClose() {
        tranferenceBottomSheetFragment?.dismiss()
        requireActivity().showMessage(getString(R.string.text_session_timeout_message),
                getString(R.string.text_transfer_error_title)) {
            setBtnLeft(getString(R.string.ok))
            setOnclickListenerRight {
                requireActivity().finish()
                Utils.logout(activity as Activity)
            }
        }
    }

    fun addMaskCpforCnpj(textoAFormatar: String, mask: String): String {
        var formatado = ""
        var i = 0
        // vamos iterar a mascara, para descobrir quais caracteres vamos adicionar e quando...
        for (m in mask.toCharArray()) {
            if (m != '#') { // se não for um #, vamos colocar o caracter informado na máscara
                formatado += m
                continue
            }
            // Senão colocamos o valor que será formatado
            try {
                formatado += textoAFormatar[i]
            } catch (e: Exception) {
                break
            }

            i++
        }
        return formatado
    }

    override fun confirmTransference(textInputEditDt: TypefaceEditTextView, textInputEditCvv: TypefaceEditTextView) {

        this.textInputEditDt = textInputEditDt
        this.textInputEditCvv = textInputEditCvv

        userCreditCard?.run userCard@{
            transferResponse?.run {
                presenter.confirmTransaction(this@userCard.proxyNumber,
                        TransferAuthorization(Authorization(textInputEditCvv.text.toString(),
                                textInputEditDt.text.toString())), this)
            }
        }

    }

    override fun logout(msg: ErrorMessage) {
        if (isAttached()) {
            val screen = requireActivity() as DirectElectronicTransferActivity
            AlertDialogCustom.Builder(requireContext(), getString(R.string.menu_meus_cartoes))
                    .setTitle(R.string.menu_meus_cartoes)
                    .setMessage("Sessão Expirada.")
                    .setBtnRight(getString(R.string.ok))
                    .setCancelable(false)
                    .setOnclickListenerRight {
                        if (!screen.isFinishing) {
                            Utils.logout(requireActivity())
                            screen.finish()
                        }

                    }
                    .show()
        }
    }
}