package br.com.mobicare.cielo.interactbannersoffers.termoAceite

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.open
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.interactbannersoffers.termoAceite.model.TermoAceiteObj
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import kotlinx.android.synthetic.main.activity_add_ec.toolbarInclude
import kotlinx.android.synthetic.main.activity_termo_aceite.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val ARG_TERMO_ACEITE = "ARG_TERMO_ACEITE"
private const val START_TEXT_LINK = 14
private const val END_TEXT_LINK = 43
private const val TERMS_PAGE = 0
private const val WEBVIWEW_PAGE = 1

class TermoAceiteActivity : BaseLoggedActivity(), TermoAceiteContract.View {

    private val mPresenter: TermoAceitePresenter by inject { parametersOf(this) }
    private lateinit var mTermoAceiteObj: TermoAceiteObj
    private var stage = TERMS_PAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_termo_aceite)
        setupToolbar(toolbarInclude as Toolbar, getString(R.string.terms_of_use))

        intent?.extras?.let {
            mTermoAceiteObj = it.getSerializable(ARG_TERMO_ACEITE) as TermoAceiteObj
        }

        setupLayout()
        setupListeners()
    }

    private fun setupLayout() {
        banner?.setImageResource(mTermoAceiteObj.banner)
        textTitle?.text = mTermoAceiteObj.title
        textSubtitle?.text = mTermoAceiteObj.subtitle

        val spannableString = SpannableString(getString(R.string.terms_of_use_message))

        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                stage = WEBVIWEW_PAGE
                setupWebviewPage()
                loadUrl()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(baseContext, R.color.brand_400)
                ds.typeface = Typeface.DEFAULT_BOLD
            }
        }

        spannableString.setSpan(
            clickableSpan,
            START_TEXT_LINK,
            END_TEXT_LINK,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        offerTerm?.text = spannableString
        offerTerm?.highlightColor = Color.TRANSPARENT
        offerTerm?.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupWebviewPage() {
        webViewTerms?.visible()
        container?.gone()

        with(webViewTerms.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
        }
    }

    private fun loadUrl() {
        webViewTerms?.open(mTermoAceiteObj.url, object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                webViewProgress?.visible()
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                webViewProgress?.gone()
                super.onPageFinished(view, url)
            }
        })
    }

    private fun setupListeners() {
        hireButton?.setOnClickListener {
            mPresenter.submitTermoAceite(mTermoAceiteObj.bannerId)
        }

        checkbox?.setOnCheckedChangeListener { _, isChecked ->
            hireButton?.isEnabled = isChecked
        }
    }

    override fun showSuccess() {
        bottomSheetGenericFlui(
            "",
            R.drawable.ic_08,
            mTermoAceiteObj.customMessageSuccess.title,
            mTermoAceiteObj.customMessageSuccess.subtitle,
            "",
            getString(R.string.go_to_initial_screen),
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
            isFullScreen = true
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    finish()
                    startActivity<MainBottomNavigationActivity>()
                }

                override fun onSwipeClosed() {
                    finish()
                    startActivity<MainBottomNavigationActivity>()
                }

                override fun onCancel() {
                    finish()
                    startActivity<MainBottomNavigationActivity>()
                }
            }

        }.show(supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun showError(error: ErrorMessage?) {
        bottomSheetGenericFlui(
            "",
            R.drawable.ic_07,
            getString(R.string.text_title_generic_error),
            getString(R.string.business_error),
            "",
            getString(R.string.go_to_initial_screen),
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
            isFullScreen = true
        ).apply {
            onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    finish()
                    startActivity<MainBottomNavigationActivity>()
                }

                override fun onSwipeClosed() {
                    finish()
                    startActivity<MainBottomNavigationActivity>()
                }

                override fun onCancel() {
                    finish()
                    startActivity<MainBottomNavigationActivity>()
                }
            }
        }.show(supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun onBackPressed() {
        if(stage == WEBVIWEW_PAGE) {
            stage = TERMS_PAGE
            setupTermsPage()
        } else {
            super.onBackPressed()
        }
    }

    private fun setupTermsPage() {
        webViewTerms?.gone()
        webViewProgress?.gone()
        container?.visible()
    }
}