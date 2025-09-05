package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.engine

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.domain.MachineInstallAddressObj
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.address.InstalacaoMaquinaChooseAddressFragment
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.address.newAddress.MachineInstallAddressListener
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.contato.InstalacaoMaquinaAdicionalContatoFragment
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.horario.InstalacaoMaquinaAdicionalHorarioFragment
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequest.OpenRequestFragment
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequestMachines.OpenRequestMachinesFragment
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequestResume.OpenRequestResumeFragment
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.ActivityBackActionListener
import br.com.mobicare.cielo.commons.listener.EngineNextActionListener
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.addWithAnimation
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.databinding.TrocaMaquinaEngineActivityBinding
import br.com.mobicare.cielo.suporteTecnico.ui.activity.TechnicalSupportSolutionActivity
import kotlinx.android.synthetic.main.fragment_receba_mais_failure.*
//import kotlinx.android.synthetic.main.troca_maquina_engine_activity.*
import org.jetbrains.anko.startActivityForResult

const val REQUEST_CODE_MACHINE_INSTALL = 8

class TrocaMaquinaEngineActivity : BaseLoggedActivity(), ActivityStepCoordinatorListener,
        ActivityBackActionListener, BaseView {

    private lateinit var binding: TrocaMaquinaEngineActivityBinding

    private var _sequence = 0
    private var _bundles = Bundle()
    private var childListener: EngineNextActionListener? = null
    private var actionIntallAddressListener: MachineInstallAddressListener? = null


    companion object {

        fun create(mActivity: TechnicalSupportSolutionActivity) {
            mActivity.startActivityForResult<TrocaMaquinaEngineActivity>(REQUEST_CODE_MACHINE_INSTALL)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TrocaMaquinaEngineActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.containerToolbar.root, "Abrir Solicitação")

        intent?.extras?.let {
            _bundles.putAll(it)
        }

        setFragment(false, _bundles)

        binding.nextButton.setOnClickListener {
            this.childListener?.onClicked()
        }
    }


    fun setFragment(isBackAnimation: Boolean, bundle: Bundle? = null) {
        showProgressBar(this._sequence)
        when (this._sequence) {
            0 -> {
                val fragment = OpenRequestFragment.create(bundle)
                this.childListener = fragment
                actionIntallAddressListener = null
                fragment.addWithAnimation(supportFragmentManager, R.id.contentLayout, isBackAnimation)
            }
            1 -> {
                val fragment = OpenRequestMachinesFragment.create(bundle)
                this.childListener = fragment
                actionIntallAddressListener = null
                fragment.addWithAnimation(supportFragmentManager, R.id.contentLayout, isBackAnimation)
            }
            2 -> {
                val fragment = InstalacaoMaquinaChooseAddressFragment.create(bundle)
                this.childListener = fragment
                this.actionIntallAddressListener = fragment
                fragment.addWithAnimation(supportFragmentManager, R.id.contentLayout, isBackAnimation)
            }
            3 -> {
                val fragment = InstalacaoMaquinaAdicionalContatoFragment.create(bundle)
                this.childListener = fragment
                this.actionIntallAddressListener = null
                fragment.addWithAnimation(supportFragmentManager, R.id.contentLayout, isBackAnimation)
            }
            4 -> {
                val fragment = InstalacaoMaquinaAdicionalHorarioFragment.create(bundle)
                this.childListener = fragment
                this.actionIntallAddressListener = null
                fragment.addWithAnimation(supportFragmentManager, R.id.contentLayout, isBackAnimation)
            }
            5 -> {
                val fragment = OpenRequestResumeFragment.create(bundle)
                this.childListener = fragment
                this.actionIntallAddressListener = null
                fragment.addWithAnimation(supportFragmentManager, R.id.contentLayout, isBackAnimation)
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
        }
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

    override fun onLogout() {
        SessionExpiredHandler.userSessionExpires(this)
    }

    override fun setTitle(title: String) {
//        if (isAttached()) {
//            txtTitle.text = title
//        }
    }

    override fun showLoading() {
        showProgressBar(0)
        binding.progressLoadingLayout.visibility = View.VISIBLE
        binding.errorLayout.root.visibility = View.GONE
        binding.contentLayout.visibility = View.GONE
        binding.buttonLayout.visibility = View.GONE
    }

    override fun hideLoading() {
        showProgressBar(this._sequence)
        binding.progressLoadingLayout.visibility = View.GONE
        binding.errorLayout.root.visibility = View.GONE
        binding.contentLayout.visibility = View.VISIBLE
        binding.buttonLayout.visibility = View.VISIBLE
    }

    override fun logout(msg: ErrorMessage?) {
        SessionExpiredHandler.userSessionExpires(this)
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
            if (it.httpStatus >= 500 || error.httpStatus == 404) {
                binding.progressLoadingLayout.visibility = View.GONE
                binding.errorLayout.root.visibility = View.VISIBLE
                binding.contentLayout.visibility = View.INVISIBLE
                binding.buttonLayout.visibility = View.INVISIBLE

                buttonUpdate.setOnClickListener {
                    if (isAttached()) {
                        showLoading()
                        this.childListener?.retry()
                    }
                }

            } else {
                showProgressBar(this._sequence)
                binding.progressLoadingLayout.visibility = View.GONE
                binding.errorLayout.root.visibility = View.GONE
                binding.contentLayout.visibility = View.VISIBLE
                binding.buttonLayout.visibility = View.VISIBLE

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

    override fun enableNextButton(isEnabled: Boolean) {
        binding.nextButton.isEnabled = isEnabled
        binding.nextButton.alpha = if (isEnabled) 1.0f else 0.80f
    }

    override fun setButtonName(title: String) {
        if (isAttached()) {
            binding.nextButton.setText(title)
        }
    }

    private fun showProgressBar(show: Int) {
        if (show == 0) {
            binding.progress00.visibility = View.GONE
        } else {
            binding.progress00.visibility = if (show >= 1) View.VISIBLE else View.INVISIBLE
            binding.progress01.visibility = if (show >= 1) View.VISIBLE else View.INVISIBLE
            binding.progress02.visibility = if (show >= 2) View.VISIBLE else View.INVISIBLE
            binding.progress03.visibility = if (show >= 3) View.VISIBLE else View.INVISIBLE
            binding.progress04.visibility = if (show >= 4) View.VISIBLE else View.INVISIBLE
            binding.progress05.visibility = if (show >= 5) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getParcelableExtra<MachineInstallAddressObj>("result")
                actionIntallAddressListener?.onAddressChosen(result)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}