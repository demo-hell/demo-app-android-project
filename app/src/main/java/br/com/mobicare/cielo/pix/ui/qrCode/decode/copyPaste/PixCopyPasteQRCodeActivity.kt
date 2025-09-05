package br.com.mobicare.cielo.pix.ui.qrCode.decode.copyPaste

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.addTextChangedListener
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.Utils.clipboardHasPlainText
import br.com.mobicare.cielo.commons.utils.Utils.pastePlainTextFromClipboard
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.processErrorMessage
import br.com.mobicare.cielo.commons.utils.showKeyboard
import br.com.mobicare.cielo.databinding.ActivityPixCopyPasteQrCodeBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.DEFAULT_BALANCE
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_BALANCE_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_DECODE_QRCODE_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_FROM_COPY_PASTE_ARGS
import br.com.mobicare.cielo.pix.domain.QRCodeDecodeResponse
import br.com.mobicare.cielo.pix.ui.qrCode.decode.PixDecodeQrCodeNavigationFlowActivity
import br.com.mobicare.cielo.pix.ui.qrCode.decode.read.PixReadQRCodeActivity
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PixCopyPasteQRCodeActivity : BaseLoggedActivity(), PixCopyPasteQRCodeContract.View,
    CieloNavigation {

    private var navigationListener: CieloNavigationListener? = null

    private lateinit var binding: ActivityPixCopyPasteQrCodeBinding

    private val presenter: PixCopyPasteQRCodePresenter by inject {
        parametersOf(this)
    }

    private val balance: String? by lazy {
        intent?.extras?.getString(PIX_BALANCE_ARGS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPixCopyPasteQrCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setListeners()
    }

    private fun setPasteCodeVisibility() {
        binding.tvPasteCode.apply {
            if (clipboardHasPlainText(context))
                visible()
            else
                gone()
        }
    }

    override fun onResume() {
        super.onResume()
        this.showKeyboard(binding.etQrCode)
        binding.etQrCode.post {
            setPasteCodeVisibility()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar.root)
        setTextToolbar(this.getString(R.string.pix_copy_paste_screen_title))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = EMPTY
    }

    private fun setListeners() {
        binding.etQrCode.addTextChangedListener {
            binding.btnCopyPasteNext.isEnabled = it?.isEmpty()?.not() ?: false
        }
        binding.tvPasteCode.setOnClickListener {
            if (clipboardHasPlainText(this).not())
                return@setOnClickListener
            binding.etQrCode.setText(pastePlainTextFromClipboard(this))
        }
        binding.btnCopyPasteNext.setOnClickListener {
            presenter.onValidateQRCode(binding.etQrCode.text.toString())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun setTextToolbar(title: String) {
        if (title.isNotBlank()) {
            val toolbarTitleTextView =
                findViewById<AppCompatTextView>(R.id.textToolbarMainTitle)
            toolbarTitleTextView?.text = title
        }
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        this.navigationListener = listener
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_qr_code, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ic_qr_code -> {
                val balance = this.balance ?: DEFAULT_BALANCE
                startActivity<PixReadQRCodeActivity>(
                    PIX_BALANCE_ARGS to balance,
                    PIX_FROM_COPY_PASTE_ARGS to true
                )
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSuccessValidateQRCode(qrCodeDecode: QRCodeDecodeResponse?) {
        val balance = this.balance ?: DEFAULT_BALANCE
        startActivity<PixDecodeQrCodeNavigationFlowActivity>(
            PIX_DECODE_QRCODE_ARGS to qrCodeDecode,
            PIX_BALANCE_ARGS to balance
        )
    }

    override fun showLoading() {
        binding.containerLoading.visible()
    }

    override fun hideLoading() {
        binding.containerLoading.gone()
    }

    override fun showError(error: ErrorMessage?) {
        val processError = processErrorMessage(
            error,
            getString(R.string.business_error),
            getString(R.string.screen_text_read_qr_code_error_message)
        )
        bottomSheetGenericFlui(
            EMPTY,
            R.drawable.ic_07,
            getString(R.string.screen_text_read_qr_code_error_title),
            processError.message,
            getString(R.string.back),
            getString(R.string.back),
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
            onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    dismiss()
                }
            }
        }.show(supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }
}
