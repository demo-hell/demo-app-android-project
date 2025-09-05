package br.com.mobicare.cielo.pagamentoLink.delivery.address

import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.pagamentoLink.delivery.address.repository.AddressTypeMapper
import br.com.mobicare.cielo.pagamentoLink.delivery.address.repository.CollectAddressInteractor
import br.com.mobicare.cielo.pagamentoLink.delivery.address.repository.CollectAddressInteractorImpl
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef.ADDRESS_TYPES
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class CollectAddressPresenterImpl(val view: CollectAddressView) : CollectAddressPresenter {

    private val configurationPreference = ConfigurationPreference.instance

    private var disposible = CompositeDisposable()

    private val interactor: CollectAddressInteractor = CollectAddressInteractorImpl()

    override fun onGetAddress(zipCode: String) {
        interactor.getAddressByZipcode(zipCode)
                .configureIoAndMainThread()
                .subscribe({
                    view.onAddressSucess(it)
                }, {
                    view.onAddressNotFound()
                    //if (it is IndexOutOfBoundsException) view.onAddressNotFound()
                    //Desconsiderar qualquer erro para que a cliente posso digitar o endereço
                })
                .addTo(disposible)
    }

    override fun validateAddressType(addreesType: String) {
//        if (addreesType.equals("Outros")) view.setEnableAddressTypeOthers(addreesType)
//        else view.setAddressTypeGone(addreesType)
        view.setAddressTypeGone(addreesType)
    }

    override fun getAddressType() {
        var types = configurationPreference.getConfigurationValue(ADDRESS_TYPES, defaultAddressType)

        view.getAddressType(AddressTypeMapper.mapper(types))
    }

    override fun onPause() {
        disposible.dispose()
    }

    companion object{
        private const val defaultAddressType = "RUA;AVENIDA;ALAMEDA;PRACA;RODOVIA;ESTRADA;TRAVESSA;OUTROS"
    }
}