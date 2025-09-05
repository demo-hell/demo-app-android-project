package br.com.mobicare.cielo.pix.ui.qrCode.decode.read

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.processErrorMessage
import br.com.mobicare.cielo.databinding.ActivityPixReadQrcodeBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.DEFAULT_BALANCE
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_BALANCE_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_DECODE_QRCODE_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_FROM_COPY_PASTE_ARGS
import br.com.mobicare.cielo.pix.domain.QRCodeDecodeResponse
import br.com.mobicare.cielo.pix.ui.qrCode.decode.PixDecodeQrCodeNavigationFlowActivity
import br.com.mobicare.cielo.pix.ui.qrCode.decode.copyPaste.PixCopyPasteQRCodeActivity
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PixReadQRCodeActivity : BaseLoggedActivity(), PixReadQRCodeContract.View {

    private val analyzer: PixQrCodeAnalyzer by inject {
        parametersOf(this)
    }
    private val presenter: PixReadQRCodePresenter by inject {
        parametersOf(this)
    }

    private val balance: String? by lazy {
        intent?.extras?.getString(PIX_BALANCE_ARGS)
    }

    private val fromCopyPaste: Boolean? by lazy {
        intent?.extras?.getBoolean(PIX_FROM_COPY_PASTE_ARGS, false)
    }

    private lateinit var binding: ActivityPixReadQrcodeBinding
    private lateinit var cameraExecutor: ExecutorService
    private var cameraProvider: ProcessCameraProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        binding = ActivityPixReadQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()
        checkCameraPermission()
        toBack()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onRestart() {
        super.onRestart()
        checkCameraPermission()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun checkCameraPermission() {
        try {
            val requiredPermissions = arrayOf(Manifest.permission.CAMERA)
            ActivityCompat.requestPermissions(this, requiredPermissions, ZERO)
        } catch (e: IllegalArgumentException) {
            checkIfCameraPermissionIsGranted()
        }
    }

    private fun checkIfCameraPermissionIsGranted() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
            startCamera()
        else {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                ).not()
            ) {
                if (presenter.isFirstTimeAskCameraPermission())
                    finish()
                else
                    showPermissionAlert()
            } else
                finish()

        }
        presenter.onUpdateAskCameraPermission()
    }


    private fun showPermissionAlert() {
        AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setMessage(getString(R.string.screen_text_read_qr_code_camera_permission_message))
            .setPositiveButton(R.string.access_permission_button) { _, _ ->
                toSetting()
            }
            .setNegativeButton(R.string.cancelar) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun toSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts(
            getString(R.string.permission_needed_package),
            packageName,
            null
        )
        intent.data = uri
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkIfCameraPermissionIsGranted()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, analyzer)
                }

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun toBack() {
        binding.ivToolbar.setOnClickListener {
            finish()
        }
    }

    override fun showLoading() {
        binding.containerLoading.visible()
    }

    override fun hideLoading() {
        binding.containerLoading.gone()
    }

    override fun finish() {
        if (fromCopyPaste == true) {
            val balance = this.balance ?: DEFAULT_BALANCE
            startActivity<PixCopyPasteQRCodeActivity>(
                PIX_BALANCE_ARGS to balance
            )
        }
        super.finish()
    }

    override fun onReadQRCode(qrcode: String) {
        presenter.onValidateQRCode(qrcode)
        cameraProvider?.unbindAll()
    }

    override fun onSuccessValidateQRCode(qrCodeDecode: QRCodeDecodeResponse) {
        val balance = this.balance ?: DEFAULT_BALANCE
        startActivity<PixDecodeQrCodeNavigationFlowActivity>(
            PIX_DECODE_QRCODE_ARGS to qrCodeDecode,
            PIX_BALANCE_ARGS to balance
        )
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
                    startCamera()
                }

                override fun onSwipeClosed() {
                    startCamera()
                }

                override fun onCancel() {
                    startCamera()
                }
            }
        }.show(supportFragmentManager, getString(R.string.bottom_sheet_generic))
    }
}