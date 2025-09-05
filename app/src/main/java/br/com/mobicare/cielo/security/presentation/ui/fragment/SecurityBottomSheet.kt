package br.com.mobicare.cielo.security.presentation.ui.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import br.com.allowme.android.contextual.AllowMeContextual
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.cielo.libflue.util.extensions.visible
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.commons.constants.FOUR
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.USER_INPUT_EC_NUMBER
import br.com.mobicare.cielo.commons.constants.USER_INPUT_INTERNAL
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.databinding.BottomSheetSecurityBinding
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.newLogin.NewLoginState
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.security.presentation.presenter.BottomSheetPresenter
import br.com.mobicare.cielo.security.presentation.ui.BottomSheetSecurityContract
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class SecurityBottomSheet : BottomSheetDialogFragment(), BottomSheetSecurityContract.View,
    AllowMeContract.View, DialogInterface.OnCancelListener {

    lateinit var frameProgress: FrameLayout
    lateinit var bottomSheet: FrameLayout

    private val userPreferences: UserPreferences = UserPreferences.getInstance()
    private var userInputType: Int = USER_INPUT_EC_NUMBER

    var onDismissListener: OnDismissListener? = null
    var onCancelListener: DialogInterface.OnCancelListener? = null

    private val presenter: BottomSheetPresenter by inject {
        parametersOf(this)
    }
    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }
    private val useSecurityHash: Boolean? by lazy {
        FeatureTogglePreference.instance.getFeatureTogle(
            FeatureTogglePreference.SECURITY_HASH
        )
    }
    private lateinit var mAllowMeContextual: AllowMeContextual

    private var binding: BottomSheetSecurityBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BottomSheetSecurityBinding.inflate(inflater, container, false)
        .also {
            binding = it
        }.root


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            ) as? FrameLayout

            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = ZERO

                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState >= FOUR) {
                            onCancelListener?.onCancel(dialog)
                            dismiss()
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }
                })
            }
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val react = Rect()
        view.getWindowVisibleDisplayFrame(react)

        val height = view.context.resources.displayMetrics.heightPixels
        val diff = height - react.bottom
        val heightMin = height / TWO
        view.minimumHeight = heightMin

        if (diff != ZERO) {
            if (view.paddingBottom != diff)
                view.setPadding(ZERO, ZERO, ZERO, diff)
        } else {
            if (view.paddingBottom != ZERO)
                view.setPadding(ZERO, ZERO, ZERO, ZERO)
        }

        initView()
        mAllowMeContextual = allowMePresenter.init(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        isCancelable = true
    }

    override fun initView() {
        presenter.validatePassword(
            if (userPreferences.userInputTypeUserLogged != USER_INPUT_EC_NUMBER &&
                userPreferences.userInputTypeUserLogged != USER_INPUT_INTERNAL
            )
                userPreferences.userName.trim()
            else
                userPreferences.numeroEC
        )
        binding?.apply {
            textInputEditLoginPasswordSecurity.requestFocus()
            changeLoginButtonState(false)

            textInputEditLoginPasswordSecurity.setOnTextChangeListener(object :
                CieloTextInputView.TextChangeListener {
                var lastUserInputValue: String? = EMPTY
                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s.toString() != lastUserInputValue) {
                        lastUserInputValue = s.toString()
                        presenter.onPasswordChanged(s?.toString())
                    }
                }
            })

            buttonNewLoginUserEnter.setOnClickListener {
                if (Utils.isNetworkAvailable(requireActivity())) {
                    useSecurityHash?.let { useSecurityHash ->
                        if (useSecurityHash)
                            allowMePresenter.collect(
                                mAllowMeContextual = mAllowMeContextual,
                                requireActivity(),
                                mandatory = false
                            )
                        else performLogin(null)
                    }
                } else
                    requireContext().showMessage(
                        getString(R.string.title_error_wifi_subtitle),
                        title = getString(R.string.title_error_wifi_title)
                    )
            }
        }
    }

    private fun performLogin(fingerprint: String?) {
        requireActivity().hideSoftKeyboard()
        val userLogin = userPreferences.numeroEC
        val userName = userPreferences.userName.trim()
        val userPassword = binding?.textInputEditLoginPasswordSecurity?.getText()?.trim()
        userInputType = userPreferences.userInputTypeUserLogged

        presenter.onLoginButtonClicked(
            identification = userLogin,
            username = userName,
            password = userPassword,
            isKeepData = true,
            fingerprint = fingerprint
        )
    }

    override fun render(state: NewLoginState) {
        binding?.apply {
            buttonNewLoginUserEnter.visible(state.isLoading.not())
            frameProgressButtonEnterSecurity.visible(state.isLoading)

            if (state.isNotBooting) {
                onDismissListener?.onNotBooting()
                dismiss()
            } else {
                state.messageError?.let { itMessageError ->
                    showAlert(itMessageError)
                }
                if (state.isToBeCleanPassword)
                    textInputEditLoginPasswordSecurity.setText(EMPTY)

                state.passwordInputType?.let { itUserType ->
                    textInputEditLoginPasswordSecurity.setInputType(itUserType)
                }

                textInputEditLoginPasswordSecurity.setMaxLength(state.passwordMaxLength ?: ONE_NEGATIVE)
            }
        }
    }

    private fun showAlert(message: String) {
        CieloAlertDialogFragment
            .Builder()
            .title(getString(R.string.dialog_title))
            .message(message)
            .closeTextButton(getString(R.string.dialog_button))
            .build().showAllowingStateLoss(
                requireActivity().supportFragmentManager,
                getString(R.string.text_cieloalertdialog)
            )
    }

    override fun changeLoginButtonState(isEnabled: Boolean) {
        binding?.buttonNewLoginUserEnter?.isEnabled = isEnabled
    }

    override fun successAuth(password: ByteArray?) {
        onDismissListener?.onDismiss()
        dismiss()
    }

    override fun successCollectToken(result: String) {
        performLogin(result)
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        if (mandatory)
            showAlert(errorMessage)
        else {
            FirebaseCrashlytics.getInstance().log(errorMessage)
            performLogin(result)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        allowMePresenter.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            requireActivity() as AppCompatActivity
        )
    }

    override fun isAttached(): Boolean {
        return isAdded && activity != null && view != null
    }

    interface OnDismissListener {
        fun onDismiss()
        fun onNotBooting()
    }

    override fun onCancel(dialog: DialogInterface) {
        onCancelListener?.onCancel(dialog)
    }

    override fun getSupportFragmentManagerInstance(): FragmentManager {
        return requireActivity().supportFragmentManager
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}