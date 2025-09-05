package br.com.mobicare.cielo.meuCadastroNovo.presetantion.userDataChange

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.constants.COMMA
import br.com.mobicare.cielo.commons.constants.HTTP_UNKNOWN
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.Text.COLON
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.meuCadastroNovo.constants.UserDataConstants.INVALID_DATA
import br.com.mobicare.cielo.meuCadastroNovo.constants.UserDataConstants.NEW_EMAIL
import br.com.mobicare.cielo.meuCadastroNovo.constants.UserDataConstants.NEW_PHONE
import br.com.mobicare.cielo.meuCadastroNovo.constants.UserDataConstants.PASSWORD
import br.com.mobicare.cielo.meuCadastroNovo.domain.usecase.PostUserValidateDataUseCase
import br.com.mobicare.cielo.meuCadastroNovo.domain.usecase.PutUserUpdateDataUseCase
import br.com.mobicare.cielo.meuCadastroNovo.utils.UserDataChangeUiState
import kotlinx.coroutines.launch

class UserDataChangeViewModel(
    private val postUserValidateDataUseCase: PostUserValidateDataUseCase,
    private val putUserUpdateDataUseCase: PutUserUpdateDataUseCase,
    private val getUserObjUseCase: GetUserObjUseCase
) : ViewModel() {

    private val _userDataChangeLiveData = MutableLiveData<UserDataChangeUiState>()
    val userDataChangeLiveData: LiveData<UserDataChangeUiState>
        get() = _userDataChangeLiveData

    fun postUserValidateData(
        email: String?,
        password: String?,
        passwordConfirmation: String?,
        cellphone: String?
    ) {
        viewModelScope.launch {
            postUserValidateDataUseCase.invoke(
                email, password, passwordConfirmation, cellphone
            ).onSuccess {
                _userDataChangeLiveData.value = UserDataChangeUiState.UserValidateSuccess(it)
            }.onError {
                it.apiException.apply {
                    if (httpStatusCode == HTTP_UNKNOWN && newErrorMessage.flagErrorCode == INVALID_DATA) {
                        val messageErrors = newErrorMessage.message.split(COMMA)
                        messageErrors.forEach { errorMessage ->
                            if (errorMessage.contains(NEW_EMAIL)) {
                                _userDataChangeLiveData.value =
                                    UserDataChangeUiState.UserValidateEmailError(
                                        getErrorMessage(errorMessage)
                                    )
                            }

                            if (errorMessage.contains(PASSWORD)) {
                                _userDataChangeLiveData.value =
                                    UserDataChangeUiState.UserValidatePasswordError(
                                        getErrorMessage(errorMessage)
                                    )

                            }

                            if (errorMessage.contains(NEW_PHONE)) {
                                _userDataChangeLiveData.value =
                                    UserDataChangeUiState.UserValidatePhoneError(
                                        getErrorMessage(errorMessage)
                                    )
                            }
                        }

                    } else {
                        newErrorHandler(
                            getUserObjUseCase = getUserObjUseCase,
                            newErrorMessage = newErrorMessage,
                            onErrorAction = {
                                _userDataChangeLiveData.value =
                                    UserDataChangeUiState.GenericError(null)
                            }
                        )
                    }
                }
            }.onEmpty {
                _userDataChangeLiveData.value = UserDataChangeUiState.GenericError(null)
            }
        }
    }

    private fun getErrorMessage(errorMessage: String): String {
        return errorMessage.split(COLON)[ONE]
    }

    fun putUserUpdateData(
        email: String?,
        password: String?,
        passwordConfirmation: String?,
        cellphone: String?,
        faceIdToken: String
    ) {
        viewModelScope.launch {
            putUserUpdateDataUseCase.invoke(
                email, password, passwordConfirmation, cellphone, faceIdToken
            ).onSuccess {
                _userDataChangeLiveData.value = UserDataChangeUiState.UserUpdateSuccess(it)
            }.onError {
                it.apiException.apply {
                    newErrorHandler(
                        getUserObjUseCase = getUserObjUseCase,
                        newErrorMessage = newErrorMessage,
                        onErrorAction = {
                            _userDataChangeLiveData.value = UserDataChangeUiState.UserUpdateError(null)
                        }
                    )
                }
            }.onEmpty {
                _userDataChangeLiveData.value = UserDataChangeUiState.GenericError(null)
            }
        }
    }

}