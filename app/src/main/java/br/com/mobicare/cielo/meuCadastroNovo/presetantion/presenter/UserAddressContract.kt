package br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.presentation.CommonPresenter
import br.com.mobicare.cielo.meuCadastroNovo.domain.AddressUpdateRequest
import br.com.mobicare.cielo.turboRegistration.data.model.response.AddressResponse

interface UserAddressContract {

    interface UserEditAddressView : BaseView {

        fun showSuccessAddress()
        fun addressUpdateError(updateAddressError: ErrorMessage)
        fun fillAddressFields(addressReturn: AddressResponse)
        fun showCepError(cepFetchError: ErrorMessage)
        fun clearAddressFields()
        fun genericError()

    }

    interface UserEditAddressPresenter : CommonPresenter {

        fun fetchAddressByCep(cep: String)
        fun getUserName(): String
        fun updateAddress(
            otpCode: String,
            addressUpdateRequest: AddressUpdateRequest
        )

    }

}