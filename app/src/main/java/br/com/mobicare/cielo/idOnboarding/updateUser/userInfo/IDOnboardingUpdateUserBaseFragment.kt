package br.com.mobicare.cielo.idOnboarding.updateUser.userInfo

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import br.com.cielo.libflue.screen.HandlerViewBuilderFlui
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.sendEmail
import br.com.mobicare.cielo.commons.utils.showSoftKeyboard
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingUpdateUserDataBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.moveToHome
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP1AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_CPF
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_CPF_PUT_DATA
import br.com.mobicare.cielo.pix.constants.EMPTY
import org.koin.android.ext.android.inject
import java.net.HttpURLConnection

open class IDOnboardingUpdateUserBaseFragment : BaseFragment(),
    CieloNavigationListener, IDOnboardingUpdateUserContract.View {

    private val analyticsGA: IDOnboardingP1AnalyticsGA by inject()
    protected val presenter: IDOnboardingUpdateUserPresenter by inject()
    protected var navigation: CieloNavigation? = null

    override val canEdit: Boolean = true
    override val isEmptyValue: Boolean = false
    override val isReviewEditing: Boolean = false
    override val isAnUpdate: Boolean = false

    protected var isEditing = false
        get() = if (canEdit) (field || isReviewEditing) else false

    private var defaultErrorLabel: CharSequence? = null
    private var errorMessage: ErrorMessage? = null

    private var _binding: FragmentIdOnboardingUpdateUserDataBinding? = null
    val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIdOnboardingUpdateUserDataBinding .inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupDefaultListeners()
    }

    override fun onResume() {
        presenter.view = this
        presenter.onResume()
        super.onResume()
        updateView()

        if (isReviewEditing) {
            activity?.showSoftKeyboard(binding?.etTypedValue)
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    fun setupView(
        title: CharSequence? = EMPTY,
        editingTitle: CharSequence? = null,
        emptyValueTitle: CharSequence? = null,
        subtitle: CharSequence? = EMPTY,
        editingSubtitle: CharSequence? = null,
        emptyValueSubtitle: CharSequence? = null,
        label: CharSequence? = EMPTY,
        editingLabel: CharSequence? = null,
        currentValue: CharSequence? = null,
        errorLabel: CharSequence? = EMPTY,
        textAlertMessage: CharSequence? = null,
        nextButton: CharSequence? = getString(R.string.text_yes_label),
        editingNextButton: CharSequence? = getString(R.string.text_next_label),
    ) {
        binding?.apply{

            if (isValid(etTypedValue?.text?.toString()).not()) {
                etTypedValue?.setText(currentValue ?: EMPTY)
            }

            defaultErrorLabel = errorLabel

            if (isAnUpdate) isEditing = true

            if (canEdit && isEditing) {
                tvTitle.text = (if (isEmptyValue) emptyValueTitle else editingTitle) ?: title
                tvSubtitle.text =
                    (if (isEmptyValue) emptyValueSubtitle else editingSubtitle) ?: subtitle
                tvLabel.visible(editingLabel != null)
                editingLabel?.let { tvLabel.text = it }
                btEditValue.gone()
                btnNext.isEnabled = isValid(etTypedValue.text?.toString())
                btnNext.text = editingNextButton ?: nextButton
                activity?.showSoftKeyboard(etTypedValue)
                etTypedValue.setSelection(etTypedValue.text?.length ?: ZERO)
            } else {
                tvTitle.text = title
                tvSubtitle.text = subtitle
                tvLabel.visible()
                tvLabel.text = label
                etTypedValue.clearFocus()
                etTypedValue.isFocusable = canEdit
                etTypedValue.isFocusableInTouchMode = canEdit
                btEditValue.visible(canEdit)
                tvTypedValueErrorLabel.gone()
                btnNext.isEnabled = true
                btnNext.text = nextButton
                activity?.hideSoftKeyboard()
            }

            btnAction.isEnabled = btnNext.isEnabled
            tvSubtitle.visible(subtitle.isNullOrEmpty().not())
            tvLabel.visible(label.isNullOrEmpty().not())
            tvAlertText.text = textAlertMessage
            alertMessage.visible(textAlertMessage.isNullOrEmpty().not())
        }
    }

    private fun setupDefaultListeners() {
        binding?.apply {
            btBackArrow.setOnClickListener {
                isEditing = false
                activity?.onBackPressed()
            }

            btEditValue.setOnClickListener {
                isEditing = true
                activity?.showSoftKeyboard(etTypedValue)
                updateView()
                etTypedValue.setSelection(etTypedValue.text?.length ?: ZERO)
                editValueOnclick()
            }

            etTypedValue.setOnFocusChangeListener { _, hasFocus ->
                if (canEdit) {
                    if (hasFocus && isEditing.not()) {
                        btEditValue.callOnClick()
                    }
                } else if (hasFocus) {
                    etTypedValue.clearFocus()
                    etTypedValue.requestFocus()
                    root.requestFocusFromTouch()
                }
            }

            etTypedValue.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == IME_ACTION_DONE) {
                    if (btnAction.isVisible.not() == true && btnNext.isEnabled == true) {
                        btnNext.callOnClick()
                    }
                }
                false
            }
        }
        setupListeners()
    }

    fun showTypedValueErrorLabel(errorMsg: String = defaultErrorLabel?.toString().orEmpty()) {
        binding?.tvTypedValueErrorLabel?.text = errorMsg
        binding?.tvTypedValueErrorLabel?.visible(errorMsg.isNotBlank())
    }

    override fun showLoading(@StringRes loadingMessage: Int?, vararg messageArgs: String) {
        doWhenResumed {
            navigation?.showLoading(true, loadingMessage, *messageArgs)
        }
    }

    override fun hideLoading(
        @StringRes successMessage: Int?,
        loadingSuccessCallback: (() -> Unit)?,
        vararg messageArgs: String
    ) {
        doWhenResumed(
            action = {
                navigation?.showContent(true, successMessage, loadingSuccessCallback, *messageArgs)
                    ?: loadingSuccessCallback?.invoke()

                if (isEditing) {
                    activity?.showSoftKeyboard(binding?.etTypedValue)
                    binding?.etTypedValue?.setSelection(binding?.etTypedValue?.text?.length ?: ZERO)
                }

                activity?.hideSoftKeyboard()
            },
            errorCallback = { loadingSuccessCallback?.invoke() }
        )
    }

    override fun showError(error: ErrorMessage?) {
        doWhenResumed {
            errorMessage = error
            binding?.tvTypedValueErrorLabel?.text = error?.message
            binding?.tvTypedValueErrorLabel?.visible(error?.message.orEmpty().isNotBlank())
        }

        if (error?.httpStatus == HttpURLConnection.HTTP_UNAUTHORIZED) {
            showErrorGeneric(error)
        }
    }

    protected fun goToHome() {
        activity?.moveToHome()
            ?: baseLogout()
    }

    protected fun goBackPressed() {
        isEditing = false
        activity?.onBackPressed()
    }

    override fun showErrorGeneric(error: ErrorMessage?) {
        doWhenResumed(
            action = {
                analyticsGA.logIDMaxTriesDisplay(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_CPF, EMPTY)
                navigation?.showCustomBottomSheet(
                    image = R.drawable.ic_generic_error_image,
                    title = getString(R.string.id_onboarding_bs_title_error_generic),
                    message = error?.message
                        ?: getString(R.string.id_onboarding_bs_description_error_generic),
                    bt2Title = getString(R.string.entendi),
                    bt2Callback = {
                        if (error?.httpStatus == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            baseLogout()
                        }
                        false
                    },
                )
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun onRetry() {
        presenter.onResume()
        presenter.retry()
    }

    override fun onBackButtonClicked(): Boolean {
        if (isEditing && isEmptyValue.not() && isReviewEditing.not()) {
            isEditing = false
            updateView()
            return true
        }
        return false
    }

    override fun onPauseActivity() {
        presenter.onPause()
    }

    override fun showErrorInvalidCPF() {
        navigation?.showCustomBottomSheet(
            image = R.drawable.img_aguardando_doc,
            title = getString(R.string.id_onboarding_update_cpf_invalid_cpf_bs_title),
            message = getString(R.string.id_onboarding_update_cpf_invalid_cpf_bs_message),
            bt1Title = getString(R.string.text_call_center_action),
            bt1Callback = {
                sendEmail(
                    requireActivity(),
                    getString(R.string.id_onboarding_cpf_email_address),
                )
                false
            },
            bt2Title = getString(R.string.entendi),
            bt2Callback = {
                baseLogout()
                false
            },
            closeCallback = {
                baseLogout()
            }
        )
    }

    override fun showErrorIrregularCPF() {
        HandlerViewBuilderFlui.Builder(requireContext())
            .isShowButtonContained(true)
            .isShowHeaderImage(true)
            .isShowButtonBack(false)
            .title(getString(R.string.id_onboarding_update_cpf_irregular_title))
            .titleStyle(R.style.TxtTitleDarkBlue)
            .message(
                getString(
                    R.string.id_onboarding_update_cpf_irregular_message,
                    IDOnboardingFlowHandler.userStatus.onboardingStatus?.p1Flow?.cpfValidation?.maxTries.toString()
                )
            )
            .messageStyle(R.style.Paragraph_400_regular_16_display_400)
            .labelContained(getString(R.string.id_onboarding_update_cpf_irregular_cpf_btn_title))
            .containedClickListener(object : HandlerViewBuilderFlui.ContainedOnClickListener {
                override fun onClick(dialog: Dialog?) {
                    dialog?.dismiss()
                    analyticsGA.logIDIrregularCpfClick(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_CPF_PUT_DATA)
                }
            })
            .headerClickListener(object : HandlerViewBuilderFlui.HeaderOnClickListener {
                override fun onClick(dialog: Dialog?) {
                    dialog?.dismiss()

                    if (IDOnboardingFlowHandler.userStatus.onboardingStatus?.
                        p1Flow?.deadlineRemainingDays != ZERO.toLong())
                        goToHome()
                    else
                        baseLogout()
                }
            })
            .build()
            .show(childFragmentManager, null)

        analyticsGA.logIDIrregularCpfDisplay(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_CPF_PUT_DATA, EMPTY)
    }

    override fun showErrorBlockedIrregularCPF() {
        HandlerViewBuilderFlui.Builder(requireContext())
            .isShowButtonContained(false)
            .isShowHeaderImage(true)
            .isShowButtonBack(false)
            .contentImage(R.drawable.img_celular_impedido_azul)
            .title(getString(R.string.id_onboarding_update_blocked_irregular_cpf_title))
            .titleStyle(R.style.TxtTitleDarkBlue)
            .message(
                getString(
                    R.string.id_onboarding_status_access_currently_blocked_message,
                    IDOnboardingFlowHandler.userStatus.onboardingStatus?.p1Flow?.cpfValidation?.maxTries.toString()
                )
            )
            .messageStyle(R.style.Paragraph_400_regular_16_display_400)
            .headerClickListener(object : HandlerViewBuilderFlui.HeaderOnClickListener {
                override fun onClick(dialog: Dialog?) {
                    dialog?.dismiss()

                    if (IDOnboardingFlowHandler.userStatus.onboardingStatus?.
                        p1Flow?.deadlineRemainingDays != ZERO.toLong())
                        goToHome()
                    else
                        baseLogout()
                }
            })
            .build()
            .show(childFragmentManager, null)
    }

    override fun showErrorNameMaxTries() {
        val canPostpone = IDOnboardingFlowHandler.canPostponeOnboarding
        navigation?.showCustomBottomSheet(
            image = R.drawable.img_aguardando_doc,
            title = getString(R.string.id_onboarding_update_cpf_max_tries_bs_title),
            message = getString(
                if (canPostpone)
                    R.string.id_onboarding_update_cpf_max_tries_bs_message
                else
                    R.string.id_onboarding_update_cpf_max_tries_mandatory_bs_message,
                IDOnboardingFlowHandler.userStatus.p1Flow?.cpfValidation?.validationBlockedTimeRemainingAsText
                    ?: getString(R.string.twenty_four_hours)
            ),
            bt2Title = if (canPostpone)
                getString(R.string.go_to_home)
            else
                getString(R.string.entendi),
            bt2Callback = if (canPostpone)
                { ->
                    goToHome()
                    false
                }
            else
                { ->
                    baseLogout()
                    false
                },
        )
    }
    override fun showErrorLabel(error: ErrorMessage?) {
        doWhenResumed {
            errorMessage = error
            binding?.tvTypedValueErrorLabel?.text = error?.message
            binding?.tvTypedValueErrorLabel?.visible(error?.message.orEmpty().isNotBlank())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}