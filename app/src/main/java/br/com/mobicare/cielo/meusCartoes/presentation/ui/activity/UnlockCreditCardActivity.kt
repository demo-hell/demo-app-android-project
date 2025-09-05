package br.com.mobicare.cielo.meusCartoes.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.injection.Injection
import br.com.mobicare.cielo.meusCartoes.presentation.ui.UnlockCreditCardContract
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.cardActivateCateno.CardNewPasswordActivity
import br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.StartCardActivationFragment
import br.com.mobicare.cielo.meusCartoes.presenter.CreditCardActivationPresenter
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_card_credit_unlock.*
import kotlinx.android.synthetic.main.content_error.*
import org.jetbrains.anko.startActivity

class UnlockCreditCardActivity : BaseActivity(),
        UnlockCreditCardContract.UnlockCreditCardView {


    var presenter: CreditCardActivationPresenter? = null

    var screenPath: String? = null
        get() = intent?.getStringExtra(StartCardActivationFragment.SCREEN_PATH)

    var issuer: String? = null
        get() = intent?.getStringExtra(StartCardActivationFragment.SCREEN_ISSUER)


    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_credit_unlock)

        setupToolbar(toolbarBackCardActivation as Toolbar,
                getString(R.string.text_my_cards_title))

        editTextCardActivation.requestFocus()

        container_error.visibility = View.GONE

        presenter = CreditCardActivationPresenter(Injection
                .provideCreditCardsRepository(this)).apply {
            this.uiScheduler = AndroidSchedulers.mainThread()
            this.ioScheduler = Schedulers.io()
        }

        presenter?.setView(this)

        compositeDisposable.add(RxTextView.afterTextChangeEvents(editTextCardActivation)
                .filter {
                    !TextUtils.isEmpty(it.editable()) && it.editable()!!.length > 14
                }.map {
                    it.editable()
                }
                .subscribe ({
                    callNextStep(it.toString())
                },{

                }))

        button_error_try.setOnClickListener {
            callNextStep(editTextCardActivation.text.toString())
           // presenter?.startCardActivation(editTextCardActivation.text.toString())
        }

        Analytics.trackScreenView(
            screenName = currentScreenPath(),
            screenClass = this.javaClass
        )
    }

    private fun callNextStep(proxy: String) {

        if (!issuer.isNullOrEmpty() && issuer == "CATENO") {
            startActivity<CardNewPasswordActivity>(
                    StartCardActivationFragment.SCREEN_PATH to currentScreenPath(),
                    "PROXY_CARD" to proxy)
            finish()
        } else {
            presenter?.startCardActivation(proxy)
        }
    }

    override fun onResume() {
        super.onResume()

        if (!TextUtils.isEmpty(editTextCardActivation.text) &&
                editTextCardActivation.text!!.length > 14) {
            callNextStep(editTextCardActivation.text.toString())
            //presenter?.startCardActivation(editTextCardActivation.text.toString())
        }
    }


    override fun showLoading() {
        hideSoftKeyboard()
        container_error.visibility = View.GONE
        frameUnlockProgress.visibility = View.VISIBLE
        linearFirstBackActivationContent.visibility = View.GONE
    }

    override fun hideLoading() {
        frameUnlockProgress.visibility = View.GONE
        linearFirstBackActivationContent.visibility = View.VISIBLE
    }

    override fun showSuccessActivation() {
        val intent = Intent("card_sent_success")
        intent.putExtra("titleCard", getString(R.string.text_card_activation_success_title))
        intent.putExtra("descriptionCard", getString(R.string.text_card_activation_success_description))
        intent.putExtra("buttonCard", getString(R.string.text_card_document_sent_success_button))
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

        finish()
    }

    override fun showError(error: ErrorMessage) {
        linearFirstBackActivationContent.visibility = View.GONE
        container_error.visibility = View.VISIBLE
        text_view_error_msg.text = error.message
    }

    override fun logout(message: String) {
        if (isAttached()) {
            AlertDialogCustom.Builder(this, getString(R.string.home_ga_screen_name))
                    .setTitle(R.string.app_name)
                    .setMessage(message)
                    .setBtnRight(getString(R.string.ok))
                    .setCancelable(false)
                    .setOnclickListenerRight {
                        onBackPressed()
                    }
                    .show()
        }
    }

    fun currentScreenPath(): String = "$screenPath/${Action.UNLOCCK_CARD_ACTIVATION_SCREEN}"

    override fun showInvalidCardNumber() {
        showErrorMessage(ErrorMessage().apply {
            this.title = getString(R.string.text_wrong_activation_number)
            this.message = getString(R.string.text_wrong_activation_number_message)
        }, getString(R.string.text_wrong_activation_number))
    }
}