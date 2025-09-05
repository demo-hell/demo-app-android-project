package br.com.mobicare.cielo.pagamentoLink.delivery.address

interface CollectAddressPresenter {

    fun onGetAddress(zipCode: String)
    fun validateAddressType(addreesType: String)
    fun getAddressType()
    fun onPause()
}