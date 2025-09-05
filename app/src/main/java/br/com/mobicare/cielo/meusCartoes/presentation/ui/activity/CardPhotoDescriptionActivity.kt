package br.com.mobicare.cielo.meusCartoes.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.meusCartoes.presentation.ui.CardPhotoDescriptionContract
import br.com.mobicare.cielo.meusCartoes.presenter.CardPhotoDescriptionPresenter
import br.com.mobicare.cielo.picture.BasePicture
import br.com.mobicare.cielo.picture.PrePaidTakePicture2Activity
import br.com.mobicare.cielo.picture.PrePaidTakePictureActivity
import kotlinx.android.synthetic.main.activity_card_photo_description.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

class CardPhotoDescriptionActivity : BaseActivity(), CardPhotoDescriptionContract.View {

    companion object {
        const val RESEND_DOC = "br.com.cielo.CardPhotoDescriptionActivity.RESEND_DOC"
    }

    var isResendDoc: Boolean? = null
        get() = intent?.getBooleanExtra(RESEND_DOC, true)

    private val presenter: CardPhotoDescriptionContract.Presenter by lazy {
        CardPhotoDescriptionPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_photo_description)
        setupToolbar(toolbarBackCardActivation as Toolbar,
                getString(R.string.text_my_cards_title))
    }

    fun btnPhotoOnClick(view: View) {
        presenter.takePicture()
    }

    override fun onStart() {
        super.onStart()
        presenter.setView(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val bundle = data?.extras
        presenter.takePicture(requestCode, resultCode,
                bundle?.get(BasePicture.EXTRA_PHOTO_RETURN).toString())
    }

    //region CardPhotoDescriptionContract.View

    fun startActivityPicture(code: Int, message: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult<PrePaidTakePicture2Activity>(code,
                    BasePicture.PHOTO_TEXT to message)
        } else {
            startActivityForResult<PrePaidTakePictureActivity>(code,
                    BasePicture.PHOTO_TEXT to message)
        }
    }

    override fun takePictureOfFront(codeFront: Int) {
        startActivityPicture(codeFront, "Foto da frente do documento")
    }

    override fun takePictureOfVerse(codeVerse: Int) {
        startActivityPicture(codeVerse, "Foto do verso do documento")
    }

    override fun showSendDocument(photoFront: String?, photoVerse: String?) {
        startActivity<CardPhotoConfirmActivity>(
                CardPhotoConfirmActivity.PATH_PHOTO_FRONT to photoFront,
                CardPhotoConfirmActivity.PATH_PHOTO_VERSE to photoVerse,
                CardPhotoConfirmActivity.RESEND_DOC to isResendDoc
        )
        finish()
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
            if (isAttached()) {
                showMessage(it.message, it.title)
            }
        }
    }

}

