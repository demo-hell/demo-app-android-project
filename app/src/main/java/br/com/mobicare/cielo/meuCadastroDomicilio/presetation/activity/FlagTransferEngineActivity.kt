package br.com.mobicare.cielo.meuCadastroDomicilio.presetation.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.ActivityBackActionListener
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.addWithAnimation
import br.com.mobicare.cielo.commons.utils.enableFlagSecure
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.FlagTransferActionListener
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.FlagTransferClickListener
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.FlagTransferQuantityListener
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.FlagTransfer01Fragment
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.FlagTransfer02Fragment
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.FlagTransfer03Fragment
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroGA4.ScreenView.SCREEN_VIEW_MY_PROFILE_ACCOUNT_FLAG_TRANSFER_BANK_SELECT
import kotlinx.android.synthetic.main.activity_receba_mais.toolbar_include
import kotlinx.android.synthetic.main.flag_transfer_engine_activity.*
import kotlinx.android.synthetic.main.fragment_receba_mais_failure.*
import kotlinx.android.synthetic.main.toolbar_dialog.*
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroGA4 as ga4

class FlagTransferEngineActivity : BaseLoggedActivity(), ActivityStepCoordinatorListener,
    FlagTransferClickListener, FlagTransferQuantityListener, ActivityBackActionListener, BaseView {

    private var _sequence = 1
    private var _bundles = Bundle()

    private lateinit var flagActionListener: FlagTransferActionListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFlagSecure(window)
        setContentView(R.layout.flag_transfer_engine_activity)
        buttonLoginNext.isEnabled = false

        intent?.extras?.let {
            _bundles.putAll(it)
        }
        setFragment(false, _bundles)


        btnLeft.setOnClickListener {
            onBackPressed()
        }

        btnRight.setOnClickListener {
            finish()
        }


        buttonLoginNext.setOnClickListener {
            flagActionListener.validade()
        }
    }

    override fun hideButtonHome() {
        btnLeft.visibility = View.GONE
    }

    override fun showButtonHome() {
        btnLeft.visibility = View.VISIBLE
    }

    override fun onButtonSelected(isCheck: Boolean) {
        buttonLoginNext.isEnabled = isCheck
    }

    override fun onButtonStatus() {
        buttonLoginNext.isEnabled = false
    }

    override fun hideTopBar() {
        toolbar_include?.visibility = View.INVISIBLE
        progress_01.visibility = View.INVISIBLE
        progress_02.visibility = View.INVISIBLE
        progress_03.visibility = View.INVISIBLE
    }

    override fun showTopBar() {
        toolbar_include?.visibility = View.VISIBLE
        progress_01.visibility = View.VISIBLE
        progress_02.visibility = View.VISIBLE
        progress_03.visibility = View.VISIBLE
    }

    fun onLayoutOptionHide() {
        text_view_quantity_flags.visibility = View.GONE
        buttonLoginNext.visibility = View.GONE
    }

    fun onLayoutOptionShow() {
        text_view_quantity_flags.visibility = View.VISIBLE
        buttonLoginNext.visibility = View.VISIBLE
    }

    override fun onButtonName(name: String) {
        buttonLoginNext.setText(name)

    }

    override fun onBackPressed() {
        showTopBar()
        if (_sequence <= 0) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        } else {
            _sequence--
            setFragment(true, _bundles)
        }
    }

    override fun onNextStep(isFinish: Boolean, bundle: Bundle?) {
        if (isAttached())
            if (isFinish) {
                setResult(Activity.RESULT_CANCELED)
                finish()
            } else {
                _sequence++

                bundle?.let { itBundle ->
                    for (key in itBundle.keySet()) {
                        if (_bundles.containsKey(key)) _bundles.remove(key)
                    }
                    _bundles.putAll(itBundle)
                }
                setFragment(false, _bundles)
            }
    }


    override fun onLogout() {
        SessionExpiredHandler.userSessionExpires(this, true)
    }

    override fun setTitle(title: String) {
        if (isAttached()) {
            txtTitle.text = title
            // setupToolbar(toolbar_include as Toolbar, title)
        }
    }
    //endregion


    fun setFragment(isBackAnimation: Boolean, bundle: Bundle? = null) {
        showTopBar()
        when (_sequence) {
            0 -> {
                bundle?.remove("bankSelected")
                showProgress01()
                quantitychosen(0)
                bundle?.let {
                    val fragment = FlagTransfer01Fragment.newInstance(it, this) {}.apply {
                        this.listener = this@FlagTransferEngineActivity
                        this.textTitleBandeiras = "Selecione de que banco deseja transferir"
                    }
                    flagActionListener = fragment
                    fragment.addWithAnimation(
                        supportFragmentManager,
                        R.id.frameFormContentInput,
                        isBackAnimation
                    )
                }
                ga4.logScreenView(SCREEN_VIEW_MY_PROFILE_ACCOUNT_FLAG_TRANSFER_BANK_SELECT)
            }
            1 -> callChooseFlag(isBackAnimation, bundle!!)
            2 -> {
                showProgress03()
                quantitychosen(0)
                bundle?.let {
                    val fragment = FlagTransfer03Fragment.newInstance(it).apply {
                        this.listener03 = this@FlagTransferEngineActivity
                    }
                    flagActionListener = fragment
                    fragment.addWithAnimation(
                        supportFragmentManager,
                        R.id.frameFormContentInput,
                        isBackAnimation
                    )
                }
            }
        }
    }

    private fun callChooseFlag(isBackAnimation: Boolean, bundle: Bundle) {
        showProgress02()
        val fragment = FlagTransfer02Fragment.newInstance(bundle, this, this, this, this)
        flagActionListener = fragment
        fragment.addWithAnimation(
            supportFragmentManager,
            R.id.frameFormContentInput,
            isBackAnimation
        )
    }

    //region FlagTransferQuatityListener
    override fun quantitychosen(quantity: Int) {
        if (quantity == 0) {
            text_view_quantity_flags.visibility = View.GONE
        } else {
            if (quantity == 1)
                text_view_quantity_flags.text = "$quantity bandeira selecionada"
            else
                text_view_quantity_flags.text = "$quantity bandeiras selecionadas"
            text_view_quantity_flags.visibility = View.VISIBLE
            buttonLoginNext.visibility = View.VISIBLE

        }
    }
    //endregion

    //region ActivityBackActionListener
    override fun onBack() {
        super.onBack()
        onBackPressed()
    }
    //endregion


    fun showProgressHide() {
        progress_01.visibility = View.INVISIBLE
        progress_02.visibility = View.INVISIBLE
        progress_03.visibility = View.INVISIBLE
    }

    fun showProgress01() {
        progress_01.visibility = View.VISIBLE
        progress_02.visibility = View.INVISIBLE
        progress_03.visibility = View.INVISIBLE
    }

    fun showProgress02() {
        progress_01.visibility = View.VISIBLE
        progress_02.visibility = View.VISIBLE
        progress_03.visibility = View.INVISIBLE
    }

    fun showProgress03() {
        progress_01.visibility = View.VISIBLE
        progress_02.visibility = View.VISIBLE
        progress_03.visibility = View.VISIBLE
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
            if (it.httpStatus >= 500) {
                include_error.visibility = View.VISIBLE
            } else {
                include_error.visibility = View.GONE
                layout_man_flag.visibility = View.VISIBLE

                buttonUpdate.setOnClickListener {
                    if (isAttached()) {
                        showLoading()
                        include_error.visibility = View.GONE
                        flagActionListener.validade()
                    }
                }

            }
        }
    }

    override fun logout(msg: ErrorMessage?) {
        if (isAttached()) {
            SessionExpiredHandler.userSessionExpires(this)
        }
    }

    override fun showBottomBar() {
    }

    override fun hideBottomBar() {
    }
}