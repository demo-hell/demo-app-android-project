package br.com.mobicare.cielo.meuCadastroDomicilio.presetation.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.ActivityBackActionListener
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.FlagTransferActionListener
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.FlagTransferClickListener
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.FlagTransferQuantityListener
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.addAccount.AddAccount01Fragment
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.addAccount.transferAccount.AddAccount02Fragment
import kotlinx.android.synthetic.main.add_account_engine_activity.*
import kotlinx.android.synthetic.main.toolbar_dialog.*

class AddAccountEngineActivity : BaseLoggedActivity(), ActivityStepCoordinatorListener,
        FlagTransferClickListener, FlagTransferQuantityListener, ActivityBackActionListener, BaseView {


    companion object {
        const val IS_USER_MFA_WHITELIST = "br.com.cielo.meuCadastroDomicilio.isUserMfaWhitelist"
    }

    private var _sequence = 0
    private var _bundles = Bundle()

    private lateinit var flagActionListener: FlagTransferActionListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFlagSecure(window)
        setContentView(R.layout.add_account_engine_activity)

        intent?.extras?.let {
            _bundles.putAll(it)
        }
        setFragment(false)


        btnLeft.setOnClickListener {
            onBackPressed()
        }

        btnRight.setOnClickListener {
            finish()
        }

        btn_ac_cancel.setOnClickListener {
            onBackPressed()
        }

        btn_ac_salve.setOnClickListener {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, MEUS_CADASTRO),
                action = listOf(MEUS_CADASTRO_CONTAS_ADICIONAR, Action.CLIQUE),
                label = listOf(Label.BOTAO, Action.SALVAR)
            )
            flagActionListener.validade()
        }
    }


    override fun showButtonHome() {
        //implement
    }

    override fun hideButtonHome() {
        //implement
    }

    override fun onButtonSelected(isCheck: Boolean) {
        btn_ac_salve.isEnabled = isCheck
        btn_ac_salve.alpha = if (isCheck) 1f else .5f
    }

    override fun onButtonStatus() {
        btn_ac_salve.isEnabled = false
        btn_ac_salve.alpha = .5f
    }

    override fun onButtonName(name: String) {
        btn_ac_salve.setText(name)

    }

    override fun hideTopBar() {
        //implement
    }

    override fun showTopBar() {
        //implement
    }

    fun backScreenError(){
        include_error.visibility = View.GONE
        content_main.visibility = View.VISIBLE
        hideLoading()
    }

    override fun onBackPressed() {
        backScreenError()
        if (_sequence <= 0) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        } else {
            _sequence--
            setFragment(true)
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
                setFragment(false)
            }
    }


    override fun onLogout() {
        SessionExpiredHandler.userSessionExpires(this, true)
    }

    override fun setTitle(title: String) {
        if (isAttached()) {
            txtTitle.text = title
        }
    }
    //endregion


    fun setFragment(isBackAnimation: Boolean) {
        when (_sequence) {
            0 -> {
                quantitychosen(0)
                val fragment = AddAccount01Fragment.newInstance(this).apply {
                    this.listener = this@AddAccountEngineActivity
                }
                flagActionListener = fragment
                fragment.addWithAnimation(supportFragmentManager, R.id.frameFormContentInput, isBackAnimation)
            }
            1 -> {
                val fragment = AddAccount02Fragment.newInstance(this, this, _bundles, this)
                flagActionListener = fragment
                fragment.addWithAnimation(supportFragmentManager, R.id.frameFormContentInput, isBackAnimation)

            }
        }
    }

    //region FlagTransferQuatityListener
    override fun quantitychosen(quantity: Int) {
        if (_sequence == 0) {
            btn_ac_salve.text = "Avançar"
            text_view_quantity_flags.visibility = View.GONE
        } else {
            btn_ac_salve.text = "Concluir"
            text_view_quantity_flags.visibility = View.VISIBLE
            val result: String = if (quantity > 1)
                "$quantity bandeiras selecionadas"
            else
                "$quantity bandeira selecionada"

            text_view_quantity_flags.text = result
        }

    }
    //endregion

    //region ActivityBackActionListener
    override fun onBack() {
        super.onBack()
        onBackPressed()
    }
    //endregion

    override fun showLoading() {
        progress_loading.visibility = View.VISIBLE
        nestedScrollView.visibility = View.GONE
    }

    override fun hideLoading() {
        progress_loading.visibility = View.GONE
        content_main.visibility = View.VISIBLE
        nestedScrollView.visibility = View.VISIBLE
    }

    override fun showError(error: ErrorMessage?) {
        include_error.visibility = View.GONE
        content_main.visibility = View.VISIBLE
        showMessage(message = "Infelizmente não conseguimos completar sua requisição.")
    }

    override fun logout(msg: ErrorMessage?) {
        if (isAttached()) {
            SessionExpiredHandler.userSessionExpires(this)
        }
    }

    override fun lockScreen() {
        onButtonSelected(false)
    }

    override fun unlockScreen() {
        onButtonSelected(true)
    }

    override fun showBottomBar() {
        options_buttons.visibility = View.VISIBLE
    }

    override fun hideBottomBar() {
        options_buttons.visibility = View.GONE
    }


}