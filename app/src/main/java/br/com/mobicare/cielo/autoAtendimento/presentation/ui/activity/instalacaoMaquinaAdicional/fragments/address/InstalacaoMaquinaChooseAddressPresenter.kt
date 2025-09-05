package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.address

import br.com.mobicare.cielo.autoAtendimento.domain.MachineInstallAddressObj
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.machine.MachineRepository
import br.com.mobicare.cielo.meuCadastroNovo.domain.Address
import br.com.mobicare.cielo.meuCadastroNovo.domain.MCMerchantResponse

class InstalacaoMaquinaChooseAddressPresenter(
        private val mView: InstalacaoMaquinaChooseAddressContract.View,
        private val mRepository: MachineRepository) : InstalacaoMaquinaChooseAddressContract.Presenter {

    private lateinit var mStablishment: MCMerchantResponse
    private var mAddressTypes = mutableListOf<String>()

    override fun onCleared() {
        mRepository.disposable()
    }

    private var mAddressChosen: MachineInstallAddressObj = MachineInstallAddressObj(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "")

    override fun addressChosen(addressChosen: MachineInstallAddressObj) {
        mAddressChosen = addressChosen
    }

    override fun getAddressTypes(): List<String> {
        return mAddressTypes.toList()
    }

    override fun addressChosen(addressChosen: String) {
        mStablishment.addresses.forEach { address ->
            address.addressTypes.forEach { addressType ->
                if (addressType == addressChosen) {
                    setAddressChosen(address)
                    mView.showAddress(address, addressType)
                    return
                }
            }
        }
    }

    override fun loadMerchant() {

        val token: String = UserPreferences.getInstance().token ?: ""

        if (token.isEmpty()) {
            mView.logout(ErrorMessage())
            return
        }

        mRepository.loadMerchant(token, object : APICallbackDefault<MCMerchantResponse, String> {
            override fun onStart() {
                mView.showLoading()
            }

            override fun onError(error: ErrorMessage) {
                if (error.logout) {
                    mView.logout(error)
                } else {
                    mView.showError(error)
                }
            }

            override fun onSuccess(response: MCMerchantResponse) {
                mStablishment = response
                response.addresses.forEach { addresses ->
                    mAddressTypes.addAll(addresses.addressTypes)
                }

                val address = mStablishment.addresses.firstOrNull()  ?: emptyAddress

                setAddressChosen(address)

                mView.showAddress(address, address.addressTypes.firstOrNull() ?: "")
                mView.merchantResponse(mStablishment)
            }
        })
    }

    private fun setAddressChosen(address: Address) {
        mAddressChosen.apply {
            streetAddress = address.streetAddress ?: ""
            address.number?.let {
                numberAddress = it
            }
            address.complementAddress?.let {
                referencePoint = it
            }
            numberAddress = address.number.toString()
            referencePoint = address.complementAddress.toString()
            zipcode = address.zipCode ?: ""
            city = address.city ?: ""
            address.neighborhood?.let {
                neighborhood = it
            }
            neighborhood = address.neighborhood.toString()
            state = address.state ?: ""
        }
    }

    override fun onNextButtonClicked() {
        mView.goToNextScreen(mAddressChosen)
    }

    private companion object {
        val emptyAddress = Address(
            "",
            emptyList(),
            "",
            "",
            "",
            "",
            "",
            "",
            emptyList(),
            ""
        )
    }


}