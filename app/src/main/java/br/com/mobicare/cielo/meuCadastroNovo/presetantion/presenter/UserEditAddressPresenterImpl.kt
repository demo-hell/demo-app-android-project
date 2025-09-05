package br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.meuCadastroNovo.MeuCadastroNovoRepository
import br.com.mobicare.cielo.meuCadastroNovo.domain.AddressUpdateRequest
import io.reactivex.Scheduler
import java.net.HttpURLConnection.HTTP_NO_CONTENT
import java.net.HttpURLConnection.HTTP_OK

class UserEditAddressPresenterImpl(
    private val userEditAddressView: UserAddressContract.UserEditAddressView,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val meuCadastroRepository: MeuCadastroNovoRepository,
    private val userPreferences: UserPreferences,
) :
    UserAddressContract.UserEditAddressPresenter {

    private val disposableHandler = CompositeDisposableHandler()

    override fun onResume() {
        disposableHandler.start()
    }

    override fun onDestroy() {
        disposableHandler.destroy()
    }

    override fun getUserName(): String = userPreferences.userName

    override fun updateAddress(
        otpCode: String,
        addressUpdateRequest: AddressUpdateRequest
    ) {
        val token: String = userPreferences.token

        if (token.isEmpty()) {
            userEditAddressView.logout(ErrorMessage())
            return
        }
            disposableHandler.compositeDisposable.add(meuCadastroRepository
                .updateUserAddress(token, otpCode, addressUpdateRequest)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { userEditAddressView.showLoading() }
                .doFinally {
                    userEditAddressView.hideLoading()
                }
                .subscribe({
                    if (it.code() in HTTP_OK..HTTP_NO_CONTENT)
                        userEditAddressView.showSuccessAddress()
                    else
                        userEditAddressView.genericError()
                }, { error ->
                    userEditAddressView.addressUpdateError(APIUtils.convertToErro(error))
                })
            )
    }

    override fun fetchAddressByCep(cep: String) {

        val token: String = userPreferences.token

        if (token.isEmpty()) {
            userEditAddressView.logout(ErrorMessage())
            return
        }
        disposableHandler.compositeDisposable.add(meuCadastroRepository
            .getAddressByCep(token, cep)
            .subscribeOn(ioScheduler)
            .observeOn(uiScheduler)
            .doOnSubscribe { userEditAddressView.showLoading() }
            .subscribe({ addressReturn ->
                userEditAddressView.clearAddressFields()
                userEditAddressView.hideLoading()
                userEditAddressView.fillAddressFields(addressReturn)
            }, { error ->
                val apiError = APIUtils.convertToErro(error)
                userEditAddressView.hideLoading()

                if (apiError.logout) {
                    userEditAddressView.logout(apiError)
                } else {
                    userEditAddressView.showCepError(apiError)
                }
            })
        )
    }
}