package br.com.mobicare.cielo.recebaMais.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.addInFrame
import br.com.mobicare.cielo.commons.utils.addWithAnimation
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import br.com.mobicare.cielo.recebaMais.domain.Installment
import br.com.mobicare.cielo.recebaMais.domain.Offer
import br.com.mobicare.cielo.recebaMais.presentation.ui.fragment.MyConfirmationFragment
import br.com.mobicare.cielo.recebaMais.presentation.ui.fragment.MyDataFragment
import kotlinx.android.synthetic.main.activity_receba_mais.*


class RecebaMaisActivity : BaseLoggedActivity(), ActivityStepCoordinatorListener {

    private var sequence = 0
    private val MY_DATA_FRAGMENT = 0

    private var userToken: String = ""

    private var installmentSelected: Installment? = null
        get() = intent?.getParcelableExtra(INSTALLMENT_SELECTED)

    private var offer: Offer? = null

    private var onKeyListener: View.OnKeyListener? = null

    companion object {
        const val INSTALLMENT_SELECTED = "br.com.cielo.recebaMaisActivity.installmentSelected"
        const val RECEBA_MAIS_OFFER = "br.com.cielo.recebaMais.userLoanSimulationFragment.offer"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_receba_mais)
        setupToolbar(toolbar_include as Toolbar, getString(R.string.rm_title_message))
        intent?.extras.run {
            userToken = this?.getString("token") ?: ""
            offer = this?.getParcelable("offer")
        }

        setFragmentSequence()
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        if (fragment is MyDataFragment) {
            onKeyListener = fragment
        }
    }

    override fun onBackPressed() {
        if (sequence <= MY_DATA_FRAGMENT) {
            finish()
        } else {
            sequence--
            setFragment(true)
        }
    }

    private fun  setFragmentSequence() {
        MyDataFragment.create(this, userToken, installmentSelected)
            .addInFrame(supportFragmentManager, R.id.frameFormContentInput)
    }

    fun setFragment(isBackAnimation: Boolean) {
        when (sequence) {
            MY_DATA_FRAGMENT -> {
                MyDataFragment.create(this, userToken, installmentSelected)
                    .addWithAnimation(
                        supportFragmentManager,
                        R.id.frameFormContentInput,
                        isBackAnimation
                    )
            }
            1 -> {
                offer?.id?.let { id ->
                    MyConfirmationFragment.create(this, userToken, installmentSelected, id)
                        .addWithAnimation(
                            supportFragmentManager,
                            R.id.frameFormContentInput,
                            isBackAnimation
                        )
                }
            }
        }
    }

    //region RecebaMaisActionListener
    override fun onNextStep(isFinish: Boolean) {
        if (isFinish) {
            LocalBroadcastManager.getInstance(this)
                .sendBroadcast(Intent(
                    MainBottomNavigationActivity
                        .NAVIGATE_TO_ACTION
                ).apply {
                    this.putExtra(
                        MainBottomNavigationActivity.HOME_INDEX_KEY,
                        MainBottomNavigationActivity.HOME_INDEX
                    )
                })

            LocalBroadcastManager.getInstance(this)
                .sendBroadcast(Intent(CLOSE_ACTIVITIES_FROM_BACKSTACK))
        } else {
            sequence++
            setFragment(false)
        }
    }

    override fun onLogout() {
        SessionExpiredHandler.userSessionExpires(this, true)
    }

    override fun setTitle(title: String) {
        setupToolbar(toolbar_include as Toolbar, title)
    }
    //endregion

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        onKeyListener?.onKey(window.decorView, keyCode, event)
        return super.onKeyUp(keyCode, event)
    }
}