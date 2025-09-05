package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.owner

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.meuCadastroNovo.MeuCadastroNovoRepository
import br.com.mobicare.cielo.meuCadastroNovo.domain.Owner
import br.com.mobicare.cielo.meuCadastroNovo.domain.Phone
import retrofit2.Response

class EditarDadosProprietarioPresenter(
    private val repository: MeuCadastroNovoRepository,
    private val view: EditarDadosProprietarioContract.View,
    private val userPreferences: UserPreferences
) : EditarDadosProprietarioContract.Presenter {

    private lateinit var owner: Owner

    override fun setView(view: EditarDadosProprietarioContract.View) {}

    override fun putOwnerData(owner: Owner) {
        this.owner = owner
        this.view.showOwnerData(owner)
    }

    private fun checkEmail(email: String) : Boolean {
        if (email.isEmpty()) {
            this.view.showEmailFillError(true)
            return false
        }
        this.view.showEmailFillError(false)
        if (email.isNotEmpty()) {
            if (!ValidationUtils.isEmail(email)) {
                return false
            }
        }
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
        if (!checkTelefone(telefone1) || !checkTelefone(telefone2) || !checkTelefone(telefone3) )
            return false

        if (telefone1.isNullOrEmpty() && telefone2.isNullOrEmpty() && telefone3.isNullOrEmpty()) {
            this.view.showPhoneFillError(true)
            return false
        }

        this.view.showPhoneFillError(false)

        return true
    }

    fun gerarTelefones(telefone1: String, telefone2: String, telefone3: String) : List<Phone> {
        var telefones = ArrayList<Phone>()
        val numeros = arrayListOf(telefone1, telefone2, telefone3)

        for(idx in 0 until numeros.size) {
            val number = ValidationUtils.justNumbers(numeros[idx])
            if (!number.isNullOrEmpty()) {
                val areaCode = number.substring(0, 2)
                val phoneNumber = number.substring(2)
                var type = "CELLPHONE"
                if (idx < this.owner.phones.size) {
                    val phone = this.owner.phones[idx]
                    if (phone.areaCode == areaCode && phone.number == phoneNumber) {
                        telefones.add(phone)
                        continue
                    }

                     phone.type?.let {
                         type = it
                    }
                }
                telefones.add(Phone(areaCode, phoneNumber, type))
            }
        }
        return telefones
    }

    override fun save(
        otpCode: String,
        email: String,
        telefone1: String,
        telefone2: String,
        telefone3: String
    ) {
        if (!checkTelefones(telefone1, telefone2, telefone3))
            return

        if (!checkEmail(email))
            return

        this.view.showLoading()
        this.owner.email = email
        this.owner.phones = gerarTelefones(telefone1, telefone2, telefone3)
        userPreferences.token?.let { itToken ->
            this.repository.putOwner(
                itToken,
                otpCode,
                this.owner,
                object : APICallbackDefault<Response<Void>, String> {
                    override fun onError(error: ErrorMessage) {
                        view.hideLoading()
                        if (error.logout) {
                            view.logout(error)
                        } else {
                            view.showError(error)
                        }
                    }

                    override fun onSuccess(response: Response<Void>) {
                        view.showSaveSuccessful()
                    }
                })
        }
    }

    override fun getUserName(): String = userPreferences.userName

}