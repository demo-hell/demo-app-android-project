package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.contato

import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.phone
import br.com.mobicare.cielo.commons.utils.phoneNumber

class InstalacaoMaquinaAdicionalContatoPresenter(
        private val view: InstalacaoMaquinaAdicionalContatoContract.View
) : InstalacaoMaquinaAdicionalContatoContract.Presenter {

    private var nomeContato: String = ""
    private var numeroTelefone: String = ""

    override fun setData(nome: String, numeroTelefone: String) {
        this.nomeContato = nome
        this.numeroTelefone =  numeroTelefone
        this.view.setPersonData(nome, numeroTelefone.phone())
    }

    override fun onNextButtonClicked(nome: String, numeroTelefone: String) {
        if (nome.isEmpty()) {
            return this.view.onShowNameError("Por favor, preencher o nome!")
        }
        this.view.onShowNameError(null)

        if (numeroTelefone.isEmpty()) {
            return this.view.onShowPhoneNumberError("Por favor, preencher o número do telefone")
        }
        if (!ValidationUtils.isValidPhoneNumber(numeroTelefone)) {
            return this.view.onShowPhoneNumberError("Por favor, digite um telefone válido!")
        }

        this.nomeContato = nome
        this.numeroTelefone = numeroTelefone.phoneNumber()
        this.view.onShowPhoneNumberError(null)
        this.view.goToNextScreen(nome, numeroTelefone.phoneNumber())
    }

}