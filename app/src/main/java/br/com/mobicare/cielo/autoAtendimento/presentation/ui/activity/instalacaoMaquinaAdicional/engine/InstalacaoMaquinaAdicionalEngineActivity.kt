package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.engine

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.domain.MachineInstallAddressObj
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.address.InstalacaoMaquinaChooseAddressFragment
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.address.newAddress.MachineInstallAddressListener
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.contato.InstalacaoMaquinaAdicionalContatoFragment
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.horario.InstalacaoMaquinaAdicionalHorarioFragment
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.quantidade.InstalacaoMaquinaAdicionalQuantidadeFragment
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.resumoEfetivacao.InstalacaoMaquinaAdicionalResumoEfetivacaoFragment
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.analytics.Action.VOLTAR
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.ActivityBackActionListener
import br.com.mobicare.cielo.commons.listener.EngineNextActionListener
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.addWithAnimation
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.databinding.InstalacaoMaquinaAdicionalEngineActivityBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.invisible
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.extensions.visibleOrInvisible
import br.com.mobicare.cielo.machine.domain.MachineItemOfferResponse
import org.jetbrains.anko.startActivityForResult

const val REQUEST_CODE_MACHINE_INSTALL = 8

class InstalacaoMaquinaAdicionalEngineActivity : BaseLoggedActivity(),
        ActivityStepCoordinatorListener,
        ActivityBackActionListener, BaseView {

    private var binding: InstalacaoMaquinaAdicionalEngineActivityBinding? = null

    private var _sequence = 0
    private var _bundles = Bundle()
    private var childListener: EngineNextActionListener? = null
    private var actionInstallAddressListener: MachineInstallAddressListener? = null

    private var machineItemOfferResponse: MachineItemOfferResponse? = null

    companion object {
        fun create(machine: MachineItemOfferResponse, mActivity: FragmentActivity) {
            mActivity.startActivityForResult<InstalacaoMaquinaAdicionalEngineActivity>(
                REQUEST_CODE_MACHINE_INSTALL,
                ARG_PARAM_MACHINE_ITEM to machine
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = InstalacaoMaquinaAdicionalEngineActivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.toolbar?.root?.let {
            setupToolbar(
                it,
                getString(R.string.install_machine_add_title_toolbar)
            )
        }

        intent?.extras?.let {
            _bundles.putAll(it)
            machineItemOfferResponse = it.getParcelable(ARG_PARAM_MACHINE_ITEM)
        }

        setFragment(false, _bundles)
        setupListeners()
    }

    private fun setupListeners() {
        binding?.nextButton?.setOnClickListener {
            childListener?.onClicked()
        }
    }

    fun setFragment(isBackAnimation: Boolean, bundle: Bundle? = null) {
        showProgressBar(this._sequence)
        when (this._sequence) {
            0 -> {
                val fragment = InstalacaoMaquinaAdicionalQuantidadeFragment.create(bundle)
                this.childListener = fragment
                this.actionInstallAddressListener = null
                fragment.addWithAnimation(
                        supportFragmentManager,
                        R.id.contentLayout,
                        isBackAnimation
                )
            }
            1 -> {
                val fragment = InstalacaoMaquinaChooseAddressFragment.create(bundle)
                this.childListener = fragment
                this.actionInstallAddressListener = fragment
                fragment.addWithAnimation(
                        supportFragmentManager,
                        R.id.contentLayout,
                        isBackAnimation
                )
            }
            2 -> {
                val fragment = InstalacaoMaquinaAdicionalContatoFragment.create(bundle)
                this.childListener = fragment
                this.actionInstallAddressListener = null
                fragment.addWithAnimation(
                        supportFragmentManager,
                        R.id.contentLayout,
                        isBackAnimation
                )
            }
            3 -> {
                val fragment = InstalacaoMaquinaAdicionalHorarioFragment.create(bundle)
                this.childListener = fragment
                this.actionInstallAddressListener = null
                fragment.addWithAnimation(
                        supportFragmentManager,
                        R.id.contentLayout,
                        isBackAnimation
                )
            }
            4 -> {
                val fragment = InstalacaoMaquinaAdicionalResumoEfetivacaoFragment.create(bundle)
                this.childListener = fragment
                this.actionInstallAddressListener = null
                fragment.addWithAnimation(
                        supportFragmentManager,
                        R.id.contentLayout,
                        isBackAnimation
                )
            }
        }
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (_sequence <= 0) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        } else {
            _sequence--
            setFragment(true, _bundles)
            gaBackPress(_sequence)
        }
    }

    private fun gaBackPress(_sequence: Int) {
        val passo = "passo %d"

        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MAQUININHA),
            action = listOf(Action.SOLICITAR_MAQUININHA, machineItemOfferResponse?.title.toString()),
            label = listOf(Label.BOTAO, String.format(passo, _sequence + 1), VOLTAR)
        )
    }

    override fun onNextStep(isFinish: Boolean, bundle: Bundle?) {
        if (isAttached())
            if (isFinish) {
                this.close()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getParcelableExtra<MachineInstallAddressObj>("result")
                actionInstallAddressListener?.onAddressChosen(result)
            }
        }
    }

    override fun onLogout() {
        SessionExpiredHandler.userSessionExpires(this, true)
    }

    override fun setTitle(title: String) {
        if (isAttached()) {
            this.supportActionBar?.title = title
        }
    }

    override fun showLoading() {
        showProgressBar(0)

        binding?.apply {
            progressLoadingLayout.visible()
            errorLayout.root.gone()
            contentLayout.gone()
            buttonLayout.gone()
        }
    }

    override fun hideLoading() {
        showProgressBar(this._sequence)

        binding?.apply {
            progressLoadingLayout.gone()
            errorLayout.root.gone()
            contentLayout.visible()
            buttonLayout.visible()
        }
    }

    override fun logout(msg: ErrorMessage?) {
        SessionExpiredHandler.userSessionExpires(this, true)
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {

            if (it.httpStatus >= 500) {
                binding?.apply {
                    progressLoadingLayout.gone()
                    errorLayout.root.visible()
                    contentLayout.invisible()
                    buttonLayout.invisible()

                    errorLayout.buttonUpdate.setOnClickListener {
                        if (isAttached()) {
                            showLoading()
                            childListener?.retry()
                        }
                    }
                }

            } else {
                showProgressBar(this._sequence)
                binding?.apply {
                    progressLoadingLayout.gone()
                    errorLayout.root.gone()
                    contentLayout.visible()
                    buttonLayout.visible()
                }
                showMessage(it.message, it.title) {
                    setBtnRight(getString(R.string.ok))
                }
            }
        }
    }

    private fun close() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onHideLoading() {
        this.hideLoading()
    }

    override fun onShowLoading() {
        this.showLoading()
    }

    override fun onShowError(error: ErrorMessage) {
        this.showError(error)
    }

    override fun onTextChangeButton(text: String) {
        binding?.nextButton?.setText(text)
    }

    override fun enableNextButton(isEnabled: Boolean) {
        binding?.nextButton?.apply {
            this.isEnabled = isEnabled
            alpha = if (isEnabled) 1.0f else 0.80f
        }
    }


    private fun showProgressBar(show: Int) {
        if (show == 0) {
            binding?.progress00?.gone()
        } else {
            binding?.apply {
                progress00.visibleOrInvisible(show >= ONE)
                progress01.visibleOrInvisible(show >= ONE)
                progress02.visibleOrInvisible(show >= TWO)
                progress03.visibleOrInvisible(show >= THREE)
                progress04.visibleOrInvisible(show >= FOUR)
            }
        }
    }

}