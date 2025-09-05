package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.contato

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.meuCadastroNovo.MeuCadastroNovoRepository
import br.com.mobicare.cielo.meuCadastroNovo.domain.Contact
import br.com.mobicare.cielo.meuCadastroNovo.domain.PhoneContato
import retrofit2.Response

class EditarDadosContatoPresenter(
    private val repository: MeuCadastroNovoRepository,
    private val view: EditarDadosContatoContract.View,
    private val userPreferences: UserPreferences
) : EditarDadosContatoContract.Presenter {

    private lateinit var contact: Contact

    override fun setView(view: EditarDadosContatoContract.View) {}

    override fun putContactData(contact: Contact) {
        this.contact = contact
        this.view.showContactData(contact)
    }

    private fun checkEmail(email: String): Boolean {
        if (email.isEmpty()) {
            this.view.showEmailFillError(true)
            return false
        }
        this.view.showEmailFillError(false)
        if (!ValidationUtils.isEmail(email)) {
            this.view.showInvalidEmail(true)
            return false
        }
        this.view.showInvalidEmail(false)
        return true
    }

    private fun checkNome(nome: String) : Boolean {
        if (nome.isNullOrEmpty()) {
            this.view.showNameFillError(true)
            return false
        }
        this.view.showNameFillError(false)
        return true
    }
    private fun checkTelefone(number: String) : Boolean {
        if (number.isNotEmpty()) {
            if (!ValidationUtils.isValidPhoneNumber(number))
                return false
        }
        return true
    }

    private fun checkTelefones(telefone1: String, telefone2: String, telefone3: String) : Boolean {
        val phoneNumbers = listOf(telefone1, telefone2, telefone3)

        for(idx in 0 until this.contact.phones.size) {
            val number = phoneNumbers[idx]
            if (number.isNullOrEmpty()) {
                this.view.showPhoneFillError(idx, R.string.text_contact_phone_empty)
                return false
            }
            else if (!checkTelefone(number)) {
                this.view.showPhoneFillError(idx, R.string.text_contact_phone_is_not_valid)
                return false
            }
            this.view.showPhoneFillError(idx)
        }

        return true
    }

    fun gerarTelefones(telefone1: String, telefone2: String, telefone3: String) : List<PhoneContato> {
        var telefones = ArrayList<PhoneContato>()
        val numeros = arrayListOf(telefone1, telefone2, telefone3)

        for(idx in 0 until this.contact.phones.size) {
            val phone = this.contact.phones[idx]
            val number = ValidationUtils.justNumbers(numeros[idx])
            if (!number.isNullOrEmpty()) {
                val areaCode = number.substring(0, 2)
                val phoneNumber = number.substring(2, number.length)
                var type = phone.type
                if (phone.areaCode != areaCode || phone.number != phoneNumber) {
                    type = "CELLPHONE"
                }
                telefones.add(PhoneContato(phone.id, areaCode, phoneNumber, type))
            }
        }

        return telefones
    }

    override fun save(
        nome: String,
        email: String,
        telefone1: String,
        telefone2: String,
        telefone3: String,
        otpCode: String
    ) {
        if (!checkNome(nome))
            return

        if (!checkEmail(email))
            return

        if (!checkTelefones(telefone1, telefone2, telefone3))
            return

        this.view.showLoading()

        this.contact.name = nome
        this.contact.email = email
        this.contact.phones = gerarTelefones(telefone1, telefone2, telefone3)
        userPreferences.token?.let { itToken ->
            this.repository.putContact(
                itToken,
                otpCode,
                this.contact,
                object : APICallbackDefault<Response<Void>, String> {
                    override fun onError(error: ErrorMessage) {
                        this@EditarDadosContatoPresenter.view.hideLoading()
                        if (error.logout) {
                            this@EditarDadosContatoPresenter.view.logout(error)
                        } else {
                            this@EditarDadosContatoPresenter.view.showError(error)
                        }
                    }

                    override fun onSuccess(response: Response<Void>) {
                        this@EditarDadosContatoPresenter.view.showSaveSuccessful()
                    }
                })
        }

    }

    override fun getUserName(): String = userPreferences.userName

}