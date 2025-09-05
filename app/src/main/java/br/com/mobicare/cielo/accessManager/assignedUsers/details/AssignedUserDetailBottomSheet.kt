package br.com.mobicare.cielo.accessManager.assignedUsers.details

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.assignedUsers.AccessManagerAssignedUsersContract
import br.com.mobicare.cielo.accessManager.assignedUsers.AccessManagerAssignedUsersFragment
import br.com.mobicare.cielo.accessManager.model.AccessManagerCustomProfileResponse
import br.com.mobicare.cielo.accessManager.model.AccessManagerUser
import br.com.mobicare.cielo.commons.constants.FOUR
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TIME_TO_DISMISS
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.Text.ATIVO
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.messageError
import br.com.mobicare.cielo.commons.utils.setLeftDrawable
import br.com.mobicare.cielo.commons.utils.setupBottomSheet
import br.com.mobicare.cielo.commons.utils.showSuccess
import br.com.mobicare.cielo.databinding.AssignedUserBottomSheetBinding
import br.com.mobicare.cielo.extensions.capitalizeWords
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.login.domains.entities.UserObj
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection

class AssignedUserDetailBottomSheet : BottomSheetDialogFragment(), AssignedUserDetailContract.View {

    private val presenter: AssignedUserDetailPresenter by inject {
        parametersOf(this)
    }

    lateinit var listener: AccessManagerAssignedUsersContract.Listener
    lateinit var listenerBS: AccessManagerAssignedUsersFragment
    lateinit var user: AccessManagerUser
    var canUserBeRemoved: Boolean = false

    lateinit var binding: AssignedUserBottomSheetBinding

    private var selectedRole: String = EMPTY
    private var currentRole: String = EMPTY
    private var mustReloadUserList: Boolean = false
    private var selectedProfileId: String = EMPTY
    private var selectedProfileName: String = EMPTY
    private var customProfileEnabled: Boolean = false
    private var changedCustomProfile: Boolean = false
    private var isCustomProfile: Boolean = false

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    companion object {
        fun onCreate(
            user: AccessManagerUser,
            listener: AccessManagerAssignedUsersContract.Listener,
            listenerBS: AccessManagerAssignedUsersFragment,
            canUserBeRemoved: Boolean,
            selectedProfileId: String,
            selectedProfileName: String,
            customProfileEnabled: Boolean,
            changedCustomProfile: Boolean,
        ) = AssignedUserDetailBottomSheet().apply {
            this.user = user
            this.listener = listener
            this.listenerBS = listenerBS
            this.canUserBeRemoved = canUserBeRemoved
            this.currentRole = user.profile?.id.toString()
            this.selectedProfileId = selectedProfileId
            this.selectedProfileName = selectedProfileName
            this.customProfileEnabled = customProfileEnabled
            this.changedCustomProfile = changedCustomProfile
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupBottomSheet(dialog = dialog,
            action = { dismiss() }
        )
        binding = AssignedUserBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    private fun setupView() {
        binding.apply{
            isCustomProfile = isCustomProfile(selectedProfileId)
            tvTitle.text = user.name.capitalizeWords()
            setupSubtitle()
            tvEmailValue.text = user.email
            tvCPFValue.text = user.cpf
            setupStatusToken()

            tvInfo.text = getString(
                if (presenter.isCnpj()) R.string.assigned_user_bs_info_cnpj else R.string.assigned_user_bs_info_cpf,
                presenter.getDocument()
            )

            if (user.cpf.isNullOrEmpty()){
                tvCPF.visibility = View.GONE
                tvCPFValue.visibility = View.GONE
            }

            setupEditProfileType()
            setupRemoveBtn()
            presenter.checkTechnicalToogle()

            if (isCustomProfile){
                rbCustomProfile.visible()
                rbCustomProfile.text = selectedProfileName
                rgProfileType.check(R.id.rbCustomProfile)
                setBtnSaveChangesIsEnabled(changedCustomProfile)
                selectedRole = selectedProfileId
            }else{
                rbCustomProfile.gone()
            }

            presenter.getCustomActiveProfiles(customProfileEnabled)
        }
    }

    private fun setupEditProfileType() {
        val radioButtonId = when (currentRole) {
            UserObj.ADMIN, UserObj.MASTER -> R.id.rbAdmin
            UserObj.READER -> R.id.rbReader
            UserObj.ANALYST -> R.id.rbAnalyst
            UserObj.TECHNICAL -> R.id.rbTechnical
            else -> R.id.rbCustomProfile
        } ?: return
        binding.rgProfileType.check(radioButtonId)
        binding.rgProfileType.setOnCheckedChangeListener { radioGroup, _ ->
            val selectedRoleView =
                radioGroup.findViewById<AppCompatRadioButton>(radioGroup.checkedRadioButtonId)
            selectedRole = when (radioGroup.indexOfChild(selectedRoleView)) {
                ZERO -> UserObj.ADMIN
                ONE -> UserObj.READER
                TWO -> UserObj.ANALYST
                THREE -> UserObj.TECHNICAL
                FOUR -> selectedProfileId
                else -> user.mainRoleDescription()
            }
            setBtnSaveChangesIsEnabled(selectedRole != currentRole)
        }
        binding.btnSaveChanges.setOnClickListener {
            saveChanges()
        }
        binding.btnSelectCustomProfile.setOnClickListener {
            listener.openSelectCustomProfile(user)
            dismiss()
        }
    }

    private fun setupSubtitle() {
        val subtitle = when (currentRole) {
            UserObj.ADMIN -> R.string.assigned_user_bs_subtitle_admin
            UserObj.ANALYST -> R.string.assigned_user_bs_subtitle_analyst
            UserObj.TECHNICAL -> R.string.assigned_user_bs_subtitle_technical
            UserObj.READER -> R.string.assigned_user_bs_subtitle_reader
            else -> R.string.assigned_user_bs_subtitle_custom
        }
        binding.tvSubtitle.text = getString(subtitle, user.statusDescription())

        context?.let { itContext ->
            binding.dotStatusColor.visible()
            binding.dotStatusColor.backgroundTintList = ContextCompat.getColorStateList(
                itContext, when (user.status) {
                    AccessManagerUser.Status.ATIVO.name -> R.color.success_400
                    AccessManagerUser.Status.EM_CRIACAO.name -> R.color.alert_400
                    AccessManagerUser.Status.BLOQUEADO.name -> R.color.danger_400
                    else -> R.color.danger_400
                }
            )
        }
    }

    private fun setBtnSaveChangesIsEnabled(canSave: Boolean) {
        context?.let {
            val bgId = if (canSave) {
                R.drawable.background_radius8dp_brand_400
            } else {
                R.drawable.background_radius8dp_display_200
            }
            binding.btnSaveChanges.isEnabled = canSave
            binding.btnSaveChanges.background = ContextCompat.getDrawable(it, bgId)
        }
    }

    private fun isCustomProfile(profileId: String?): Boolean {
        return when (profileId) {
            UserObj.ADMIN, UserObj.MASTER, UserObj.ANALYST, UserObj.TECHNICAL, UserObj.READER -> false
            else -> true
        }
    }

    private fun saveChanges(isAnimation: Boolean = true) {
        validationTokenWrapper.generateOtp(showAnimation = isAnimation) { otpCode ->
            presenter.assignRole(user.id.toString(), selectedRole, otpCode)
        }
    }

    override fun onRoleAssigned() {
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    currentRole = selectedRole
                    setupEditProfileType()
                    setupSubtitle()
                    setBtnSaveChangesIsEnabled(false)
                    Toast(requireContext()).showSuccess(
                        getString(R.string.assigned_user_bs_assign_role_success),
                        requireActivity()
                    )
                    mustReloadUserList = currentRole != user.profile?.id

                    Handler(Looper.getMainLooper()).postDelayed({
                        dismiss()
                    }, TIME_TO_DISMISS)
                }
            }
        )
    }

    override fun showCustomProfiles(customProfiles: List<AccessManagerCustomProfileResponse>) {
        binding.btnSelectCustomProfile.visible(customProfiles.isNotEmpty())
    }

    private fun setupRemoveBtn() {
        if (canUserBeRemoved) {
            binding.btnRemove.setOnClickListener {
                listener.onRemoveClicked(user)
                dismiss()
            }
            return
        }
        binding.btnRemove.gone()
    }

    override fun dismiss() {
        if (mustReloadUserList) {
            listener.onUserProfileTypeUpdated(user.id.toString())
        }
        super.dismiss()
    }

    private fun setupStatusToken() {
        if (user.statusToken == null) {
            binding.tvTokenStatusValue.text = SIMPLE_LINE
            return
        }
        binding.tvTokenStatusValue.text = user.statusToken
        val lockerDrawable = if (user.statusToken == ATIVO)
            R.drawable.ic_locker_green
        else
            R.drawable.ic_locker_red
        val icon = context?.let { ContextCompat.getDrawable(it, lockerDrawable) }
        binding.tvTokenStatusValue.setLeftDrawable(icon)
    }

    override fun showLoading() {
        binding.containerProgressBar.visible()
    }

    override fun hideLoading() {
        binding.containerProgressBar.gone()
    }

    override fun showError(error: ErrorMessage?) {
        validationTokenWrapper.playAnimationError(error,
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenError() {
                    if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString()) {
                        showCommonError(error)
                    }
                }
            })
    }

    fun showCommonError(error: ErrorMessage?) {
        listenerBS.bottomSheetGenericFlui(
            EMPTY,
            R.drawable.img_dark_07,
            getString(R.string.assigned_user_bs_error_title),
            activity?.let { messageError(error, it, R.string.assigned_user_bs_error) }.toString(),
            getString(R.string.cancelar),
            getString(R.string.text_try_again_label),
            statusNameTopBar = false,
            statusTitle = true,
            statusSubTitle = true,
            statusImage = true,
            statusBtnClose = false,
            statusBtnFirst = true,
            statusBtnSecond = true,
            statusView1Line = true,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = false
        ).apply {
            onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnFirst(dialog: Dialog) {
                    dialog.dismiss()
                }

                override fun onBtnSecond(dialog: Dialog) {
                    dialog.dismiss()
                    saveChanges(true)
                }

                override fun onSwipeClosed() {
                    dialog?.dismiss()
                }
            }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun showErrorProfile() {
        hideLoading()
        listener.showErrorProfile()
    }

    override fun hideTechnicalUser() {
        binding.rbTechnical.gone()
    }
}