package br.com.mobicare.cielo.meusCartoes.presentation.ui.activity

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.FragmentDetector
import br.com.mobicare.cielo.commons.utils.addWithAnimation
import br.com.mobicare.cielo.commons.utils.remove
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BankTransferRequest
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.TransferResponse
import br.com.mobicare.cielo.meusCartoes.domains.entities.Card
import br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.BankAccountToTransferInput2Fragment
import br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.BankAccountToTransferInput3Fragment
import br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.BankAccountToTransferInput4Fragment
import br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.BankAccountToTransferInputFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_transfer_electronic_direct.*

class DirectElectronicTransferActivity : BaseLoggedActivity() {

    val innerFragmentStack = mutableListOf<androidx.fragment.app.Fragment>()
    var indexPosSubject: Subject<Int> = PublishSubject.create()
    var currentPosition = 0

    private var isBloquedBackPress = false

    var userCreditCard: Card? = null
        get() = intent?.getParcelableExtra(USER_CREDIT_CARD)

    private val bankAccountToTransferFrag = BankAccountToTransferInputFragment.create()
            .apply {

                finishActionListener = object : BankAccountToTransferInputFragment.OnInputFinishActionListener {
                    override fun onBloquedBackPressed(isBloqued: Boolean) {
                        this@DirectElectronicTransferActivity.isBloquedBackPress = isBloqued
                    }

                    override fun onFinish(bankTransferRequest: BankTransferRequest,
                                          transferResponse: TransferResponse?) {
                        nextStep(bankTransferRequest, transferResponse)
                    }
                }
            }

    companion object {
        const val TRANFER_DIRECT_KEY = "br.com.cielo.directTransferObject"
        const val USER_CREDIT_CARD = "br.com.cielo.userCreditCards"
        const val TRANSFER_RESPONSE_KEY = "br.com.cielo.transferResponse"

        const val TRANSFER_STEP = 2
        const val TRANSFER_DETAIL_STEP = 3

    }

    private var compositeDisp = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer_electronic_direct)

        if (compositeDisp.isDisposed) {
            compositeDisp = CompositeDisposable()
        }

        configureTranferFragments()

        val transferElectronicToolbar = toolbarTransferProgress as Toolbar?

        updateToolbar(transferElectronicToolbar)

        innerFragmentStack[currentPosition].addWithAnimation(supportFragmentManager,
                R.id.frameFormContentInput)

        compositeDisp.add(indexPosSubject.subscribe({ currentIndex ->

            innerFragmentStack[currentIndex].addWithAnimation(supportFragmentManager,
                    R.id.frameFormContentInput, innerFragmentStack.map {
                it::class.java.simpleName
            }.indexOf(FragmentDetector.fragmentStack.last()) > currentIndex)

            updateToolbar(transferElectronicToolbar)
        }, { error ->
            FirebaseCrashlytics.getInstance().recordException(error)
        }))
    }

    private fun configureTranferFragments() {
        innerFragmentStack.add(bankAccountToTransferFrag)
        innerFragmentStack.add(BankAccountToTransferInput2Fragment.create().apply {

            this.onFinishedListener = object : BankAccountToTransferInputFragment.OnInputFinishActionListener {

                override fun onBloquedBackPressed(isBloqued: Boolean) {
                    this@DirectElectronicTransferActivity.isBloquedBackPress = isBloqued
                }

                override fun onFinish(bankTransferRequest: BankTransferRequest,
                                      transferResponse: TransferResponse?) {
                    nextStep(bankTransferRequest, transferResponse)
                }

            }

        })

        innerFragmentStack.add(BankAccountToTransferInput3Fragment.create().apply {

            val bundleParam = Bundle()

            bundleParam.putParcelable(USER_CREDIT_CARD,
                    userCreditCard)
            arguments = bundleParam

            this.onInputFinishListener = object : BankAccountToTransferInputFragment.OnInputFinishActionListener {

                override fun onBloquedBackPressed(isBloqued: Boolean) {
                    this@DirectElectronicTransferActivity.isBloquedBackPress = isBloqued
                }

                override fun onFinish(bankTransferRequest: BankTransferRequest,
                                      transferResponse: TransferResponse?) {
                    nextStep(bankTransferRequest, transferResponse)
                }

            }
        })

        innerFragmentStack.add(BankAccountToTransferInput4Fragment.create())
    }


    private fun nextStep(bankTransferRequest: BankTransferRequest,
                         transferResponse: TransferResponse?) {
        currentPosition++
        val bundleParams = Bundle()
        bundleParams.putParcelable(TRANFER_DIRECT_KEY, bankTransferRequest)

        when (currentPosition) {
            TRANSFER_STEP -> bundleParams
                    .putParcelable(DirectElectronicTransferActivity.USER_CREDIT_CARD,
                            userCreditCard)
            TRANSFER_DETAIL_STEP -> {
                bundleParams
                        .putParcelable(DirectElectronicTransferActivity.USER_CREDIT_CARD,
                                userCreditCard)
                bundleParams
                        .putParcelable(DirectElectronicTransferActivity.TRANSFER_RESPONSE_KEY,
                                transferResponse)
            }
        }

        innerFragmentStack[currentPosition].arguments = bundleParams
        indexPosSubject.onNext(currentPosition)
    }

    private fun updateToolbar(transferElectronicToolbar: Toolbar?) {
        transferElectronicToolbar?.run {
            setupToolbar(transferElectronicToolbar, getString(R.string.text_transfer_title,
                    currentPosition + 1, innerFragmentStack.size))
        }
    }

    override fun onBackPressed() {
        if (!isBloquedBackPress) {
            if (currentPosition > 0) {
                innerFragmentStack[currentPosition].remove(supportFragmentManager)
                indexPosSubject.onNext(--currentPosition)
            } else {
                innerFragmentStack.clear()
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisp.dispose()
    }
}