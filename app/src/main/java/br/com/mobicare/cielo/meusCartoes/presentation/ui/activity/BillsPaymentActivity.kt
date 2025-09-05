package br.com.mobicare.cielo.meusCartoes.presentation.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.ui.barcode.PaymentsBarCodeScanActivity
import br.com.mobicare.cielo.commons.utils.FragmentDetector
import br.com.mobicare.cielo.commons.utils.addWithAnimation
import br.com.mobicare.cielo.commons.utils.remove
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentRequest
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentResponse
import br.com.mobicare.cielo.meusCartoes.domains.entities.Card
import br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.payments.PaymentStep2Fragment
import br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.payments.PaymentStep3Fragment
import br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.payments.PaymentStep4Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_payment_bills.*

class BillsPaymentActivity : BaseActivity() {

    private val innerFragmentStack = mutableListOf<Fragment>()
    var indexPosSubject: Subject<Int> = PublishSubject.create()
    var currentPosition = 0
    private var isBloquedBackPress = false


    private val paymentFrag = PaymentStep2Fragment.create().apply {

        onFinishActionListener = object : OnFinishInputActionListener {

            override fun onBloquedBackPressed(isBloqued: Boolean) {
                this@BillsPaymentActivity.isBloquedBackPress = isBloqued
            }

            override fun onFinish(paymentRequest: PrepaidPaymentRequest,
                                  paymentResponse: PrepaidPaymentResponse?,
                                  userCreditCard: Card?) {
                nextStep(paymentRequest, userCreditCard = userCreditCard)
            }


        }
    }

    private var userCreditCard: Card? = null
        get() = intent?.getParcelableExtra(USER_CREDIT_CARD)

    private var compositeDisp = CompositeDisposable()

    private var onBarcodeActionListener: BillsPaymentActivity.OnBarcodeActionListener? = paymentFrag

    companion object {
        const val USER_CREDIT_CARD: String = "br.com.cielo.meusCartoes.userCreditCard"
        const val PREPAID_REQUEST_OBJ_KEY = "br.com.cielo.meusCartoes.prepaidRequest"
        const val PREPAID_USER_CREDIT_CARD = "br.com.cielo.meusCartoes.userCreditCard"
        const val PREPAID_RESPONSE_OBJ_KEY = "br.com.cielo.meusCartoes.prepaidResponse"
        const val BARCODE_SCAN_CODE = 1

        const val IS_CAPTURED_BY_CAMERA = "br.com.cielo.meusCartoes.isCapturedByCamera"
    }


    interface OnFinishInputActionListener {

        fun onFinish(paymentRequest: PrepaidPaymentRequest,
                     paymentResponse: PrepaidPaymentResponse? = null,
                     userCreditCard: Card? = null)

        fun onBloquedBackPressed(isBloqued: Boolean)
    }

    interface OnBarcodeActionListener {
        fun onBarcodeCaptured(barcode: String?)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val i = Intent(this, PaymentsBarCodeScanActivity::class.java)
        startActivityForResult(i, BARCODE_SCAN_CODE)

        setContentView(R.layout.activity_payment_bills)

        if (compositeDisp.isDisposed) {
            compositeDisp = CompositeDisposable()
        }

        configureTranferFragments()

        val paymentBillToolbar = toolbarPaymentBills as Toolbar?

        updateToolbar(paymentBillToolbar)

        innerFragmentStack[currentPosition].addWithAnimation(supportFragmentManager,
                R.id.framePaymentBillsContent)

        compositeDisp.add(indexPosSubject.subscribe({ currentIndex ->

            var lastFragmentInBackStack: String? = null

            if (FragmentDetector.fragmentStack.isNotEmpty()) {
                lastFragmentInBackStack = FragmentDetector.fragmentStack.last()
            }

            innerFragmentStack[currentIndex].addWithAnimation(
                supportFragmentManager,
                R.id.framePaymentBillsContent,
                haveBackAnimation(lastFragmentInBackStack) > currentIndex
            )

            updateToolbar(paymentBillToolbar)
        }, {

        }))
    }

    private fun haveBackAnimation(lastFragmentInBackStack: String?): Int {

        lastFragmentInBackStack?.run {
            return innerFragmentStack.map {
                it::class.java.simpleName
            }.indexOf(this)
        } ?: return -1

    }

    private fun configureTranferFragments() {

        paymentFrag.apply {
            arguments = Bundle().apply {
                putParcelable(USER_CREDIT_CARD, this@BillsPaymentActivity.userCreditCard)
            }
        }

        innerFragmentStack.add(paymentFrag)

        innerFragmentStack.add(PaymentStep3Fragment.create().apply {
            onFinishActionListener = object : OnFinishInputActionListener {

                override fun onBloquedBackPressed(isBloqued: Boolean) {
                    this@BillsPaymentActivity.isBloquedBackPress = isBloqued
                }

                override fun onFinish(paymentRequest: PrepaidPaymentRequest,
                                      paymentResponse: PrepaidPaymentResponse?,
                                      userCreditCard: Card?) {
                    nextStep(paymentRequest, paymentResponse,
                            userCreditCard = userCreditCard)
                }
            }
        })

        innerFragmentStack.add(PaymentStep4Fragment.create())
    }


    private fun nextStep(paymentRequest: PrepaidPaymentRequest,
                         paymentResponse: PrepaidPaymentResponse? = null,
                         userCreditCard: Card? = null) {
        currentPosition++

        //INFO se tiver parametros a passar
        val bundleParams = Bundle()
        bundleParams.putParcelable(PREPAID_REQUEST_OBJ_KEY, paymentRequest)

        if (paymentResponse != null) {
            bundleParams.putParcelable(PREPAID_RESPONSE_OBJ_KEY, paymentResponse)
        }

        if (userCreditCard != null) {
            bundleParams.putParcelable(PREPAID_USER_CREDIT_CARD, userCreditCard)
        }

        innerFragmentStack[currentPosition].arguments = bundleParams

        indexPosSubject.onNext(currentPosition)
    }

    private fun updateToolbar(paymentBillToolbar: Toolbar?) {
        paymentBillToolbar?.run {
            setupToolbar(paymentBillToolbar, getString(R.string.text_transfer_title,
                    currentPosition + 1, innerFragmentStack.size))
        }
    }

    override fun onBackPressed() {
        if (!isBloquedBackPress) {
            if (currentPosition > 0) {
                innerFragmentStack[currentPosition].remove(supportFragmentManager)
                indexPosSubject.onNext(--currentPosition)
            } else {
                if (!innerFragmentStack.isNullOrEmpty()) {
                    innerFragmentStack.first().remove(supportFragmentManager)
                    innerFragmentStack.clear()
                }
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisp.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BARCODE_SCAN_CODE) {
                if (data != null) {
                    val result = data.getStringExtra("result")
                    onBarcodeActionListener?.onBarcodeCaptured(result)
                }
            }
        }

    }
}