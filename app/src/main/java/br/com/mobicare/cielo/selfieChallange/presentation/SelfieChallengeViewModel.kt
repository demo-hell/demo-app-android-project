package br.com.mobicare.cielo.selfieChallange.presentation

import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.HTTP_UNKNOWN
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.utils.convertBase64
import br.com.mobicare.cielo.selfieChallange.constants.SelfieChallengeConstants
import br.com.mobicare.cielo.selfieChallange.constants.SelfieChallengeConstants.FACEID_1X1_REJECT
import br.com.mobicare.cielo.selfieChallange.domain.usecase.GetStoneAgeTokenUseCase
import br.com.mobicare.cielo.selfieChallange.domain.usecase.PostSelfieChallengeUseCase
import br.com.mobicare.cielo.selfieChallange.utils.SelfieChallengeParams
import br.com.mobicare.cielo.selfieChallange.utils.SelfieChallengeResult
import br.com.mobicare.cielo.selfieChallange.utils.SelfieChallengeUiState
import br.com.stoneage.identify.enums.ErrorStatusEnum
import br.com.stoneage.identify.enums.LiveSelfieValidationError
import br.com.stoneage.identify.models.LiveSelfieResult
import br.com.stoneage.identify.models.LiveSelfieResultError
import com.acesso.acessobio_android.services.dto.ResultCamera
import kotlinx.coroutines.launch

class SelfieChallengeViewModel(
    private val getStoneAgeTokenUseCase: GetStoneAgeTokenUseCase,
    private val postSelfieChallengeUseCase: PostSelfieChallengeUseCase
) : ViewModel() {

    private val _selfieChallengeLiveData = MutableLiveData<SelfieChallengeUiState>()
    val selfieChallengeLiveData: LiveData<SelfieChallengeUiState>
        get() = _selfieChallengeLiveData

    private lateinit var selfieChallengeParams: SelfieChallengeParams

    fun processUnicoSuccessData(result: ResultCamera?) {
        result?.let { resultCamera ->
            postSelfieChallenge(
                resultCamera.base64,
                resultCamera.encrypted,
                selfieChallengeParams.username,
                selfieChallengeParams.operation.id
            )
        } ?: sendGenericError()
    }

    private fun sendGenericError() {
        _selfieChallengeLiveData.value = SelfieChallengeUiState.GenericError()
    }

    private fun postSelfieChallenge(
        base64: String?, encrypted: String?, username: String?, operation: String
    ) {
        viewModelScope.launch {
            postSelfieChallengeUseCase.invoke(
                base64, encrypted, username, operation
            ).onSuccess {
                _selfieChallengeLiveData.value =
                    SelfieChallengeUiState.SelfieChallengeSuccess(
                        SelfieChallengeResult(
                            faceIdToken = it,
                            photo64 = base64
                        )
                    )
            }.onError {
                if (it.apiException.httpStatusCode == HTTP_UNKNOWN) {
                    checkIfSelfieRejected(it)
                } else {
                    _selfieChallengeLiveData.value =
                        SelfieChallengeUiState.SelfieError(it.apiException.newErrorMessage.flagErrorCode)
                }
            }.onEmpty {
                _selfieChallengeLiveData.value = SelfieChallengeUiState.SelfieError()
            }
        }
    }

    private fun checkIfSelfieRejected(apiError: CieloDataResult.APIError) {
        val errorCode = apiError.apiException.newErrorMessage.flagErrorCode
        if (errorCode == FACEID_1X1_REJECT){
            _selfieChallengeLiveData.value =
                SelfieChallengeUiState.SelfieError(errorCode)
        } else {
            processCaptureError(LiveSelfieValidationError.UNKNOWN_ERROR)
        }
    }

    fun getStoneAgeToken() {
        viewModelScope.launch {
            getStoneAgeTokenUseCase.invoke().onSuccess {
                _selfieChallengeLiveData.value = SelfieChallengeUiState.StoneAgeTokenSuccess(it)
            }.onError {
                sendGenericError()
            }.onEmpty {
                sendGenericError()
            }
        }
    }

    fun processStoneAgeSuccessData(result: ActivityResult) {
        result.data?.let { data ->
            getStoneAgeSuccessData(data)?.let {
                if (it.selfieFile?.exists() == true) {
                    postSelfieChallenge(
                        it.selfieFile?.convertBase64(),
                        it.selfieRequestId,
                        selfieChallengeParams.username,
                        selfieChallengeParams.operation.id
                    )
                } else {
                    sendGenericError()
                }
            } ?: sendGenericError()
        } ?: sendGenericError()
    }

    fun processStoneAgeErrorData(result: ActivityResult) {
        result.data?.let { data ->
            getStoneAgeErrorData(data)?.let {
                if (it.errorMessage != null && !it.validationErrors.isNullOrEmpty()) {
                    processCaptureError(it.validationErrors?.first())
                } else if (it.errorStatus == ErrorStatusEnum.USER_CANCELLED) {
                    _selfieChallengeLiveData.value = SelfieChallengeUiState.UserCancelled()
                } else {
                    _selfieChallengeLiveData.value =
                        SelfieChallengeUiState.GenericError(it.errorMessage)
                }
            } ?: sendGenericError()
        } ?: sendGenericError()
    }

    private fun getStoneAgeSuccessData(data: Intent): LiveSelfieResult? {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            data.getParcelableExtra(
                SelfieChallengeConstants.STONEAGE_SUCCESS_RESULT, LiveSelfieResult::class.java
            )
        } else {
            data.getParcelableExtra(SelfieChallengeConstants.STONEAGE_SUCCESS_RESULT)
        }
    }

    private fun getStoneAgeErrorData(data: Intent): LiveSelfieResultError? {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            data.getParcelableExtra(
                SelfieChallengeConstants.STONEAGE_ERROR_RESULT, LiveSelfieResultError::class.java
            )
        } else {
            data.getParcelableExtra(SelfieChallengeConstants.STONEAGE_ERROR_RESULT)
        }
    }

    private fun processCaptureError(error: LiveSelfieValidationError?) {
        val message = when (error) {
            LiveSelfieValidationError.IMAGE_NOT_GOOD_ENOUGH -> {
                R.string.selfie_challenge_image_not_good_message
            }

            LiveSelfieValidationError.NO_FACE_DETECTED -> {
                R.string.selfie_challenge_no_face_detected_message
            }

            LiveSelfieValidationError.TOO_MANY_FACES -> {
                R.string.selfie_challenge_too_many_faces_message
            }

            LiveSelfieValidationError.WEARING_HAT -> {
                R.string.selfie_challenge_wearing_hat_message
            }

            LiveSelfieValidationError.WEARING_GLASSES -> {
                R.string.selfie_challenge_wearing_glasses_message
            }

            LiveSelfieValidationError.WEARING_READING_GLASSES -> {
                R.string.selfie_wearing_glasses_message
            }

            LiveSelfieValidationError.WEARING_MASK -> {
                R.string.selfie_challenge_wearing_mask_message
            }

            LiveSelfieValidationError.FACE_NOT_CENTERED -> {
                R.string.selfie_challenge_face_not_centered_message
            }

            LiveSelfieValidationError.TILTED_FACE -> {
                R.string.selfie_face_not_centered_message
            }

            LiveSelfieValidationError.FACE_TOO_FAR -> {
                R.string.selfie_challenge_face_too_far_message
            }

            LiveSelfieValidationError.FACE_IS_SMILING -> {
                R.string.selfie_challenge_face_is_smiling_message
            }

            LiveSelfieValidationError.FACE_TOO_CLOSE -> {
                R.string.selfie_challenge_face_too_close_message
            }

            LiveSelfieValidationError.FACE_TOO_BRIGHT -> {
                R.string.selfie_challenge_face_too_bright_message
            }

            LiveSelfieValidationError.FACE_TOO_DARK -> {
                R.string.selfie_challenge_face_too_dark_message
            }

            else -> {
                R.string.selfie_challenge_unknown_error_message
            }
        }

        _selfieChallengeLiveData.value = SelfieChallengeUiState.StoneAgeError(message)
    }

    fun saveSelfieChallengeParams(selfieChallengeParams: SelfieChallengeParams) {
        this.selfieChallengeParams = selfieChallengeParams
    }

    fun getSelfieChallengeParams(): SelfieChallengeParams {
        return this.selfieChallengeParams
    }

}