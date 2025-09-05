//package br.com.mobicare.cielo.meuCadastro.presetantion.ui.fragments
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import br.com.mobicare.cielo.R
//import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
//import br.com.mobicare.cielo.commons.ui.BaseFragment
//import br.com.mobicare.cielo.commons.utils.Utils
//import br.com.mobicare.cielo.commons.utils.createAead
//import br.com.mobicare.cielo.commons.utils.showMessage
//import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroObj
//import br.com.mobicare.cielo.meuCadastro.presetantion.presenter.MeuCadastroPresenter
//import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BodyChangePassword
//import br.com.mobicare.cielo.notification.WelcomeInfoNotificationFragment
//import kotlinx.android.synthetic.main.minha_empresa.*
//
///**
// * Created by Enzo Teles on 11/03/19
// * email: enzo.carvalho.teles@gmail.com
// * Software Developer Sr.
// */
//
//@SuppressLint("ValidFragment")
//class MinhaEmpresaFragment(var meuCadastroObj: MeuCadastroObj?, var presenterCad: MeuCadastroPresenter) : BaseFragment(), MinhaEmpresaContract.View {
//
//    var isCheck = true
//    var account = 0
//    lateinit var body: BodyChangePassword
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
//            inflater.inflate(R.layout.minha_empresa, container, false)
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initView()
//    }
//
//    /**
//     * método para popular do dados do usuário
//     * */
//    private fun initView() {
//
//        val isConvivenciaUser = UserPreferences.getInstance().isConvivenciaUser
//
//        card_second.visibility = if (isConvivenciaUser) View.VISIBLE else View.GONE
//
//        meuCadastroObj?.apply {
//
//            layout_main_view.visibility = View.VISIBLE
//
//            me_tv_cnpj.text = documentNumber
//            me_tv_dt_abertura.text = openingDate
//            me_tv_rm_atividade.text = businessSegment
//            me_tv_proprietario.text = owner
//            me_tv_phone.text = phone
//            tv_numero_estabelecimento.text = ec
//            me_tv_name.text = name
//
//            this.userAddresses?.run userAddresses@ {
//
//                me_tv_address.text = this.first().addressConcatenado
//                me_tv_address_2.text = this.last().addressConcatenado
//
//            }
//
//        } ?:run {
//            layout_main_view.visibility = View.GONE
//        }
//
//        btn_change_pass.setOnClickListener {
//
//            if (Utils.isNetworkAvailable(requireActivity())) {
//
//                verificationButtonChangePassword()
//
//                val current = textInputPasswordActual.text!!.trim().toString()
//                val newPass = textInputNewPassword.text!!.trim().toString()
//                val newPassConf = textInputNewPasswordConfirm.text!!.trim().toString()
//
//
//                //TODO remover repetição de código e verificar essa lógica
//                if (account > 1) {
//
//                    txtPasswordActual.run {
//                        when {
//                            (current.isNullOrEmpty()) -> error = "preencha o campo corretamente"
//                            else -> error = ""
//
//                        }
//                    }
//
//                    txtNewPassword.run {
//                        error = when {
//                            (newPass.isNullOrEmpty()) -> "preencha o campo corretamente"
//                            (newPass != newPassConf) -> "a nova senha tem que ser igual a confirmar nova senha"
//                            else -> ""
//
//                        }
//                    }
//
//                    txtNewPasswordConfirm.run {
//                        when {
//                            (newPassConf.isNullOrEmpty()) -> error = "preencha o campo corretamente"
//                            (newPassConf != newPass) -> error = "a nova senha tem que ser igual a confirmar nova senha"
//                            else -> error = ""
//
//                        }
//                    }
//
//                }
//
//
//                if (!current.isNullOrEmpty() && !newPass.isNullOrEmpty() && !newPassConf.isNullOrEmpty() && newPass.equals(newPassConf)) {
//                    body = BodyChangePassword(current, newPass, newPassConf)
//
//                    updateFingerprint(newPass)
//
//                    presenterCad.getChangePassword(body, this)
//                    showProgress()
//                }
//
//            } else {
//                requireContext().showMessage(getString(R.string.title_error_wifi_subtitle),
//                        title = getString(R.string.title_error_wifi_title))
//            }
//
//        }
//
//
//    }
//
//    private fun updateFingerprint(newPass: String) {
//        var aead = requireActivity()
//                .createAead(WelcomeInfoNotificationFragment.KEY_NAME,
//                        WelcomeInfoNotificationFragment.MASTER_KEY_URI)
//        var passwordEncrypted = aead.encrypt(newPass.toByteArray(), null)
//
//        UserPreferences.getInstance().saveFingerprintData(passwordEncrypted)
//        UserPreferences.getInstance().saveFingerprintRecorded(true)
//    }
//
//
//    /**
//     * método para mostrar as opção de mudar senha ou não
//     * */
//    private fun verificationButtonChangePassword() {
//        if (isCheck) {
//            layout_options.visibility = View.VISIBLE
//            //isCheck = false
//            btn_change_pass.setText(getString(R.string.me_button_open))
//            account += 1
//        } else {
//            layout_options.visibility = View.GONE
//            isCheck = true
//            btn_change_pass.setText(getString(R.string.me_button_close))
//            account += 1
//        }
//    }
//
//    fun hideProgress(){
//        btn_change_pass.visibility = View.VISIBLE
//        frameProgress.visibility = View.GONE
//    }
//
//    fun showProgress(){
//        btn_change_pass.visibility = View.GONE
//        frameProgress.visibility = View.VISIBLE
//    }
//
//    override fun onError() {
//        requireContext().showMessage(getString(R.string.text_unavaiable_server_message),
//                title = getString(R.string.text_title_server_generic_error))
//        hideProgress()
//
//    }
//
//    override fun onSucess() {
//        textInputPasswordActual.text!!.clear()
//        textInputNewPassword.text!!.clear()
//        textInputNewPasswordConfirm.text!!.clear()
//
//        requireContext().showMessage(getString(R.string.text_change_password_message),
//                title = getString(R.string.text_change_password_message_title))
//        hideProgress()
//        isCheck = false
//        verificationButtonChangePassword()
//    }
//
//    override fun onErrorAuthentication() {
//
//        requireContext().showMessage(getString(R.string.text_title_server_generic_error_400),
//                title = getString(R.string.text_title_server_generic_error))
//        hideProgress()
//    }
//
//    override fun onChancePasswordError(error: String) {
//
//    }
//
//    override fun onChancePasswordSuccess() {
//
//    }
//
//    override fun logout() {
//    }
//
//}
//
//
