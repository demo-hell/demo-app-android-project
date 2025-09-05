package br.com.mobicare.cielo.pagamentoLink.delivery.address

import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.meuCadastro.domains.entities.CepAddressResponse

interface CollectAddressView {

    fun callValidate(view: CieloTextInputView, errorString: String?)
    fun onAddressSucess(addressResponse: CepAddressResponse)
    fun onAddressError(error: ErrorMessage)
    fun onAddressNotFound()
    fun setAddressTypeGone(type: String)
    fun setEnableAddressTypeOthers(type: String)
    fun getAddressType(addressTypes: List<String>)
}