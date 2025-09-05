package br.com.mobicare.cielo.commons.ui.barcode

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.barcode.CaptureManager
import br.com.mobicare.cielo.commons.presentation.utils.barcode.CompoundBarcodeView
import br.com.mobicare.cielo.meusCartoes.domains.entities.Card
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.BillsPaymentActivity
import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.android.synthetic.main.activity_payments_barcode_scan.*

class PaymentsBarCodeScanActivity : AppCompatActivity(), CaptureManager.OnBarcodeScanResult {

    var capture: CaptureManager? = null
    private var scanned = false
    private val handler = Handler()
    private var barcode: String? = null

    private lateinit var barcodeScannerView: CompoundBarcodeView

    private var userCreditCard: Card? = null
        get() = intent?.getParcelableExtra(BillsPaymentActivity.USER_CREDIT_CARD)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureFullScreen()

        setContentView(R.layout.activity_payments_barcode_scan)

        configureBarcode()

        configureImageClose()

        configureButtonTypeBarcode()

        configureDelayExit()
    }

    private fun configureDelayExit() {
        handler.postDelayed({
            if (!scanned) {
                exitCanceled()
            }
        }, 30000)
    }

    private fun configureButtonTypeBarcode() {
        buttonTypeBarcode.setOnClickListener {
            exitCanceled()
        }
    }

    private fun configureImageClose() {
        imageClose.setOnClickListener {
            exitCanceled()
        }
    }

    private fun configureBarcode() {
        barcodeScannerView = findViewById<CompoundBarcodeView>(R.id.zxing_barcode_scanner)
        barcodeScannerView.setListenerPerSecond {}

        capture = CaptureManager(this, barcodeScannerView)
        capture?.initializeFromIntent(intent, null)
        capture?.decode()
    }

    private fun configureFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onResume() {
        super.onResume()
        capture?.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture?.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onBackPressed() {
        scanned = true
        handler.removeCallbacksAndMessages(null)
        exitCanceled()
    }

    private fun exitCanceled() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        capture?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }


    override fun onBarcodeScanSuccess(result: BarcodeResult?) {

        barcode = result?.text
        scanned = true

        handler.removeCallbacksAndMessages(null)

        val returnIntent = intent
        returnIntent.putExtra("result", barcode)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()

    }

}