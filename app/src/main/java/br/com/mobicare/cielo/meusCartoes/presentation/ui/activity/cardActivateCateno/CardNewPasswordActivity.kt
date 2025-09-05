package br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.cardActivateCateno

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.afterTextChangesNotEmptySubscribe
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.CardActivationCatenoRequest
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.UnlockCreditCardActivity
import br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.StartCardActivationFragment
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_card_photo_confirm.toolbarBackCardActivation
import kotlinx.android.synthetic.main.content_error.*
import kotlinx.android.synthetic.main.fragment_card_new_password.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class CardNewPasswordActivity : BaseActivity(), CardNewPasswordContract.View {


    private var cardNewPasswordBottomSheetFragment: CardNewPasswordBottomSheetFragment? = null

    private val presenter: CardNewPasswordPresenter by inject {
        parametersOf(this)
    }

    private lateinit var proxyCard: String
    private lateinit var mCardActivation: CardActivationCatenoRequest
    private lateinit var mCvv: String
    private lateinit var mDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_card_new_password)
        setupToolbar(toolbarBackCardActivation as Toolbar,
                getString(R.string.text_my_cards_title))

        container_error.visibility = View.GONE
        frame_progress_view.visibility = View.GONE

        intent?.let {
            proxyCard = it.getStringExtra("PROXY_CARD") ?: ""
        } ?: kotlin.run {
            proxyCard = ""
        }

        textInputPasswordConfirm.afterTextChangesNotEmptySubscribe {
            if (isAttached()) {
                errorFieldCancel(txtPasswordConfirm)
            }
        }

        textInputPassword.afterTextChangesNotEmptySubscribe {
            if (isAttached()) {
                errorFieldCancel(txtPassword)
            }
        }

        button_card_activate.setOnClickListener {
            val cardActivation = CardActivationCatenoRequest(
                    textInputPassword.text.toString(), textInputPasswordConfirm.text.toString())
            if (presenter.isValidPassword(cardActivation)) {
                cardNewPasswordBottomSheetFragment = CardNewPasswordBottomSheetFragment.newInstance(this)
                cardNewPasswordBottomSheetFragment?.show(supportFragmentManager, "cardNewPasswordBottomSheetFragment")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleared()
    }

    override fun onBackPressed() {
        if (isAttached()) {
            startActivity<UnlockCreditCardActivity>(
                    StartCardActivationFragment.SCREEN_PATH to "",
                    StartCardActivationFragment.SCREEN_ISSUER to "CATENO")
            super.onBackPressed()
        }
    }

    //region CardNewPasswordContract.View
    override fun passwordEmpty() {
        if (isAttached()) {
            txtPassword.error = "Preencher o campo senha"
        }
    }

    override fun passwordConfirmEmpty() {
        if (isAttached()) {
            txtPasswordConfirm.error = "Preencher o campo confirmar senha"
        }
    }

    override fun passwordNotMatch() {
        if (isAttached()) {
            txtPasswordConfirm.error = "Senhas não conferem"
        }
    }

    override fun showLoading() {
        if (isAttached()) {
            cardNewPasswordBottomSheetFragment?.changeDialogShowLoading(View.VISIBLE, View.GONE)
            //constraint_main.visibility = View.GONE
            frame_progress_view.visibility = View.VISIBLE
            container_error.visibility = View.GONE
        }
    }

    override fun hideLoading() {
        if (isAttached()) {
            cardNewPasswordBottomSheetFragment?.changeDialogShowLoading(View.GONE, View.VISIBLE)
            constraint_main.visibility = View.VISIBLE
            frame_progress_view.visibility = View.GONE
            container_error.visibility = View.GONE
        }
    }

    override fun showSubmit(error: ErrorMessage) {
        if (isAttached()) {
            cardNewPasswordBottomSheetFragment?.hideDialog()
            constraint_main.visibility = View.GONE
            frame_progress_view.visibility = View.GONE
            container_error.visibility = View.VISIBLE
            text_view_error_msg.text = error.message

            button_error_try.setOnClickListener {
                presenter.activateCard(proxyCard, mCvv, mDate, mCardActivation)
            }
        }
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, CONTA_DIGITAL),
                action = listOf(CONTA_DIGITAL_DESBLOQUEIO_CARTAO, Action.CALLBACK),
                label = listOf(ERRO, it.errorMessage, it.errorCode)
            )

            if (isAttached()) {
                cardNewPasswordBottomSheetFragment?.changeDialogShowLoading(View.GONE, View.VISIBLE)
                cardNewPasswordBottomSheetFragment?.hideDialog()
                frame_progress_view.visibility = View.GONE

                showErrorMessage(ErrorMessage().apply {
                    this.title = getString(R.string.text_wrong_activation_number)
                    this.message = it.message
                }, getString(R.string.text_wrong_activation_number))
            }
        }
    }

    override fun showSuccessActivation() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, CONTA_DIGITAL),
            action = listOf(CONTA_DIGITAL_DESBLOQUEIO_CARTAO, Action.CALLBACK),
            label = listOf(SUCESSO, getString(R.string.text_card_activation_success_title))
        )

        if (isAttached()) {

            val intent = Intent("card_sent_success")
            intent.putExtra("titleCard", getString(R.string.text_card_activation_success_title))
            intent.putExtra("descriptionCard", "Você já pode utiliza-lo para movimentar o dinheiro da sua Conta Digital")
            intent.putExtra("buttonCard", getString(R.string.text_card_document_sent_success_button))
            androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

            finish()
        }
    }

    override fun activateCar(cvv: String, date: String) {
        if (isAttached()) {
            mCardActivation = CardActivationCatenoRequest(
                    textInputPassword.text.toString(), textInputPasswordConfirm.text.toString())
            mCvv = cvv
            mDate = date

            presenter.activateCard(proxyCard, mCvv, mDate, mCardActivation)
        }
    }

    override fun logout(msg: ErrorMessage?) {
        if (isAttached()) {
            AlertDialogCustom.Builder(this, getString(R.string.home_ga_screen_name))
                    .setMessage("Sessão Expirada.")
                    .setBtnRight(getString(R.string.ok))
                    .setCancelable(false)
                    .setOnclickListenerRight {
                        if (!isFinishing) {
                            Utils.logout(this)
                            finish()
                        }
                    }
                    .show()
        }
    }
    //endregion


    private fun errorFieldCancel(view: TextInputLayout) {
        if (isAttached()) {
            view.error = null
            view.isErrorEnabled = false
        }
    }


    override fun showInvalidCardNumber() {
        if (isAttached()) {
            showErrorMessage(ErrorMessage().apply {
                this.title = getString(R.string.text_wrong_activation_number)
                this.message = getString(R.string.text_wrong_activation_number_message)
            }, getString(R.string.text_wrong_activation_number))
        }
    }
}