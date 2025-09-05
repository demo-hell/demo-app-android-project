package br.com.mobicare.cielo.meusCartoes.presentation.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.injection.Injection
import br.com.mobicare.cielo.meusCartoes.presentation.ui.CardPhotoConfirmContract
import br.com.mobicare.cielo.meusCartoes.presenter.CardPhotoConfirmPresenter
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_card_photo_confirm.*
import kotlinx.android.synthetic.main.content_error.*
import org.jetbrains.anko.startActivity
import java.io.File

class CardPhotoConfirmActivity : BaseActivity(), CardPhotoConfirmContract.View {


    companion object {
        const val PATH_PHOTO_FRONT = "br.com.cielo.CardPhotoConfirmActivity.photo_front"
        const val PATH_PHOTO_VERSE = "br.com.cielo.CardPhotoConfirmActivity.photo_verse"
        const val RESEND_DOC = "br.com.cielo.CardPhotoConfirmActivity.resend_doc"
    }

    var path_photo_front: String? = null
        get() = intent?.getStringExtra(PATH_PHOTO_FRONT)

    var path_photo_verse: String? = null
        get() = intent?.getStringExtra(PATH_PHOTO_VERSE)

    var isResendDoc: Boolean? = null
        get() = intent?.getBooleanExtra(RESEND_DOC, true)

    private val presenter: CardPhotoConfirmContract.Presenter by lazy {
        CardPhotoConfirmPresenter(this, Injection
                .provideCreditCardsRepository(this)).apply {
            this.uiScheduler = AndroidSchedulers.mainThread()
            this.ioScheduler = Schedulers.io()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_photo_confirm)
        setupToolbar(toolbarBackCardActivation as Toolbar,
                getString(R.string.text_my_cards_title))

        presenter.setView(this)

        path_photo_front?.let {
            showImageFromFile(it, img_photo_front)
        }

        path_photo_verse?.let {
            showImageFromFile(it, img_photo_verse)
        }

        button_error_try.setOnClickListener {
            path_photo_front?.let { pfront ->
                path_photo_verse?.let { pverse ->
                    presenter.sendDocument(pfront, pverse, isResendDoc!!)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (progress.visibility == View.GONE) {
            startActivity<CardPhotoDescriptionActivity>(
                    CardPhotoDescriptionActivity.RESEND_DOC to isResendDoc)
            finish()
        }
    }

    fun showImageFromFile(pathImageFile: String, imageView: ImageView) {
        val imgFile = File(pathImageFile)

        if (imgFile.exists()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Picasso.get()
                        .load(imgFile)
                        .rotate(90f)
                        .into(imageView)
            } else {
                Picasso.get()
                        .load(imgFile)
                        .fit()
                        .rotate(90f)
                        .into(imageView)
            }
        }
    }


    fun btnSendDocumentOnClick(view: View) {
        path_photo_front?.let { pfront ->
            path_photo_verse?.let { pverse ->

                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, CONTA_DIGITAL),
                    action = listOf(CONTA_DIGITAL_ATIVAR_CADASTRO_ATIVACAO, Action.CLIQUE),
                    label = listOf(Label.BOTAO, getString(R.string.text_conta_digital_enviar_fotos))
                )

                presenter.sendDocument(pfront, pverse, isResendDoc!!)
            }
        }
    }


    //region CardPhotoConfirmContract.View

    override fun onSuccessSentCard() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, CONTA_DIGITAL),
            action = listOf(CONTA_DIGITAL_ATIVAR_CADASTRO, Action.CALLBACK),
            label = listOf(SUCESSO, getString(R.string.text_card_document_sent_success_title))
        )

        val intent = Intent("card_sent_success")
        intent.putExtra("titleCard", getString(R.string.text_card_document_sent_success_title))
        intent.putExtra("descriptionCard", getString(R.string.text_card_document_sent_success_description))
        intent.putExtra("buttonCard", getString(R.string.text_card_document_sent_success_button))
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//        startActivity<CardActivationSuccessActivity>(
//                StartCardActivationFragment.SUCESS_TITLE to getString(R.string.text_card_document_sent_success_title),
//                StartCardActivationFragment.SUCESS_DESCRIPTION to getString(R.string.text_card_document_sent_success_description)
//        )
        finish()
    }

    override fun showLoading() {
        cl_content.visibility = View.GONE
        progress.visibility = View.VISIBLE
        container_error.visibility = View.GONE
    }

    override fun hideLoading() {
        progress.visibility = View.GONE
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, CONTA_DIGITAL),
            action = listOf(CONTA_DIGITAL_ATIVAR_CADASTRO, Action.CALLBACK),
            label = listOf(ERRO, it.errorMessage, it.errorCode)
        )

        cl_content.visibility = View.GONE
        progress.visibility = View.GONE
        container_error.visibility = View.VISIBLE
        text_view_error_msg.text = it.message
    }
    }

    override fun logout(msg: ErrorMessage?) {
        msg?.let {
        if (isAttached()) {
            AlertDialogCustom.Builder(this, getString(R.string.home_ga_screen_name))
                    .setTitle(R.string.app_name)
                    .setMessage(it.message)
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
    }

    override fun lockScreen() {
        super.lockScreen()

        btn_send_document.isEnabled = false
    }

    override fun unlockScreen() {
        super.unlockScreen()
        btn_send_document.isEnabled = true
    }

    //endregion
}