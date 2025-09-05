package br.com.mobicare.cielo.research.presentation.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.constants.Store.STORE_APP_CIELO_PACKAGE
import br.com.mobicare.cielo.commons.constants.Store.STORE_MARKET_URI_PREFIX
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.ui.widget.ClosableFullscrenDialog
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.notNull
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.login.domains.entities.CieloInfoDialogContent
import br.com.mobicare.cielo.research.ResearchPresenter
import br.com.mobicare.cielo.research.domains.entities.ResearchRating
import kotlinx.android.synthetic.main.closable_fullscreen_dialog.*
import kotlinx.android.synthetic.main.fragment_dialog_research.*
import kotlinx.android.synthetic.main.layout_ratingbar.*


class ResearchActivityDialog : BaseActivity(), ClosableFullscrenDialog.OnCloseActionButton,
        ClosableFullscrenDialog.OnActionButtonClick {

    private lateinit var researchPresenter: ResearchPresenter

    companion object {
        fun create(context: Context, screen: String) {
            UserPreferences.getInstance().researchData.notNull {
                Analytics.trackScreenView(
                    screenName = "$screen/PesquisaAvalicao",
                    screenClass = ResearchActivityDialog.javaClass
                )
                context.startActivity(Intent(context, ResearchActivityDialog::class.java))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)


        setContentView(R.layout.fragment_dialog_research)

        try {
            researchPresenter = ResearchPresenter(CieloAPIServices.getInstance(this, BuildConfig.HOST_API), null, FeatureTogglePreference.instance)
        } catch (e: Exception) {
            finish()
            return
        }

        configureDialogContent()
        configureRatingChangeListener()
        setTitle()

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }


    private fun setTitle() {
        UserPreferences.getInstance().researchData.notNull {
            text_research?.text = UserPreferences.getInstance().researchData?.title
        }
    }

    private fun configureRatingChangeListener() {
        ratingBar.setOnRatingBarChangeListener { _, _, _ ->
            btn_bottom.visibility = View.VISIBLE

            if (ratingIsLessTreeThree()) {
                configureInputEditText()
            } else {
                textInputRatingBar.visibility = View.GONE
            }
        }
    }

    private fun configureInputEditText() {
        textInputRatingBar.visibility = View.VISIBLE
        editTextInputRatingBar.imeOptions = EditorInfo.IME_ACTION_DONE
        editTextInputRatingBar.setRawInputType(InputType.TYPE_CLASS_TEXT)

        editTextInputRatingBar.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                onButtonClick()
            }
            false
        }
    }

    private fun ratingIsLessTreeThree(): Boolean {
        if ((ratingBar.rating <= 3 && ratingBar.rating > 0)) {
            return true
        }
        return false
    }

    private fun configureDialogContent() {
        var obj = CieloInfoDialogContent()

        obj.apply {
            this.pageElements.add(CieloInfoDialogContent.PageContent().apply {
                this.listener = this@ResearchActivityDialog
                this.buttonLabel = getString(R.string.esqueci_usuario_button_enviar)
            })
        }

        closableFullScreenDialog.configElements(supportFragmentManager, obj, onCloseActionButtonListener = this)

        btn_bottom.visibility = View.GONE
    }

    override fun onCloseButtonClick() {
        if (layout_rating.visibility == View.VISIBLE) {
            researchPresenter.saveResearch(ResearchRating(0), UserPreferences.getInstance().numeroEC)
        }
        onBackPressed()
    }


    override fun onButtonClick() {
        if (ratingIsLessTreeThree()) {
            researchPresenter.saveResearch(
                ResearchRating(ratingBar.rating.toInt(), editTextInputRatingBar.text.toString()),
                UserPreferences.getInstance().numeroEC
            )
            layout_sorry.visibility = View.VISIBLE
            layout_rating.visibility = View.GONE
            btn_bottom.visibility = View.GONE

        } else {
            researchPresenter.saveResearch(ResearchRating(ratingBar.rating.toInt()), UserPreferences.getInstance().numeroEC)
            showAlertRating()
        }


    }

    private fun showAlertRating() {
        AlertDialogCustom.Builder(this, getString(R.string.title_rating_google_play))
                .setTitle(getString(R.string.title_rating_google_play))
                .setMessage(getString(R.string.message_rating_google_play))
                .setBtnRight(getString(R.string.rating))
                .setBtnLeft(getString(R.string.rating_not_now))
                .setOnclickListenerRight {
                    apply {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$STORE_MARKET_URI_PREFIX$STORE_APP_CIELO_PACKAGE")))
                        onBackPressed()
                    }
                }
                .setOnclickListenerLeft {
                    onBackPressed()
                }
                .show()
    }

}