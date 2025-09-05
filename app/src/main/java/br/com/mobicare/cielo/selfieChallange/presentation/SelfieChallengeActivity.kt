package br.com.mobicare.cielo.selfieChallange.presentation

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.DatadogEvent
import br.com.mobicare.cielo.commons.constants.LgpdLinks
import br.com.mobicare.cielo.commons.constants.SIXTY_DOUBLE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.helpers.getStoneAgeEnvironment
import br.com.mobicare.cielo.commons.helpers.getStoneAgeTheme
import br.com.mobicare.cielo.commons.helpers.getUnicoConfig
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.databinding.ActivitySelfieChallengeBinding
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.showApplicationConfiguration
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.selfieChallange.constants.SelfieChallengeConstants.PARAMETERS
import br.com.mobicare.cielo.selfieChallange.constants.SelfieChallengeConstants.SELFIE_CHALLENGE_ERROR
import br.com.mobicare.cielo.selfieChallange.constants.SelfieChallengeConstants.SELFIE_CHALLENGE_PARAMS
import br.com.mobicare.cielo.selfieChallange.constants.SelfieChallengeConstants.SELFIE_CHALLENGE_SUCCESS
import br.com.mobicare.cielo.selfieChallange.utils.SelfieBottomSheet
import br.com.mobicare.cielo.selfieChallange.utils.SelfieCameraSDK
import br.com.mobicare.cielo.selfieChallange.utils.SelfieChallengeError
import br.com.mobicare.cielo.selfieChallange.utils.SelfieChallengeParams
import br.com.mobicare.cielo.selfieChallange.utils.SelfieChallengeResult
import br.com.mobicare.cielo.selfieChallange.utils.SelfieChallengeUiState
import br.com.mobicare.cielo.selfieChallange.utils.SelfieErrorEnum
import br.com.stoneage.identify.models.LiveSelfieParameters
import br.com.stoneage.identify.sdk.LiveSelfieActivity
import br.com.stoneage.identify.sdk.STAUserSession
import com.acesso.acessobio_android.AcessoBioListener
import com.acesso.acessobio_android.iAcessoBioSelfie
import com.acesso.acessobio_android.onboarding.AcessoBio
import com.acesso.acessobio_android.onboarding.camera.CameraListener
import com.acesso.acessobio_android.onboarding.camera.UnicoCheckCamera
import com.acesso.acessobio_android.onboarding.camera.UnicoCheckCameraOpener
import com.acesso.acessobio_android.services.dto.ErrorBio
import com.acesso.acessobio_android.services.dto.ResultCamera
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelfieChallengeActivity : BaseActivity() {

    private val viewModel: SelfieChallengeViewModel by viewModel()
    private lateinit var binding: ActivitySelfieChallengeBinding
    private lateinit var cameraListener: iAcessoBioSelfie
    private lateinit var unicoCheckCamera: UnicoCheckCamera
    private lateinit var stoneAgeSelfieListener: ActivityResultLauncher<Intent>
    private lateinit var datadogEvent: DatadogEvent
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                datadogEvent.LoggerInfo(
                    message = "Permissão da câmera concedida",
                    key = "CameraPermissionGranted",
                    value = "Permissão concedida pelo usuário"
                )
                startCamera()
            } else {
                datadogEvent.LoggerInfo(
                    message = "Permissão da câmera negada",
                    key = "CameraPermissionDenied",
                    value = "Permissão negada pelo usuário"
                )
                showExplainPermissionBS()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelfieChallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.saveSelfieChallengeParams(intent?.extras?.getSerializable(SELFIE_CHALLENGE_PARAMS) as SelfieChallengeParams)
        this.datadogEvent  = DatadogEvent(this.applicationContext, UserPreferences.getInstance())
        datadogEvent.LoggerInfo(
            message = "inicialização da SelfieChallenge",
            key = "postDelayed",
            value = "this.webview.postDelayed"
        )
        setupText()
        setupListeners()
        setupObservers()
        setupUnicoCamera()
        setupStoneAgeListener()
    }

    private fun setupText() {
        binding.apply {
            tvSelfieChallengePrivacy.fromHtml(R.string.selfie_challenge_privacy)
        }
    }

    private fun setupListeners() {
        binding.apply {
            btBackArrow.setOnClickListener {
                callbackError(SelfieChallengeError(SelfieErrorEnum.CAMERA_CLOSED_MANUALLY))
            }

            btNext.setOnClickListener {
                showLoading(true)
                checkCameraPermission()
            }

            tvSelfieChallengePrivacy.setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(LgpdLinks.CieloPolicy)
                    )
                )
            }
        }
    }

    private fun setupObservers() {
        viewModel.selfieChallengeLiveData.observe(this) { uiState ->
            when (uiState) {
                is SelfieChallengeUiState.StoneAgeError -> {
                    showLoading(false)
                    showCustomBottomSheet(
                        image = R.drawable.img_selfie_correta,
                        title = getString(R.string.selfie_challenge_error_title),
                        message = getString(uiState.message),
                        nameBtnConfirm = getString(R.string.text_try_again_label)
                    )
                }

                is SelfieChallengeUiState.StoneAgeTokenSuccess -> {
                    STAUserSession.initialize(
                        stage = getStoneAgeEnvironment(),
                        sdkToken = uiState.token,
                        owner = ContextWrapper(this),
                        theme = getStoneAgeTheme(this)
                    )
                    val intent = Intent(this, LiveSelfieActivity::class.java).apply {
                        putExtra(PARAMETERS, LiveSelfieParameters(ZERO))
                    }
                    stoneAgeSelfieListener.launch(intent)
                }

                is SelfieChallengeUiState.SelfieChallengeSuccess -> callbackSuccess(uiState.selfieChallengeResult)
                is SelfieChallengeUiState.GenericError -> callbackError(SelfieChallengeError(SelfieErrorEnum.SDK_SELFIE_CHALLENGE_GENERIC_ERROR))
                is SelfieChallengeUiState.UserCancelled -> callbackError(SelfieChallengeError(SelfieErrorEnum.CAMERA_CLOSED_MANUALLY))
                is SelfieChallengeUiState.SelfieError -> callbackError(SelfieChallengeError(SelfieErrorEnum.SEND_SELFIE_ERROR, errorCode = uiState.errorCode))
            }
        }
    }

    private fun setupUnicoCamera() {
        datadogEvent.LoggerInfo(
            message = "Pedido de permissão da câmera",
            key = "CameraPermissionRequest",
            value = "Solicitando permissão da câmera"
        )
        // Log antes de configurar a câmera Unico
        datadogEvent.LoggerInfo(
            message = "Configurando câmera Unico",
            key = "UnicoCameraSetupStart",
            value = "Iniciando"
        )
        val callback = object : AcessoBioListener {
            override fun onErrorAcessoBio(errorBio: ErrorBio?) {
                datadogEvent.LoggerInfo(
                    message = "Erro AcessoBio: ${errorBio?.description}",
                    key = "AcessoBioError",
                    value = "Erro"
                )
                callbackError(SelfieChallengeError(SelfieErrorEnum.SDK_SELFIE_CHALLENGE_GENERIC_ERROR, errorBio?.description))
            }

            override fun onUserClosedCameraManually() {
                datadogEvent.LoggerInfo(
                    message = "Usuário fechou a câmera manualmente",
                    key = "UserClosedCameraManually",
                    value = "Manual"
                )
                callbackError(SelfieChallengeError(SelfieErrorEnum.CAMERA_CLOSED_MANUALLY))
            }

            override fun onSystemClosedCameraTimeoutSession() {
                datadogEvent.LoggerInfo(
                    message = "Sessão de câmera fechada por timeout do sistema",
                    key = "SystemClosedCameraTimeout",
                    value = "Timeout"
                )
                callbackError(SelfieChallengeError(SelfieErrorEnum.CAMERA_CLOSED_MANUALLY))
            }

            override fun onSystemChangedTypeCameraTimeoutFaceInference() {
                datadogEvent.LoggerInfo(
                    message = "Mudança de tipo de câmera por timeout de inferência facial",
                    key = "CameraTypeChangeTimeoutFaceInference",
                    value = "Timeout"
                )
                callbackError(SelfieChallengeError(SelfieErrorEnum.CAMERA_CLOSED_MANUALLY))
            }
        }

        unicoCheckCamera = AcessoBio(this, callback)
            .setAutoCapture(false)
            .setSmartFrame(false)
            .setTimeoutSession(SIXTY_DOUBLE)
            .build()

        // Log após a configuração da câmera Unico
        datadogEvent.LoggerInfo(
            message = "Câmera Unico configurada",
            key = "UnicoCameraSetupComplete",
            value = "Concluído"
        )

        cameraListener = object : iAcessoBioSelfie {
            override fun onSuccessSelfie(result: ResultCamera?) {
                datadogEvent.LoggerInfo(
                    message = "Sucesso na captura de selfie",
                    key = "SuccessSelfieCapture",
                    value = "Sucesso"
                )
                showLoading(true)
                viewModel.processUnicoSuccessData(result)
            }

            override fun onErrorSelfie(errorBio: ErrorBio?) {
                datadogEvent.LoggerInfo(
                    message = "Erro na captura de selfie: ${errorBio?.description}",
                    key = "ErrorSelfieCapture",
                    value = "Erro"
                )
                showLoading(false)
                callbackError(SelfieChallengeError(SelfieErrorEnum.SDK_SELFIE_CHALLENGE_GENERIC_ERROR, errorBio?.description))
            }
        }
    }
    override fun onPause() {
        super.onPause()
        datadogEvent.LoggerInfo(
            message = "SelfieChallengeActivity pausada - App pode estar indo para o segundo plano",
            key = "AppBackground",
            value = "SelfieChallengeActivity onPause"
        )
    }

    override fun onResume() {
        super.onResume()
        datadogEvent.LoggerInfo(
            message = "SelfieChallengeActivity retomada - App voltou ao primeiro plano",
            key = "AppForeground",
            value = "SelfieChallengeActivity onResume"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        datadogEvent.LoggerInfo(
            message = "SelfieChallengeActivity destruída - App pode estar sendo fechado",
            key = "AppClose",
            value = "OnboardingWebActivity onDestroy"
        )
    }
    private fun checkCameraPermission() {
        datadogEvent.LoggerInfo(
            message = "Pedido de permissão da câmera",
            key = "CameraPermissionRequest",
            value = "Solicitando permissão da câmera"
        )
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showExplainPermissionBS()
            }

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        if (viewModel.getSelfieChallengeParams().isForeign || viewModel.getSelfieChallengeParams().cameraSDK == SelfieCameraSDK.STONEAGE) {
            openStoneAgeSelfie()
        } else {
            openUnicoSelfie()
        }
    }

    private fun openStoneAgeSelfie() {
        datadogEvent.LoggerInfo(
            message = "Iniciando câmera StoneAge",
            key = "CameraStart",
            value = "StoneAge"
        )
        viewModel.getStoneAgeToken()
    }

    private fun openUnicoSelfie() {
        datadogEvent.LoggerInfo(
            message = "Iniciando câmera Unico",
            key = "CameraStart",
            value = "Unico"
        )

        unicoCheckCamera.prepareCamera(getUnicoConfig(), object : CameraListener {
            override fun onCameraReady(cameraOpener: UnicoCheckCameraOpener.Camera) {
                datadogEvent.LoggerInfo(
                    message = "Iniciando câmera Unico onCameraReady",
                    key = "onCameraReady",
                    value = "Unico"
                )
                cameraOpener.open(cameraListener)
                showLoading(false)
            }

            override fun onCameraFailed(message: String?) {
                datadogEvent.LoggerInfo(
                    message = "Iniciando câmera Unico Error",
                    key = "onCameraFailed",
                    value = "Unico"
                )
                callbackError(SelfieChallengeError(SelfieErrorEnum.SDK_SELFIE_CHALLENGE_GENERIC_ERROR, message))
                showLoading(false)
            }
        })
    }

    private fun showExplainPermissionBS() {
        showCustomBottomSheet(
            image = R.drawable.ic_content_symbol_alert_orange_72_dp,
            title = getString(R.string.selfie_challenge_permission_title),
            message = getString(R.string.selfie_challenge_permission_message),
            nameBtnCancel = getString(R.string.selfie_challenge_permission_not_enable),
            btnCancelCallback = {
                callbackError(SelfieChallengeError(SelfieErrorEnum.USER_DENIED_CAMERA_PERMISSION))
                false
            },
            nameBtnConfirm = getString(R.string.selfie_challenge_permission_enable),
            btnConfirmCallback = {
                showApplicationConfiguration()
                showLoading(false)
                false
            },
            closeCallback = {
                callbackError(SelfieChallengeError(SelfieErrorEnum.USER_DENIED_CAMERA_PERMISSION))
            }
        )
    }

    private fun setupStoneAgeListener() {
        stoneAgeSelfieListener = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    viewModel.processStoneAgeSuccessData(result)
                }

                else -> {
                    viewModel.processStoneAgeErrorData(result)
                }
            }
        }
    }

    private fun callbackSuccess(selfieChallengeResult: SelfieChallengeResult) {
        datadogEvent.LoggerInfo(
            message = "Desafio de selfie concluído com sucesso",
            key = "SelfieChallengeSuccess",
            value = "Sucesso"
        )
        Intent().apply {
            putExtra(SELFIE_CHALLENGE_SUCCESS, selfieChallengeResult)
            setResult(RESULT_OK, this)
        }

        finish()
    }

    private fun callbackError(selfieChallengeError: SelfieChallengeError) {
        datadogEvent.LoggerInfo(
            message = "Erro no desafio de selfie",
            key = "SelfieChallengeError",
            value = "Erro: ${selfieChallengeError.message}"
        )
        Intent().apply {
            putExtra( SELFIE_CHALLENGE_ERROR, selfieChallengeError)
            setResult(RESULT_CANCELED, this)
        }

        finish()
    }

    fun showLoading(isShow: Boolean) {
        binding.apply {
            if (isShow) {
                messageProgressView.showLoading()
                containerView.gone()
            } else {
                containerView.visible()
                messageProgressView.hideLoading()
            }
        }
    }

    fun showCustomBottomSheet(
        @DrawableRes image: Int? = null,
        title: String? = null,
        message: String? = null,
        nameBtnConfirm: String? = null,
        nameBtnCancel: String? = null,
        btnConfirmCallback: (() -> Boolean)? = null,
        btnCancelCallback: (() -> Boolean)? = null,
        closeCallback: (() -> Unit)? = null
    ) {
        lifecycleScope.launchWhenResumed {
            SelfieBottomSheet.newInstance(
                image = image ?: R.drawable.ic_generic_error_image,
                title = title ?: getString(R.string.generic_error_title),
                subtitle = message ?: getString(R.string.error_generic),
                nameBtnConfirm = nameBtnConfirm ?: EMPTY,
                nameBtnCancel = nameBtnCancel ?: getString(R.string.ok),
                statusBtnCancel = nameBtnCancel != null ,
                isFullScreen = false,
                isCancelable = true
            ).apply {
                onClickListeners = object : SelfieBottomSheet.OnClickListeners {
                    override fun onBtnConfirm(dialog: Dialog) {
                        if (btnConfirmCallback?.invoke() != true) dismiss()
                    }

                    override fun onBtnCancel(dialog: Dialog) {
                        if (btnCancelCallback?.invoke() != true) dismiss()
                    }

                    override fun onSwipe() {
                        closeCallback?.invoke()
                    }
                }
            }.show(supportFragmentManager, EMPTY)
        }
    }
}